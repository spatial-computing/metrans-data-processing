package edu.usc.infolab.metrans.mapmatching;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import infolab.usc.geo.util.WGS2MetricTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Project points or list of points onto a line
 */
public class MapProjector {
    // Transform GPS to metric space.
    public static final WGS2MetricTransformer transformer = WGS2MetricTransformer.LATransformer;

    /**
     * Computes the index for the closest point on the line to the given point.
     * If more than one point has the closest distance the first one along the line
     * is returned.
     * (The point does not necessarily have to lie precisely on the line.)
     *
     * @param point       the given point to project
     * @param indexedLine the line to project on
     * @return the coordinates of the projection
     */
    public static Coordinate project(Coordinate point, LocationIndexedLine indexedLine) {
        LinearLocation projectInd = indexedLine.project(point);
        Coordinate projection = indexedLine.extractPoint(projectInd);

        return projection;
    }

    /**
     * Computes the index for the closest point on the line to the given point.
     * If more than one point has the closest distance the first one along the line
     * is returned.
     * (The point does not necessarily have to lie precisely on the line.)
     *
     * @param points      the list given points to project
     * @param indexedLine the line to project on
     * @return the coordinates of the projections
     */
    public static List<Coordinate> project(List<Coordinate> points, LocationIndexedLine indexedLine) {
        List<Coordinate> projections = new ArrayList<>();
        for (Coordinate point : points) {
            projections.add(project(point, indexedLine));
        }

        return projections;
    }


    /**
     * Remove projected points that is too far from the original points
     * @param orgPoints list of original points
     * @param projectedPoints list of projected points
     * @param maxAllowedDistance maximum allowed distance between the original and project points
     * @return list of projected points that is not too far from the original points
     */
    public static List<Coordinate> filterDistantPoints(List<Coordinate> orgPoints,
                                                       List<Coordinate> projectedPoints,
                                                       double maxAllowedDistance) {
        List<Coordinate> filtered = new ArrayList<>();
        for (int i = 0; i < orgPoints.size(); i++) {
            Coordinate point = orgPoints.get(i);
            Coordinate projection = projectedPoints.get(i);
            double distance = projection.distance(point);
            if (distance <= maxAllowedDistance) {
                filtered.add(projection);
            }
        }

        return filtered;
    }
}
