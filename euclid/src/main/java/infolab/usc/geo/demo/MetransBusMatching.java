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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MetransBusMatching {

    public static void main(String[] args) throws TransformException, IOException {
        // Transform GPS to metric space.
        WGS2MetricTransformer transformer = WGS2MetricTransformer.LATransformer;

        // Read the shape file as a polyline
        String routeShapeFilename = "data/metrans/shape-10.csv";
        LineString route = readShapeFile(routeShapeFilename);
        route = (LineString) transformer.fromWGS84(route);
        LocationIndexedLine indexedLine = new LocationIndexedLine(route);


        // Reads the raw GPS data as a list of coordinates
        double maxAllowedDistance = 250;
        String gpsFilename = "data/metrans/gps-10.csv";
        List<Coordinate> points = new ArrayList<>();
        List<String> lines = FileUtils.readLines(new File(gpsFilename),StandardCharsets.UTF_8);
        List<String[]> fieldsList = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            String[] fields = line.split(",");
            fieldsList.add(fields);
            double lat = Double.valueOf(fields[7]), lon = Double.valueOf(fields[8]);
            points.add(new Coordinate(lon, lat));
        }
        points = transformer.fromWGS84(points);

        // For each GPS point calculate the distance.

        List<String> matchedLines = new ArrayList<>();
        // Adds header.
        matchedLines.add(lines.get(0));
        for (int i = 0; i < points.size(); ++i) {
            Coordinate point = points.get(i);
            String[] fields = fieldsList.get(i);
            LinearLocation projectInd = indexedLine.project(point);
            Coordinate projection = indexedLine.extractPoint(projectInd);
            double distance = projection.distance(point);
            if (distance > maxAllowedDistance) {
                System.out.println(point.toString() + ", " + distance);
            } else {
                // Transform back to WGS85
                projection = transformer.toWGS84(projection);
                fields[7] = String.format("%.5f", projection.y);
                fields[8] = String.format("%.5f", projection.x);
                matchedLines.add(String.join(",", fields));
            }
        }

        // Writes to output file.
        String outputGPSFilename = "data/metrans/matched_gps-10.csv";
        FileUtils.writeLines(new File(outputGPSFilename), "utf-8", matchedLines);
    }

    private static LineString readShapeFile(String routeShapeFilename) throws IOException {
        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        List<Coordinate> points = new ArrayList<>();
        List<String> lines = FileUtils.readLines(new File(routeShapeFilename), StandardCharsets.UTF_8);
        for (String line : lines.subList(1, lines.size())) {
            String[] fields = line.split(",");
            double lat = Double.valueOf(fields[1]), lon = Double.valueOf(fields[2]);
            points.add(new Coordinate(lon, lat));
        }
        Coordinate[] coordinates = new Coordinate[points.size()];
        return factory.createLineString(points.toArray(coordinates));
    }
}
