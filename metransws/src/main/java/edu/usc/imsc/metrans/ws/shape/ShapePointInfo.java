package edu.usc.imsc.metrans.ws.shape;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ShapePointInfo {
    private String shapeId;
    private double shapePointLat;
    private double shapePointLon;
    private int shapePointSequence;

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public double getShapePointLat() {
        return shapePointLat;
    }

    public void setShapePointLat(double shapePointLat) {
        this.shapePointLat = shapePointLat;
    }

    public double getShapePointLon() {
        return shapePointLon;
    }

    public void setShapePointLon(double shapePointLon) {
        this.shapePointLon = shapePointLon;
    }

    public int getShapePointSequence() {
        return shapePointSequence;
    }

    public void setShapePointSequence(int shapePointSequence) {
        this.shapePointSequence = shapePointSequence;
    }
}
