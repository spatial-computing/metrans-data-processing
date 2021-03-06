package edu.usc.imsc.metrans;

import edu.usc.imsc.metrans.demo.HelloWorld;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;
import edu.usc.imsc.metrans.ws.GeneralInfoWS;
import edu.usc.imsc.metrans.ws.TripsOfStopsWs;
import edu.usc.imsc.metrans.ws.basicinfo.BasicInfoWs;
import edu.usc.imsc.metrans.ws.list.ListingWs;
import edu.usc.imsc.metrans.ws.shape.ShapeWs;
import edu.usc.imsc.metrans.ws.stats.StatsBusBunchingWs;
import edu.usc.imsc.metrans.ws.stats.StatsDeviationWs;
import edu.usc.imsc.metrans.ws.stats.StatsWs;
import edu.usc.imsc.metrans.ws.storage.DataCache;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

//Defines the base URI for all resource URIs.
@ApplicationPath("/")
//The java class declares root resource and provider classes
public class MyApplication extends Application {
    //The method returns a non-empty collection with classes, that must be included in the published JAX-RS application
    @Override
    public Set<Class<?>> getClasses() {
        Config.load();
        GtfsStoreProvider.getGtfsStore();
        DataCache.prepareConstantKeyCache();

        HashSet h = new HashSet<Class<?>>();
        h.add(HelloWorld.class);
        h.add(GeneralInfoWS.class);
        h.add(ShapeWs.class);
        h.add(BasicInfoWs.class);
        h.add(TripsOfStopsWs.class);
        h.add(StatsDeviationWs.class);
        h.add(StatsBusBunchingWs.class);
        h.add(StatsWs.class);
        h.add(ListingWs.class);
        h.add(CORSResponseFilter.class);
        return h;
    }
}