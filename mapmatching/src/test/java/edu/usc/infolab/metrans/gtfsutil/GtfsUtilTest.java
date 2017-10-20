package edu.usc.infolab.metrans.gtfsutil;

import com.vividsolutions.jts.geom.LineString;
import org.junit.Test;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.ShapePoint;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class GtfsUtilTest {
    private static final String INPUT_DIR = "data/gtfs_bus_170718";
    private static final String SHAPE_ID = "21054_JUN17";

    @Test
    public void readGtfsFromDir() throws Exception {
        GtfsDaoImpl store = GtfsUtil.readGtfsFromDir(INPUT_DIR);


        ArrayList<ShapePoint> shapePoints = new ArrayList<ShapePoint>(store.getAllShapePoints());

        assertEquals((long) shapePoints.get(0).getId(), 1);
        assertEquals(shapePoints.get(0).getShapeId().getId(), SHAPE_ID);
        assertFalse(store.isPackShapePoints());
    }

    @Test
    public void getLineStrings() throws Exception {
        GtfsDaoImpl store = GtfsUtil.readGtfsFromDir(INPUT_DIR);

        Map<String, LineString> shapeLineStrings = GtfsUtil.getLineStrings(store);
        assertEquals((long)shapeLineStrings.get(SHAPE_ID).getNumPoints(), 857);
    }

    @Test
    public void getShapePointsMap() throws Exception {
        GtfsDaoImpl store = GtfsUtil.readGtfsFromDir(INPUT_DIR);

        Map<String, ArrayList<ShapePoint> > shapePointsMap = GtfsUtil.getShapePointsMap(store);
        assertEquals((long)shapePointsMap.get(SHAPE_ID).size(), 857);
    }

}