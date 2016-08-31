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
import java.util.stream.Collectors;

/**
 * @author lupin
 */
public class TestExecutor implements Callable<Boolean> {

    String pathFolderExcel = "";
    boolean status = false;
    String currentName = "";

    public TestExecutor(String pathFolderExcel, String nameDataset, boolean status)
            throws Exception {
        this.pathFolderExcel = pathFolderExcel;
        this.status = status;
    }

    public void executeTest() throws Exception {

        double alpha = 0.05; //default               

        List<String> names = getListFiles(pathFolderExcel, "xlsx");
                
        /*
        int size = names.size();
        for(int i = 0; i < size; i++){
            //for current exc file
            String name = names.get(i);
            currentName = name;
            String currentPath4Folds = pathFolderExcel + getNameFromPath(name) + "-Analysis-4Folds.xlsx";
            String path = pathFolderExcel + name;
            List<TestResult> currentResults = readIntermediateResults4Folds(path);
            //remove not compatible results
            List<TestResult> compatible = currentResults.stream().filter((TestResult tr) -> tr.compatible)
                    .collect(Collectors.toList());                         
            //true = 4Folds
            AutomaticTest.computeResults(compatible, alpha, currentPath4Folds, status, true);
            
            String currentPath10Folds = pathFolderExcel + getNameFromPath(name) + "-Analysis-10Folds.xlsx";
            currentResults = readIntermediateResults10Folds(path);
            //remove not compatible results
            compatible = currentResults.stream().filter((TestResult tr) -> tr.compatible)
                    .collect(Collectors.toList());                         
            //false = 10Folds
            AutomaticTest.computeResults(compatible, alpha, currentPath10Folds, status, false);
        }        
        */
        String pathFinal = pathFolderExcel + "Final-Analysis-4Folds.xlsx";
        List<TestResult> globalResults = readAllIntermediateResults4Folds(names);

        //remove not compatible results
        List<TestResult> compatible = globalResults.stream().filter((TestResult tr) -> tr.compatible)
                .collect(Collectors.toList());
        //true = 4Folds
        AutomaticTest.computeResultsFinal(compatible, alpha, pathFinal, status, true);

        //final file only for status
        pathFinal = pathFolderExcel + "Final-Analysis-10Folds.xlsx";
        globalResults = readAllIntermediateResults10Folds(names);

        //remove not compatible results
        compatible = globalResults.stream().filter((TestResult tr) -> tr.compatible)
                .collect(Collectors.toList());
        //true = 10Folds
        AutomaticTest.computeResultsFinal(compatible, alpha, pathFinal, status, false);
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Running " + this.pathFolderExcel);
        try {
            this.executeTest();
        } catch (Exception ex) {
            File fff = new File(pathFolderExcel + "error");
            Writer www = new FileWriter(fff);
            www.append(" :BEGIN, ");
            ex.printStackTrace(new PrintWriter(www));
            www.append(",:END ");
            www.append(ex.getMessage());
            www.flush();
            www.close();
            return false;
        }
        return true;
    }

    public List<TestResult> readIntermediateResults4Folds(String name) throws Exception {

        //read file results
        IntermediateExcelFile exc = new IntermediateExcelFile(name);
        List<TestResult> res;
        res = AutomaticTest.readValues4(exc);
        return res;
    }

    public List<TestResult> readIntermediateResults10Folds(String name) throws Exception {

        //read file results
        IntermediateExcelFile exc = new IntermediateExcelFile(name);
        List<TestResult> res;
        res = AutomaticTest.readValues10(exc);
        return res;
    }

    public List<TestResult> readAllIntermediateResults4Folds(List<String> names) throws Exception {

        List<TestResult> res = new ArrayList<>();
        //for each file, read its results
        for (int i = 0; i < names.size(); i++) {
            String name = pathFolderExcel + names.get(i);
            IntermediateExcelFile exc = new IntermediateExcelFile(name);
            List<TestResult> current;
            current = AutomaticTest.readValues4(exc);
            res.addAll(current);
        }
        return res;
    }

    public List<TestResult> readAllIntermediateResults10Folds(List<String> names) throws Exception {

        List<TestResult> res = new ArrayList<>();
        //for each file, read its results
        for (int i = 0; i < names.size(); i++) {
            String name = pathFolderExcel + names.get(i);
            IntermediateExcelFile exc = new IntermediateExcelFile(name);
            List<TestResult> current;
            current = AutomaticTest.readValues10(exc);
            res.addAll(current);
        }
        return res;
    }

    public List<String> getListFiles(String basePath, String extension) {

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
                        fileNames.add(filename);
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
}
