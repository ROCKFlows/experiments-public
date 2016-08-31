package fr.unice.i3s.rockflows.experiments;

import fr.unice.i3s.rockflows.datamining.DataMiningUtils;
import fr.unice.i3s.rockflows.datamining.Dataset;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.MultiClassClassifier;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author camillieri on 22/08/16.
 */
public class ExperimentsUtil {

    private ExperimentsUtil() {
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
            res.modelSize10f[iii] = res.infoclassifier.getClassifierByteSize();
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
            res.modelSize4f[iii] = ic.getClassifierByteSize();
            avgModelSize += res.modelSize4f[iii];

            //test
            Metric testRes = testTrainedClassifier(folds[iii].testSet, ic.classifier);
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
        Metric testRes = testTrainedClassifier(res.dataset.testSet,
                res.infoclassifier.classifier);

        res.accuracyTest = testRes.accuracy;
        res.testTimeTest = testRes.testTime;
        res.trainingTimeTest = timerStop - timerStart;
        res.modelSizeTest = res.infoclassifier.getClassifierByteSize();
    }

    public static Dataset removeAttributesDataset(Dataset data, int[] indexes) throws Exception {
        data.trainingSet = DataMiningUtils.removeAttributes(data.trainingSet, indexes, false);
        data.testSet = DataMiningUtils.removeAttributes(data.testSet, indexes, false);
        return data;
    }

    /**
     * If dataset has number of classes > 2, then all the classifiers that can't manage
     * directly the multi classification problem are contained inside a weka
     * class MultiClassClassifier, which as default use the strategy 1 against all.
     */
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
                    //1 vs 1
                    mc.setOptions(new String[] {"-M", "3"});
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
        Dataset validation = DataMiningUtils.getValidationDataset(res.dataset.trainingSet, pathConxuntos);

        if (currentIC.classifier instanceof LibSVM) {
            LibSVM svm = (LibSVM) currentIC.classifier;
            //evaluate C and gamma
            double[] cvalues = new double[] {0.1, 1, 10, 100, 1000};
            double[] gvalues = new double[] {0.00001, 0.0001, 0.001, 0.01, 0.1, 1};
            double max = 0;
            double bestC = 0;
            double bestG = 0;
            //modify classifier
            for (double cvalue : cvalues) {
                for (double gvalue : gvalues) {
                    svm.setCost(cvalue);
                    svm.setGamma(gvalue);
                    svm.buildClassifier(validation.trainingSet);
                    Metric testRes = testTrainedClassifier(validation.testSet, svm);
                    if (testRes.accuracy > max) {
                        max = testRes.accuracy;
                        bestC = cvalue;
                        bestG = gvalue;
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
}
