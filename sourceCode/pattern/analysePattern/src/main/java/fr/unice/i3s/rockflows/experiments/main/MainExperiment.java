package fr.unice.i3s.rockflows.experiments.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Luca
 */
public class MainExperiment {

    public static void main(String[] args) throws Exception {

        String pathExcelFolder = "";
        int nthread = 2; //default;
        boolean status = false;

        int numParameters = args.length;
        for (int iii = 0; iii < numParameters; iii++) {
            switch (args[iii]) {
                case "-pef": {
                    pathExcelFolder = args[++iii];
                    break;
                }
                case "-nthread": {
                    nthread = Integer.parseInt(args[++iii]);
                    break;
                }
                case "-status": {
                    status = true;
                    break;
                }
            }
        }

        //input list datasets
        List<String> datasets = inputDatasets(pathExcelFolder);

        int numFiles = datasets.size();
        ExecutorService exec = Executors.newFixedThreadPool(nthread);
        List<ResTest> results = new ArrayList<>();

        for (int iii = 0; iii < numFiles; iii++) {
            String dsName = datasets.get(iii);
            String currentDataset = pathExcelFolder + dsName + "/";
            TestExecutor test = new TestExecutor(currentDataset,
                    datasets.get(iii), status);
            results.add(new ResTest(exec.submit(test), dsName));
        }
        exec.shutdown();
        exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        //wait until all tasks have finished the tests
        for (ResTest rt : results) {
            Boolean output = rt.future.get();
            System.out.println("Dataset " + rt.datasetName + " Completed Successfully: " + output.toString());
        }
    }

    public static List<String> inputDatasets(String pathDatasets) {

        List<String> datasets = getListDirectories(pathDatasets);
        return datasets;
    }

    public static List<String> getListDirectories(String basePath) {

        List<String> fileNames = new ArrayList<>();
        File folder = new File(basePath);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                fileNames.add(listOfFiles[i].getName());
            }
        }
        return fileNames;
    }
}
