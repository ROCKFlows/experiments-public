/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.datamining.DataMiningUtils;
import fr.unice.i3s.rockflows.experiments.datamining.InfoClassifier;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;

/**
 * @author Luca
 */
public final class AutomaticTest {

    public static void compute4Folds(TestResult res,
                                     String conxuntosKFold, boolean paper) throws Exception {

        //determine strategy to compute the Avg metrics
        if (paper) {
            if (res.dataset.unique) {
                //4-folds
                DataMiningUtils.crossValidation4Folds(res, conxuntosKFold);
            } else {
                //train and test set separately
                DataMiningUtils.trainAndTestClassifier(res);
            }
        } else {
            if (!res.dataset.unique) {
                //merge train and test set
                DataMiningUtils.mergeDataset(res.dataset);
            }
        }

    }

    /**
     * For each classifier, perform the following operations:
     * <ol>
     * <li>execute the needed pre-processing both on the training set and test set</li>
     * <li>train the classifier</li>
     * <li>test the classifier</li>
     * <li>save the accuracy, execution training time and execution test time on the relative fields of the
     * {@link InfoClassifier} class of the classifier and into the Excel file</li>
     * </ol>
     */
    public static void compute10folds(TestResult res,
                                      String conxuntosKFold, boolean paper) throws Exception {

        //perform 10-folds cross-validation, for the intermediate Excel file
        try {
            //only for Bagging NBTree and ImageSegmentation database: Weka gives a null pointer 
            //Exception during the 10 folds CV of the training set (for significant different)
            DataMiningUtils.crossValidation10Folds(res);
        } catch (Exception exx) {
            res.infoclassifier.properties.compatibleWithDataset = false;
        }
        if (res.dataset.unique) {
            DataMiningUtils.computeAvg(res);
        }

    }
}
