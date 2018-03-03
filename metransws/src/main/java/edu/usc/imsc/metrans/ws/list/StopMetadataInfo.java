package edu.usc.imsc.metrans.ws.list;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StopMetadataInfo {
    private long stopId;
    private String stopName;
    private double lat;
    private double lon;

    public long getStopId() {
        return stopId;
    }

    public void setStopId(long stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
