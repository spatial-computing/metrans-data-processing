package edu.usc.imsc.metrans.ws.generalinfo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeneralInfo {
    int numBusLines = 0;
    int numBusStops = 0;
    int numDataPoints = 0;

    public GeneralInfo() {
    }

    public GeneralInfo(int numBusLines, int numBusStops, int numDataPoints) {
        this.numBusLines = numBusLines;
        this.numBusStops = numBusStops;
        this.numDataPoints = numDataPoints;
    }

    public int getNumBusLines() {
        return numBusLines;
    }

    public void setNumBusLines(int numBusLines) {
        this.numBusLines = numBusLines;
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
}
