package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.BestAvgExcFile;
import fr.unice.i3s.rockflows.experiments.datamining.ResultsAnalyzer;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fr.unice.i3s.rockflows.experiments.significance.Statistics;


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
        int[] index1 = new int[2];
        int[] index2 = new int[2];
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
                case "-id1":{
                    String id1 = args[++iii];
                    String[] indices = id1.split(",");
                    index1 = new int[indices.length];
                    for(int i = 0; i < indices.length; i++){
                        index1[i] = Integer.parseInt(indices[i]);
                    }
                    break;                    
                }
                case "-id2":{
                    String id1 = args[++iii];
                    String[] indices = id1.split(",");
                    index2 = new int[indices.length];
                    for(int i = 0; i < indices.length; i++){
                        index2[i] = Integer.parseInt(indices[i]);
                    }
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

        computePerformances(datasets, nthread, pathExcelFolder, pathOutput, index1, index2);
        //preProcImpact(datasets, nthread, pathExcelFolder, pathOutput);
        //missingVImpact(datasets, nthread, pathExcelFolder, pathOutput);
        //attSelImpact(datasets, nthread, pathExcelFolder, pathOutput);
        
    }
    
    public static void computePerformances(List<String> datasets, int nthread, String pathExcelFolder,
            String pathOutput, int[] indices1, int[] indices2) throws Exception{
                
        //read 999 results 4 folds
        List<TestResult> fin1 = computeFile(datasets, nthread, pathExcelFolder, true, indices1);
        //write file 999
        BestAvgExcFile exc1 = new BestAvgExcFile(pathOutput);  
        exc1.writeAvgSorted(pathOutput + "Accuracy1_4Folds.xlsx", fin1, numDatasets);
        exc1.writeTimeSorted(pathOutput + "Time1_4Folds.xlsx", fin1, numDatasets);
        exc1.writeRamSorted(pathOutput + "Memory1_4Folds.xlsx", fin1, numDatasets);

        //read 999 results 10 folds
        List<TestResult> fin2 = computeFile(datasets, nthread, pathExcelFolder, false, indices1);
        //write file 999
        BestAvgExcFile exc2 = new BestAvgExcFile(pathOutput);  
        exc2.writeAvgSorted(pathOutput + "Accuracy1_10Folds.xlsx", fin2, numDatasets);
        exc2.writeTimeSorted(pathOutput + "Time1_10Folds.xlsx", fin2, numDatasets);
        exc2.writeRamSorted(pathOutput + "Memory1_10Folds.xlsx", fin2, numDatasets);        
        
        //read best results 4 folds
        List<TestResult> fin3 = computeFile(datasets, nthread, pathExcelFolder, true, indices2);
        //write file
        BestAvgExcFile exc3 = new BestAvgExcFile(pathOutput);  
        exc3.writeAvgSorted(pathOutput + "Accuracy2_4Folds.xlsx", fin3, numDatasets);
        exc3.writeTimeSorted(pathOutput + "Time2_4Folds.xlsx", fin3, numDatasets);
        exc3.writeRamSorted(pathOutput + "Memory2_4Folds.xlsx", fin3, numDatasets);
        
        //read best results 10 folds
        List<TestResult> fin4 = computeFile(datasets, nthread, pathExcelFolder, false, indices2);
        //write file
        BestAvgExcFile exc4 = new BestAvgExcFile(pathOutput);  
        exc4.writeAvgSorted(pathOutput + "Accuracy2_10Folds.xlsx", fin4, numDatasets);
        exc4.writeTimeSorted(pathOutput + "Time2_10Folds.xlsx", fin4, numDatasets);
        exc4.writeRamSorted(pathOutput + "Memory2_10Folds.xlsx", fin4, numDatasets);        
    }    
        
    public static List<TestResult> computeFile(List<String> datasets, int nthread, String pathExcelFolder,
            boolean folds4, int[] pids) throws Exception{
        
        int numFiles = datasets.size();
        List<List<TestResult>> globalResults = new ArrayList<>();
        int numAlgo = -1;
        for (int iii = 0; iii < numFiles; iii++) {
            String dsName = datasets.get(iii);
            String currentDataset = pathExcelFolder + dsName + "/";                        
            MVExecutor test = new MVExecutor(currentDataset,
                    datasets.get(iii), pids, folds4);
            List<TestResult> res = test.call();
            if(numAlgo == -1){
                numAlgo = res.size();
            }
            globalResults.add(res);
        }
        
        //compute avg for each classifier
        numDatasets = globalResults.size();
        List<TestResult> resFinal = new ArrayList<>();
        for(int id = 0; id < numAlgo; id++){
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
            for(int k = 0; k < numDatasets; k++){
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
                if(idPrep == 999){
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
