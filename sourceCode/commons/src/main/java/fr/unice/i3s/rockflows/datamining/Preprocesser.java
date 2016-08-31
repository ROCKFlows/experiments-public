package fr.unice.i3s.rockflows.datamining;

import weka.core.Instances;
import weka.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class Preprocesser {

    public List<Filter> operation;
    public int id;
    public PreprocesserProperties properties;
    public String name;

    public static final String REPLACE_MISSING_VALUES = "Replace-Missing-Values";
    public static final String DISCRETIZE = "Discretize";
    public static final String NOMINAL_TO_BINARY = "Nominal-To-Binary";
    public static final String ATTRIBUTE_SELECTION = "Attribute-Selection";
    public static final String STANDARDIZE = "Standardize";

    public Preprocesser(int id, String name) {
        this.id = id;
        this.name = name;
        this.properties = new PreprocesserProperties();
        this.operation = new ArrayList<>();
    }

    //apply pre-processing to the input dataset and return
    //the pre-processed dataset
    public Dataset applyPreprocessing(Dataset original) throws Exception {

        //initialize output dataset
        Dataset output = new Dataset(this.id, original.name);
        //unique?
        output.unique = original.unique;
        output.trainingSet = new Instances(original.trainingSet);
        if (!original.unique) {
            output.testSet = new Instances(original.testSet);
        }
        //for each filter, apply the pre-proprocessing on the dataset
        //in the order defined by the list of filters
        int count = this.operation.size();
        for (int iii = 0; iii < count; iii++) {
            Filter current = this.operation.get(iii);
            current.setInputFormat(output.trainingSet);
            output.trainingSet = Filter.useFilter(output.trainingSet, current);
            if (!original.unique) {
                //if attribute selection, the same attributes are removed manually
                //the filter is not re-applied
                if (current instanceof CfsSubsetFilter) {
                    output.testSet = DataMiningUtils.removeAttributes(
                            output.testSet, getIndicesAttributesUsedArray(), true);
                } else {
                    //the same filter is used to obtain the test set
                    current.setInputFormat(output.testSet);
                    output.testSet = Filter.useFilter(output.testSet, current);
                }
            }
        }

        //change dataset properties
        Dataset.findProperties(output, this.id);
        if (this.properties.attributesCorrelationSelection) {
            output.properties.attributesSelectedForCorrelation = true;
        }

        return output;
    }

    /**
     * Check if this filter is applicable to the input dataset.
     * for example, if the dataset is already discretized it doesn't make sense to re-discretize it
     * N.B: the list of preprocesser contains also the "combined" preprocesser
     * but here it is enough to see the single properties
     */
    public boolean checkIfApplicable(Dataset dataset) {
        if ((this.properties.numericToNominal && dataset.properties.isNominal)
                || (this.properties.nominalToBinary && dataset.properties.isNumeric)) {
            return false;
        } else if (this.properties.replaceMissingValuesMeanMode && !dataset.properties.hasMissingValues) {
            return false;
        }
        return !(dataset.properties.attributesSelectedForCorrelation
                && this.properties.attributesCorrelationSelection);
    }

    public String getIndicesAttributesUsed() {

        for (Filter pr : this.operation) {
            if (pr instanceof CfsSubsetFilter) {
                return ((CfsSubsetFilter) pr).getAttributesSelectedIndices();
            }
        }
        return "all attributes used";
    }

    private int[] getIndicesAttributesUsedArray() {

        for (Filter pr : this.operation) {
            if (pr instanceof CfsSubsetFilter) {
                return ((CfsSubsetFilter) pr).getAttributesSelectedIndicesArray();
            }
        }
        return null;
    }

}