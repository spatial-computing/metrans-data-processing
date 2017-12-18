package edu.usc.imsc.metrans.delaytime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import infolab.usc.geo.util.WGS2MetricTransformer;
import org.apache.commons.io.FileUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.onebusaway.gtfs.model.StopTime;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PolylineUtil {
    private static WGS2MetricTransformer transformer = WGS2MetricTransformer.LATransformer;

    public static ArrayList<LinearLocation> getIdxOnLine(List<Coordinate> runCoords, LocationIndexedLine indexedLine)
            throws TransformException {

        ArrayList<LinearLocation> idxOnLine = new ArrayList<>();
        for (Coordinate coord: runCoords) {
            idxOnLine.add(indexedLine.project(coord));
        }
        return idxOnLine;
    }

    public static boolean isDirection(Coordinate point1, Coordinate point2, LocationIndexedLine indexedLine)
            throws TransformException {

        LinearLocation projectInd1 = indexedLine.project(point1);
        LinearLocation projectInd2 = indexedLine.project(point2);

       if(projectInd1.compareTo(projectInd2) <= 0)
           return true;
       else
           return false;
    }

    public static double distanceToLine(Coordinate point, LocationIndexedLine indexedLine)
            throws TransformException {

        LinearLocation projectInd = indexedLine.project(point);
        Coordinate projection = indexedLine.extractPoint(projectInd);
        double distance = projection.distance(point);
        return distance;
    }

    public static double distanceOnShape(Coordinate start, Coordinate end, LineString line) {
        LocationIndexedLine indexedLine = new LocationIndexedLine(line);
        LinearLocation startInd = indexedLine.project(start);
        LinearLocation endInd = indexedLine.project(end);
        // Make startInd is before endInd.
        if (startInd.compareTo(endInd) > 0) {
            LinearLocation temp = endInd;
            endInd = startInd;
            startInd = temp;
        }
        // Calculate the distance from startInd to endInd
        double distance = 0;
        if (startInd.getSegmentIndex() == endInd.getSegmentIndex()) {
            distance = indexedLine.extractPoint(startInd).distance(indexedLine.extractPoint(endInd));
        } else {
            Coordinate matchedStart = indexedLine.extractPoint(startInd);
            Coordinate matchedEnd = null;
            // Sum the length of intermediate segments.
            for (int i = startInd.getSegmentIndex() + 1; i <= endInd.getSegmentIndex(); ++i) {
                matchedEnd = line.getCoordinateN(i);
                distance += matchedStart.distance(matchedEnd);
                matchedStart = matchedEnd;
            }
            matchedEnd = indexedLine.extractPoint(endInd);
            distance += matchedStart.distance(matchedEnd);
        }
        return distance;
    }

    public static List<Coordinate> getRunCoord(ArrayList<BusGpsRecord> run) throws TransformException {
        List<Coordinate> runCoord = new ArrayList<>();
        for (BusGpsRecord g: run) {
            Coordinate coord = new Coordinate(g.getLon(), g.getLat());
            runCoord.add(coord);
        }
        runCoord = transformer.fromWGS84(runCoord);
        return runCoord;
    }

    public static List<Coordinate> getStopCoord(ArrayList<StopTime> schedules) throws TransformException {
        List<Coordinate> stopCoord = new ArrayList<>();
        for (StopTime s: schedules) {
            Coordinate coord = new Coordinate(s.getStop().getLon(), s.getStop().getLat());
            stopCoord.add(coord);
        }
        stopCoord = transformer.fromWGS84(stopCoord);
        return stopCoord;
    }
}
