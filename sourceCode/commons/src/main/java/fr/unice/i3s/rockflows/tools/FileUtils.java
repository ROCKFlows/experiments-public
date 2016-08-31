package fr.unice.i3s.rockflows.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author camillieri on 22/08/16.
 */
public class FileUtils {

    private FileUtils() {
    }

    public static List<String> getListFiles(String basePath) {

        List<String> fileNames = new ArrayList<>();
        File folder = new File(basePath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return fileNames;
        }
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                fileNames.add(listOfFile.getName());
            }
        }
        return fileNames;
    }

    public static List<String> getListDirectories(String basePath) {

        List<String> fileNames = new ArrayList<>();
        File folder = new File(basePath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return fileNames;
        }
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isDirectory()) {
                fileNames.add(listOfFile.getName());
            }
        }
        return fileNames;
    }

}
