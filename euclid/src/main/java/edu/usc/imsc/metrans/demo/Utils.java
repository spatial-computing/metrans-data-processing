package edu.usc.imsc.metrans.demo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Utils {
    public static ArrayList<String> getFilesWithExtInFolder(String folderPath, String extention) {
        File f = new File(folderPath);

        FilenameFilter extFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(extention);
            }
        };

        File[] files = f.listFiles(extFilter);

        ArrayList<String> res = new ArrayList<>();

        for (File file : files) {
            if (!file.isDirectory()) {
                res.add(file.getPath());
            }
        }


        return res;
    }
}
