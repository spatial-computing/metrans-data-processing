package edu.usc.infolab.metrans.demo;

import com.vividsolutions.jts.geom.LineString;
import edu.usc.infolab.metrans.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.ShapePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;



public class GtfsReaderExampleMain {

    private static final Logger logger = LoggerFactory.getLogger(GtfsReaderExampleMain.class);

    public static void main(String[] args) throws IOException {

//        if (args.length != 1) {
//            System.err.println("usage: gtfs_feed_path");
//            System.exit(-1);
//        }
        logger.info("Start reading gtfs data");
        GtfsDaoImpl store = GtfsUtil.readGtfsFromDir("data/gtfs_bus_170718");

        // Access entities through the store
//        for (Route route : store.getAllRoutes()) {
//            System.out.println("route: " + route.getShortName());
//        }

        ArrayList<ShapePoint> shapePoints = new ArrayList<ShapePoint>(store.getAllShapePoints());
        logger.info(shapePoints.get(0).toString());
        logger.info(shapePoints.get(0).getId().toString());
        logger.info(shapePoints.get(0).getShapeId().getId());
        logger.info(String.valueOf(store.isPackShapePoints()));



        Map<String, LineString> shapeLineStrings = GtfsUtil.getLineStrings(store);
        logger.info(String.valueOf(shapeLineStrings.get("21054_JUN17").getNumPoints()));
        logger.info(shapeLineStrings.get("21054_JUN17").toString());
    }
}