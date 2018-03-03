package edu.usc.imsc.metrans.ws.list;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TripMetadataInfo {
    private String id;
    private String service;
    private String arrivalTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "TripMetadataInfo{" +
                "id='" + id + '\'' +
                ", service='" + service + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                '}';
    }
}