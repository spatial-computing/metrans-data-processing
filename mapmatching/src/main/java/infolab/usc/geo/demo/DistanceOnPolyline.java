package infolab.usc.geo.demo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import infolab.usc.geo.util.WGS2MetricTransformer;
import org.apache.commons.io.FileUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DistanceOnPolyline {

    public static void main(String[] args) throws TransformException, IOException {
        distanceOnShape();
    }

    private static double distanceOnShape(LineString line, Coordinate start, Coordinate end) {
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

    private static void distanceOnShape() throws TransformException, IOException {
        // Transform GPS to metric space.
        WGS2MetricTransformer transformer = WGS2MetricTransformer.LATransformer;

        // Read the shape file as a polyline
        String routeShapeFilename = "data/metrans/shape-10.csv";
        LineString route = readShapeFile(routeShapeFilename);
        route = (LineString) transformer.fromWGS84(route);
        LocationIndexedLine indexedLine = new LocationIndexedLine(route);

        String gpsFilename = "data/metrans/gps-10.csv";
        List<Coordinate> points = new ArrayList<>();
        List<String> lines = FileUtils.readLines(new File(gpsFilename));
        for (String line : lines.subList(1, lines.size())) {
            String[] fields = line.split(",");
            double lat = Double.valueOf(fields[7]), lon = Double.valueOf(fields[8]);
            points.add(new Coordinate(lon, lat));
        }
        points = transformer.fromWGS84(points);

        // Calculate the distance of two points from on the shape.
        // See points[0], points[10]
        Coordinate start = points.get(0), end = points.get(Math.min(points.size() - 1, 10));
        double distance = distanceOnShape(route, start, end);
        System.out.println(distance);
    }

    private static LineString readShapeFile(String routeShapeFilename) throws IOException {
        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        List<Coordinate> points = new ArrayList<>();
        List<String> lines = FileUtils.readLines(new File(routeShapeFilename));
        for (String line : lines.subList(1, lines.size())) {
            String[] fields = line.split(",");
            double lat = Double.valueOf(fields[1]), lon = Double.valueOf(fields[2]);
            points.add(new Coordinate(lon, lat));
        }
        Coordinate[] coordinates = new Coordinate[points.size()];
        return factory.createLineString(points.toArray(coordinates));
    }
}
