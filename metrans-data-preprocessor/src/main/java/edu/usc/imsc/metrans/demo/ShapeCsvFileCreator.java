package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.gtfsutil.ShapeInfoItem;
import edu.usc.imsc.metrans.gtfsutil.ShapeInfoItemComparator;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class ShapeCsvFileCreator {
    private static final Logger logger = LoggerFactory.getLogger(ShapeCsvFileCreator.class);

    public static final String SHAPE_CSV_FILE = "shape.csv";

    public static void main(String[] args) {
        String dataDir = "../../";

        String gtfsDir = dataDir + "gtfs_bus_171212";
        try {
            GtfsStore gtfsStore = new GtfsStore(gtfsDir);

            ArrayList<ShapeInfoItem> items = extractShapeInfoItems(gtfsStore);
            items.sort(new ShapeInfoItemComparator());

            saveShapeInfoItems(items);
        } catch (IOException e) {
            logger.error("Error creating shape.csv file", e);
        }

    }

    public static ArrayList<ShapeInfoItem> extractShapeInfoItems(GtfsStore gtfsStore) {
        ArrayList<ShapeInfoItem> items = new ArrayList<>();

        Map<String, Trip> routeMaxLengthTrip = gtfsStore.getRouteMaxLengthTrip();
        for (String routeId : routeMaxLengthTrip.keySet()) {

            String tripId = routeMaxLengthTrip.get(routeId).getId().getId();
            ArrayList<StopTime> stopTimes = gtfsStore.getTripStopTimes().get(tripId);

            if (routeId.startsWith("9")) {
                logger.info(routeId + "--" + GtfsUtil.getShortRouteId(routeId) + "--" + tripId);
            }

            for (StopTime stopTime : stopTimes) {
                ShapeInfoItem item = new ShapeInfoItem();

                item.setRoute_id(Long.valueOf(GtfsUtil.getShortRouteId(routeId)));
                item.setStop_sequence(stopTime.getStopSequence());
                item.setStop_id(Long.valueOf(stopTime.getStop().getId().getId()));
                item.setStop_name(stopTime.getStop().getName());
                item.setLat(stopTime.getStop().getLat());
                item.setLon(stopTime.getStop().getLon());

                items.add(item);
            }
        }

        return items;
    }

    public static void saveShapeInfoItems(ArrayList<ShapeInfoItem> items) {
        try {
            String[] columns = new String[]{"route_id", "stop_sequence", "stop_name", "lat", "lon", "stop_id"};
            PrintWriter writer = new PrintWriter(new FileWriter(SHAPE_CSV_FILE));

            // write header
            for (int i = 0; i < columns.length - 1; i++)
                writer.print(columns[i] + ",");
            writer.println(columns[columns.length-1]);

            for (ShapeInfoItem item : items) {
                writer.print(item.getRoute_id() + ", ");
                writer.print(item.getStop_sequence() + ", ");
                writer.print(item.getStop_name() + ", ");
                writer.print(item.getLat() + ", ");
                writer.print(item.getLon() + ", ");
                writer.println(item.getStop_id());
            }

            writer.flush();
            writer.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
