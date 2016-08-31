package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.ExperimentsUtil;
import fr.unice.i3s.rockflows.experiments.InfoClassifier;
import fr.unice.i3s.rockflows.experiments.TestResult;

/**
 * @author Luca
 */
public final class AutomaticTest {

    public static void compute4Folds(TestResult res, String conxuntosKFold) throws Exception {
        try {
            ExperimentsUtil.crossValidation4Folds(res, conxuntosKFold);
        } catch (Exception exx) {
            res.infoclassifier.properties.compatibleWithDataset = false;
        }
    }

    /**
     * For each classifier, perform the following operations.
     * <ol>
     * <li>execute the needed pre-processing both on the training set and test set</li>
     * <li>train the classifier</li>
     * <li>test the classifier</li>
     * <li>save the accuracy, execution training time and execution test time on the relative fields of the
     * {@link InfoClassifier} class of the classifier and into the Excel file</li>
     * </ol>
     */
    public static void compute10folds(TestResult res) throws Exception {

        //perform 10-folds cross-validation, for the intermediate Excel file
        try {
            ExperimentsUtil.crossValidation10Folds(res);
        } catch (Exception exx) {
            res.infoclassifier.properties.compatibleWithDataset = false;
        }

    }
}
