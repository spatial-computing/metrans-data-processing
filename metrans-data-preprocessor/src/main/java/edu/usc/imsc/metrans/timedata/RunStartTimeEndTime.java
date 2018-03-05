package edu.usc.imsc.metrans.timedata;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.arrivaltimeestimators.Util;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class RunStartTimeEndTime extends StartTimeEndTime {

    public RunStartTimeEndTime(ArrayList<BusGpsRecord> run) {
        super();

        ZonedDateTime startTimeZoned = Util.convertEpochSecondsToZonedDateTime(run.get(0).getBusLocationTime());
        this.startTime = Util.getSecondsFromNoonMinus12Hours(startTimeZoned);

        ZonedDateTime endTimeZoned = Util.convertEpochSecondsToZonedDateTime(run.get(run.size() - 1).getBusLocationTime());
        this.endTime = Util.getSecondsFromNoonMinus12Hours(endTimeZoned);;
    }

}
