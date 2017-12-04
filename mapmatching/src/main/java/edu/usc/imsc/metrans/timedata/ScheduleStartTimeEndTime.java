package edu.usc.imsc.metrans.timedata;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ScheduleStartTimeEndTime {

    private int startTime;
    private int endTime;

    public ScheduleStartTimeEndTime(ArrayList<StopTime> scheduleTime) {
        this.startTime = scheduleTime.get(0).getArrivalTime();
        this.endTime = scheduleTime.get(scheduleTime.size() - 1).getArrivalTime();
    }

    public int getScheduleStartTime() {
        return startTime;
    }
    public int getScheduleEndTime() {
        return endTime;
    }



}
