package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.AutomaticTest;
import fr.unice.i3s.rockflows.experiments.automatictest.IntermediateExcelFile;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author lupin
 */
public class MVExecutor implements Callable<List<TestResult>> {

    String pathFolderExcel = "";
    int[] pids;
    boolean folds4 = true;

    public MVExecutor(String pathFolderExcel, String nameDataset, int[] pids,
                      boolean folds4) throws Exception {
        this.pathFolderExcel = pathFolderExcel;
        this.pids = pids;
        this.folds4 = folds4;
    }

    public List<TestResult> executeTest() throws Exception {

        List<String> names = getListFiles(pathFolderExcel, "xlsx", pids);
        List<List<TestResult>> datasetResults = new ArrayList<>();
        int size = names.size();
        for (int i = 0; i < size; i++) {
            //for current exc file
            String name = names.get(i);
            String pathTestExc = pathFolderExcel + name;
            int preProcId = this.getIdFromPath(pathTestExc);
            List<TestResult> currentResults;
            if (this.folds4) {
                currentResults = readIntermediateResults4Folds(
                        pathFolderExcel + name, preProcId);
                datasetResults.add(currentResults);
            } else {
                currentResults = readIntermediateResults10Folds(
                        pathFolderExcel + name, preProcId);
                datasetResults.add(currentResults);
            }
        }
        List<TestResult> output = AutomaticTest.getBestResults(datasetResults);
        return output;
    }

    @Override
    public List<TestResult> call() throws Exception {
        System.out.println("Running " + this.pathFolderExcel);
        try {
            return this.executeTest();
        } catch (Exception ex) {
            File fff = new File(pathFolderExcel + "error");
            Writer www = new FileWriter(fff);
            www.append(" :BEGIN, ");
            ex.printStackTrace(new PrintWriter(www));
            www.append(",:END ");
            www.append(ex.getMessage());
            www.flush();
            www.close();
            return null;
        }
    }

    public List<TestResult> readIntermediateResults4Folds(String name, int preProcId) throws Exception {

        //read file results
        IntermediateExcelFile exc = new IntermediateExcelFile(name);
        List<TestResult> res = AutomaticTest.readValues(exc, preProcId, exc.sheet);
        return res;
    }

    public List<TestResult> readIntermediateResults10Folds(String name, int preProcId) throws Exception {

        //read file results
        IntermediateExcelFile exc = new IntermediateExcelFile(name);
        List<TestResult> res = AutomaticTest.readValues(exc, preProcId, exc.sheet10);
        return res;
    }

    public List<String> getListFiles(String basePath, String extension, int[] pids) {

        List<String> fileNames = new ArrayList<>();
        File folder = new File(basePath);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String filename = listOfFiles[i].getName();
                //check if it has the extension of Excel
                String ext = filename.substring(filename.lastIndexOf(".") + 1);
                if (ext.equals(extension)) {
                    //check if it's not final
                    if (!filename.contains("Final.xlsx")
                            && !filename.contains("-Analysis")) {
                        //check path
                        int id = getIdFromPath(listOfFiles[i].getPath());
                        if (isContained(pids, id)) {
                            fileNames.add(filename);
                        }
                    }
                }
            }
        }
        return fileNames;
    }

    public boolean isUnique(String path) {
        //check if unique or not
        boolean unique = false;
        File check = new File(path + "Test-0.arff");
        if (check.exists()) {
            unique = true;
        }
        return unique;
    }

    public String getNameFromPath(String path) {

        return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
    }

    public int getIdFromPath(String path) {

        String tmp = path.substring(path.lastIndexOf("-") + 1);
        return Integer.parseInt(tmp.substring(0, tmp.lastIndexOf(".")));
    }

    public boolean isContained(int[] array, int value) {

        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }
}
