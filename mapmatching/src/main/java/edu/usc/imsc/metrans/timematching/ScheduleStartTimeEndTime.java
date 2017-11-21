package edu.usc.imsc.metrans.timematching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ScheduleStartTimeEndTime {

    private Integer startTime;
    private Integer endTime;

    public ScheduleStartTimeEndTime(ArrayList<StopTime> scheduleTime) {
        this.startTime = scheduleTime.get(0).getArrivalTime();
        this.endTime = scheduleTime.get(scheduleTime.size() - 1).getArrivalTime();
    }

    public Integer getScheduleStartTime() {
        return startTime;
    }
    public Integer getScheduleEndTime() {
        return endTime;
    }



}
