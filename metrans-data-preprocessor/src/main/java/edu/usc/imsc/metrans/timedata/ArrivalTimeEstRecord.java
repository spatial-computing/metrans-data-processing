package edu.usc.imsc.metrans.timedata;

import org.onebusaway.gtfs.model.StopTime;

/**
 * Arrival Time Estimation Record
 */
public class ArrivalTimeEstRecord {

    private StopTime stopTime;
    private long estimatedTime;
    private int busId;
    private double delayTime;

    public ArrivalTimeEstRecord(StopTime stopTime, long estimatedTime, int busId, double delayTime){
        this.stopTime = stopTime;
        this.estimatedTime = estimatedTime;
        this.busId = busId;
        this.delayTime = delayTime;
    }

    public StopTime getStopTime() {return stopTime;}
    public long getEstimatedTime() {return estimatedTime;}
    public int getBusId() {return busId;}
    public double getDelayTime() {return delayTime;}
}
