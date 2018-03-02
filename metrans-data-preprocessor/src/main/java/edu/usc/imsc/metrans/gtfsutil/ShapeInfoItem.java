package edu.usc.imsc.metrans.gtfsutil;

public class ShapeInfoItem {
    private long route_id;
    private long stop_sequence;
    private long stop_id;
    private String stop_name;
    private double lat;
    private double lon;

    public long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(long route_id) {
        this.route_id = route_id;
    }

    public long getStop_sequence() {
        return stop_sequence;
    }

    public void setStop_sequence(long stop_sequence) {
        this.stop_sequence = stop_sequence;
    }

    public long getStop_id() {
        return stop_id;
    }

    public void setStop_id(long stop_id) {
        this.stop_id = stop_id;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
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

    @Override
    public String toString() {
        return "ShapeInfoItem{" +
                "route_id=" + route_id +
                ", stop_sequence=" + stop_sequence +
                ", stop_id=" + stop_id +
                ", stop_name='" + stop_name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
