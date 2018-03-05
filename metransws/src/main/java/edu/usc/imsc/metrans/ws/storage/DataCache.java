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
    public static final String DEFAULT_ONLY_ONE_KEY = "DEFAULT_ONLY_ONE_KEY";

    public static final String AVG_DEVIATION_OVERALL = "AVG_DEVIATION_OVERALL";
    public static final String RELIABILITY_OVERALL = "RELIABILITY_OVERALL";
    public static final String START_TIME = "START_TIME";
    public static final String END_TIME = "END_TIME";

    public static final String AVG_DEVIATION_BY_MONTH_OVERALL = "AVG_DEVIATION_BY_MONTH_OVERALL";
    public static final String AVG_DEVIATION_BY_DOW_OVERALL = "AVG_DEVIATION_BY_DOW_OVERALL";
    public static final String AVG_DEVIATION_BY_HOUR_OVERALL = "AVG_DEVIATION_BY_HOUR_OVERALL";

    private static  LoadingCache<String, ArrayList<DbItemInfo>> routeAvgDeviationsCache = null;
    private static  LoadingCache<Long, ArrayList<DbItemInfo>> stopAvgDeviationsCache = null;

    private static  LoadingCache<String, ArrayList<DbItemInfo>> routeAvgMinPosDelaysCache = null;
    private static  LoadingCache<Long, ArrayList<DbItemInfo>> stopAvgMinPosDelaysCache = null;

    private static  LoadingCache<String, ArrayList<Double>> avgDeviationsByDatePartCache = null;

    private static LoadingCache<String, Double> oneValueCache = null;

    /**
     * Get value of 1 predefined key
     * @param key a predefined key
     * @return result from cache or {@code null} if error occurred
     */
    public static Double getValue(String key) {
        if (oneValueCache == null) {
            synchronized (DataCache.class) {
                if (oneValueCache == null) {
                    oneValueCache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(1, TimeUnit.DAYS)
                            .build(
                                    new CacheLoader<String, Double>() {
                                        @Override
                                        public Double load(String key) {
                                            Double value = null;
                                            switch (key) {
                                                case AVG_DEVIATION_OVERALL:
                                                    DbItemInfo info = DatabaseIO.getAvgDeviationOverall();
                                                    if (info != null) {
                                                        value = info.getTimeDiff();
                                                    }
                                                    break;
                                                case RELIABILITY_OVERALL:
                                                    value = DatabaseIO.getReliability();
                                                    break;
                                                case START_TIME:
                                                    value = (double)DatabaseIO.getMinMaxTime(true);
                                                    break;
                                                case END_TIME:
                                                    value = (double)DatabaseIO.getMinMaxTime(false);
                                                    break;
                                                default:
                                                    value = null;
                                            }

                                            return value;
                                        }
                                    });
                }
            }
        }

        try {
            return oneValueCache.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

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
                            .expireAfterWrite(1, TimeUnit.DAYS)
                            .build(
                                    new CacheLoader<String, ArrayList<DbItemInfo>>() {
                                        @Override
                                        public ArrayList<DbItemInfo> load(String key) {
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
                            .expireAfterWrite(1, TimeUnit.DAYS)
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
                            .expireAfterWrite(1, TimeUnit.DAYS)
                            .build(
                                    new CacheLoader<String, ArrayList<DbItemInfo>>() {
                                        @Override
                                        public ArrayList<DbItemInfo> load(String key) {
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
                            .expireAfterWrite(1, TimeUnit.DAYS)
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


    /**
     * Get average arrival time deviation of all routes by date part (month, day of week, hour)
     * @return average deviation objects of routes or empty list if error occurred
     */
    public static ArrayList<Double> getAvgDeviationsByDatePart(String datePart) {
        if (avgDeviationsByDatePartCache == null) {
            synchronized (DataCache.class) {
                if (avgDeviationsByDatePartCache == null) {
                    avgDeviationsByDatePartCache = CacheBuilder.newBuilder()
                            .maximumSize(10000)
                            .expireAfterWrite(1, TimeUnit.DAYS)
                            .build(
                                    new CacheLoader<String, ArrayList<Double>>() {
                                        @Override
                                        public ArrayList<Double> load(String key) {
                                            ArrayList<Double> avgDeviations;
                                            switch (key) {
                                                case AVG_DEVIATION_BY_MONTH_OVERALL:
                                                    avgDeviations = DatabaseIO.getAvgDeviationByMonth();
                                                    break;
                                                case AVG_DEVIATION_BY_DOW_OVERALL:
                                                    avgDeviations = DatabaseIO.getAvgDeviationByDayOfWeek();
                                                    break;
                                                case AVG_DEVIATION_BY_HOUR_OVERALL:
                                                    avgDeviations = DatabaseIO.getAvgDeviationByHourOfDay();
                                                    break;
                                                default:
                                                    avgDeviations = new ArrayList<>();
                                            }

                                            return avgDeviations;
                                        }
                                    });
                }
            }
        }

        try {
            return avgDeviationsByDatePartCache.get(datePart);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Prepare values of caches that uses constant keys
     */
    public static void prepareConstantKeyCache() {
        getValue(AVG_DEVIATION_OVERALL);
        getValue(RELIABILITY_OVERALL);

        getAvgDeviationsOfAllRoutes();
        getAvgMinPosDelaysOfAllRoutes();

        DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_MONTH_OVERALL);
        DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_HOUR_OVERALL);
        DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_DOW_OVERALL);
    }
}
