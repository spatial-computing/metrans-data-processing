package edu.usc.imsc.metrans.delaytime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import infolab.usc.geo.util.WGS2MetricTransformer;
import org.onebusaway.gtfs.model.StopTime;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;
import java.util.List;

import static edu.usc.imsc.metrans.delaytime.Util.getAngle;

public class InBetweenStops {

    public static Integer getGpsPair(ArrayList<LinearLocation> runIdxOnLine, LinearLocation stopIdx) {

        if (stopIdx.compareTo(runIdxOnLine.get(0)) < 0) return 0;
        else if ((stopIdx.compareTo(runIdxOnLine.get(runIdxOnLine.size() - 1)) > 0))
            return runIdxOnLine.size() - 2;

        else {
            for (int i = 0; i < runIdxOnLine.size() - 1; i++) {
                if (stopIdx.compareTo(runIdxOnLine.get(i)) >= 0 && stopIdx.compareTo(runIdxOnLine.get(i + 1)) < 0)
                    return i;
            }
        }
        return -1;
    }


    public static ArrayList<Integer> findInBetweenStopsOnLine(Coordinate gps1Coord, Coordinate gps2Coord,
            List<Coordinate> stopCoord, LineString line) throws TransformException {

        ArrayList<Integer> inBetweenStopsInd = new ArrayList<>();

        LocationIndexedLine indexedLine = new LocationIndexedLine(line);

        LinearLocation gps1ProjectInd = indexedLine.project(gps1Coord);
        LinearLocation gps2ProjectInd = indexedLine.project(gps2Coord);

        for (int i = 0; i < stopCoord.size(); i++) {
            Coordinate point = stopCoord.get(i);
            LinearLocation stopProjectInd = indexedLine.project(point);

            if (gps1ProjectInd.compareTo(stopProjectInd) < 0 && gps2ProjectInd.compareTo(stopProjectInd) >= 0) {
                inBetweenStopsInd.add(i);
            }
        }
        return inBetweenStopsInd;
    }

    public static ArrayList<StopTime> findInBetweenStops(BusGpsRecord gps1, BusGpsRecord gps2, ArrayList<StopTime> stopTimes) {

        ArrayList<StopTime> inBetweenStops = new ArrayList<>();

        double gps1Lon = gps1.getLon();
        double gps1Lat = gps1.getLat();
        double gps2Lon = gps2.getLon();
        double gps2Lat = gps2.getLat();

        for (int i = 0; i < stopTimes.size(); i++) {
            double stopLon = stopTimes.get(i).getStop().getLon();
            double stopLat = stopTimes.get(i).getStop().getLat();
            if (getAngle(stopLon, stopLat, gps1Lon, gps1Lat, gps2Lon, gps2Lat) >= 150.0) {
                inBetweenStops.add(stopTimes.get(i));
            }
        }
        return inBetweenStops;
    }
}
