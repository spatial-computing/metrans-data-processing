package edu.usc.imsc.metrans.busdata;

import edu.usc.imsc.metrans.arrivaltimeestimators.Util;

/**
 * A simplified bus GPS record
 */
public class BusGpsRecord {

    private long dateAndTime; //epoch timestamp
    private int busId;
    private int lineId;
    private int runId;
    private int routeId;
    private int busDirection;
    private double lat;
    private double lon;
    private long busLocationTime; //epoch timestamp

    public BusGpsRecord() {
    }

    public BusGpsRecord(long dateAndTime, int busId, int lineId, int runId, int routeId, int busDirection, double lat, double lon, long busLocationTime) {
        this.dateAndTime = dateAndTime;
        this.busId = busId;
        this.lineId = lineId;
        this.runId = runId;
        this.routeId = routeId;
        this.busDirection = busDirection;
        this.lat = lat;
        this.lon = lon;
        this.busLocationTime = busLocationTime;
    }


    public long getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(long dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getBusDirection() {
        return busDirection;
    }

    public void setBusDirection(int busDirection) {
        this.busDirection = busDirection;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getBusLocationTime() {
        return busLocationTime;
    }

    public void setBusLocationTime(long busLocationTime) {
        this.busLocationTime = busLocationTime;
    }

    @Override
    public String toString() {
        return "BusGpsRecord{" +
                "dateAndTime=" + Util.convertEpochSecondsToZonedDateTime(dateAndTime).format(BusDataIO.defaultDateTimeParser) +
                ", busId=" + busId +
                ", lineId=" + lineId +
                ", runId=" + runId +
                ", routeId=" + routeId +
                ", busDirection=" + busDirection +
                ", lat=" + lat +
                ", lon=" + lon +
                ", busLocationTime=" + Util.convertEpochSecondsToZonedDateTime(busLocationTime).format(BusDataIO.defaultDateTimeParser) +
                '}';
    }
}
