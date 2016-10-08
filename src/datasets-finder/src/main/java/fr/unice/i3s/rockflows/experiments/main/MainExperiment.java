package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.datamining.FoldsEnum;
import java.io.File;
import java.io.PrintWriter;
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

    public static void main(String[] args) throws Exception {

        String pathSource = "";
        String pathOut = "";
        String workflowName = "";
        int workflowPid = -1;
        FoldsEnum type = FoldsEnum.Both;
        
        int numParameters = args.length;
        for (int iii = 0; iii < numParameters; iii++) {
            switch (args[iii]) {
                case "-src": {
                    pathSource = args[++iii];
                    break;
                }
                case "-out": {
                    pathOut = args[++iii];
                    break;
                }        
                case "-wname": {
                    workflowName = args[++iii];
                    break;
                }
                case "-wpid": {
                    workflowPid = Integer.parseInt(args[++iii]);
                    break;
                } 
                case "-cv4": {
                    type = FoldsEnum.CV4;
                    break;
                }                 
                case "-cv10": {
                    type = FoldsEnum.CV10;
                    break;
                }                        
                case "-cvBoth": {
                    type = FoldsEnum.Both;
                    break;
                }                                 
            }
        }                
        
        //organize dataset into patterns
        List<String> results = findDatasetBestRank(pathSource, workflowName, workflowPid,
                type, pathOut);
        
        //write output file
        writeDatasets(results, pathOut);
    }
    
    public static void writeDatasets(List<String> results, String pathOut) throws Exception{
        
        int count = 0;
        PrintWriter writer = new PrintWriter(pathOut, "UTF-8");
        for(String res:results){
            if(!res.isEmpty()){
                writer.println(res);
                count++;
            }                
        }
        writer.println();
        writer.println("Count = " + count);
        writer.close();        
    }
    
    public static List<String> findDatasetBestRank(String pathSource, String workflowName, 
            int workflowPid, FoldsEnum type, String pathOut) 
            throws Exception{
        
        //input list datasets
        List<String> datasets = getListDirectories(pathSource);

        int numFiles = datasets.size();
        
        List<String> results = new ArrayList<>();
        for (int iii = 0; iii < numFiles; iii++) {
            String dsName = datasets.get(iii);
            String currentSource = pathSource + dsName + "/";
            TestExecutor test = new TestExecutor(currentSource, workflowName, workflowPid,
                type, pathOut);
            results.add(test.call());
        }
        return results;
    }          
    
    public static List<String> getListFiles(String basePath) {

        List<String> fileNames = new ArrayList<>();
        File folder = new File(basePath);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileNames.add(listOfFiles[i].getName());
            }
        }
        return fileNames;
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
