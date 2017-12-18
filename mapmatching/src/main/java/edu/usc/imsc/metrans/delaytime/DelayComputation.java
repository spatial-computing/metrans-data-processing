package edu.usc.imsc.metrans.delaytime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import infolab.usc.geo.util.WGS2MetricTransformer;
import org.onebusaway.gtfs.model.StopTime;
import org.opengis.referencing.operation.TransformException;

import static edu.usc.imsc.metrans.delaytime.InBetweenStops.findInBetweenStopsOnLine;
import static edu.usc.imsc.metrans.delaytime.InBetweenStops.getGpsPair;
import static edu.usc.imsc.metrans.delaytime.PolylineUtil.*;
import static edu.usc.imsc.metrans.delaytime.Util.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayComputation {

    private static double delayTimeThreshold = 15 * 60;

    public static ArrayList<DelayTimeRecord> delayComputation(Integer runId, ArrayList<BusGpsRecord> run,
            List<Coordinate> runCoords, Map<String, ArrayList<StopTime>> closestCandidateSchedules,
            Map<String, LineString> shapes) throws TransformException {

        int busId = run.get(0).getBusId();

        ArrayList<DelayTimeRecord> estimatedArrivalTimeResult =  new ArrayList<>();

        for(String schedule : closestCandidateSchedules.keySet()) {

            // Get the shape and stops of a schedule
            LineString line = shapes.get(schedule);
            LocationIndexedLine indexedLine = new LocationIndexedLine(line);
            ArrayList<StopTime> stopTimes = closestCandidateSchedules.get(schedule);
            List<Coordinate> stopCoords = getStopCoord(stopTimes);
            ArrayList<LinearLocation> runIdxOnLine = getIdxOnLine(runCoords, indexedLine);
            ArrayList<LinearLocation> stopIdxOnLine = getIdxOnLine(stopCoords, indexedLine);

            for (int i = 0; i < stopTimes.size(); i++) {

                StopTime stopTime = stopTimes.get(i);
                LinearLocation stopIdx = stopIdxOnLine.get(i);
                Integer gps1_ = getGpsPair(runIdxOnLine, stopIdx);

                if (gps1_ == -1) continue;

                BusGpsRecord gps1 = run.get(gps1_);
                BusGpsRecord gps2 = run.get(gps1_ + 1);

                int gps1Time = zonedDateTimeToInteger(gps1.getBusLocationTime());
                int gps2Time = zonedDateTimeToInteger(gps2.getBusLocationTime());

                Coordinate gps1Coord = runCoords.get(gps1_);
                Coordinate gps2Coord = runCoords.get(gps1_ + 1);
                Coordinate stopCoord = stopCoords.get(i);

                double estimatedTime
                        = calEstimatedArrivalTime(gps1Coord, gps2Coord, stopCoord, line, gps1Time, gps2Time);

                double delay = estimatedTime - stopTime.getArrivalTime();

                // Filter the one that delay too much or arrival too early
                if (delay >= -delayTimeThreshold && delay <= delayTimeThreshold) {
                    ZonedDateTime estimatedArrivalZDT
                            = doubleToZonedDateTime(estimatedTime, gps1.getBusLocationTime());
                    DelayTimeRecord tmp = new DelayTimeRecord(stopTime, estimatedArrivalZDT, runId, busId, delay);
//                    System.out.println(runId + "|||||" + stopIdx.getSegmentIndex() + "|||||"+ estimatedTime + "|||||" + delay);

                    estimatedArrivalTimeResult.add(tmp);
                }
            }
        }
        return estimatedArrivalTimeResult;
    }


    public static Double calEstimatedArrivalTime(Coordinate gps1Coord, Coordinate gps2Coord,
        Coordinate stop, LineString line, int gps1Time, int gps2Time) throws TransformException {

        double d0 = distanceOnShape(gps1Coord, stop, line);
        double d1 = distanceOnShape(gps1Coord, gps2Coord, line);

        double estimatedArrivalTime = d0 / (d1 / (gps2Time - gps1Time)) + gps1Time;
        return estimatedArrivalTime;
    }

    public static Double calEstimatedArrivalTime(BusGpsRecord gps1, BusGpsRecord gps2,
        int gps1Time, int gps2Time, StopTime inBetweenStop) throws TransformException {


        double d0 = getDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat());
        double d1 = getDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getStop().getLon(),
                inBetweenStop.getStop().getLat());

        double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        return estimatedArrivalTime;
    }

}
