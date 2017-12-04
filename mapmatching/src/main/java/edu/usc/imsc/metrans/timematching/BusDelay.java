package edu.usc.imsc.metrans.timematching;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import java.time.ZonedDateTime;

public class BusDelay {

    private StopTime stopTime;
    private ZonedDateTime estimatedTime;
    private Integer busId;
    private Double delayTime;


    public BusDelay(StopTime stopTime, ZonedDateTime estimatedTime, Integer busId, Double delayTime){
        this.stopTime = stopTime;
        this.estimatedTime = estimatedTime;
        this.busId = busId;
        this.delayTime = delayTime;
    }

    public StopTime getStopTime() {return stopTime;}
    public ZonedDateTime getEstimatedTime() {return estimatedTime;}
    public Double getDelayTime() {return delayTime;}
    public Integer getBusId() {return busId;}

}
