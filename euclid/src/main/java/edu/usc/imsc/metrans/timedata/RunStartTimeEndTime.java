package edu.usc.imsc.metrans.timedata;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.arrivaltimeestimators.Util;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class RunStartTimeEndTime {

    private long startTime;
    private long endTime;

    public RunStartTimeEndTime(ArrayList<BusGpsRecord> run) {
        ZonedDateTime startTimeZoned = Util.convertSecondsToZonedDateTime(run.get(0).getBusLocationTime());
        this.startTime = Util.getSecondsFromNoonMinus12Hours(startTimeZoned);

        ZonedDateTime endTimeZoned = Util.convertSecondsToZonedDateTime(run.get(run.size() - 1).getBusLocationTime());
        this.endTime = Util.getSecondsFromNoonMinus12Hours(endTimeZoned);;
    }

    public long getRunStartTime() {
        return startTime;
    }
    public long getRunEndTime() { return endTime; }


}
