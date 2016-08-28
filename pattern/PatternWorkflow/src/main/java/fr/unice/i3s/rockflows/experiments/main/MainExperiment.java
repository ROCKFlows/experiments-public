package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.datamining.AttributeType;
import fr.unice.i3s.rockflows.experiments.datamining.InfoPattern;
import fr.unice.i3s.rockflows.experiments.datamining.MVType;
import fr.unice.i3s.rockflows.experiments.datamining.ResWorkflow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

        String pathSource = "";
        String pathDest = "";
        String names = "";
        
        int numParameters = args.length;
        for (int iii = 0; iii < numParameters; iii++) {
            switch (args[iii]) {
                case "-src": {
                    pathSource = args[++iii];
                    break;
                }
                case "-dest": {
                    pathDest = args[++iii];
                    break;
                }        
                case "-names": {
                    names = args[++iii];
                    break;
                }                     
            }
        }        
        
        //input patterns
        List<InfoPattern> patterns = getPatterns();
        int numPatterns = patterns.size();
                        
        //create pattern folders
        List<String> dirPatterns = new ArrayList<>();
        for(int k = 0; k < numPatterns; k++){         
            String patternDir = pathDest + patterns.get(k).id + "/";
            File dir = new File(patternDir);
            dir.mkdirs();
            //create read me file
            String readMe = patternDir + "readme";
            writeReadMe(readMe, patterns.get(k));
            dirPatterns.add(patternDir);
        }       
        
        //organize dataset into patterns
        organize(pathSource, pathDest, patterns);
        
        //analyse datasets for each pattern
        analyse(dirPatterns, names);
        
        deleteEmpty(dirPatterns);
        
    }
    
    public static void deleteEmpty(List<String> dirPatterns){
    
        int minDb = 1;
        int numDir = dirPatterns.size();
        for (int i = 0; i < numDir; i++) {
            String dir = dirPatterns.get(i);
            File folder = new File(dir);
            int len = folder.listFiles().length;
            if(len < minDb + 3){ //config pattern + 2 analysis files
                File[] files = folder.listFiles();
                for(int k = 0; k < len; k++){
                    files[k].delete();
                }
                //get database folders
                files = folder.listFiles();
                //for each folder, delete files that it contains
                int size = files.length;
                for(int k = 0; k < size; k++){
                    File[] toDel = files[k].listFiles();
                    int count = toDel.length;
                    for(int p = 0; p < count; p++){
                        toDel[p].delete();
                    }
                    files[k].delete();
                }
                folder.delete();
            }
        }        
    }
    
    public static void analyse(List<String> dirPatterns, String pathNames) throws Exception{
                
        List<String> clNames = readClassifierNames(pathNames);
        int numDir = dirPatterns.size();
        //ExecutorService exec = Executors.newFixedThreadPool(numDir);
        ExecutorService exec = Executors.newFixedThreadPool(4);
        
        for (int i = 0; i < numDir; i++) {
            String dir = dirPatterns.get(i);
            AnalysePatternExecutor test = new AnalysePatternExecutor(dir, clNames);
            exec.submit(test);
        }        
        exec.shutdown();
        exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);        
    }
    
 
    
    public static void organize(String pathSource, String pathDest, List<InfoPattern> patterns) 
            throws Exception{
    
        int numPatterns = patterns.size();
        
        //input list datasets
        List<String> datasets = getListDirectories(pathSource);

        int numFiles = datasets.size();
        //ExecutorService exec = Executors.newFixedThreadPool(numPatterns * numFiles);
        ExecutorService exec = Executors.newFixedThreadPool(4);
        
        for(int k = 0; k < numPatterns; k++){
            for (int iii = 0; iii < numFiles; iii++) {
                String dsName = datasets.get(iii);
                String currentSource = pathSource + dsName + "/";
                String currentDest = pathDest + patterns.get(k).id + "/" + dsName + "/";
                TestExecutor test = new TestExecutor(currentSource, currentDest, 
                    patterns.get(k));
                exec.submit(test);
            }        
        }
        exec.shutdown();
        exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);                 
    }
    
    public static void writeReadMe(String path, InfoPattern pattern)
        throws IOException{
        
        FileWriter readMe = new FileWriter(path);
        PrintWriter writer = new PrintWriter(readMe);
        writer.write(pattern.toString());
        writer.close();
        readMe.close();
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

    public static List<String> readClassifierNames(String path){
        List<String> names = new ArrayList<>();
        //read file of classifiers
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            String line;
            while((line = br.readLine()) != null){
                names.add(line);
            }
        }
        catch(Exception ex){
            System.exit(-1);
        }
        return names;
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
    
    public static List<InfoPattern> getPatterns(){
        
        String base = "pattern";
        int id = 1;
        List<InfoPattern> patterns = new ArrayList<>();

        int[] minAtt = new int[]{11};
        int[] maxAtt = new int[]{89};
        int[] minClass = new int[]{2};
        int[] maxClass = new int[]{-1};        
        int[] minInst = new int[]{0};
        int[] maxInst = new int[]{10000};                
        MVType[] missingValues = new MVType[]{
            MVType.Ignore
        };     
        AttributeType[] types = new AttributeType[]{
            AttributeType.Ignore
            //AttributeType.Mixed,
            //AttributeType.Nominal,
            //AttributeType.NumericOriginal
            //AttributeType.NumericStandardized
        };
        
        for(int att = 0; att < minAtt.length; att++){
            for(int cls = 0; cls < minClass.length; cls++){
                for(int mv = 0; mv < missingValues.length; mv++){
                    for(int type = 0; type < types.length; type++){
                        for(int inst = 0; inst < maxInst.length; inst++){
                            InfoPattern ip1 = new InfoPattern();
                            ip1.minNumAtt = minAtt[att];
                            ip1.maxNumAtt = maxAtt[att];
                            ip1.minNumInst = minInst[inst]; 
                            ip1.maxNumInst = maxInst[inst];
                            ip1.minNumClasses = minClass[cls];
                            ip1.maxNumClasses = maxClass[cls];
                            ip1.withMissingValues = missingValues[mv];
                            ip1.attributeType = types[type];
                            ip1.id = base + id++;
                            patterns.add(ip1);                                          
                        }
                    }
                }
            }
        }
        
        return patterns;
    }
    
}
