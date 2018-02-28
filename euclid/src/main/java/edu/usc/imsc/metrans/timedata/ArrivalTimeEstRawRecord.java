package edu.usc.imsc.metrans.timedata;

public class ArrivalTimeEstRawRecord {
    private String routeId;
    private String stopId;
    private String tripId;
    private int busId;
    private long estimatedTime;
    private double delayTime;
    private int scheduleTime;


    public ArrivalTimeEstRawRecord() {
    }

    public ArrivalTimeEstRawRecord(String routeId, String stopId, String tripId, int busId, long estimatedTime, double delayTime, int scheduleTime) {
        this.routeId = routeId;
        this.stopId = stopId;
        this.tripId = tripId;
        this.busId = busId;
        this.estimatedTime = estimatedTime;
        this.delayTime = delayTime;
        this.scheduleTime = scheduleTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(double delayTime) {
        this.delayTime = delayTime;
    }

    public int getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(int scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Override
    public String toString() {
        return "ArrivalTimeEstRawRecord{" +
                "routeId='" + routeId + '\'' +
                ", stopId='" + stopId + '\'' +
                ", tripId='" + tripId + '\'' +
                ", busId=" + busId +
                ", estimatedTime=" + estimatedTime +
                ", delayTime=" + delayTime +
                ", scheduleTime=" + scheduleTime +
                '}';
    }
}
