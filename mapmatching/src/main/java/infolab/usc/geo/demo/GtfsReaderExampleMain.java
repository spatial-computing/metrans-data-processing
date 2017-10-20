package infolab.usc.geo.demo;

import com.vividsolutions.jts.geom.LineString;
import infolab.usc.geo.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.ShapePoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class GtfsReaderExampleMain {

    public static void main(String[] args) throws IOException {

//        if (args.length != 1) {
//            System.err.println("usage: gtfs_feed_path");
//            System.exit(-1);
//        }

        GtfsDaoImpl store = GtfsUtil.readGtfsFromDir("data/gtfs_bus_170718");

        // Access entities through the store
//        for (Route route : store.getAllRoutes()) {
//            System.out.println("route: " + route.getShortName());
//        }

        ArrayList<ShapePoint> shapePoints = new ArrayList<ShapePoint>(store.getAllShapePoints());
        System.out.println(shapePoints.get(0).toString());
        System.out.println(shapePoints.get(0).getId());
        System.out.println(shapePoints.get(0).getShapeId().getId());
        System.out.println(store.isPackShapePoints());



        Map<String, LineString> shapeLineStrings = GtfsUtil.getLineStrings(store);
        System.out.println(shapeLineStrings.get("21054_JUN17").getNumPoints());
        System.out.println(shapeLineStrings.get("21054_JUN17"));
    }
}