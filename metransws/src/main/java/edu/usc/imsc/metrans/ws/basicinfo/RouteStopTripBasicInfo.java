package edu.usc.imsc.metrans.ws.basicinfo;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RouteStopTripBasicInfo {
    double avgDeviation = 0.0;
    double reliability = 0.0;

    public double getAvgDeviation() {
        return avgDeviation;
    }

    public void setAvgDeviation(double avgDeviation) {
        this.avgDeviation = avgDeviation;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }
}
