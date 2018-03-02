package edu.usc.imsc.metrans.busdata;

import org.junit.Test;

import static org.junit.Assert.*;

public class BusDataIOTest {
    @Test
    public void readBusGpsRecordsFromFile() throws Exception {
    }

    @Test
    public void convertCsvBusLineToRecord() throws Exception {
        String line = "2016-08-03 07:29:37,3859,10,400,10,010 W HOLLYWOOD-DTWN LA -AVALON STA VIA,2,33.923682,-118.265266,2016-08-03 07:28:00,1,451,AVAL AVALON     I105 STA.,452";
        BusGpsRecord record = BusDataIO.convertCsvBusLineToRecord(line);
        assertEquals(record.getBusId(), 3859);
        assertEquals(record.getLat(), 33.923682, 1e-9);
    }

}