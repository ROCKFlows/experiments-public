package fr.unice.i3s.rockflows.experiments.datamining;

import java.util.ArrayList;
import java.util.List;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class Dataset {
    
    public String name = "";
    public Instances trainingSet;
    public Instances testSet;
    public long preprocessingTime; //seconds
    public int id; //dataset id
    public DatasetProperties properties;
    //the filter used in order to obtain this dataset from the original one
    //if it is the original, this filter is null
    public Preprocesser filterUsed; 
    public boolean existing = false;

    public Dataset(int id, String name){
        this.id = id;
        this.properties = new DatasetProperties();
        this.name = name;
    }        
    
    public static void copyProperties(Dataset source, Dataset dest){
                
        dest.properties =  new DatasetProperties(source.properties);
    }
    
    public static void findProperties(Dataset data, int preProcId){
        
        DatasetProperties dp = new DatasetProperties();
        dp.hasMissingValues = Dataset.hasMissingValues(data);
        if(preProcId == 4 || preProcId == 5 || preProcId == 9 || preProcId == 10){
            //for Weka, the binaries attributes are nominal, for us numeric (0,1)
            dp.isMixedTypesAttributes = false;
            dp.isNominal = false;
            dp.isNumeric = true;
        }
        else{
            dp.isNominal = Dataset.isDiscretized(data.trainingSet);
            dp.isMixedTypesAttributes = Dataset.hasMixedTypesAttributes(data.trainingSet);
            dp.isNumeric = Dataset.isNumeric(data.trainingSet);            
        }           
        dp.hasStringAttributes = Dataset.hasXAttributes(data.trainingSet, Attribute.STRING);
        dp.hasDateAttributes = Dataset.hasXAttributes(data.trainingSet, Attribute.DATE);
        
        data.properties = dp;        
    }   
    
    public static boolean isDiscretized(Instances data){
        
        //check if all attributes are nominal
        int numAtt = data.numAttributes();
        for(int iii = 0; iii < numAtt; iii++){
            if(iii != data.classIndex()){
                Attribute att = data.attribute(iii);
                if(!att.isNominal()){
                    return false;
                }                
            }            
        }
        return true;
    }        
    
    public static boolean isNumeric(Instances data){
        
        //check if all attributes are nominal
        int numAtt = data.numAttributes();
        for(int iii = 0; iii < numAtt; iii++){
            if(iii != data.classIndex()){
                Attribute att = data.attribute(iii);
                if(!att.isNumeric()){
                    return false;
                }            
            }
        }
        return true;
    }    
    
    public static boolean hasMissingValues(Dataset data){           
            
        if(haveMissingValues(data.trainingSet)){
            return true;
        }
                
        return false;
    }    
    
    private static boolean haveMissingValues(Instances dataset){
        //check if at least one instance contains at least one missing value
        int numAtt = dataset.numAttributes();
        int numInstances = dataset.numInstances();
        for(int iii = 0; iii < numInstances; iii++){
            Instance current = dataset.instance(iii);
            for(int jjj = 0; jjj < numAtt; jjj++){
                if(current.isMissing(jjj)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean hasMixedTypesAttributes(Instances tmp){
        
        //true if nominal and numeric, nominal and text, ....
        //false only nominal, only numerical, ...
        List<EnumAttribute> atts = new ArrayList<>();        
        int classIndex = tmp.classIndex();
        int numAtt = tmp.numAttributes();
        for(int iii = 0; iii < numAtt; iii++){
            if(iii == classIndex){
                continue;
            }
            Attribute att = tmp.attribute(iii);
            if(att.isNominal()){
                if(atts.contains(EnumAttribute.Date) || atts.contains(EnumAttribute.Numerical)
                            || atts.contains(EnumAttribute.String)){
                    return true;
                }
                else{
                    atts.add(EnumAttribute.Nominal);
                }
            }
            if(att.isDate()){
                if(atts.contains(EnumAttribute.Nominal) || atts.contains(EnumAttribute.Numerical)
                            || atts.contains(EnumAttribute.String)){
                    return true;
                }
                else{
                    atts.add(EnumAttribute.Date);
                }
            }           
            if(att.isNumeric()){
                if(atts.contains(EnumAttribute.Date) || atts.contains(EnumAttribute.Nominal)
                            || atts.contains(EnumAttribute.String)){
                    return true;
                }
                else{
                    atts.add(EnumAttribute.Numerical);
                }
            }           
            if(att.isString()){
                if(atts.contains(EnumAttribute.Date) || atts.contains(EnumAttribute.Numerical)
                            || atts.contains(EnumAttribute.Nominal)){
                    return true;
                }
                else{
                    atts.add(EnumAttribute.String);
                }
            }           
        }
        return false;       
    }

    public static boolean hasXAttributes(Instances data, int typeAtt){
                
        int numAtt = data.numAttributes();
        for(int iii = 0; iii < numAtt; iii++){
            Attribute att = data.attribute(iii);
            if(att.type() == typeAtt){
                return true;
            }
        }
        return false;
    }
    
}