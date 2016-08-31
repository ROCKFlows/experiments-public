package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.ExperimentsUtil;
import fr.unice.i3s.rockflows.experiments.InfoClassifier;
import fr.unice.i3s.rockflows.experiments.TestResult;
import fr.unice.i3s.rockflows.experiments.automatictest.AutomaticTest;
import fr.unice.i3s.rockflows.experiments.automatictest.IntermediateExcelFile;
import weka.classifiers.functions.LibSVM;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * @author lupin
 */
public class AlgoExecutor implements Callable<Boolean> {

    private TestResult res;
    private String conxuntosPath = "";
    private String conxuntosKFoldPath = "";
    IntermediateExcelFile excFile;

    public AlgoExecutor(TestResult res, String conxuntosPath, String conxuntosKFoldPath,
                        IntermediateExcelFile exc) {

        this.res = res;
        this.conxuntosPath = conxuntosPath;
        this.conxuntosKFoldPath = conxuntosKFoldPath;
        this.excFile = exc;
    }

    @Override
    public Boolean call() throws Exception {

        try {
            this.execute();
        } catch (Exception exc) {

            String clName = res.infoclassifier.name;
            File fff = new File(excFile.path + clName + ".error");
            Writer www = new FileWriter(fff);
            www.append(clName).append(" :BEGIN, ");
            exc.printStackTrace(new PrintWriter(www));
            www.append(",:END ").append(clName);
            www.append(exc.getMessage());
            www.flush();
            www.close();
            return false;
        }

        return true;

    }

    public void execute() throws Exception {

        if (!res.infoclassifier.isUsable()) {
            excFile.writeNotCompatible(res);
            return;
        }
        //check if this classifier is compatible with this dataset
        if (!res.infoclassifier.isCompatibleWithDataset(res.dataset)) {
            excFile.writeNotCompatible(res);
            return;
        }

        //check parameter tuning
        InfoClassifier classif = res.infoclassifier;
        if (classif.classifier instanceof LibSVM) {
            if (classif.isUsable()) {
                ExperimentsUtil.evaluateParameters(res, this.conxuntosPath);
                excFile.writeSvmBestValues(excFile.sheet, classif);
                excFile.writeSvmBestValues(excFile.sheet10, classif);
            }
        }

        AutomaticTest.compute4Folds(res,
                this.conxuntosKFoldPath);
        //write results of 4 folds cross validation / unique test dataset
        excFile.write4FoldsResults(res);
        //write avg sheets
        excFile.writeSheetResults(res);

        AutomaticTest.compute10folds(res);
        //write results of 10 folds cross validation / unique test dataset
        excFile.write10FoldsResults(res);
        excFile.writeSheet10Results(res);

    }

}
