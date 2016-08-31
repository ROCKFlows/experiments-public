package fr.unice.i3s.rockflows.datamining;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DataMiningUtils {

    private DataMiningUtils() {
    }

    private static boolean datasetExists(String incompletePath, int id) {
        String path = incompletePath + id + ".arff";
        File data = new File(path);
        return data.exists();
    }

    //create one dataset for each filter, by applying the filter to the original dataset
    public static List<Dataset> getPreprocessedDatasets(String basePath, Dataset original,
                                                        List<Preprocesser> filters, int classIndex) throws Exception {
        List<Dataset> output = new ArrayList<>();
        output.add(original);
        int count = filters.size();
        for (Preprocesser filter : filters) {
            //check if this filter is applicable to this dataset
            if (filter.checkIfApplicable(original)) {
                //if already exists, read it
                Dataset current;
                if (DataMiningUtils.datasetExists(basePath, filter.id)) {
                    //read dataset
                    current = new Dataset(filter.id, original.name);
                    String path = basePath + filter.id + ".arff";
                    current.trainingSet = DataMiningUtils.readDataset(path, classIndex, filter.id);
                    current.existing = true;
                    Dataset.findProperties(current, filter.id);
                } else {
                    //calculate time for pre-processing
                    long timerStart = System.currentTimeMillis();
                    current = filter.applyPreprocessing(original);
                    long timerStop = System.currentTimeMillis();
                    current.preprocessingTime = (timerStop - timerStart); //milliseconds
                    current.existing = false;
                }
                current.filterUsed = filter;
                output.add(current);
            }
        }
        return output;
    }

    //create one dataset for each filter, by applying the filter to the original dataset
    public static Dataset getPreprocessedDataset(String basePath, Dataset original,
                                                 Preprocesser filter, int classIndex, int datasetId) throws Exception {

        Dataset output = new Dataset(datasetId, "d11");
        //check if this filter is applicable to this dataset
        if (filter.checkIfApplicable(original)) {
            //if already exists, read it
            if (DataMiningUtils.datasetExists(basePath, datasetId)) {
                //read dataset
                output = new Dataset(datasetId, original.name);
                String path = basePath + filter.id + ".arff";
                output.trainingSet = DataMiningUtils.readDataset(path, classIndex, filter.id);
                output.existing = true;
                Dataset.findProperties(output, filter.id);
            } else {
                //calculate time for pre-processing
                long timerStart = System.currentTimeMillis();
                output = filter.applyPreprocessing(original);
                output.id = datasetId;
                long timerStop = System.currentTimeMillis();
                output.preprocessingTime = (timerStop - timerStart); //milliseconds
                output.existing = false;
            }
            output.filterUsed = filter;
        }

        return output;
    }

    /**
     * Pre-processing operation to remove a subset of attributes from
     * the dataset. The attributes are identified by an index 0 based.
     *
     * @param dataSet the dataset
     * @param indexes The array of indexes to remove
     * @return the new dataset filtered.
     * @throws Exception
     */
    public static Instances removeAttributes(Instances dataSet, int[] indexes,
                                             boolean inverted) throws Exception {
        //delete attributes
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indexes);
        remove.setInvertSelection(inverted);
        // inform filter about dataset **AFTER** setting options
        remove.setInputFormat(dataSet);
        // apply filter
        return Filter.useFilter(dataSet, remove);
    }

    public static void removeTypeAttribute(Dataset dataset, int typeAtt) throws Exception {

        String type = "";
        if (typeAtt == Attribute.STRING) {
            type = "string";
        }
        if (typeAtt == Attribute.DATE) {
            type = "date";
        }

        RemoveType rt = new RemoveType();
        rt.setOptions(new String[] {"-T", type});
        rt.setInputFormat(dataset.trainingSet);
        dataset.trainingSet = Filter.useFilter(dataset.trainingSet, rt);
        dataset.testSet = Filter.useFilter(dataset.testSet, rt);
    }

    /**
     * Read the dataset at the given path and assign the class label at the given index.
     *
     * @param path the path
     * @param classIndex the class index
     * @param prepId the id of associated pre-processing
     * @return the read dataset
     * @throws Exception
     */
    public synchronized static Instances readDataset(String path, int classIndex, int prepId)
            throws Exception {
        System.out.println("Reading " + path);
        Instances dataSet = ConverterUtils.DataSource.read(path);
        if (classIndex != -1) {
            if (prepId < 6) {
                dataSet.setClassIndex(classIndex);
            } else {
                dataSet.setClassIndex(dataSet.numAttributes() - 1);
            }
        } else {
            dataSet.setClassIndex(dataSet.numAttributes() - 1);
        }
        return dataSet;
    }

    public static void writeArff(Instances dataset, String path) throws IOException {

        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        saver.setFile(new File(path));
        saver.writeBatch();
    }

    public static void writeArff(Dataset dataset, String basePath) throws IOException {

        if (dataset.unique) {
            writeArff(dataset.trainingSet, basePath + ".arff");
        } else {
            writeArff(dataset.trainingSet, basePath + "_train.arff");
            writeArff(dataset.testSet, basePath + "_test.arff");
        }
    }

    public static Dataset getValidationDataset(Instances original, String pathConxuntos) throws IOException {
        Dataset validation = new Dataset(0, "validation");
        BufferedReader br = new BufferedReader(new FileReader(pathConxuntos));
        String[] trainIndeces = br.readLine().split(" ");
        String[] testIndeces = br.readLine().split(" ");
        Instances newTrain = original.stringFreeStructure();
        Instances newTest = original.stringFreeStructure();

        int num = trainIndeces.length;
        for (int i = 0; i < num; i++) {
            newTrain.add(original.instance(Integer.parseInt(trainIndeces[i])));
        }

        num = testIndeces.length;
        for (int i = 0; i < num; i++) {
            newTest.add(original.instance(Integer.parseInt(testIndeces[i])));
        }

        validation.trainingSet = newTrain;
        validation.testSet = newTest;
        return validation;
    }

    public static Dataset[] get4FoldDatasets(Instances original, String pathConxuntos) throws IOException {

        Dataset[] folds = new Dataset[4];
        BufferedReader br = new BufferedReader(new FileReader(pathConxuntos));
        String[] trainIndeces = br.readLine().split(" ");
        String[] testIndeces = br.readLine().split(" ");

        for (int iii = 0; iii < 4; iii++) {
            folds[iii] = new Dataset(-1, "fold" + iii);

            Instances newTrain = original.stringFreeStructure();
            Instances newTest = original.stringFreeStructure();
            int num = trainIndeces.length;
            for (int i = 0; i < num; i++) {
                newTrain.add(original.instance(Integer.parseInt(trainIndeces[i])));
            }
            num = testIndeces.length;
            for (int i = 0; i < num; i++) {
                newTest.add(original.instance(Integer.parseInt(testIndeces[i])));
            }
            folds[iii].trainingSet = newTrain;
            folds[iii].testSet = newTest;
        }
        return folds;
    }

    public static void mergeDataset(Dataset data) {

        int num = data.trainingSet.numInstances();
        for (int iii = 0; iii < num; iii++) {
            data.trainingSet.add(data.testSet.instance(iii));
        }
    }

    public static boolean has2DistinctNominalValues(Dataset data) {

        if (data.properties.isNumeric) {
            return true;
        }
        Instances train = data.trainingSet;
        int num = train.numAttributes();
        for (int iii = 0; iii < num; iii++) {
            Attribute att = train.attribute(iii);
            if (att.isNominal()) {
                //check if it contains at least 2 distinct nominal values
                int rows = train.numInstances();
                String value1 = "";
                boolean distinct = false;
                for (int jjj = 1; jjj < rows; jjj++) {
                    Instance inst = train.instance(jjj);
                    if (!inst.isMissing(iii)) {
                        if (value1.equals("")) {
                            value1 = inst.stringValue(iii);
                        } else {
                            String value2 = inst.stringValue(iii);
                            if (!value1.equals(value2)) {
                                distinct = true;
                                break;
                            }
                        }
                    }
                }
                if (!distinct) {
                    return false;
                }
            }
        }
        return true;
    }

}