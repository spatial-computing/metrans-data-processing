package edu.usc.imsc.metrans.timedata;

import org.onebusaway.gtfs.model.StopTime;

import java.time.ZonedDateTime;

/**
 * A record containing estimated arrival time, and hence, delay time for stop-time schedule
 */
public class DelayTimeRecord {

    private StopTime stopTime; // stop-time schedule we want to estimate
    private ZonedDateTime estimatedTime;
    private int busId;
    private double delayTime;

    public DelayTimeRecord(StopTime stopTime, ZonedDateTime estimatedTime, int busId, double delayTime){
        this.stopTime = stopTime;
        this.estimatedTime = estimatedTime;
        this.busId = busId;
        this.delayTime = delayTime;
    }

    public StopTime getStopTime() {return stopTime;}
    public ZonedDateTime getEstimatedTime() {return estimatedTime;}
    public int getBusId() {return busId;}
    public double getDelayTime() {return delayTime;}

}
