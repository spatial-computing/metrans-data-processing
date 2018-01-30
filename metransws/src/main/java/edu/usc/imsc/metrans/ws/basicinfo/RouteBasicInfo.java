package edu.usc.imsc.metrans.ws.basicinfo;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RouteBasicInfo {
    double avgDeviation = 0.0;
    double reliability = 0.0;

    int avgDeviationRank = 0;
    int numTripsPerDay = 0;
    int numDataPointsPerDay = 0;

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

    public int getNumTripsPerDay() {
        return numTripsPerDay;
    }

    public void setNumTripsPerDay(int numTripsPerDay) {
        this.numTripsPerDay = numTripsPerDay;
    }

    public int getNumDataPointsPerDay() {
        return numDataPointsPerDay;
    }

    public void setNumDataPointsPerDay(int numDataPointsPerDay) {
        this.numDataPointsPerDay = numDataPointsPerDay;
    }
}
