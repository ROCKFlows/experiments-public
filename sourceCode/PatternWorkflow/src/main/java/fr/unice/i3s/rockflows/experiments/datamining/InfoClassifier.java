/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

import weka.classifiers.Classifier;

import java.io.IOException;
import java.util.List;

/**
 * @author Luca
 */
public class InfoClassifier {

    public Classifier classifier;
    public int id;
    public String name; //name visualized in the Excel file
    public ClassifierProperties properties;
    public List<Double> parametersTuned;

    public InfoClassifier(int id) {
        this.id = id;
        this.properties = new ClassifierProperties();
    }

    public boolean isCompatibleWithDataset(Dataset dataset) {
        if (this.properties.manageMissingValues == false && dataset.properties.hasMissingValues) {
            return false;
        }
        if (this.properties.requireNominalDataset == true && !dataset.properties.isNominal) {
            return false;
        }
        if (this.properties.requireNumericDataset == true && !dataset.properties.isNumeric) {
            return false;
        }
        if (this.properties.minNumTrainingInstances > dataset.trainingSet.numInstances()) {
            return false;
        }
        return true;
    }
    
    public boolean isUsable(){
        if(this.properties.satisfy2DistinctNominalValues
                && this.properties.satisfyMinNumInstances
                && this.properties.compatibleWithDataset){
            return true;
        }
        return false;
    }

}
