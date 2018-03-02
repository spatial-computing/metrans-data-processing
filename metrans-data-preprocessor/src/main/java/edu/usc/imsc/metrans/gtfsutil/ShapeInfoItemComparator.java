package edu.usc.imsc.metrans.gtfsutil;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class ShapeInfoItemComparator implements Comparator<ShapeInfoItem> {
    @Override
    public int compare(ShapeInfoItem o1, ShapeInfoItem o2) {
        return new CompareToBuilder()
                .append(o1.getRoute_id(), o2.getRoute_id())
                .append(o1.getStop_sequence(), o2.getStop_sequence())
                .toComparison();
    }
}
