/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.IntermediateExcelFile;
import fr.unice.i3s.rockflows.experiments.datamining.DataMiningUtils;
import fr.unice.i3s.rockflows.experiments.datamining.Dataset;
import fr.unice.i3s.rockflows.experiments.datamining.InfoClassifier;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
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
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.ClassificationViaClustering;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.Decorate;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiBoostAB;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.meta.OrdinalClassClassifier;
import weka.classifiers.meta.RacedIncrementalLogitBoost;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.meta.RotationForest;
import weka.classifiers.misc.HyperPipes;
import weka.classifiers.misc.VFI;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.DTNB;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.NNge;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.Ridor;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.clusterers.FarthestFirst;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

/**
 *
 * @author lupin
 */
public class IntermediateExecutor implements Callable<Boolean> { 
    
    Dataset data;
    String dataPath = "";
    String excPath = "";
    String conxuntosPath = "";
    String conxuntosKFoldPath = "";
    int indexDecorate = -1;
    List<InfoClassifier> classifiers;
    double alpha = 0.05;
    
    public IntermediateExecutor(Dataset data, String dataPath,
            String excPath, String conxuntosPath, String conxuntosKFoldPath, 
            double alpha) throws Exception{
        this.data = data;
        this.dataPath = dataPath;
        this.excPath = excPath;
        this.conxuntosPath = conxuntosPath;
        this.conxuntosKFoldPath = conxuntosKFoldPath;
        this.alpha = alpha;
        this.classifiers = inputClassifier(this.data);        
    }
    
    @Override
    public Boolean call() throws Exception {
        
        try{
            this.execute();
        }
        catch(Exception exc){
            
            File fff = new File(excPath + "000.error");
            Writer www = new FileWriter(fff);
            www.append(this.data.name + " :BEGIN, ");
            exc.printStackTrace(new PrintWriter(www));
            www.append(",:END " + this.data.name);
            www.append(exc.getMessage());
            www.flush();
            www.close();            
            return false;
        }

        return true;
    }
 
    public void execute() throws Exception{

        if(!data.existing){
            DataMiningUtils.writeArff(data.trainingSet, dataPath); 
        }        

        //initExcel
        IntermediateExcelFile excFile = new IntermediateExcelFile(excPath, data);

        //check if each nominal attributes contains at least 2 distinct values
        //(for Decorate classifier, it generates an exception if a nominal attribute)
        //does not contain at least 2 distinct nominal values
        //this.classifiers.get(indexDecorate).properties.satisfy2DistinctNominalValues = 
        //DataMiningUtils.has2DistinctNominalValues(data);

        int numClassifiers = classifiers.size();
        //ExecutorService executor = Executors.newFixedThreadPool(numClassifiers);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        //read values of classifiers if it is already existing
        for (int jjj = 0; jjj < numClassifiers; jjj++) {                  
            InfoClassifier currentIC = classifiers.get(jjj);            
            TestResult res = new TestResult(data, currentIC);                
            AlgoExecutor exec = new AlgoExecutor(res, conxuntosPath, conxuntosKFoldPath, excFile);
            executor.submit(exec);                        
        }
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    }
    
    private List<InfoClassifier> inputClassifier(Dataset original) throws Exception {
        List<InfoClassifier> cls = new ArrayList<>();
        int id = 0;
        
        //LogisticRegression:
        InfoClassifier ic1 = new InfoClassifier(id++);
        ic1.classifier = new Logistic();
        ic1.name = "Logistic Regression";
        ic1.properties.requireNumericDataset = true;
        cls.add(ic1);
        //SVM:
        InfoClassifier ic2 = new InfoClassifier(id++);
        LibSVM ccc = new LibSVM();
        //disable 
        ccc.setOptions(new String[]{
            "-J", //Turn off nominal to binary conversion.
            "-V"  //Turn off missing value replacement
        });
        ccc.setGamma(0.01);
        ccc.setCost(10);
        //ccc.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
        //ccc.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
        //ccc.setEps(0.001); //tolerance
        ic2.classifier = ccc;
        ic2.name = "Svm";
        ic2.properties.requireNumericDataset = true;
        cls.add(ic2);
        //J48:
        InfoClassifier ic3 = new InfoClassifier(id++);
        ic3.classifier = new J48();
        ic3.name = "J48";
        ic3.properties.manageMissingValues = true;
        cls.add(ic3);
        //NBTree:
        InfoClassifier ic4 = new InfoClassifier(id++);
        ic4.classifier = new NBTree();
        ic4.name = "NBTree";
        ic4.properties.manageMissingValues = true;
        cls.add(ic4);
        /*
        //RandomForest: 
        InfoClassifier ic5 = new InfoClassifier(id++);
        RandomForest ccc2 = new RandomForest();
        ccc2.setNumTrees(500);
        ccc2.setMaxDepth(0);
        ic5.classifier = ccc2;
        ic5.name = "Random Forest";
        ic5.properties.manageMissingValues = true;
        cls.add(ic5);
        //Logistic Model Trees (LMT):
        InfoClassifier ic6 = new InfoClassifier(id++);
        ic6.classifier = new LMT();
        ic6.name = "Logistic Model Tree";
        ic6.properties.manageMissingValues = true;
        cls.add(ic6);
        /*
        //Alternating Decision Trees (ADTree):
        InfoClassifier ic7 = new InfoClassifier(id++);
        if(original.trainingSet.numClasses() > 2){
            MultiClassClassifier mc = new MultiClassClassifier();
            mc.setOptions(new String[]{"-M", "3"}); //1 vs 1
            mc.setClassifier(new ADTree());
            ic7.classifier = mc;
            ic7.name = "1-vs-1 Alternating Decision Tree";
        }
        else{
            ic7.classifier = new ADTree();
            ic7.name = "Alternating Decision Tree";
        }
        ic7.properties.manageMultiClass = false;
        ic7.properties.manageMissingValues = true;        
        cls.add(ic7);        
        //Naive Bayes:
        InfoClassifier ic8 = new InfoClassifier(id++);
        ic8.classifier = new NaiveBayes();
        ic8.name = "Naive Bayes";
        ic8.properties.manageMissingValues = true;
        cls.add(ic8);
        //Bayesian Networks:
        InfoClassifier ic9 = new InfoClassifier(id++);
        ic9.classifier = new BayesNet();
        ic9.name = "Bayesian Network";
        ic9.properties.requireNominalDataset = true;
        cls.add(ic9);
        //IBK
        InfoClassifier ic10 = new InfoClassifier(id++);
        ic10.classifier = new IBk();
        ic10.name = "IBk";
        ic10.properties.manageMissingValues = true;
        cls.add(ic10);
        //JRip:
        InfoClassifier ic11 = new InfoClassifier(id++);
        ic11.classifier = new JRip();
        ic11.name = "JRip";
        ic11.properties.manageMissingValues = true;
        cls.add(ic11);
        */
        //MultilayerPerceptron(MLP):
        InfoClassifier ic12 = new InfoClassifier(id++);
        ic12.classifier = new MultilayerPerceptron();
        ic12.name = "Multillayer Perceptron";
        ic12.properties.requireNumericDataset = true;
        cls.add(ic12);
        /*
        //Bagging RepTree:
        InfoClassifier ic14 = new InfoClassifier(id++);
        REPTree base3 = new REPTree();
        Bagging ccc4 = new Bagging();
        ccc4.setClassifier(base3);
        ic14.classifier = ccc4;
        ic14.name = "Bagging RepTree";
        ic14.properties.manageMissingValues = true;
        cls.add(ic14);
        //Bagging J48
        InfoClassifier ic15 = new InfoClassifier(id++);
        Bagging ccc5 = new Bagging();
        ccc5.setClassifier(new J48());
        ic15.classifier = ccc5;
        ic15.name = "Bagging J48";
        ic15.properties.manageMissingValues = true;
        cls.add(ic15);
        
        //Bagging NBTree
        InfoClassifier ic16 = new InfoClassifier(id++);
        Bagging ccc6 = new Bagging();
        ccc6.setClassifier(new NBTree());
        ic16.classifier = ccc6;
        ic16.name = "Bagging NBTree";
        ic16.properties.manageMissingValues = true;
        cls.add(ic16);
        
        //Bagging OneR:
        InfoClassifier ic17 = new InfoClassifier(id++);
        Bagging ccc7 = new Bagging();
        ccc7.setClassifier(new OneR());
        ic17.classifier = ccc7;
        ic17.name = "Bagging OneR";
        ic17.properties.requireNominalDataset = true;
        ic17.properties.manageMissingValues = true;
        cls.add(ic17);
        //Bagging Jrip
        InfoClassifier ic18 = new InfoClassifier(id++);
        Bagging ccc8 = new Bagging();
        ccc8.setClassifier(new JRip());
        ic18.classifier = ccc8;
        ic18.name = "Bagging JRip";
        ic18.properties.manageMissingValues = true;
        cls.add(ic18);
        //MultiboostAB DecisionStump
        InfoClassifier ic24 = new InfoClassifier(id++);
        MultiBoostAB ccc14 = new MultiBoostAB();
        ccc14.setClassifier(new DecisionStump());
        ic24.classifier = ccc14;
        ic24.name = "MultiboostAB DecisionStump";
        ic24.properties.manageMissingValues = true;
        cls.add(ic24);
        //MultiboostAB OneR
        InfoClassifier ic25 = new InfoClassifier(id++);
        MultiBoostAB ccc15 = new MultiBoostAB();
        ccc15.setClassifier(new OneR());
        ic25.classifier = ccc15;
        ic25.name = "MultiboostAB OneR";
        ic25.properties.requireNominalDataset = true;
        cls.add(ic25);
        //MultiboostAB J48
        InfoClassifier ic27 = new InfoClassifier(id++);
        MultiBoostAB ccc17 = new MultiBoostAB();
        ccc17.setClassifier(new J48());
        ic27.classifier = ccc17;
        ic27.name = "MultiboostAB J48";
        ic27.properties.manageMissingValues = true;
        cls.add(ic27);
        //MultiboostAB Jrip
        InfoClassifier ic28 = new InfoClassifier(id++);
        MultiBoostAB ccc18 = new MultiBoostAB();
        ccc18.setClassifier(new JRip());
        ic28.classifier = ccc18;
        ic28.name = "MultiboostAB JRip";
        cls.add(ic28);
        
        //MultiboostAB NBTree
        InfoClassifier ic29 = new InfoClassifier(id++);
        MultiBoostAB ccc19 = new MultiBoostAB();
        ccc19.setClassifier(new NBTree());
        ic29.classifier = ccc19;
        ic29.name = "MultiboostAB NBTree";
        ic29.properties.manageMissingValues = true;
        cls.add(ic29);
        
        //RotationForest RandomTree
        InfoClassifier ic32 = new InfoClassifier(id++);
        RotationForest ccc21 = new RotationForest();
        RandomTree rtr5 = new RandomTree();
        rtr5.setMinNum(2);
        rtr5.setAllowUnclassifiedInstances(true);        
        ccc21.setClassifier(rtr5);
        ic32.classifier = ccc21;
        ic32.name = "RotationForest RandomTree";
        ic32.properties.manageMissingValues = true;
        cls.add(ic32);
        //RotationForest J48:
        InfoClassifier ic33 = new InfoClassifier(id++);
        J48 base6 = new J48();
        RotationForest ccc22 = new RotationForest();
        ccc22.setClassifier(base6);
        ic33.classifier = ccc22;
        ic33.name = "RotationForest J48";
        ic33.properties.manageMissingValues = true;
        cls.add(ic33);
        //RandomCommittee RandomTree:
        InfoClassifier ic34 = new InfoClassifier(id++);
        RandomTree rtr4 = new RandomTree();
        rtr4.setMinNum(2);
        rtr4.setAllowUnclassifiedInstances(true);
        RandomCommittee ccc23 = new RandomCommittee();
        ccc23.setClassifier(rtr4);
        ic34.classifier = ccc23;
        ic34.name = "RandomComittee RandomTree";
        ic34.properties.manageMissingValues = true;
        cls.add(ic34);
        //Class via Clustering: SimpleKMeans
        //N.B: it can't handle date attributes
        InfoClassifier ic35 = new InfoClassifier(id++);
        ClassificationViaClustering ccc24 = new ClassificationViaClustering();
        SimpleKMeans km = new SimpleKMeans();
        km.setNumClusters(original.trainingSet.numClasses());
        ccc24.setClusterer(km);
        ic35.classifier = ccc24;
        ic35.name = "Classification via Clustering: KMeans";
        ic35.properties.requireNumericDataset = true;
        cls.add(ic35);
        //Class via Clustering: FarthestFirst
        InfoClassifier ic36 = new InfoClassifier(id++);
        ClassificationViaClustering ccc25 = new ClassificationViaClustering();
        FarthestFirst ff = new FarthestFirst();
        ff.setNumClusters(original.trainingSet.numClasses());
        ccc25.setClusterer(ff);
        ic36.classifier = ccc25;
        ic36.name = "Classification via Clustering: FarthestFirst";
        ic36.properties.requireNumericDataset = true;
        cls.add(ic36);
        //SMO
        InfoClassifier ic37 = new InfoClassifier(id++);
        ic37.classifier = new SMO();
        ic37.properties.requireNumericDataset = true;
        ic37.properties.manageMultiClass = false;
        ic37.name = "Smo";
        cls.add(ic37);
        //Random Subspace
        InfoClassifier ic38 = new InfoClassifier(id++);
        RandomSubSpace sub = new RandomSubSpace();
        sub.setClassifier(new REPTree());
        ic38.classifier = sub;
        ic38.name = "Random Subspaces of RepTree";
        ic38.properties.manageMissingValues = true;
        cls.add(ic38);
        //PART rule based
        InfoClassifier ic39 = new InfoClassifier(id++);
        PART p39 = new PART();
        p39.setOptions(new String[]{"-C", "0.5"});        
        ic39.classifier = new PART();
        ic39.name = "PART";
        ic39.properties.manageMissingValues = true;
        cls.add(ic39);
        //Decision-Table / Naive Bayes
        InfoClassifier ic40 = new InfoClassifier(id++);
        ic40.classifier = new DTNB();
        ic40.name = "DTNB";
        ic40.properties.manageMissingValues = true;
        cls.add(ic40);
        //Ridor Rule based
        InfoClassifier ic41 = new InfoClassifier(id++);
        ic41.classifier = new Ridor();
        ic41.name = "Ridor";
        ic41.properties.manageMissingValues = true;
        cls.add(ic41);
        //Decision Table
        InfoClassifier ic42 = new InfoClassifier(id++);
        ic42.classifier = new DecisionTable();
        ic42.name = "Decision Table";
        ic42.properties.manageMissingValues = true;
        cls.add(ic42);
        //Conjunctive Rule
        InfoClassifier ic43 = new InfoClassifier(id++);
        ic43.classifier = new ConjunctiveRule();
        ic43.name = "Conjunctive Rule";
        ic43.properties.manageMissingValues = true;
        cls.add(ic43);
        //LogitBoost Decision Stump
        InfoClassifier ic44 = new InfoClassifier(id++);
        LogitBoost lb = new LogitBoost();
        lb.setOptions(new String[]{"-L","1.79"});
        lb.setClassifier(new DecisionStump());
        ic44.classifier = lb;
        ic44.name = "LogitBoost Decision Stump";
        ic44.properties.manageMissingValues = true;
        cls.add(ic44);
        //Raced Incremental Logit Boost, Decision Stump
        InfoClassifier ic45 = new InfoClassifier(id++);
        RacedIncrementalLogitBoost rlb = new RacedIncrementalLogitBoost();
        rlb.setClassifier(new DecisionStump());
        ic45.classifier = rlb;
        ic45.name = "Raced Incremental Logit Boost, Decision Stumps";
        ic45.properties.manageMissingValues = true;
        cls.add(ic45);
        //AdaboostM1 decision stump
        InfoClassifier ic46 = new InfoClassifier(id++);
        AdaBoostM1 adm = new AdaBoostM1();
        adm.setClassifier(new DecisionStump());
        ic46.classifier = adm;
        ic46.name = "AdaboostM1, Decision Stumps";
        ic46.properties.manageMissingValues = true;
        cls.add(ic46);
        //AdaboostM1 J48
        InfoClassifier ic47 = new InfoClassifier(id++);
        AdaBoostM1 adm2 = new AdaBoostM1();
        adm2.setClassifier(new J48());
        ic47.classifier = adm2;
        ic47.name = "AdaboostM1, J48";
        ic47.properties.manageMissingValues = true;
        cls.add(ic47);
        //MultiboostAb Decision Table
        InfoClassifier ic48 = new InfoClassifier(id++);
        MultiBoostAB mba = new MultiBoostAB();
        mba.setClassifier(new DecisionTable());
        ic48.classifier = mba;
        ic48.name = "MultiboostAB, Decision Table";
        ic48.properties.manageMissingValues = true;
        cls.add(ic48);
        //Multiboost NaiveBayes
        InfoClassifier ic49 = new InfoClassifier(id++);
        MultiBoostAB mba2 = new MultiBoostAB();
        mba2.setClassifier(new NaiveBayes());
        ic49.classifier = mba2;
        ic49.name = "MultiboostAB, Naive Bayes";
        ic49.properties.manageMissingValues = true;
        cls.add(ic49);
        //Multiboost PART
        InfoClassifier ic50 = new InfoClassifier(id++);
        MultiBoostAB mba3 = new MultiBoostAB();
        mba3.setClassifier(new PART());
        ic50.classifier = mba3;
        ic50.name = "MultiboostAB, PART";
        ic50.properties.manageMissingValues = true;
        cls.add(ic50);
        //Multiboost Random Tree
        InfoClassifier ic51 = new InfoClassifier(id++);
        MultiBoostAB mba4 = new MultiBoostAB();
        RandomTree rtr3 = new RandomTree();
        rtr3.setMinNum(2);
        rtr3.setAllowUnclassifiedInstances(true);        
        mba4.setClassifier(rtr3);
        ic51.classifier = mba4;
        ic51.name = "MultiboostAB, RandomTree";
        ic51.properties.manageMissingValues = true;
        cls.add(ic51);
        //Multiboost Rep Tree
        InfoClassifier ic52 = new InfoClassifier(id++);
        MultiBoostAB mba5 = new MultiBoostAB();
        mba5.setClassifier(new REPTree());
        ic52.classifier = mba5;
        ic52.name = "MultiboostAB, RepTree";
        ic52.properties.manageMissingValues = true;
        cls.add(ic52);
        //Bagging Decision Stump
        InfoClassifier ic53 = new InfoClassifier(id++);
        Bagging bag = new Bagging();
        bag.setClassifier(new DecisionStump());
        ic53.classifier = bag;
        ic53.name = "Bagging Decision Stump";
        ic53.properties.manageMissingValues = true;
        cls.add(ic53);
        //Bagging Decision Table
        InfoClassifier ic54 = new InfoClassifier(id++);
        Bagging bag1 = new Bagging();
        bag1.setClassifier(new DecisionTable());
        ic54.classifier = bag1;
        ic54.name = "Bagging Decision Table";
        ic54.properties.manageMissingValues = true;
        cls.add(ic54);
        //Bagging HyperPipes
        InfoClassifier ic55 = new InfoClassifier(id++);
        Bagging bag2 = new Bagging();
        bag2.setClassifier(new HyperPipes());
        ic55.classifier = bag2;
        ic55.name = "Bagging Hyper Pipes";
        cls.add(ic55);
        //Bagging Naive Bayes
        InfoClassifier ic56 = new InfoClassifier(id++);
        Bagging bag3 = new Bagging();
        bag3.setClassifier(new NaiveBayes());
        ic56.classifier = bag3;
        ic56.name = "Bagging Naive Bayes";
        ic56.properties.manageMissingValues = true;
        cls.add(ic56);
        //Bagging PART
        InfoClassifier ic57 = new InfoClassifier(id++);
        Bagging bag4 = new Bagging();
        bag4.setClassifier(new PART());
        ic57.classifier = bag4;
        ic57.name = "Bagging PART";
        ic57.properties.manageMissingValues = true;
        cls.add(ic57);
        //Bagging RandomTree
        InfoClassifier ic58 = new InfoClassifier(id++);
        Bagging bag5 = new Bagging();
        RandomTree rtr2 = new RandomTree();
        rtr2.setMinNum(2);
        rtr2.setAllowUnclassifiedInstances(true);
        bag5.setClassifier(rtr2);
        ic58.classifier = bag5;
        ic58.name = "Bagging RandomTree";
        ic58.properties.manageMissingValues = true;
        cls.add(ic58);
        //NNge
        InfoClassifier ic59 = new InfoClassifier(id++);
        NNge nng = new NNge();
        nng.setNumFoldersMIOption(1);
        nng.setNumAttemptsOfGeneOption(5);
        ic59.classifier = nng;
        ic59.name = "NNge";        
        cls.add(ic59);
        //OrdinalClassClassifier J48
        InfoClassifier ic60 = new InfoClassifier(id++);
        OrdinalClassClassifier occ = new OrdinalClassClassifier();
        occ.setClassifier(new J48());
        ic60.classifier = occ;
        ic60.name = "OrdinalClassClassifier J48";
        ic60.properties.manageMissingValues = true;
        cls.add(ic60);
        //Hyper Pipes
        InfoClassifier ic61 = new InfoClassifier(id++);
        ic61.classifier = new HyperPipes();
        ic61.name = "Hyper Pipes";
        cls.add(ic61);
        //Classification via Regression, M5P used by default
        InfoClassifier ic62 = new InfoClassifier(id++);
        ic62.classifier = new ClassificationViaRegression();
        ic62.name = "Classification ViaRegression, M5P";
        ic62.properties.requireNumericDataset = true;
        cls.add(ic62);
        //RBF Network
        InfoClassifier ic64 = new InfoClassifier(id++);
        RBFNetwork rbf = new RBFNetwork();
        rbf.setRidge(0.00000001); //10^-8
        rbf.setNumClusters(original.trainingSet.numAttributes() / 2);
        ic64.classifier = rbf;
        ic64.name = "RBF Network";
        ic64.properties.requireNumericDataset = true;
        if(!original.properties.isStandardized){
            ic64.properties.compatibleWithDataset = false;
        }
        cls.add(ic64);
        //RandomTree
        InfoClassifier ic66 = new InfoClassifier(id++);
        RandomTree rtr = new RandomTree();
        rtr.setMinNum(2);
        rtr.setAllowUnclassifiedInstances(true);
        ic66.classifier = rtr;
        ic66.name = "Random Tree";
        ic66.properties.manageMissingValues = true;
        cls.add(ic66);
        //RepTree
        InfoClassifier ic67 = new InfoClassifier(id++);
        REPTree rept = new REPTree();
        ic67.classifier = rept;
        ic67.name = "Rep Tree";
        ic67.properties.manageMissingValues = true;
        cls.add(ic67);
        //Decision Stump
        InfoClassifier ic68 = new InfoClassifier(id++);
        ic68.classifier = new DecisionStump();
        ic68.name = "Decision Stump";
        ic68.properties.manageMissingValues = true;
        cls.add(ic68);
        //OneR
        InfoClassifier ic69 = new InfoClassifier(id++);
        ic69.classifier = new OneR();
        ic69.name = "OneR";
        ic69.properties.requireNominalDataset = true;
        ic69.properties.manageMissingValues = true;
        cls.add(ic69);
        //LWL
        InfoClassifier ic71 = new InfoClassifier(id++);
        ic71.classifier = new LWL();
        ic71.name = "LWL";
        ic71.properties.manageMissingValues = true;
        cls.add(ic71);
        //Bagging LWL
        InfoClassifier ic72 = new InfoClassifier(id++);
        Bagging bg72 = new Bagging();
        bg72.setClassifier(new LWL());
        ic72.classifier = bg72;
        ic72.name = "Bagging LWL";
        ic72.properties.manageMissingValues = true;
        cls.add(ic72);        
        
        //Decorate
        InfoClassifier ic73 = new InfoClassifier(id++);
        ic73.classifier = new Decorate();
        ic73.name = "Decorate";
        ic73.properties.manageMissingValues = true;
        ic73.properties.minNumTrainingInstances = 15;
        this.indexDecorate = id - 1;
        cls.add(ic73);
        
        //Dagging
        InfoClassifier ic74 = new InfoClassifier(id++);
        Dagging dng = new Dagging();
        dng.setClassifier(new SMO());
        dng.setNumFolds(4);
        ic74.classifier = dng;        
        ic74.properties.requireNumericDataset = true;
        ic74.properties.manageMultiClass = false;
        ic74.name = "Dagging SMO";
        cls.add(ic74);
        //IB1
        InfoClassifier ic75 = new InfoClassifier(id++);
        ic75.classifier = new IB1();
        ic75.properties.manageMissingValues = true;
        ic75.name = "IB1";
        cls.add(ic75);
        //Simple Logistic
        InfoClassifier ic76 = new InfoClassifier(id++);
        ic76.classifier = new SimpleLogistic();
        ic76.properties.requireNumericDataset = true;
        ic76.name = "Simple Logistic";
        cls.add(ic76);
        //VFI
        InfoClassifier ic77 = new InfoClassifier(id++);
        ic77.classifier = new VFI();
        ic77.properties.manageMissingValues = true;
        ic77.name = "VFI";
        cls.add(ic77);        
        
        //check if classifier satisfies the constraints of min #instances
        checkMinNumInstanes(cls, original.trainingSet);
        */
        return cls;
    }
    
    private boolean checkMinInstances(Instances data, int min){       
        
        for(int iii = 0; iii < 4; iii++){
            Instances train4 = data.trainCV(4, iii);
            if(train4.numInstances() < min){
                return false;
            }
        }        
        for(int iii = 0; iii < 10; iii++){
            Instances train10 = data.trainCV(10, iii);
            if(train10.numInstances() < min){
                return false;
            }
        }
        return true;
    }
    
    private void checkMinNumInstanes(List<InfoClassifier> classifiers, Instances data){
        
        int num = classifiers.size();
        for(int iii = 0; iii < num; iii++){
            InfoClassifier current = classifiers.get(iii);
            current.properties.satisfyMinNumInstances = checkMinInstances(data, 
                    current.properties.minNumTrainingInstances);
        }
    }    
    
    public int getNumClassifiers(){
        return classifiers.size();
    }
    
}
