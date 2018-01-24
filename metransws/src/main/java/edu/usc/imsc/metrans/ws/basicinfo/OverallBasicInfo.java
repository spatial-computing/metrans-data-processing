package edu.usc.imsc.metrans.ws.basicinfo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OverallBasicInfo {
    int numBusRoutes = 0;
    int numBusStops = 0;
    int numDataPoints = 0;
    String startTime = "";
    String endTime = "";
    double avgDevation = 0.0;
    double reliability = 0.0;

    public OverallBasicInfo() {
    }


}
