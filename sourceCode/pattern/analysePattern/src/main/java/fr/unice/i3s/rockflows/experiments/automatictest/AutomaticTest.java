/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.datamining.ResultsAnalyzer;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Luca
 */
public final class AutomaticTest {

    public static int getIdFromPath(String path) {

        String tmp = path.substring(path.lastIndexOf("-") + 1);
        return Integer.parseInt(tmp.substring(0, tmp.lastIndexOf(".")));
    }

    //N.B: classifiers are identified by the same position in the different sheets
    public static List<TestResult> readValues4(IntermediateExcelFile file) {

        List<TestResult> res = new ArrayList<>();
        Iterator<Row> rows = file.sheet.rowIterator();

        //read id of pre-processer
        int id = getIdFromPath(file.path);

        //1st row: read dataset properties
        Row first = rows.next();
        String dataProp = ExcelUtils.getStringValue((XSSFCell) first.getCell(6));

        //3rd row read pre-proc time and pre-proc properties

        rows.next();
        Row third = rows.next();
        double preProcTime = ExcelUtils.getDoubleValue((XSSFCell) third.getCell(10));
        String preProcProp = ExcelUtils.getStringValue((XSSFCell) third.getCell(3));

        //4th row contains titles (to fold)
        rows.next();

        int algoId = 0;
        while (rows.hasNext()) {

            TestResult currentRes = new TestResult();
            currentRes.preProcId = id;
            currentRes.preProcTime = (long) preProcTime;
            currentRes.algoId = algoId++;
            currentRes.dataProp = dataProp;
            currentRes.preProcProp = preProcProp;

            currentRes.accuracy4f = new double[4];
            currentRes.trainingTime4f = new double[4];
            currentRes.testTime4f = new double[4];
            currentRes.totalTime4f = new double[4];
            currentRes.modelSize4f = new double[4];

            Row res4 = rows.next(); //Result row
            int rowId = res4.getRowNum();

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
                XSSFCell resTrainTimeCell = (XSSFCell) res4.getCell(file.trainingTimeColumn);
                XSSFCell resTestTimeCell = (XSSFCell) res4.getCell(file.testTimeColumn);
                XSSFCell resModelSizeCell = (XSSFCell) res4.getCell(file.modelSizeColumn);
                currentRes.accuracyAvg = ExcelUtils.getDoubleValue(resAccuracyCell);
                currentRes.trainingTimeAvg = ExcelUtils.getIntValue(resTrainTimeCell);
                currentRes.testTimeAvg = ExcelUtils.getIntValue(resTestTimeCell);
                currentRes.modelSizeAvg = ExcelUtils.getIntValue(resModelSizeCell);

                XSSFRow acc4Row = file.acc4Sheet.getRow(rowId);
                XSSFRow train4Row = file.train4Sheet.getRow(rowId);
                XSSFRow test4Row = file.test4Sheet.getRow(rowId);
                XSSFRow size4Row = file.size4Sheet.getRow(rowId);
                XSSFCell resValue1Cell = acc4Row.getCell(file.value1Column);
                XSSFCell resValue2Cell = acc4Row.getCell(file.value2Column);
                XSSFCell resValue3Cell = acc4Row.getCell(file.value3Column);
                XSSFCell resValue4Cell = acc4Row.getCell(file.value4Column);
                currentRes.accuracy4f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.accuracy4f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.accuracy4f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.accuracy4f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                //read training time 10-folds array values
                resValue1Cell = train4Row.getCell(file.value1Column);
                resValue2Cell = train4Row.getCell(file.value2Column);
                resValue3Cell = train4Row.getCell(file.value3Column);
                resValue4Cell = train4Row.getCell(file.value4Column);
                currentRes.trainingTime4f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.trainingTime4f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.trainingTime4f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.trainingTime4f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                //read test time 10-folds array values
                resValue1Cell = test4Row.getCell(file.value1Column);
                resValue2Cell = test4Row.getCell(file.value2Column);
                resValue3Cell = test4Row.getCell(file.value3Column);
                resValue4Cell = test4Row.getCell(file.value4Column);
                currentRes.testTime4f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.testTime4f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.testTime4f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.testTime4f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                //read model size 10-folds array values
                resValue1Cell = size4Row.getCell(file.value1Column);
                resValue2Cell = size4Row.getCell(file.value2Column);
                resValue3Cell = size4Row.getCell(file.value3Column);
                resValue4Cell = size4Row.getCell(file.value4Column);
                currentRes.modelSize4f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.modelSize4f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.modelSize4f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.modelSize4f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
            } else {
                currentRes.compatible = false;
            }

            if (currentRes.compatible) {
                res.add(currentRes);
            }

        }

        return res;
    }

    //N.B: classifiers are identified by the same position in the different sheets
    public static List<TestResult> readValues10(IntermediateExcelFile file) {

        List<TestResult> res = new ArrayList<>();
        Iterator<Row> rows = file.sheet10.rowIterator();

        //read id of pre-processer
        int id = getIdFromPath(file.path);

        //1st row: read dataset properties
        Row first = rows.next();
        String dataProp = ExcelUtils.getStringValue((XSSFCell) first.getCell(6));

        //3rd row read pre-proc time and pre-proc properties

        rows.next();
        Row third = rows.next();
        double preProcTime = ExcelUtils.getDoubleValue((XSSFCell) third.getCell(10));
        String preProcProp = ExcelUtils.getStringValue((XSSFCell) third.getCell(3));

        //4th row contains titles (to fold)
        rows.next();

        int algoId = 0;
        while (rows.hasNext()) {

            TestResult currentRes = new TestResult();
            currentRes.preProcId = id;
            currentRes.preProcTime = (long) preProcTime;
            currentRes.algoId = algoId++;
            currentRes.dataProp = dataProp;
            currentRes.preProcProp = preProcProp;

            currentRes.accuracy10f = new double[10];
            currentRes.trainingTime10f = new double[10];
            currentRes.testTime10f = new double[10];
            currentRes.totalTime10f = new double[10];
            currentRes.modelSize10f = new double[10];

            Row res10 = rows.next(); //Result row
            int rowId = res10.getRowNum();

            //read algo name
            XSSFCell resAlgoCell = (XSSFCell) res10.getCell(file.algorithmColumn);
            if (resAlgoCell == null) {
                continue;
            }
            currentRes.algoName = ExcelUtils.getStringValue(resAlgoCell);

            XSSFCell resCompatibleCell = (XSSFCell) res10.getCell(file.algoCompatible);
            if (resCompatibleCell == null) {
                continue;
            }
            String compatible = ExcelUtils.getStringValue(resCompatibleCell);

            if (compatible.equals("y")) {
                currentRes.compatible = true;
                //read avg values
                XSSFCell resAccuracyCell = (XSSFCell) res10.getCell(file.accuracyColumn);
                XSSFCell resTrainTimeCell = (XSSFCell) res10.getCell(file.trainingTimeColumn);
                XSSFCell resTestTimeCell = (XSSFCell) res10.getCell(file.testTimeColumn);
                XSSFCell resModelSizeCell = (XSSFCell) res10.getCell(file.modelSizeColumn);
                currentRes.accuracyAvg10 = ExcelUtils.getDoubleValue(resAccuracyCell);
                currentRes.trainingTimeAvg10 = ExcelUtils.getIntValue(resTrainTimeCell);
                currentRes.testTimeAvg10 = ExcelUtils.getIntValue(resTestTimeCell);
                currentRes.modelSizeAvg10 = ExcelUtils.getIntValue(resModelSizeCell);
                //get rows from other sheets
                XSSFRow acc10Row = file.acc10Sheet.getRow(rowId);
                XSSFRow train10Row = file.train10Sheet.getRow(rowId);
                XSSFRow test10Row = file.test10Sheet.getRow(rowId);
                XSSFRow size10Row = file.size10Sheet.getRow(rowId);
                //read accuracy 10-folds array values
                XSSFCell resValue1Cell = acc10Row.getCell(file.value1Column);
                XSSFCell resValue2Cell = acc10Row.getCell(file.value2Column);
                XSSFCell resValue3Cell = acc10Row.getCell(file.value3Column);
                XSSFCell resValue4Cell = acc10Row.getCell(file.value4Column);
                XSSFCell resValue5Cell = acc10Row.getCell(file.value5Column);
                XSSFCell resValue6Cell = acc10Row.getCell(file.value6Column);
                XSSFCell resValue7Cell = acc10Row.getCell(file.value7Column);
                XSSFCell resValue8Cell = acc10Row.getCell(file.value8Column);
                XSSFCell resValue9Cell = acc10Row.getCell(file.value9Column);
                XSSFCell resValue10Cell = acc10Row.getCell(file.value10Column);
                currentRes.accuracy10f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.accuracy10f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.accuracy10f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.accuracy10f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                currentRes.accuracy10f[4] = ExcelUtils.getDoubleValue(resValue5Cell);
                currentRes.accuracy10f[5] = ExcelUtils.getDoubleValue(resValue6Cell);
                currentRes.accuracy10f[6] = ExcelUtils.getDoubleValue(resValue7Cell);
                currentRes.accuracy10f[7] = ExcelUtils.getDoubleValue(resValue8Cell);
                currentRes.accuracy10f[8] = ExcelUtils.getDoubleValue(resValue9Cell);
                currentRes.accuracy10f[9] = ExcelUtils.getDoubleValue(resValue10Cell);
                //read training time 10-folds array values
                resValue1Cell = train10Row.getCell(file.value1Column);
                resValue2Cell = train10Row.getCell(file.value2Column);
                resValue3Cell = train10Row.getCell(file.value3Column);
                resValue4Cell = train10Row.getCell(file.value4Column);
                resValue5Cell = train10Row.getCell(file.value5Column);
                resValue6Cell = train10Row.getCell(file.value6Column);
                resValue7Cell = train10Row.getCell(file.value7Column);
                resValue8Cell = train10Row.getCell(file.value8Column);
                resValue9Cell = train10Row.getCell(file.value9Column);
                resValue10Cell = train10Row.getCell(file.value10Column);
                currentRes.trainingTime10f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.trainingTime10f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.trainingTime10f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.trainingTime10f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                currentRes.trainingTime10f[4] = ExcelUtils.getDoubleValue(resValue5Cell);
                currentRes.trainingTime10f[5] = ExcelUtils.getDoubleValue(resValue6Cell);
                currentRes.trainingTime10f[6] = ExcelUtils.getDoubleValue(resValue7Cell);
                currentRes.trainingTime10f[7] = ExcelUtils.getDoubleValue(resValue8Cell);
                currentRes.trainingTime10f[8] = ExcelUtils.getDoubleValue(resValue9Cell);
                currentRes.trainingTime10f[9] = ExcelUtils.getDoubleValue(resValue10Cell);
                //read test time 10-folds array values
                resValue1Cell = test10Row.getCell(file.value1Column);
                resValue2Cell = test10Row.getCell(file.value2Column);
                resValue3Cell = test10Row.getCell(file.value3Column);
                resValue4Cell = test10Row.getCell(file.value4Column);
                resValue5Cell = test10Row.getCell(file.value5Column);
                resValue6Cell = test10Row.getCell(file.value6Column);
                resValue7Cell = test10Row.getCell(file.value7Column);
                resValue8Cell = test10Row.getCell(file.value8Column);
                resValue9Cell = test10Row.getCell(file.value9Column);
                resValue10Cell = test10Row.getCell(file.value10Column);
                currentRes.testTime10f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.testTime10f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.testTime10f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.testTime10f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                currentRes.testTime10f[4] = ExcelUtils.getDoubleValue(resValue5Cell);
                currentRes.testTime10f[5] = ExcelUtils.getDoubleValue(resValue6Cell);
                currentRes.testTime10f[6] = ExcelUtils.getDoubleValue(resValue7Cell);
                currentRes.testTime10f[7] = ExcelUtils.getDoubleValue(resValue8Cell);
                currentRes.testTime10f[8] = ExcelUtils.getDoubleValue(resValue9Cell);
                currentRes.testTime10f[9] = ExcelUtils.getDoubleValue(resValue10Cell);
                //read model size 10-folds array values
                resValue1Cell = size10Row.getCell(file.value1Column);
                resValue2Cell = size10Row.getCell(file.value2Column);
                resValue3Cell = size10Row.getCell(file.value3Column);
                resValue4Cell = size10Row.getCell(file.value4Column);
                resValue5Cell = size10Row.getCell(file.value5Column);
                resValue6Cell = size10Row.getCell(file.value6Column);
                resValue7Cell = size10Row.getCell(file.value7Column);
                resValue8Cell = size10Row.getCell(file.value8Column);
                resValue9Cell = size10Row.getCell(file.value9Column);
                resValue10Cell = size10Row.getCell(file.value10Column);
                currentRes.modelSize10f[0] = ExcelUtils.getDoubleValue(resValue1Cell);
                currentRes.modelSize10f[1] = ExcelUtils.getDoubleValue(resValue2Cell);
                currentRes.modelSize10f[2] = ExcelUtils.getDoubleValue(resValue3Cell);
                currentRes.modelSize10f[3] = ExcelUtils.getDoubleValue(resValue4Cell);
                currentRes.modelSize10f[4] = ExcelUtils.getDoubleValue(resValue5Cell);
                currentRes.modelSize10f[5] = ExcelUtils.getDoubleValue(resValue6Cell);
                currentRes.modelSize10f[6] = ExcelUtils.getDoubleValue(resValue7Cell);
                currentRes.modelSize10f[7] = ExcelUtils.getDoubleValue(resValue8Cell);
                currentRes.modelSize10f[8] = ExcelUtils.getDoubleValue(resValue9Cell);
                currentRes.modelSize10f[9] = ExcelUtils.getDoubleValue(resValue10Cell);

            } else {
                currentRes.compatible = false;
            }
            if (currentRes.compatible) {
                res.add(currentRes);
            }
        }

        return res;
    }

    public static void computeResults(List<TestResult> results,
                                      double alpha, String pathExcel, boolean status, boolean folds4)
            throws Exception {

        //set rank for each list of final results        
        ResultsAnalyzer.setRankModelSize(results, alpha, folds4);
        ResultsAnalyzer.setRankTotalTime(results, alpha, folds4);
        ResultsAnalyzer.setRankTrainTime(results, alpha, folds4);
        ResultsAnalyzer.setRankAccuracy(results, alpha, folds4);

        if (!status) {
            //write final excel file best accuracy   
            OnlyRankExc finalExc = new OnlyRankExc(pathExcel);
            finalExc.writeFinalExcelFile(pathExcel, results, folds4);
        } else {
            //write final excel file best accuracy   
            RankAndStatusExc finalExc = new RankAndStatusExc(pathExcel);
            finalExc.writeFinalExcelFile(pathExcel, results, folds4);
        }
    }

    public static void computeResultsFinal(List<TestResult> results,
                                           double alpha, String pathExcel, boolean status, boolean folds4)
            throws Exception {

        //set rank for each list of final results        
        ResultsAnalyzer.setRankModelSize(results, alpha, folds4);
        ResultsAnalyzer.setRankTotalTime(results, alpha, folds4);
        ResultsAnalyzer.setRankTrainTime(results, alpha, folds4);
        ResultsAnalyzer.setRankAccuracy(results, alpha, folds4);

        if (!status) {
            //write final excel file best accuracy   
            FinalOnlyRankExc finalExc = new FinalOnlyRankExc(pathExcel);
            finalExc.writeFinalExcelFile(pathExcel, results, folds4);
        } else {
            //write final excel file best accuracy   
            RankAndStatusExc finalExc = new RankAndStatusExc(pathExcel);
            finalExc.writeFinalExcelFile(pathExcel, results, folds4);
        }
    }

}
