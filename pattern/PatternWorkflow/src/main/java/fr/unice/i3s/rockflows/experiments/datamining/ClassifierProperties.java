package fr.unice.i3s.rockflows.experiments.datamining;

public class ClassifierProperties {

    //require all attributes discretized, for example
    //all Bayes Network can't work with numeric values, only categorical (discrete)
    //in this case, the dataset is copied, so it is not discretized also for the other algorithms
    public boolean manageMultiClass = true; //default value
    public boolean requireNominalDataset = false; //default value
    public boolean manageMissingValues = false; //default value
    public boolean requireNumericDataset = false;
    
    public boolean compatibleWithDataset = true;
    //for the moment, the 2 following are only for Decorate classifiers
    public boolean satisfyMinNumInstances = true;
    public boolean satisfy2DistinctNominalValues = true;    

    public int minNumTrainingInstances = 0;

    public ClassifierProperties() {
    }

    public ClassifierProperties(ClassifierProperties prop) {
        this.manageMissingValues = prop.manageMissingValues;
        this.manageMultiClass = prop.manageMultiClass;
        this.minNumTrainingInstances = prop.minNumTrainingInstances;
        this.requireNominalDataset = prop.requireNominalDataset;
        this.requireNumericDataset = prop.requireNumericDataset;
    }

}
