package fr.unice.i3s.rockflows.experiments;

import com.javamex.classmexer.MemoryUtil;
import fr.unice.i3s.rockflows.datamining.Dataset;
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
        if (dataset.properties.hasMissingValues && !this.properties.manageMissingValues) {
            return false;
        }
        if (!dataset.properties.isNominal && this.properties.requireNominalDataset) {
            return false;
        }
        if (!dataset.properties.isNumeric && this.properties.requireNumericDataset) {
            return false;
        }
        if (this.properties.minNumTrainingInstances > dataset.trainingSet.numInstances()) {
            return false;
        }
        return true;
    }

    public long getClassifierByteSize() throws IOException {
        return MemoryUtil.deepMemoryUsageOf(this.classifier);
    }

    public boolean isUsable() {
        return this.properties.satisfy2DistinctNominalValues
                && this.properties.satisfyMinNumInstances
                && this.properties.compatibleWithDataset;
    }

}
