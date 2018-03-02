package edu.usc.imsc.metrans.timedata;

import org.onebusaway.gtfs.model.StopTime;

import java.util.ArrayList;

public class ScheduleStartTimeEndTime {

    private long startTime;
    private long endTime;

    public ScheduleStartTimeEndTime(ArrayList<StopTime> scheduleTime) {
        this.startTime = scheduleTime.get(0).getArrivalTime();
        this.endTime = scheduleTime.get(scheduleTime.size() - 1).getArrivalTime();
    }

    public long getScheduleStartTime() {
        return startTime;
    }
    public long getScheduleEndTime() {
        return endTime;
    }



}
