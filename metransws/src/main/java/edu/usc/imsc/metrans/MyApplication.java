package edu.usc.imsc.metrans;

import edu.usc.imsc.metrans.demo.HelloWorld;
import edu.usc.imsc.metrans.ws.GeneralInfoWS;
import edu.usc.imsc.metrans.ws.MetransMainWs;
import edu.usc.imsc.metrans.ws.TripsOfStopsWs;
import edu.usc.imsc.metrans.ws.basicinfo.BasicInfoWs;
import edu.usc.imsc.metrans.ws.stats.StatsDeviationWs;

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

        HashSet h = new HashSet<Class<?>>();
        h.add(HelloWorld.class);
        h.add(GeneralInfoWS.class);
        h.add(MetransMainWs.class);
        h.add(BasicInfoWs.class);
        h.add(TripsOfStopsWs.class);
        h.add(StatsDeviationWs.class);
        h.add(CORSResponseFilter.class);
        return h;
    }
}