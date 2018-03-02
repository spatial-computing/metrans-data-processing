package edu.usc.imsc.metrans.timedata;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

/**
 * Compare objects of ArrivalTimeEstRawRecord by route, then stop, then trip
 */
public class ArrivalTimeEstRawRecordComparator implements Comparator<ArrivalTimeEstRawRecord> {
    @Override
    public int compare(ArrivalTimeEstRawRecord o1, ArrivalTimeEstRawRecord o2) {
        return new CompareToBuilder()
                .append(o1.getRouteId(), o2.getRouteId())
                .append(o1.getStopId(), o2.getStopId())
                .append(o1.getTripId(), o2.getTripId())
                .toComparison();
    }
}
