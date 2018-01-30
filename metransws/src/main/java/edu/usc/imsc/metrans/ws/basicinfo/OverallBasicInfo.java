package edu.usc.imsc.metrans.ws.basicinfo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OverallBasicInfo {
    double avgDeviation = 0.0;
    double reliability = 0.0;

    int numBusRoutes = 0;
    int numBusStops = 0;
    int numDataPoints = 0;
    long startTime = 0;
    long endTime = 0;


    public OverallBasicInfo() {
    }

    public int getNumBusRoutes() {
        return numBusRoutes;
    }

    public void setNumBusRoutes(int numBusRoutes) {
        this.numBusRoutes = numBusRoutes;
    }

    public int getNumBusStops() {
        return numBusStops;
    }

    public void setNumBusStops(int numBusStops) {
        this.numBusStops = numBusStops;
    }

    public int getNumDataPoints() {
        return numDataPoints;
    }

    public void setNumDataPoints(int numDataPoints) {
        this.numDataPoints = numDataPoints;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

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
