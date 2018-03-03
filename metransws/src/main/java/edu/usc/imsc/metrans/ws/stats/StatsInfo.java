package edu.usc.imsc.metrans.ws.stats;

import java.util.ArrayList;
import java.util.TreeMap;

public class StatsInfo {
    private TreeMap<String, Double> year = new TreeMap<>();
    private ArrayList<Double> month = new ArrayList<>();
    private ArrayList<Double> week = new ArrayList<>();
    private ArrayList<Double> day = new ArrayList<>();

    public TreeMap<String, Double> getYear() {
        return year;
    }

    public void setYear(TreeMap<String, Double> year) {
        this.year = year;
    }

    public ArrayList<Double> getMonth() {
        return month;
    }

    public void setMonth(ArrayList<Double> month) {
        this.month = month;
    }

    public ArrayList<Double> getWeek() {
        return week;
    }

    public void setWeek(ArrayList<Double> week) {
        this.week = week;
    }

    public ArrayList<Double> getDay() {
        return day;
    }

    public void setDay(ArrayList<Double> day) {
        this.day = day;
    }
}
