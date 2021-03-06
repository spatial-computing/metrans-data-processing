package edu.usc.imsc.metrans.timedata;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import java.time.ZonedDateTime;

public class DelayTimeRecord {

    private StopTime stopTime;
    private ZonedDateTime estimatedTime;
    private int runId;
    private int busId;
    private double delayTime;

    public DelayTimeRecord(StopTime stopTime, ZonedDateTime estimatedTime, int runId, int busId, double delayTime){
        this.stopTime = stopTime;
        this.estimatedTime = estimatedTime;
        this.runId = runId;
        this.busId = busId;
        this.delayTime = delayTime;
    }

    public StopTime getStopTime() {return stopTime;}
    public ZonedDateTime getEstimatedTime() {return estimatedTime;}
    public int getRunId() {return runId;}
    public int getBusId() {return busId;}
    public double getDelayTime() {return delayTime;}

}
