package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.datamining.DataMiningUtils;
import fr.unice.i3s.rockflows.experiments.datamining.Dataset;
import fr.unice.i3s.rockflows.experiments.datamining.Preprocesser;
import fr.unice.i3s.rockflows.experiments.weka.CfsSubsetFilter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author lupin
 */
public class TestExecutor implements Callable<Boolean> {

    String pathFolder = "";
    List<Preprocesser> filters;
    String conxuntos = "conxuntos.dat";
    String conxuntosKFold = "conxuntos_kfold.dat";
    int classIndex = -1;
    boolean parallel = true;

    public TestExecutor(int classIndex, String pathFolder, boolean parallel)
            throws Exception {
        this.pathFolder = pathFolder;
        this.classIndex = classIndex;
        this.parallel = parallel;
    }

    public void executeTest() throws Exception {

        String baseNameExcel = "Test-";
        double alpha = 0.05; //default

        String d999Path = pathFolder + "/" + baseNameExcel + "11.arff";
        Dataset paperOriginal = new Dataset(11, d999Path);
        paperOriginal.trainingSet = DataMiningUtils.readDataset(d999Path, -1, 999);

        //initialize original dataset (for our own tests)
        String d0Path = pathFolder + "/" + baseNameExcel + "0.arff";
        Dataset original = new Dataset(0, d0Path);
        original.trainingSet = DataMiningUtils.readDataset(d0Path, classIndex, 0);

        //init filters
        this.filters = inputPreprocessers();

        //find properties on the original dataset
        Dataset.findProperties(original, 0);
        paperOriginal.properties.isNumeric = true;
        paperOriginal.properties.isStandardized = true;

        //get list of datasets preprocessed        
        String basePath = pathFolder + "/" + baseNameExcel;
        List<Dataset> datasets = DataMiningUtils.getPreprocessedDatasets(basePath, original,
                filters, classIndex);
        int numDatasets = datasets.size();

        Dataset d12 = DataMiningUtils.getPreprocessedDataset(basePath, paperOriginal, filters.get(5),
                -1, 12);
        d12.properties.isStandardized = true;

        String conxuntosPath = pathFolder + "/" + conxuntos;
        String conxuntosKFoldPath = pathFolder + "/" + conxuntosKFold;

        if (parallel) {

            //consider the dataset of the paper
            ExecutorService executor = Executors.newFixedThreadPool(numDatasets + 1);

            //first of all, create thread for dataset of the paper
            {
                String dataPath = basePath + paperOriginal.id + ".arff";
                String excPath = basePath + paperOriginal.id + ".xlsx";
                IntermediateExecutor exec = new IntermediateExecutor(paperOriginal, dataPath,
                        excPath, conxuntosPath, conxuntosKFoldPath, alpha);
                executor.submit(exec);
                //attribute selection on the dataset of the paper
                dataPath = basePath + d12.id + ".arff";
                excPath = basePath + d12.id + ".xlsx";
                exec = new IntermediateExecutor(d12, dataPath,
                        excPath, conxuntosPath, conxuntosKFoldPath, alpha);
                executor.submit(exec);
            }
            //then execute the original and the pre-processed datasets
            for (int iii = 0; iii < numDatasets; iii++) {
                Dataset currentData = datasets.get(iii);
                String dataPath = basePath + currentData.id + ".arff";
                String excPath = basePath + currentData.id + ".xlsx";
                IntermediateExecutor exec = new IntermediateExecutor(currentData, dataPath,
                        excPath, conxuntosPath, conxuntosKFoldPath, alpha);
                executor.submit(exec);
            }
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } else {
            //first of all the dataset of the paper
            String dataPath = basePath + paperOriginal.id + ".arff";
            String excPath = basePath + paperOriginal.id + ".xlsx";
            IntermediateExecutor exec = new IntermediateExecutor(paperOriginal, dataPath,
                    excPath, conxuntosPath, conxuntosKFoldPath, alpha);
            exec.call();

            //attribute selection on the dataset of the paper
            dataPath = basePath + d12.id + ".arff";
            excPath = basePath + d12.id + ".xlsx";
            exec = new IntermediateExecutor(d12, dataPath,
                    excPath, conxuntosPath, conxuntosKFoldPath, alpha);
            exec.call();

            //then execute the original and the pre-processed datasets
            for (int iii = 0; iii < numDatasets; iii++) {
                Dataset currentData = datasets.get(iii);
                dataPath = basePath + currentData.id + ".arff";
                excPath = basePath + currentData.id + ".xlsx";
                exec = new IntermediateExecutor(currentData, dataPath, excPath,
                        conxuntosPath, conxuntosKFoldPath, alpha);
                exec.call();
            }
        }
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Running " + this.pathFolder);
        try {
            this.executeTest();
        } catch (Exception ex) {
            File fff = new File(pathFolder + "/error");
            Writer www = new FileWriter(fff);
            www.append(this.pathFolder + " :BEGIN, ");
            ex.printStackTrace(new PrintWriter(www));
            www.append(",:END " + this.pathFolder);
            www.append(ex.getMessage());
            www.flush();
            www.close();
            return false;
        }
        //finished
        File fff = new File(pathFolder + "/finish");
        Writer www = new FileWriter(fff);
        www.append(this.pathFolder + " :END, ");
        www.flush();
        www.close();
        return true;
    }

    //define the preprocessers
    private List<Preprocesser> inputPreprocessers() {

        List<Preprocesser> output = new ArrayList<>();
        int id = 1;
        //#1 discretize dataset
        Preprocesser p1 = new Preprocesser(id++, Preprocesser.DISCRETIZE);
        Discretize discretize = new Discretize();
        discretize.setAttributeIndices("first-last");
        p1.operation.add(discretize); //unsupervise, uses simple bins
        p1.properties.numericToNominal = true;
        output.add(p1);

        //#2 replace missing values with mean or mode
        Preprocesser p2 = new Preprocesser(id++, Preprocesser.REPLACE_MISSING_VALUES);
        p2.operation.add(new ReplaceMissingValues());
        p2.properties.replaceMissingValuesMeanMode = true;
        output.add(p2);

        //#3 discretize and replace missing values with mean or mode
        Preprocesser p3 = new Preprocesser(id++, Preprocesser.DISCRETIZE
                + ", " + Preprocesser.REPLACE_MISSING_VALUES);
        p3.operation.add(new ReplaceMissingValues());
        p3.operation.add(discretize); //unsupervise, uses simple bins        
        p3.properties.numericToNominal = true;
        p3.properties.replaceMissingValuesMeanMode = true;
        output.add(p3);

        //#4 nominal to binary, dummy variables 1-K
        Preprocesser p4 = new Preprocesser(id++, Preprocesser.NOMINAL_TO_BINARY);
        NominalToBinary nominalToBinary = new NominalToBinary();
        nominalToBinary.setBinaryAttributesNominal(true);
        nominalToBinary.setTransformAllValues(true);
        nominalToBinary.setAttributeIndices("first-last");
        p4.operation.add(nominalToBinary); //unsupervised
        p4.properties.nominalToBinary = true;
        output.add(p4);

        //#5 nominal to binary, dummy variables 1-K And Replace Missing Values Mean Mode
        Preprocesser p5 = new Preprocesser(id++, Preprocesser.NOMINAL_TO_BINARY + ", "
                + Preprocesser.REPLACE_MISSING_VALUES);
        p5.operation.add(new ReplaceMissingValues()); //unsupervised
        p5.operation.add(nominalToBinary); //unsupervised
        p5.properties.nominalToBinary = true;
        p5.properties.replaceMissingValuesMeanMode = true;
        output.add(p5);

        //#6 perform attributes selection - original
        Preprocesser p6 = new Preprocesser(id++, Preprocesser.ATTRIBUTE_SELECTION);
        p6.operation.add(new CfsSubsetFilter());
        p6.properties.attributesCorrelationSelection = true;
        output.add(p6);

        //#7 perform attributes selection - Discretized dataset
        Preprocesser p7 = new Preprocesser(id++, Preprocesser.DISCRETIZE + ", "
                + Preprocesser.ATTRIBUTE_SELECTION);
        p7.operation.add(discretize);
        p7.operation.add(new CfsSubsetFilter());
        p7.properties.attributesCorrelationSelection = true;
        p7.properties.numericToNominal = true;
        output.add(p7);

        //#8 perform attributes selection - Discretized dataset, replace MV
        Preprocesser p8 = new Preprocesser(id++, Preprocesser.REPLACE_MISSING_VALUES + ", "
                + Preprocesser.DISCRETIZE + ", "
                + Preprocesser.ATTRIBUTE_SELECTION);
        p8.operation.add(new ReplaceMissingValues());
        p8.operation.add(discretize);
        p8.operation.add(new CfsSubsetFilter());
        p8.properties.attributesCorrelationSelection = true;
        p8.properties.numericToNominal = true;
        p8.properties.replaceMissingValuesMeanMode = true;
        output.add(p8);

        //#9 perform attributes selection - Nominal to Binary
        Preprocesser p9 = new Preprocesser(id++, Preprocesser.NOMINAL_TO_BINARY + ", "
                + Preprocesser.ATTRIBUTE_SELECTION);
        p9.operation.add(nominalToBinary);
        p9.operation.add(new CfsSubsetFilter());
        p9.properties.attributesCorrelationSelection = true;
        p9.properties.nominalToBinary = true;
        output.add(p9);

        //#10 perform attributes selection - Nominal to Binary - replace MV
        Preprocesser p10 = new Preprocesser(id++, Preprocesser.REPLACE_MISSING_VALUES + ", "
                + Preprocesser.NOMINAL_TO_BINARY + ", "
                + Preprocesser.ATTRIBUTE_SELECTION);
        p10.operation.add(new ReplaceMissingValues());
        p10.operation.add(nominalToBinary);
        p10.operation.add(new CfsSubsetFilter());
        p10.properties.attributesCorrelationSelection = true;
        p10.properties.nominalToBinary = true;
        p10.properties.replaceMissingValuesMeanMode = true;
        output.add(p10);

        return output;
    }

}
