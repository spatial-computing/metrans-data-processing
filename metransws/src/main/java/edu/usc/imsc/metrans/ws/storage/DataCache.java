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
    private static  LoadingCache<Integer, ArrayList<DbItemInfo>> routeAvgDeviationsCache = null;
    private static  LoadingCache<Long, ArrayList<DbItemInfo>> stopAvgDeviationsCache = null;

    private static  LoadingCache<Integer, ArrayList<DbItemInfo>> routeAvgMinPosDelaysCache = null;
    private static  LoadingCache<Long, ArrayList<DbItemInfo>> stopAvgMinPosDelaysCache = null;

    /**
     * Get average deviation objects of routes
     * @return average deviation objects of routes or empty list if error occurred
     */
    public static ArrayList<DbItemInfo> getAvgDeviationsOfAllRoutes() {
        if (routeAvgDeviationsCache == null) {
            synchronized (DataCache.class) {
                if (routeAvgDeviationsCache == null) {
                    routeAvgDeviationsCache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(1, TimeUnit.HOURS)
                            .build(
                                    new CacheLoader<Integer, ArrayList<DbItemInfo>>() {
                                        @Override
                                        public ArrayList<DbItemInfo> load(Integer key) {
                                            ArrayList<DbItemInfo> avgDeviations = DatabaseIO.getAvgDeviationPerRoutes();
                                            Utils.rankTimeDiff(avgDeviations);

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

    /**
     * Get average deviation objects of stops of a route
     * @return average deviation objects of stops of a route or empty list if error occurred
     */
    public static ArrayList<DbItemInfo> getAvgDeviationsOfStopsOfRoute(Long routeId) {
        if (stopAvgDeviationsCache == null) {
            synchronized (DataCache.class) {
                if (stopAvgDeviationsCache == null) {
                    stopAvgDeviationsCache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(1, TimeUnit.HOURS)
                            .build(
                                    new CacheLoader<Long, ArrayList<DbItemInfo>>() {
                                        @Override
                                        public ArrayList<DbItemInfo> load(Long key) {
                                            ArrayList<DbItemInfo> avgDeviations = DatabaseIO.getAvgDeviationOfStopsOfRoute(key);
                                            Utils.rankTimeDiff(avgDeviations);

                                            return avgDeviations;
                                        }
                                    });
                }
            }
        }

        try {
            return stopAvgDeviationsCache.get(routeId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get average min positive delay objects of routes
     * @return average min positive delay objects of routes or empty list if error occurred
     */
    public static ArrayList<DbItemInfo> getAvgMinPosDelaysOfAllRoutes() {
        if (routeAvgMinPosDelaysCache == null) {
            synchronized (DataCache.class) {
                if (routeAvgMinPosDelaysCache == null) {
                    routeAvgMinPosDelaysCache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(1, TimeUnit.HOURS)
                            .build(
                                    new CacheLoader<Integer, ArrayList<DbItemInfo>>() {
                                        @Override
                                        public ArrayList<DbItemInfo> load(Integer key) {
                                            ArrayList<DbItemInfo> avgMinPosDelays = DatabaseIO.getAvgMinPosDelayPerRoutes();
                                            Utils.rankTimeDiff(avgMinPosDelays);

                                            return avgMinPosDelays;
                                        }
                                    });
                }
            }
        }

        try {
            return routeAvgMinPosDelaysCache.get(DEFAULT_ONLY_ONE_KEY);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get average min positive delay objects of a route
     * @return average min positive delay objects of routes or empty list if error occurred
     */
    public static ArrayList<DbItemInfo> getAvgMinPosDelaysOfStopsOfRoute(Long routeId) {
        if (stopAvgMinPosDelaysCache == null) {
            synchronized (DataCache.class) {
                if (stopAvgMinPosDelaysCache == null) {
                    stopAvgMinPosDelaysCache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(1, TimeUnit.HOURS)
                            .build(
                                    new CacheLoader<Long, ArrayList<DbItemInfo>>() {
                                        @Override
                                        public ArrayList<DbItemInfo> load(Long key) {
                                            ArrayList<DbItemInfo> avgMinPosDelays = DatabaseIO.getAvgMinPosDelayOfStopsOfRoute(key);
                                            Utils.rankTimeDiff(avgMinPosDelays);

                                            return avgMinPosDelays;
                                        }
                                    });
                }
            }
        }

        try {
            return stopAvgMinPosDelaysCache.get(routeId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
