package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.AnalyseExcelFile;
import fr.unice.i3s.rockflows.experiments.datamining.AttributeType;
import fr.unice.i3s.rockflows.experiments.datamining.DataMiningUtils;
import fr.unice.i3s.rockflows.experiments.datamining.Dataset;
import fr.unice.i3s.rockflows.experiments.datamining.FoldsEnum;
import fr.unice.i3s.rockflows.experiments.datamining.InfoPattern;
import fr.unice.i3s.rockflows.experiments.datamining.Preprocesser;
import fr.unice.i3s.rockflows.experiments.datamining.ResWorkflow;
import fr.unice.i3s.rockflows.experiments.weka.CfsSubsetFilter;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import java.nio.file.StandardCopyOption;

/**
 * @author lupin
 */
public class TestExecutor implements Callable<String> {

    String pathSource = "";
    ResWorkflow workflow;
    FoldsEnum typeFolds;
    String pathOut = "";

    public TestExecutor(String pathSource, String classifierName, int workflowPid, FoldsEnum type,
            String pathOut) throws Exception {
        this.pathSource = pathSource;
        this.typeFolds = type;
        workflow = new ResWorkflow();
        workflow.classifierName = classifierName;
        workflow.preProcId = workflowPid;
        this.pathOut = pathOut;
    }

    public String executeTest() throws Exception {               
        
        switch(this.typeFolds){
            case CV4:{
                String pathFinal = this.pathSource + "Final-Analysis-4Folds.xlsx";
                AnalyseExcelFile exc = new AnalyseExcelFile(pathFinal);
                if(exc.readBest(this.workflow)){
                    return this.pathSource;
                }
                break;
            }
            case CV10:{
                String pathFinal = this.pathSource + "Final-Analysis-10Folds.xlsx";
                AnalyseExcelFile exc = new AnalyseExcelFile(pathFinal);
                if(exc.readBest(this.workflow)){
                    return this.pathSource;
                }
                break;
            }
            case Both:{
                String pathFinal4 = this.pathSource + "Final-Analysis-4Folds.xlsx";
                AnalyseExcelFile exc4 = new AnalyseExcelFile(pathFinal4);
                String pathFinal10 = this.pathSource + "Final-Analysis-10Folds.xlsx";
                AnalyseExcelFile exc10 = new AnalyseExcelFile(pathFinal10);
                if(exc4.readBest(this.workflow) && exc10.readBest(workflow)){
                    return this.pathSource;
                }                
                break;
            }            
        }
        return "";     
    }    

    @Override
    public String call() throws Exception {
        try{
            return this.executeTest();
        }
        catch(Exception ex){
            File fff = new File(pathOut + "error");
            Writer www = new FileWriter(fff);
            www.append(" :BEGIN, ");
            ex.printStackTrace(new PrintWriter(www));
            www.append(",:END ");
            www.append(ex.getMessage());
            www.flush();
            www.close();
            return "";
        }
    }       

}
