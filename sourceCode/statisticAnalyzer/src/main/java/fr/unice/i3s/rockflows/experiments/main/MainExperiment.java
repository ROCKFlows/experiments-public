package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.AvgExcFile;
import fr.unice.i3s.rockflows.experiments.automatictest.BestAvgExcFile;
import fr.unice.i3s.rockflows.experiments.datamining.ResultsAnalyzer;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import fr.unice.i3s.rockflows.experiments.significance.Statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Luca
 */
public class MainExperiment {

    public static int numDatasets = 0;

    public static void main(String[] args) throws Exception {

        String pathExcelFolder = "";
        String pathOutput = "";
        int nthread = 2; //default;

        int numParameters = args.length;
        for (int iii = 0; iii < numParameters; iii++) {
            switch (args[iii]) {
                case "-pef": {
                    pathExcelFolder = args[++iii];
                    break;
                }
                case "-out": {
                    pathOutput = args[++iii];
                    break;
                }
                case "-nthread": {
                    nthread = Integer.parseInt(args[++iii]);
                    break;
                }
            }
        }

        //input list datasets
        List<String> datasets = getListDirectories(pathExcelFolder);

        computeTimeRam(datasets, nthread, pathExcelFolder, pathOutput);
        //preProcImpact(datasets, nthread, pathExcelFolder, pathOutput);
        //missingVImpact(datasets, nthread, pathExcelFolder, pathOutput);
        //attSelImpact(datasets, nthread, pathExcelFolder, pathOutput);

    }

    public static void computeTimeRam(List<String> datasets, int nthread, String pathExcelFolder,
                                      String pathOutput) throws Exception {

        //int[] noSel = new int[]{0,1,2,3,11};                
        //int[] noSel = new int[]{6,7,8,12};
        int[] noSel = new int[] {9, 10};

        //read 999 results 4 folds
        List<TestResult> fin1 = computeFile(datasets, nthread, pathExcelFolder, true, noSel);
        //write file 999
        BestAvgExcFile exc1 = new BestAvgExcFile(pathOutput);
        exc1.writeTimeSorted(pathOutput + "Time4Folds.xlsx", fin1, numDatasets);

        //read 999 results 10 folds
        List<TestResult> fin2 = computeFile(datasets, nthread, pathExcelFolder, false, noSel);
        //write file 999
        BestAvgExcFile exc2 = new BestAvgExcFile(pathOutput);
        exc2.writeTimeSorted(pathOutput + "Time10Folds.xlsx", fin2, numDatasets);

        BestAvgExcFile exc3 = new BestAvgExcFile(pathOutput);
        exc3.writeRamSorted(pathOutput + "RAM4Folds.xlsx", fin1, numDatasets);

        //write file 999
        BestAvgExcFile exc4 = new BestAvgExcFile(pathOutput);
        exc4.writeRamSorted(pathOutput + "RAM10Folds.xlsx", fin2, numDatasets);

    }

    public static void attSelImpact(List<String> datasets, int nthread, String pathExcelFolder,
                                    String pathOutput) throws Exception {

        int[] noSel = new int[] {0, 1, 2, 3, 4, 5, 11};
        int[] sel = new int[] {6, 7, 8, 9, 10, 12};

        //read 999 results 4 folds
        List<TestResult> fin1 = computeFile(datasets, nthread, pathExcelFolder, true, noSel);
        //write file 999
        BestAvgExcFile exc1 = new BestAvgExcFile(pathOutput);
        exc1.writeAvgSorted(pathOutput + "NoSel4Folds.xlsx", fin1, numDatasets);

        //read 999 results 10 folds
        List<TestResult> fin2 = computeFile(datasets, nthread, pathExcelFolder, false, noSel);
        //write file 999
        BestAvgExcFile exc2 = new BestAvgExcFile(pathOutput);
        exc2.writeAvgSorted(pathOutput + "NoSel10Folds.xlsx", fin2, numDatasets);

        //read best results 4 folds
        List<TestResult> fin3 = computeFile(datasets, nthread, pathExcelFolder, true, sel);
        //write file
        BestAvgExcFile exc3 = new BestAvgExcFile(pathOutput);
        exc3.writeAvgSorted(pathOutput + "Sel4Folds.xlsx", fin3, numDatasets);

        //read best results 10 folds
        List<TestResult> fin4 = computeFile(datasets, nthread, pathExcelFolder, false, sel);
        //write file
        BestAvgExcFile exc4 = new BestAvgExcFile(pathOutput);
        exc4.writeAvgSorted(pathOutput + "Sel10Folds.xlsx", fin4, numDatasets);
    }

    public static void missingVImpact(List<String> datasets, int nthread, String pathExcelFolder,
                                      String pathOutput) throws Exception {

        int[] mvA = new int[] {0};
        //pids = new int[]{2,3,5,8,10};
        int[] noMV = new int[] {2};

        //read 999 results 4 folds
        List<TestResult> fin1 = computeFile(datasets, nthread, pathExcelFolder, true, mvA);
        //write file 999
        BestAvgExcFile exc1 = new BestAvgExcFile(pathOutput);
        exc1.writeAvgSorted(pathOutput + "MVAvg4Folds.xlsx", fin1, numDatasets);

        //read 999 results 10 folds
        List<TestResult> fin2 = computeFile(datasets, nthread, pathExcelFolder, false, mvA);
        //write file 999
        BestAvgExcFile exc2 = new BestAvgExcFile(pathOutput);
        exc2.writeAvgSorted(pathOutput + "MVAvg10Folds.xlsx", fin2, numDatasets);

        //read best results 4 folds
        List<TestResult> fin3 = computeFile(datasets, nthread, pathExcelFolder, true, noMV);
        //write file
        BestAvgExcFile exc3 = new BestAvgExcFile(pathOutput);
        exc3.writeAvgSorted(pathOutput + "ReplacedAvg4Folds.xlsx", fin3, numDatasets);

        //read best results 10 folds
        List<TestResult> fin4 = computeFile(datasets, nthread, pathExcelFolder, false, noMV);
        //write file
        BestAvgExcFile exc4 = new BestAvgExcFile(pathOutput);
        exc4.writeAvgSorted(pathOutput + "ReplacedAvg10Folds.xlsx", fin4, numDatasets);
    }

    public static void preProcImpact(List<String> datasets, int nthread, String pathExcelFolder,
                                     String pathOutput) throws Exception {

        int[] avg11 = new int[] {11};
        int[] avgBest = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        //read 999 results 4 folds
        List<TestResult> fin1 = computeFile(datasets, nthread, pathExcelFolder, true, avg11);
        //write file 999
        AvgExcFile exc1 = new AvgExcFile(pathOutput);
        exc1.writeFinalExcelFile(pathOutput + "999Avg4Folds.xlsx", fin1);

        //read 999 results 10 folds
        List<TestResult> fin2 = computeFile(datasets, nthread, pathExcelFolder, false, avg11);
        //write file 999
        AvgExcFile exc2 = new AvgExcFile(pathOutput);
        exc2.writeFinalExcelFile(pathOutput + "999Avg10Folds.xlsx", fin2);

        //read best results 4 folds
        List<TestResult> fin3 = computeFile(datasets, nthread, pathExcelFolder, true, avgBest);
        //write file
        BestAvgExcFile exc3 = new BestAvgExcFile(pathOutput);
        exc3.writeAvgSorted(pathOutput + "BestAvg4Folds.xlsx", fin3, numDatasets);

        //read best results 10 folds
        List<TestResult> fin4 = computeFile(datasets, nthread, pathExcelFolder, false, avgBest);
        //write file
        BestAvgExcFile exc4 = new BestAvgExcFile(pathOutput);
        exc4.writeAvgSorted(pathOutput + "BestAvg10Folds.xlsx", fin4, numDatasets);
    }

    public static List<TestResult> computeFile(List<String> datasets, int nthread, String pathExcelFolder,
                                               boolean folds4, int[] pids) throws Exception {

        int numFiles = datasets.size();
        List<List<TestResult>> globalResults = new ArrayList<>();
        int numAlgo = -1;
        for (int iii = 0; iii < numFiles; iii++) {
            String dsName = datasets.get(iii);
            String currentDataset = pathExcelFolder + dsName + "/";
            MVExecutor test = new MVExecutor(currentDataset,
                    datasets.get(iii), pids, folds4);
            List<TestResult> res = test.call();
            if (numAlgo == -1) {
                numAlgo = res.size();
            }
            globalResults.add(res);
        }

        //compute avg for each classifier
        numDatasets = globalResults.size();
        List<TestResult> resFinal = new ArrayList<>();
        for (int id = 0; id < numAlgo; id++) {
            TestResult trBest = new TestResult();
            trBest.accuracyAvg = 0;
            trBest.totalTimeAvg = 0;
            trBest.ramAvg = 0;
            trBest.accuracies = new double[numDatasets];
            trBest.times = new double[numDatasets];
            trBest.rams = new double[numDatasets];
            trBest.algoId = id;
            trBest.algoName = globalResults.get(0).get(id).algoName;
            trBest.compatible = globalResults.get(0).get(id).compatible;
            List<Double> avgs = new ArrayList<>();
            for (int k = 0; k < numDatasets; k++) {
                TestResult tmp = globalResults.get(k).get(id);
                trBest.accuracyAvg += tmp.accuracyAvg;
                trBest.ramAvg += tmp.ramAvg;
                trBest.totalTimeAvg += tmp.trainTimeAvg + tmp.testTimeAvg;
                trBest.accuracies[k] = tmp.accuracyAvg;
                trBest.rams[k] = tmp.ramAvg;
                trBest.times[k] = tmp.trainTimeAvg + tmp.testTimeAvg;
                avgs.add(tmp.accuracyAvg);
                //get best pre-processed used
                int idPrep = tmp.preProcId;
                if (idPrep == 999) {
                    idPrep = 11;
                }
                trBest.contPreProc[idPrep]++;
            }
            trBest.accuracyAvg /= numDatasets;
            trBest.totalTimeAvg /= numDatasets;
            trBest.ramAvg /= numDatasets;
            trBest.accuracyStDev = Statistics.getStdDev(avgs);
            resFinal.add(trBest);
        }

        ResultsAnalyzer.setRankAccuracy(resFinal, 0.05);
        return resFinal;
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
