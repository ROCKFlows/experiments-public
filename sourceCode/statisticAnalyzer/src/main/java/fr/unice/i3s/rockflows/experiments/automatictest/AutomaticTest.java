/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Luca
 */
public final class AutomaticTest {

    //N.B: classifiers are identified by the same position in the different sheets
    public static List<TestResult> readValues(IntermediateExcelFile file, int preProcId,
                                              XSSFSheet sheet) {

        List<TestResult> res = new ArrayList<>();
        Iterator<Row> rows = sheet.rowIterator();

        //1st row: read dataset properties
        rows.next();
        //3rd row read pre-proc time and pre-proc properties        
        rows.next();
        rows.next();
        //4th row contains titles (to fold)
        rows.next();

        int algoId = 0;
        while (rows.hasNext()) {

            TestResult currentRes = new TestResult();
            currentRes.algoId = algoId++;
            currentRes.preProcId = preProcId;

            Row res4 = rows.next(); //Result row

            //read algo name
            XSSFCell resAlgoCell = (XSSFCell) res4.getCell(file.algorithmColumn);
            if (resAlgoCell == null) {
                continue;
            }
            currentRes.algoName = ExcelUtils.getStringValue(resAlgoCell);


            XSSFCell resCompatibleCell = (XSSFCell) res4.getCell(file.algoCompatible);
            if (resCompatibleCell == null) {
                continue;
            }
            String compatible = ExcelUtils.getStringValue(resCompatibleCell);

            if (compatible.equals("y")) {
                currentRes.compatible = true;
                //read avg values
                XSSFCell resAccuracyCell = (XSSFCell) res4.getCell(file.accuracyColumn);
                currentRes.accuracyAvg = ExcelUtils.getDoubleValue(resAccuracyCell);
                XSSFCell resTrainCell = (XSSFCell) res4.getCell(file.trainingTimeColumn);
                currentRes.trainTimeAvg = ExcelUtils.getDoubleValue(resTrainCell);
                XSSFCell resTestCell = (XSSFCell) res4.getCell(file.testTimeColumn);
                currentRes.testTimeAvg = ExcelUtils.getDoubleValue(resTestCell);
                XSSFCell resRamCell = (XSSFCell) res4.getCell(file.modelSizeColumn);
                currentRes.ramAvg = ExcelUtils.getDoubleValue(resRamCell);
            } else {
                currentRes.compatible = false;
            }

            res.add(currentRes);

        }

        return res;
    }

    public static List<TestResult> getBestResults(List<List<TestResult>> results)
            throws Exception {

        List<TestResult> best = new ArrayList<>();
        //get num algo
        int numAlgo = results.get(0).size();
        //get size of pre-processers
        int numPrep = results.size();
        for (int id = 0; id < numAlgo; id++) {
            //for each classifier, get best value in the best value list in output    
            TestResult trBest = results.get(0).get(id);
            for (int index = 1; index < numPrep; index++) {
                TestResult current = results.get(index).get(id);
                //check if avg >
                if (current.accuracyAvg > trBest.accuracyAvg) {
                    trBest = current;
                }
            }
            best.add(trBest);
        }
        return best;
    }

}
