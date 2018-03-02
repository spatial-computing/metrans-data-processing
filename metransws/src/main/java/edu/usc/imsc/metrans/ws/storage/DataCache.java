package edu.usc.imsc.metrans.ws.storage;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.usc.imsc.metrans.database.DatabaseIO;
import edu.usc.imsc.metrans.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DataCache {
    private static final Integer DEFAULT_ONLY_ONE_KEY = 1;
    private static  LoadingCache<Integer, ArrayList<AvgDeviation>> routeAvgDeviationsCache = null;

    private static  LoadingCache<Long, AvgDeviation> routeAvgDeviationCache = null;

    /**
     * Get average deviation objects of routes
     * @return average deviation objects of routes or empty list if error occurred
     */
    public static ArrayList<AvgDeviation> getAvgDeviationsOfAllRoutes() {
        if (routeAvgDeviationsCache == null) {
            synchronized (DataCache.class) {
                if (routeAvgDeviationsCache == null) {
                    routeAvgDeviationsCache = CacheBuilder.newBuilder()
                            .maximumSize(1000)
                            .expireAfterWrite(1, TimeUnit.HOURS)
                            .build(
                                    new CacheLoader<Integer, ArrayList<AvgDeviation>>() {
                                        @Override
                                        public ArrayList<AvgDeviation> load(Integer key) {
                                            ArrayList<AvgDeviation> avgDeviations = DatabaseIO.getAvgDeviationPerRoutes();
                                            Utils.rankAvgDeviationForRoutes(avgDeviations);

                                            return avgDeviations;
                                        }
                                    });
                }
            }
        }

        try {
            return routeAvgDeviationsCache.get(DEFAULT_ONLY_ONE_KEY);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
