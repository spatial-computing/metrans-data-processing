package infolab.usc.geo.demo;

import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.IOException;

public class GtfsReaderExampleMain {

    public static void main(String[] args) throws IOException {

//        if (args.length != 1) {
//            System.err.println("usage: gtfs_feed_path");
//            System.exit(-1);
//        }

        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(new File("/home/kiennd/Downloads/Metrans/gtfs_bus_fixed"));

        /**
         * You can register an entity handler that listens for new objects as they
         * are read
         */
//        reader.addEntityHandler(new GtfsEntityHandler());

        /**
         * Or you can use the internal entity store, which has references to all the
         * loaded entities
         */
        GtfsDaoImpl store = new GtfsDaoImpl();
        reader.setEntityStore(store);

        reader.run();

        // Access entities through the store
        for (Route route : store.getAllRoutes()) {
            System.out.println("route: " + route.getShortName());
        }
//        System.out.println(store.getShapePointForId(0).toString());
//        System.out.println(store.isPackShapePoints());
    }

    private static class GtfsEntityHandler implements EntityHandler {

        public void handleEntity(Object bean) {
            if (bean instanceof Stop) {
                Stop stop = (Stop) bean;
                System.out.println("stop: " + stop.getName());
            }
        }
    }
}