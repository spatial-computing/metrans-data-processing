package edu.usc.infolab.metrans.demo;

import edu.usc.infolab.metrans.busdata.BusDataIO;
import edu.usc.infolab.metrans.busdata.BusDataUtil;
import edu.usc.infolab.metrans.busdata.BusGpsRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class BusDataIOMain {
    private static final Logger logger = LoggerFactory.getLogger(BusDataIOMain.class);

    public static void main(String[] args) {
        ArrayList<BusGpsRecord> records10 = BusDataIO.readBusGpsRecordsFromFile("data/busdata/Bus10.csv");
        System.out.println(records10.size());
        System.out.println(records10.get(0).toString());

        Map<Integer, ArrayList<BusGpsRecord>> busIdRecordsMaps = BusDataUtil.getBusIdRecordsMap(records10);
        System.out.println(busIdRecordsMaps.size());

    }
}
