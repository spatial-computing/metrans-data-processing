package edu.usc.imsc.metrans.timematching;

import org.onebusaway.gtfs.model.Stop;

import java.time.ZonedDateTime;

public class BusDelay {

    private Stop stop;
    private Integer scheduleTime;
    private Double delayTime;
    private Integer countDelayTime;
    private Integer noShow;

    public BusDelay(Stop stop, Integer scheduleTime, Double delayTime, Integer countDelayTime, Integer noShow){
        this.stop = stop;
        this.scheduleTime = scheduleTime;
        this.delayTime = delayTime;
        this.countDelayTime = countDelayTime;
        this.noShow = noShow;
    }

    public void resetDelayTime(Double time) {
        this.delayTime += time;
    }

    public void resetNoSHow(Integer noshow) {this.noShow += noshow; }

    public void resetCountDelayTime(Integer countdelaytime) {
        this.countDelayTime += countdelaytime;
    }

    public Stop getStopId() {return stop;}
    public Integer getScheduleTime() {return scheduleTime;}
    public Double getDelayTime() {return delayTime;}
    public Integer getCountDelayTime() {return countDelayTime;}
    public Integer getNoShow() {return noShow;}


}
