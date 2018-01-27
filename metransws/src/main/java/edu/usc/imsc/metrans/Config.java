package edu.usc.imsc.metrans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    public static final String CONFIG_FILE = "/opt/glassfish5/metransws-data/config.properties";

    public static final String KEY_DATA_DIR = "data.dir";
    public static final String KEY_DATA_SHAPE_FILE = "data.shapefile";

    public static String dataDir = "";

    public static String dataShapeFile = "";


    /**
     * Load configuration from files
     */
    public static void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILE));

            if (properties.containsKey(KEY_DATA_DIR)) {
                dataDir = properties.getProperty(KEY_DATA_DIR);
            }
            if (properties.containsKey(KEY_DATA_SHAPE_FILE)) {
                String fileName = properties.getProperty(KEY_DATA_SHAPE_FILE);
                dataShapeFile = Paths.get(dataDir, fileName).toAbsolutePath().toString();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
