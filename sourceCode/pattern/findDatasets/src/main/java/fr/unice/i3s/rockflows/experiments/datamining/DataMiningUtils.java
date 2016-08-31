package fr.unice.i3s.rockflows.experiments.datamining;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.MultiClassClassifier;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DataMiningUtils {

    //create one dataset for each filter, by applying the filter to the original dataset
    public static List<Dataset> getPreprocessedDatasets(String basePath, Dataset original,
                                                        List<Preprocesser> filters) throws Exception {

        List<Dataset> output = new ArrayList<>();
        output.add(original);
        int count = filters.size();
        for (int iii = 0; iii < count; iii++) {
            Preprocesser filter = filters.get(iii);
            //check if this filter is applicable to this dataset
            if (filter.checkIfApplicable(original)) {
                //calculate time for pre-processing
                long timerStart = System.currentTimeMillis();
                Dataset current = filter.applyPreprocessing(original);
                long timerStop = System.currentTimeMillis();
                current.preprocessingTime = (timerStop - timerStart); //milliseconds 
                current.filterUsed = filter;
                output.add(current);
            }
        }

        return output;
    }

    public static void crossValidation10Folds(TestResult res) throws Exception {

        //one result for each fold
        int folds = 10;
        res.accuracy10f = new double[folds];
        res.modelSize10f = new double[folds];
        res.trainingTime10f = new double[folds];
        res.testTime10f = new double[folds];
        long avgTrainTime = 0;
        long avgTestTime = 0;
        long avgModelSize = 0;
        double avgAccuracy = 0;
        for (int iii = 0; iii < folds; iii++) {
            //prepare data for cross-validation
            Instances cvData = res.dataset.trainingSet;
            Instances train = cvData.trainCV(folds, iii);
            Instances test = cvData.testCV(folds, iii);
            //train the classifier
            long timerStart = System.currentTimeMillis();
            res.infoclassifier.classifier.buildClassifier(train);
            long timerStop = System.currentTimeMillis();
            //test the classifier
            Metric testRes = testTrainedClassifier(test, res.infoclassifier.classifier);
            res.accuracy10f[iii] = testRes.accuracy;
            avgAccuracy += res.accuracy10f[iii];
            res.testTime10f[iii] = testRes.testTime;
            avgTestTime += res.testTime10f[iii];
            res.trainingTime10f[iii] = timerStop - timerStart;
            avgTrainTime += res.trainingTime10f[iii];
            avgModelSize += res.modelSize10f[iii];
        }
        res.trainingTimeAvg10 = avgTrainTime / folds;
        res.testTimeAvg10 = avgTestTime / folds;
        res.modelSizeAvg10 = avgModelSize / folds;
        res.accuracyAvg10 = avgAccuracy / folds;
    }

    public static void crossValidation4Folds(TestResult res, String conxuntosKFold) throws Exception {

        res.accuracy4f = new double[4];
        res.modelSize4f = new double[4];
        res.trainingTime4f = new double[4];
        res.testTime4f = new double[4];
        InfoClassifier ic = res.infoclassifier;
        Dataset[] folds = DataMiningUtils.get4FoldDatasets(res.dataset.trainingSet, conxuntosKFold);
        long avgTrainTime = 0;
        long avgTestTime = 0;
        long avgModelSize = 0;
        double avgAccuracy = 0;
        for (int iii = 0; iii < 4; iii++) {
            //train
            //timer start
            long timerStart = System.currentTimeMillis();
            ic.classifier.buildClassifier(folds[iii].trainingSet);
            //timer stop
            long timerStop = System.currentTimeMillis();

            res.trainingTime4f[iii] = timerStop - timerStart; //milliseconds
            avgTrainTime += res.trainingTime4f[iii];
            avgModelSize += res.modelSize4f[iii];

            //test
            Metric testRes = DataMiningUtils.testTrainedClassifier(folds[iii].testSet, ic.classifier);
            res.testTime4f[iii] = testRes.testTime;
            avgTestTime += res.testTime4f[iii];
            res.accuracy4f[iii] = testRes.accuracy;
            avgAccuracy += res.accuracy4f[iii];
        }
        res.trainingTimeAvg = avgTrainTime / folds.length;
        res.testTimeAvg = avgTestTime / folds.length;
        res.modelSizeAvg = avgModelSize / folds.length;
        res.accuracyAvg = avgAccuracy / folds.length;
    }

    /**
     * Evaluate the classifier on the dataset passed as input.
     * Pre-conitions: the classifier is already trained.
     *
     * @param dataSet the dataset
     * @param cls the classifier
     * @return the accuracy value
     */
    public static Metric testTrainedClassifier(Instances dataSet, Classifier cls) {
        Metric output = new Metric();
        int correct = 0;
        int count = dataSet.numInstances();
        long timerStart = System.currentTimeMillis();
        for (int kkk = 0; kkk < count; kkk++) {
            Instance currentToClassify = dataSet.instance(kkk);
            double actualValue = currentToClassify.value(currentToClassify.classAttribute());
            double predictedValue = 0;
            try {
                predictedValue = cls.classifyInstance(currentToClassify);
            } catch (Exception ex) {
                // Logger.getLogger(ClassifierThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (predictedValue == actualValue) {
                correct++;
            }
        }
        long timerStop = System.currentTimeMillis();
        output.accuracy = (double) correct / (double) count;
        output.testTime = timerStop - timerStart;
        return output;
    }

    public static void trainAndTestClassifier(TestResult res) throws Exception {

        long timerStart = System.currentTimeMillis();
        res.infoclassifier.classifier.buildClassifier(res.dataset.trainingSet);
        long timerStop = System.currentTimeMillis();
        Metric testRes = DataMiningUtils.testTrainedClassifier(res.dataset.testSet,
                res.infoclassifier.classifier);

        res.accuracyAvg = testRes.accuracy;
        res.testTimeAvg = testRes.testTime;
        res.trainingTimeAvg = timerStop - timerStart;
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
        Remove remove = new Remove();                         // new instance of filter
        remove.setAttributeIndicesArray(indexes);
        remove.setInvertSelection(inverted);
        remove.setInputFormat(dataSet);// inform filter about dataset **AFTER** setting options
        return Filter.useFilter(dataSet, remove);   // apply filter           
    }

    public static Dataset removeAttributesDataset(Dataset data, int[] indexes) throws Exception {
        data.trainingSet = removeAttributes(data.trainingSet, indexes, false);
        data.testSet = removeAttributes(data.testSet, indexes, false);
        return data;
    }

    /**
     * Read the dataset at the given path and assign
     * the class label at the given index
     *
     * @param path the path
     * @param classIndex the class index
     * @return the read dataset
     * @throws Exception
     */
    public synchronized static Instances readDataset(String path, int classIndex) throws Exception {
        Instances dataSet = ConverterUtils.DataSource.read(path);
        if (classIndex != -1) {
            dataSet.setClassIndex(classIndex);
        } else {
            dataSet.setClassIndex(dataSet.numAttributes() - 1);
        }
        return dataSet;
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

    //if dataset has number of classes > 2, then all the classifiers that can't manage
    //directly the multi classification problem are contained inside a weka
    //class MultiClassClassifier, which as default use the strategy 1 against all
    public static void checkMultiClass(List<InfoClassifier> classifiers,
                                       Dataset original) throws Exception {
        //check the number of classes
        if (original.trainingSet.numClasses() > 2) {
            int count = classifiers.size();
            for (int ii = 0; ii < count; ii++) {
                InfoClassifier ic = classifiers.get(ii);
                if (!ic.properties.manageMultiClass) {
                    Classifier cl = ic.classifier;
                    String name = ic.name;
                    //uses MultiClass classifier as meta classifier for this one
                    MultiClassClassifier mc = new MultiClassClassifier();
                    mc.setOptions(new String[] {"-M", "3"}); //1 vs 1
                    mc.setClassifier(cl);
                    ic.classifier = mc;
                    ic.name = "1 vs 1: " + name;

                    //add 1 vs all in the end of the list of classifiers
                    //it will not be rechecked because the variable count
                    //has the value previous the addition to the list
                    MultiClassClassifier mc2 = new MultiClassClassifier();
                    //1 vs all
                    mc2.setClassifier(cl);
                    InfoClassifier newCl = new InfoClassifier(classifiers.size());
                    newCl.classifier = mc2;
                    newCl.name = "1 vs all: " + name;
                    newCl.properties = new ClassifierProperties(ic.properties);
                    classifiers.add(newCl);
                }
            }
        }
    }

    public static void evaluateParameters(TestResult res,
                                          String pathConxuntos) throws IOException, Exception {

        if (res.alreadyTested) {
            return;
        }

        InfoClassifier currentIC = res.infoclassifier;

        //check if it is compatible with the validation dataset
        if (!currentIC.isCompatibleWithDataset(res.dataset)) {
            return;
        }

        //get validation dataset and training set
        Dataset validation = getValidationDataset(res.dataset.trainingSet, pathConxuntos);

        if (currentIC.classifier instanceof LibSVM) {
            LibSVM svm = (LibSVM) currentIC.classifier;
            //evaluate C and gamma
            double[] cvalues = new double[] {0.1, 1, 10, 100, 1000};
            double[] gvalues = new double[] {0.00001, 0.0001, 0.001, 0.01, 0.1, 1};
            double max = 0;
            double bestC = 0;
            double bestG = 0;
            //modify classifier
            for (int iii = 0; iii < cvalues.length; iii++) {
                for (int jjj = 0; jjj < gvalues.length; jjj++) {
                    svm.setCost(cvalues[iii]);
                    svm.setGamma(gvalues[jjj]);
                    svm.buildClassifier(validation.trainingSet);
                    Metric testRes = DataMiningUtils.testTrainedClassifier(validation.testSet, svm);
                    if (testRes.accuracy > max) {
                        max = testRes.accuracy;
                        bestC = cvalues[iii];
                        bestG = gvalues[jjj];
                    }
                }
            }
            //set best parameters found for Svm
            svm.setCost(bestC);
            svm.setGamma(bestG);
            currentIC.classifier = svm;
            currentIC.parametersTuned = new ArrayList<>();
            currentIC.parametersTuned.add(bestC);
            currentIC.parametersTuned.add(bestG);
        }
    }

    public static Dataset getValidationDataset(Instances original, String pathConxuntos)
            throws FileNotFoundException, IOException {

        Dataset validation = new Dataset(0, "validation"); //id = 0
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
            newTest.add(original.instance(Integer.parseInt(trainIndeces[i])));
        }

        validation.trainingSet = newTrain;
        validation.testSet = newTest;
        return validation;
    }

    public static Dataset[] get4FoldDatasets(Instances original, String pathConxuntos)
            throws FileNotFoundException, IOException {

        Dataset[] folds = new Dataset[4];
        BufferedReader br = new BufferedReader(new FileReader(pathConxuntos));

        for (int iii = 0; iii < 4; iii++) {
            folds[iii] = new Dataset(-1, "fold" + iii);
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
                newTest.add(original.instance(Integer.parseInt(trainIndeces[i])));
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

    public static void computeAvg(TestResult res) {
        double acc = 0;
        long train = 0;
        long test = 0;
        long size = 0;
        int num = res.accuracy10f.length;
        for (int iii = 0; iii < num; iii++) {
            acc += res.accuracy10f[iii];
            train += res.trainingTime10f[iii];
            test += res.testTime10f[iii];
            size += res.modelSize10f[iii];
        }
        res.accuracyAvg = acc / num;
        res.trainingTimeAvg = train / num;
        res.testTimeAvg = test / num;
        res.modelSizeAvg = size / num;
    }

    public static void writeArff(Dataset dataset, String basePath) throws IOException {

        if (dataset.unique) {
            //write only training set, the unique dataset
            ArffSaver saver = new ArffSaver();
            saver.setInstances(dataset.trainingSet);
            saver.setFile(new File(basePath + ".arff"));
            saver.writeBatch();
        } else {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(dataset.trainingSet);
            saver.setFile(new File(basePath + "_train.arff"));
            saver.writeBatch();
            ArffSaver saver2 = new ArffSaver();
            saver2.setInstances(dataset.testSet);
            saver2.setFile(new File(basePath + "_test.arff"));
            saver2.writeBatch();
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