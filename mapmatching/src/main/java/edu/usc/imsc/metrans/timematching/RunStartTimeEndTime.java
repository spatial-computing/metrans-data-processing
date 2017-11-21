package edu.usc.imsc.metrans.timematching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class RunStartTimeEndTime {

    private Integer startTime;
    private Integer endTime;

    public RunStartTimeEndTime(ArrayList<BusGpsRecord> run) {
        ZonedDateTime busLocationStartTime = run.get(0).getBusLocationTime();
        ZonedDateTime busLocationEndTime = run.get(run.size() - 1).getBusLocationTime();
        this.startTime = busLocationStartTime.getHour() * 3600 + busLocationStartTime.getMinute() * 60 +
                busLocationStartTime.getSecond();
        this.endTime = busLocationEndTime.getHour() * 3600 + busLocationEndTime.getMinute() * 60 +
                busLocationEndTime.getSecond();
    }

    public Integer getRunStartTime() {
        return startTime;
    }
    public Integer getRunEndTime() { return endTime; }


}
