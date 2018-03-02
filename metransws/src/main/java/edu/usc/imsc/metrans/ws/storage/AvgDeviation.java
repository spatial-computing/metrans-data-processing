package edu.usc.imsc.metrans.ws.storage;

public class AvgDeviation {
    private long routeId;
    private double avgDeviation = Double.NEGATIVE_INFINITY;
    private int rank;

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public double getAvgDeviation() {
        return avgDeviation;
    }

    public void setAvgDeviation(double avgDeviation) {
        this.avgDeviation = avgDeviation;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
