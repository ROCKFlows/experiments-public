package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.AnalyseExcelFile;
import fr.unice.i3s.rockflows.experiments.datamining.FoldsEnum;
import fr.unice.i3s.rockflows.experiments.datamining.ResWorkflow;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * @author lupin
 */
public class TestExecutor implements Callable<String> {

    String pathSource = "";
    ResWorkflow workflow;
    FoldsEnum typeFolds;
    String pathOut = "";

    public TestExecutor(String pathSource, String classifierName, int workflowPid, FoldsEnum type,
                        String pathOut) throws Exception {
        this.pathSource = pathSource;
        this.typeFolds = type;
        workflow = new ResWorkflow();
        workflow.classifierName = classifierName;
        workflow.preProcId = workflowPid;
        this.pathOut = pathOut;
    }

    public String executeTest() throws Exception {

        switch (this.typeFolds) {
            case CV4: {
                String pathFinal = this.pathSource + "Final-Analysis-4Folds.xlsx";
                AnalyseExcelFile exc = new AnalyseExcelFile(pathFinal);
                if (exc.readBest(this.workflow)) {
                    return this.pathSource;
                }
                break;
            }
            case CV10: {
                String pathFinal = this.pathSource + "Final-Analysis-10Folds.xlsx";
                AnalyseExcelFile exc = new AnalyseExcelFile(pathFinal);
                if (exc.readBest(this.workflow)) {
                    return this.pathSource;
                }
                break;
            }
            case Both: {
                String pathFinal4 = this.pathSource + "Final-Analysis-4Folds.xlsx";
                AnalyseExcelFile exc4 = new AnalyseExcelFile(pathFinal4);
                String pathFinal10 = this.pathSource + "Final-Analysis-10Folds.xlsx";
                AnalyseExcelFile exc10 = new AnalyseExcelFile(pathFinal10);
                if (exc4.readBest(this.workflow) && exc10.readBest(workflow)) {
                    return this.pathSource;
                }
                break;
            }
        }
        return "";
    }

    @Override
    public String call() throws Exception {
        try {
            return this.executeTest();
        } catch (Exception ex) {
            File fff = new File(pathOut + "error");
            Writer www = new FileWriter(fff);
            www.append(" :BEGIN, ");
            ex.printStackTrace(new PrintWriter(www));
            www.append(",:END ");
            www.append(ex.getMessage());
            www.flush();
            www.close();
            return "";
        }
    }

}
