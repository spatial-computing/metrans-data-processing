package edu.usc.imsc.metrans.utils;

import edu.usc.imsc.metrans.ws.storage.AvgDeviation;

import java.util.ArrayList;
import java.util.Comparator;

public class Utils {
    public static void rankAvgDeviationForRoutes(ArrayList<AvgDeviation> avgDeviations) {
        avgDeviations.sort(new Comparator<AvgDeviation>() {
            @Override
            public int compare(AvgDeviation o1, AvgDeviation o2) {
                return Double.compare(o2.getAvgDeviation(), o1.getAvgDeviation());
            }
        });

        for (int i = 0; i < avgDeviations.size(); i++) {
            avgDeviations.get(i).setRank(i + 1);
        }
    }
}
