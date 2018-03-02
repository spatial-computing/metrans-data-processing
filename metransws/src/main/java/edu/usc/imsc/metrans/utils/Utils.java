package edu.usc.imsc.metrans.utils;

import edu.usc.imsc.metrans.ws.storage.DbItemInfo;

import java.util.ArrayList;
import java.util.Comparator;

public class Utils {
    public static final long ERROR_VALUE = -999999999;
    /**
     * Rank time diff of items in descending order
     * @param avgDeviations items
     */
    public static void rankTimeDiff(ArrayList<DbItemInfo> avgDeviations) {
        avgDeviations.sort(new Comparator<DbItemInfo>() {
            @Override
            public int compare(DbItemInfo o1, DbItemInfo o2) {
                return Double.compare(o2.getTimeDiff(), o1.getTimeDiff());
            }
        });

        for (int i = 0; i < avgDeviations.size(); i++) {
            avgDeviations.get(i).setRank(i + 1);
        }
    }
}
