package edu.usc.imsc.metrans.timedata;

public class BusBunchingRecord extends ArrivalTimeEstRawRecord {
    private int numBusBunching;

    public BusBunchingRecord() {
    }

    public BusBunchingRecord(String routeId, String stopId, String tripId, int busId, long estimatedTime, double delayTime, int scheduleTime, int numBusBunching) {
        super(routeId, stopId, tripId, busId, estimatedTime, delayTime, scheduleTime);
        this.numBusBunching = numBusBunching;
    }

    public int getNumBusBunching() {
        return numBusBunching;
    }

    public void setNumBusBunching(int numBusBunching) {
        this.numBusBunching = numBusBunching;
    }

    @Override
    public String toString() {
        return "BusBunchingRecord{" +
                super.toString() +
                "numBusBunching=" + numBusBunching +
                '}';
    }
}
