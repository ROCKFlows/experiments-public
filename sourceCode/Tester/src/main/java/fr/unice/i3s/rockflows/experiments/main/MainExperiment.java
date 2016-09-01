package fr.unice.i3s.rockflows.experiments.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

        String pathFolder = "";
        int nthread = 2; //default;
        boolean parallel = false;

        int numParameters = args.length;
        for (int iii = 0; iii < numParameters; iii++) {
            switch (args[iii]) {
                case "-pef": {
                    pathFolder = args[++iii];
                    break;
                }
                case "-nthread": {
                    nthread = Integer.parseInt(args[++iii]);
                    parallel = true;
                    break;
                }                
            }
        }

        //input list datasets
        List<String> datasets = inputDatasets(pathFolder);

        int numFiles = datasets.size();
        ExecutorService exec = Executors.newFixedThreadPool(nthread);
        
        for (int iii = 0; iii < numFiles; iii++) {
            String dsName = datasets.get(iii);
            String currentDataset = pathFolder + dsName;
            String classIndexPath = currentDataset + "/class";
            int classIndex = -1; //if -1, the class index is the last attribute of the dataset
            //check if exists file
            File classIndexFile = new File(classIndexPath);
            if(classIndexFile.exists()){
                classIndex = getClassIndex(classIndexPath);
            }
            
            TestExecutor test = new TestExecutor(classIndex, currentDataset, parallel);
            exec.submit(test);
        }
        exec.shutdown();
        exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);        
        
    }
    
    public static int getClassIndex(String path) throws Exception{
        InputStream fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        return Integer.parseInt(line);
    }
    
    public static List<String> getFileConfigNames(String pathFolder) {

        List<String> fileNames = new ArrayList<>();
        File folder = new File(pathFolder);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileNames.add(listOfFiles[i].getName());
            }
        }
        return fileNames;
    }

    public static List<String> inputDatasets(String pathDatasets) {

        List<String> datasets = getListDirectories(pathDatasets);
        return datasets;
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
