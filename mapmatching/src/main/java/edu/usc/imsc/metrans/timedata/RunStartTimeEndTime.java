package edu.usc.imsc.metrans.timedata;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Start time and End time of a GPS run
 */
public class RunStartTimeEndTime {

    private int startTime;
    private int endTime;

    public RunStartTimeEndTime(ArrayList<BusGpsRecord> run) {
        ZonedDateTime busLocationStartTime = run.get(0).getBusLocationTime();
        ZonedDateTime busLocationEndTime = run.get(run.size() - 1).getBusLocationTime();
        this.startTime = busLocationStartTime.getHour() * 3600 + busLocationStartTime.getMinute() * 60 +
                busLocationStartTime.getSecond();
        this.endTime = busLocationEndTime.getHour() * 3600 + busLocationEndTime.getMinute() * 60 +
                busLocationEndTime.getSecond();
    }

    public int getRunStartTime() {
        return startTime;
    }
    public int getRunEndTime() { return endTime; }


}
