package edu.usc.imsc.metrans.timedata;

import org.onebusaway.gtfs.model.StopTime;

import java.util.ArrayList;

public class TripStartTimeEndTime extends StartTimeEndTime {
    public TripStartTimeEndTime(ArrayList<StopTime> stopTimes) {
        this.startTime = stopTimes.get(0).getArrivalTime();
        this.endTime = stopTimes.get(stopTimes.size() - 1).getArrivalTime();
    }

}
