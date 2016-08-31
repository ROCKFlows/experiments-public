package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.datamining.DataMiningUtils;
import fr.unice.i3s.rockflows.datamining.Dataset;
import fr.unice.i3s.rockflows.experiments.datamining.AttributeType;
import fr.unice.i3s.rockflows.experiments.datamining.InfoPattern;

import weka.core.Attribute;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

/**
 * @author lupin
 */
public class TestExecutor implements Callable<Boolean> {

    private String pathSource = "";
    private String pathDest = "";
    private InfoPattern pattern;

    public TestExecutor(String pathSource, String pathDest, InfoPattern pattern)
            throws Exception {

        this.pathDest = pathDest;
        this.pathSource = pathSource;
        this.pattern = pattern;
    }

    public void executeTest() throws Exception {

        String name = "";
        //read dataset
        name = pathSource + "Test-0.arff";
        //check if database with the current id exists
        File arff = new File(name);
        if (!arff.exists()) {
            return;
        }

        if (checkDataset(0, name)) {
            copyDataset(name);
        }
    }

    public boolean checkDataset(int id, String name)
            throws Exception {

        //check if the class index is not the last attribute
        int classIndex = -1;
        File cls = new File(pathSource + "class");
        if (cls.exists()) {
            //read class index
            BufferedReader br = new BufferedReader(new FileReader(cls));
            classIndex = Integer.parseInt(br.readLine());
        }

        Instances data = DataMiningUtils.readDataset(name, classIndex, id);

        if (pattern.attributeType != AttributeType.Ignore) {
            //check atribute types
            AttributeType dataAtt = findAttributeType(data, id);
            if (dataAtt != pattern.attributeType) {
                return false;
            }
        }        
        
        /*
        if(pattern.attributeType != AttributeType.Mixed){
            if(dataAtt != pattern.attributeType){
                return false;
            }
        }    
        else{
            if(dataAtt == AttributeType.NumericOriginal || dataAtt == AttributeType.NumericStandardized){
                return false;
            }
        }        
        */
        //check num classes
        if (!isInInterval(data.numClasses(), pattern.minNumClasses, pattern.maxNumClasses)) {
            return false;
        }

        //check num attributes, remove class attribute        
        if(!isInInterval(data.numAttributes() - 1, pattern.minNumAtt, pattern.maxNumAtt)){
            return false;
        }
        
        /*
        //check num attributes, remove class attribute        
        if(!isInInterval(data.numAttributes() - 1, pattern.minNumAtt, pattern.maxNumAtt)
                && !isInInterval(data.numInstances(), pattern.minNumInst, pattern.maxNumInst)){
            return false;
        }
        */
        
        
        //check num instances
        if(!isInInterval(data.numInstances(), pattern.minNumInst, pattern.maxNumInst)){
            return false;
        }      
        
        //check missing values                

        switch (pattern.withMissingValues) {
            case Ignore: {
                break;
            }
            case False: {
                if (Dataset.haveMissingValues(data)) {
                    return false;
                }
                break;
            }
            case True: {
                if (!Dataset.haveMissingValues(data)) {
                    return false;
                }
                break;
            }
        }

        //if it is here, the dataset has the same data pattern
        return true;
    }

    private void copyDataset(String name) throws IOException {

        //if here -> dataset is of the same pattern
        //copy arff and xlsx
        File dir = new File(pathDest);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String nameDest = pathDest + "Test-0.arff";
        Files.copy(new File(name).toPath(), new File(nameDest).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        //-Analysis 4Folds
        String resAnExc = pathSource + "Final-Analysis-4Folds.xlsx";
        String resAnDestExc = pathDest + "Final-Analysis-4Folds.xlsx";
        Files.copy(new File(resAnExc).toPath(), new File(resAnDestExc).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        //-Analysis 10Folds
        resAnExc = pathSource + "Final-Analysis-10Folds.xlsx";
        resAnDestExc = pathDest + "Final-Analysis-10Folds.xlsx";
        Files.copy(new File(resAnExc).toPath(), new File(resAnDestExc).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

    }

    private AttributeType findAttributeType(Instances data, int id) {

        if (Dataset.hasMixedTypesAttributes(data)) {
            return AttributeType.Mixed;
        }
        if (Dataset.hasXAttributes(data, Attribute.NOMINAL)) {
            return AttributeType.Nominal;
        }
        if (Dataset.hasXAttributes(data, Attribute.NUMERIC)) {
            return AttributeType.NumericOriginal;
        }
        return null;
    }

    public boolean isInInterval(int value, int min, int max) {
        if (max != -1) {
            if (min > value || max < value) {
                return false;
            }
        } else { //num classes > 2
            if (min > value) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            this.executeTest();
        } catch (Exception ex) {
            File fff = new File(pathDest + "error");
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

}
