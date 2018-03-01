package edu.usc.imsc.metrans.ws.basicinfo;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RouteStopBasicInfo {
    double avgDeviation = 0.0;
    double reliability = -1;

    int avgDeviationRank = -1;
    String stopName = "";
    double waitingTimeEstimation = -01;

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

    public int getAvgDeviationRank() {
        return avgDeviationRank;
    }

    public void setAvgDeviationRank(int avgDeviationRank) {
        this.avgDeviationRank = avgDeviationRank;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public double getWaitingTimeEstimation() {
        return waitingTimeEstimation;
    }

    public void setWaitingTimeEstimation(double waitingTimeEstimation) {
        this.waitingTimeEstimation = waitingTimeEstimation;
    }
}
