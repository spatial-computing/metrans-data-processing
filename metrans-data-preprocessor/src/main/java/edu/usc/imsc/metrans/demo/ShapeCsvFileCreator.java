package edu.usc.imsc.metrans.demo;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.gtfsutil.ShapeInfoItem;
import edu.usc.imsc.metrans.gtfsutil.ShapeInfoItemComparator;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
                private final String[] HEADER = new String[]{"route_id", "stop_sequence", "stop_name", "lat", "lon", "stop_id"};

                @Override
                public String[] generateHeader() {
                    return HEADER;
                }
            }

            Writer writer = Files.newBufferedWriter(Paths.get(SHAPE_CSV_FILE));

            ColumnPositionMappingStrategy mappingStrategy =
                    new ColumnPositionMappingStrategy();

            mappingStrategy.setType(ShapeInfoItem.class);
            //Fields in ShapeInfoItem
            String[] columns = new String[]{"route_id", "stop_sequence", "stop_name", "lat", "lon", "stop_id"};
            //Setting the colums for mappingStrategy
            mappingStrategy.setColumnMapping(columns);

            CustomMappingStrategy customMappingStrategy = new CustomMappingStrategy();
            customMappingStrategy.setType(ShapeInfoItem.class);

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
//                    .withMappingStrategy(mappingStrategy)
                    .withMappingStrategy(customMappingStrategy)
                    .build();

            beanToCsv.write(items);
        }  catch (CsvRequiredFieldEmptyException | IOException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }
}
