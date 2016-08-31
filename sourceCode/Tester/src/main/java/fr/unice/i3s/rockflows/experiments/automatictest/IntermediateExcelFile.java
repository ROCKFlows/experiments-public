package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.datamining.Dataset;
import fr.unice.i3s.rockflows.experiments.InfoClassifier;
import fr.unice.i3s.rockflows.experiments.TestResult;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.classifiers.functions.LibSVM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Luca
 */
public class IntermediateExcelFile {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;
    public XSSFSheet sheet10;
    public XSSFSheet acc10Sheet;
    public XSSFSheet train10Sheet;
    public XSSFSheet test10Sheet;
    public XSSFSheet size10Sheet;
    public XSSFSheet acc4Sheet;
    public XSSFSheet train4Sheet;
    public XSSFSheet test4Sheet;
    public XSSFSheet size4Sheet;
    public String path;
    public boolean alreadyExisting = false;
    //indices of rows and columns of the file
    public int rowNumTrainingInstances = 0;
    public int columnTitleNumTrainingInstances = 2;
    public int columnNumTrainingInstances = 3;
    public int columnTitleDatasetProperties = 5;
    public int columnDatasetProperties = 6;
    public int svmBestParameterTitle = 13;
    public int svmCParameterTitle = 14;
    public int svmGammaParameterTitle = 15;
    public int rowNumAttributes = 1;
    public int columnTitleNumAttributes = 2;
    public int columnNumAttributes = 3;
    public int rowNumPreprocesserApplied = 2;
    public int columnTitlePreProcesserApplied = 2;
    public int columnNamePreProcesserApplied = 3;
    public int columnTitleAttributesUsed = 6;
    public int columnValueAttributesUsed = 7;
    public int columnTitlePreProcesserTime = 9;
    public int columnPreProcesserTime = 10;
    public int svmCValue = 14;
    public int svmGammaValue = 15;
    public int rowTitleIndex = 4;
    public int rowValueOffset = 6;
    public int start = 2;
    public int algorithmColumn = start++;
    public int algoCompatible = start++;
    public int accuracyColumn = start++;
    public int trainingTimeColumn = start++;
    public int testTimeColumn = start++;
    public int modelSizeColumn = start++;
    public int trainingTimeTextColumn = start++;
    public int testTimeTextColumn = start++;

    //sheet 10-folds result accuracy
    public int value1Column = 3;
    public int value2Column = 4;
    public int value3Column = 5;
    public int value4Column = 6;
    public int value5Column = 7;
    public int value6Column = 8;
    public int value7Column = 9;
    public int value8Column = 10;
    public int value9Column = 11;
    public int value10Column = 12;


    public IntermediateExcelFile(String path, Dataset dataset)
            throws FileNotFoundException, IOException, InvalidFormatException {

        //check if the file already exists, maybe it contains already the results
        //for some classifiers
        File excFile = new File(path);
        if (excFile.exists() && !excFile.isDirectory()) {
            // read existing file and then modify this one
            FileInputStream fis = new FileInputStream(path);
            workbook = (XSSFWorkbook) WorkbookFactory.create(fis);
            fis.close();
            sheet = workbook.getSheet("Results");
            sheet10 = workbook.getSheet("Results10");
            acc10Sheet = workbook.getSheet("accuracy10Folds");
            train10Sheet = workbook.getSheet("train10Folds");
            test10Sheet = workbook.getSheet("test10Folds");
            size10Sheet = workbook.getSheet("size10Folds");
            acc4Sheet = workbook.getSheet("accuracy4Folds");
            train4Sheet = workbook.getSheet("train4Folds");
            test4Sheet = workbook.getSheet("test4Folds");
            size4Sheet = workbook.getSheet("size4Folds");
            alreadyExisting = true;
        } else {
            //create new excel File
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Results");
            sheet10 = workbook.createSheet("Results10");

            createResultSheet(sheet, dataset, true);
            createResultSheet(sheet10, dataset, true);


            //10 folds accuracy results
            acc10Sheet = workbook.createSheet("accuracy10Folds");
            Row rowAcc = acc10Sheet.createRow((short) rowTitleIndex);
            rowAcc.createCell(algorithmColumn).setCellValue("Algorithm");
            rowAcc.createCell(value1Column).setCellValue("Accuracy-1");
            rowAcc.createCell(value2Column).setCellValue("Accuracy-2");
            rowAcc.createCell(value3Column).setCellValue("Accuracy-3");
            rowAcc.createCell(value4Column).setCellValue("Accuracy-4");
            rowAcc.createCell(value5Column).setCellValue("Accuracy-5");
            rowAcc.createCell(value6Column).setCellValue("Accuracy-6");
            rowAcc.createCell(value7Column).setCellValue("Accuracy-7");
            rowAcc.createCell(value8Column).setCellValue("Accuracy-8");
            rowAcc.createCell(value9Column).setCellValue("Accuracy-9");
            rowAcc.createCell(value10Column).setCellValue("Accuracy-10");

            //10 folds training time results
            train10Sheet = workbook.createSheet("train10Folds");
            Row rowTrain = train10Sheet.createRow((short) rowTitleIndex);
            rowTrain.createCell(algorithmColumn).setCellValue("Algorithm");
            rowTrain.createCell(value1Column).setCellValue("Train-1");
            rowTrain.createCell(value2Column).setCellValue("Train-2");
            rowTrain.createCell(value3Column).setCellValue("Train-3");
            rowTrain.createCell(value4Column).setCellValue("Train-4");
            rowTrain.createCell(value5Column).setCellValue("Train-5");
            rowTrain.createCell(value6Column).setCellValue("Train-6");
            rowTrain.createCell(value7Column).setCellValue("Train-7");
            rowTrain.createCell(value8Column).setCellValue("Train-8");
            rowTrain.createCell(value9Column).setCellValue("Train-9");
            rowTrain.createCell(value10Column).setCellValue("Train-10");

            //10 folds test time results
            test10Sheet = workbook.createSheet("test10Folds");
            Row rowTest = test10Sheet.createRow((short) rowTitleIndex);
            rowTest.createCell(algorithmColumn).setCellValue("Algorithm");
            rowTest.createCell(value1Column).setCellValue("Test-1");
            rowTest.createCell(value2Column).setCellValue("Test-2");
            rowTest.createCell(value3Column).setCellValue("Test-3");
            rowTest.createCell(value4Column).setCellValue("Test-4");
            rowTest.createCell(value5Column).setCellValue("Test-5");
            rowTest.createCell(value6Column).setCellValue("Test-6");
            rowTest.createCell(value7Column).setCellValue("Test-7");
            rowTest.createCell(value8Column).setCellValue("Test-8");
            rowTest.createCell(value9Column).setCellValue("Test-9");
            rowTest.createCell(value10Column).setCellValue("Test-10");

            //10 folds model size results
            size10Sheet = workbook.createSheet("size10Folds");
            Row rowSize = size10Sheet.createRow((short) rowTitleIndex);
            rowSize.createCell(algorithmColumn).setCellValue("Algorithm");
            rowSize.createCell(value1Column).setCellValue("Size-1");
            rowSize.createCell(value2Column).setCellValue("Size-2");
            rowSize.createCell(value3Column).setCellValue("Size-3");
            rowSize.createCell(value4Column).setCellValue("Size-4");
            rowSize.createCell(value5Column).setCellValue("Size-5");
            rowSize.createCell(value6Column).setCellValue("Size-6");
            rowSize.createCell(value7Column).setCellValue("Size-7");
            rowSize.createCell(value8Column).setCellValue("Size-8");
            rowSize.createCell(value9Column).setCellValue("Size-9");
            rowSize.createCell(value10Column).setCellValue("Size-10");

            //4 folds accuracy results
            acc4Sheet = workbook.createSheet("accuracy4Folds");
            Row rowAcc4 = acc4Sheet.createRow((short) rowTitleIndex);
            rowAcc4.createCell(algorithmColumn).setCellValue("Algorithm");
            rowAcc4.createCell(value1Column).setCellValue("Accuracy-1");
            rowAcc4.createCell(value2Column).setCellValue("Accuracy-2");
            rowAcc4.createCell(value3Column).setCellValue("Accuracy-3");
            rowAcc4.createCell(value4Column).setCellValue("Accuracy-4");

            //4 folds training time results
            train4Sheet = workbook.createSheet("train4Folds");
            Row rowTrain4 = train4Sheet.createRow((short) rowTitleIndex);
            rowTrain4.createCell(algorithmColumn).setCellValue("Algorithm");
            rowTrain4.createCell(value1Column).setCellValue("Train-1");
            rowTrain4.createCell(value2Column).setCellValue("Train-2");
            rowTrain4.createCell(value3Column).setCellValue("Train-3");
            rowTrain4.createCell(value4Column).setCellValue("Train-4");

            //4 folds test time results
            test4Sheet = workbook.createSheet("test4Folds");
            Row rowTest4 = test4Sheet.createRow((short) rowTitleIndex);
            rowTest4.createCell(algorithmColumn).setCellValue("Algorithm");
            rowTest4.createCell(value1Column).setCellValue("Test-1");
            rowTest4.createCell(value2Column).setCellValue("Test-2");
            rowTest4.createCell(value3Column).setCellValue("Test-3");
            rowTest4.createCell(value4Column).setCellValue("Test-4");

            //4 folds size results
            size4Sheet = workbook.createSheet("size4Folds");
            Row rowSize4 = size4Sheet.createRow((short) rowTitleIndex);
            rowSize4.createCell(algorithmColumn).setCellValue("Algorithm");
            rowSize4.createCell(value1Column).setCellValue("Size-1");
            rowSize4.createCell(value2Column).setCellValue("Size-2");
            rowSize4.createCell(value3Column).setCellValue("Size-3");
            rowSize4.createCell(value4Column).setCellValue("Size-4");

        }
        this.path = path;
    }

    private synchronized void createResultSheet(Sheet res, Dataset dataset, boolean avg) {

        //write number of instances on the excel file
        // Create a title row. Rows are 0 based.
        Row row2 = res.createRow((short) rowNumTrainingInstances);
        // Create cells
        row2.createCell(columnTitleNumTrainingInstances).setCellValue("Num Instances dataset");
        row2.createCell(columnNumTrainingInstances).setCellValue(dataset.trainingSet.numInstances());
        row2.createCell(columnTitleDatasetProperties).setCellValue("Dataset Properties");
        row2.createCell(columnDatasetProperties).setCellValue(dataset.properties.getStringProperties());

        Row row3 = res.createRow((short) rowNumAttributes);
        // Create cells
        row3.createCell(columnTitleNumAttributes).setCellValue("Num Attributes dataset");
        row3.createCell(columnNumAttributes).setCellValue(dataset.trainingSet.numAttributes());


        Row row4 = res.createRow((short) rowNumPreprocesserApplied);
        // Create cells
        row4.createCell(columnTitlePreProcesserApplied).setCellValue("Preprocesser used");
        row4.createCell(columnTitleAttributesUsed).setCellValue("Indices original attributes Used");
        if (dataset.filterUsed == null) {
            row4.createCell(columnNamePreProcesserApplied).setCellValue("No Pre-processing");
            row4.createCell(columnValueAttributesUsed).setCellValue("all attributes used");
        } else {
            row4.createCell(columnNamePreProcesserApplied).setCellValue(dataset.filterUsed.name);
            row4.createCell(columnValueAttributesUsed).setCellValue(
                    dataset.filterUsed.getIndicesAttributesUsed());
        }
        row4.createCell(columnTitlePreProcesserTime).setCellValue("Preprocesser Time (ms)");
        row4.createCell(columnPreProcesserTime).setCellValue(dataset.preprocessingTime);

        // Create a title row. Rows are 0 based.
        Row row = res.createRow((short) rowTitleIndex);
        // Create cells
        Cell cell = row.createCell(algorithmColumn);
        cell.setCellValue("Algorithm");
        row.createCell(algoCompatible).setCellValue("Compatible");
        if (avg) {
            row.createCell(accuracyColumn).setCellValue("Accuracy Avg");
            row.createCell(trainingTimeColumn).setCellValue("Training Time Avg (ms)");
            row.createCell(testTimeColumn).setCellValue("Test Time (ms)");
            row.createCell(modelSizeColumn).setCellValue("Trained Model Size Avg (bytes)");
        } else {
            row.createCell(accuracyColumn).setCellValue("Accuracy");
            row.createCell(trainingTimeColumn).setCellValue("Training Time (ms)");
            row.createCell(testTimeColumn).setCellValue("Test Time (ms)");
            row.createCell(modelSizeColumn).setCellValue("Trained Model Size (bytes)");
        }
    }

    public synchronized void writeSheetResults(TestResult current) throws Exception {

        int rowIndex = current.infoclassifier.id + this.rowValueOffset;
        Row row = this.sheet.createRow((short) (rowIndex));
        row.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        //main sheet, avg accuracy,... for the 4 folds cross validation
        row.createCell(algoCompatible).setCellValue("y");
        row.createCell(accuracyColumn).setCellValue(current.accuracyAvg);
        row.createCell(trainingTimeColumn).setCellValue(current.trainingTimeAvg);
        row.createCell(testTimeColumn).setCellValue(current.testTimeAvg);
        row.createCell(modelSizeColumn).setCellValue(current.modelSizeAvg);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }

    public synchronized void write4FoldsResults(TestResult current) throws Exception {

        int rowIndex = current.infoclassifier.id + this.rowValueOffset;
        //sheet accuracy 4 folds
        Row rowAcc4 = acc4Sheet.createRow((short) (rowIndex));
        rowAcc4.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowAcc4.createCell(value1Column).setCellValue(current.accuracy4f[0]);
        rowAcc4.createCell(value2Column).setCellValue(current.accuracy4f[1]);
        rowAcc4.createCell(value3Column).setCellValue(current.accuracy4f[2]);
        rowAcc4.createCell(value4Column).setCellValue(current.accuracy4f[3]);
        //sheet training time 4 folds
        Row rowTrain4 = train4Sheet.createRow((short) (rowIndex));
        rowTrain4.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowTrain4.createCell(value1Column).setCellValue(current.trainingTime4f[0]);
        rowTrain4.createCell(value2Column).setCellValue(current.trainingTime4f[1]);
        rowTrain4.createCell(value3Column).setCellValue(current.trainingTime4f[2]);
        rowTrain4.createCell(value4Column).setCellValue(current.trainingTime4f[3]);
        //sheet test time 4 folds
        Row rowTest4 = test4Sheet.createRow((short) (rowIndex));
        rowTest4.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowTest4.createCell(value1Column).setCellValue(current.testTime4f[0]);
        rowTest4.createCell(value2Column).setCellValue(current.testTime4f[1]);
        rowTest4.createCell(value3Column).setCellValue(current.testTime4f[2]);
        rowTest4.createCell(value4Column).setCellValue(current.testTime4f[3]);
        //sheet model size 4 folds
        Row rowSize4 = size4Sheet.createRow((short) (rowIndex));
        rowSize4.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowSize4.createCell(value1Column).setCellValue(current.modelSize4f[0]);
        rowSize4.createCell(value2Column).setCellValue(current.modelSize4f[1]);
        rowSize4.createCell(value3Column).setCellValue(current.modelSize4f[2]);
        rowSize4.createCell(value4Column).setCellValue(current.modelSize4f[3]);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }

    public synchronized void writeNotCompatible(TestResult current)
            throws Exception {

        int rowIndex = current.infoclassifier.id + this.rowValueOffset;
        Row row = this.sheet.createRow((short) (rowIndex));
        row.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        row.createCell(algoCompatible).setCellValue("n");

        row = this.sheet10.createRow((short) (rowIndex));
        row.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        row.createCell(algoCompatible).setCellValue("n");

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }

    public synchronized void writeSheet10Results(TestResult current)
            throws Exception {

        int rowIndex = current.infoclassifier.id + this.rowValueOffset;
        Row row = this.sheet10.createRow((short) (rowIndex));
        row.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        //main sheet, avg accuracy,... for the 4 folds cross validation
        row.createCell(algoCompatible).setCellValue("y");
        row.createCell(accuracyColumn).setCellValue(current.accuracyAvg10);
        row.createCell(trainingTimeColumn).setCellValue(current.trainingTimeAvg10);
        row.createCell(testTimeColumn).setCellValue(current.testTimeAvg10);
        row.createCell(modelSizeColumn).setCellValue(current.modelSizeAvg10);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }

    public synchronized void write10FoldsResults(TestResult current)
            throws Exception {

        int rowIndex = current.infoclassifier.id + this.rowValueOffset;

        //sheet accuracy 10 folds
        Row rowAcc = acc10Sheet.createRow((short) (rowIndex));
        rowAcc.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowAcc.createCell(value1Column).setCellValue(current.accuracy10f[0]);
        rowAcc.createCell(value2Column).setCellValue(current.accuracy10f[1]);
        rowAcc.createCell(value3Column).setCellValue(current.accuracy10f[2]);
        rowAcc.createCell(value4Column).setCellValue(current.accuracy10f[3]);
        rowAcc.createCell(value5Column).setCellValue(current.accuracy10f[4]);
        rowAcc.createCell(value6Column).setCellValue(current.accuracy10f[5]);
        rowAcc.createCell(value7Column).setCellValue(current.accuracy10f[6]);
        rowAcc.createCell(value8Column).setCellValue(current.accuracy10f[7]);
        rowAcc.createCell(value9Column).setCellValue(current.accuracy10f[8]);
        rowAcc.createCell(value10Column).setCellValue(current.accuracy10f[9]);
        //sheet training time 10 folds
        Row rowTrain = train10Sheet.createRow((short) (rowIndex));
        rowTrain.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowTrain.createCell(value1Column).setCellValue(current.trainingTime10f[0]);
        rowTrain.createCell(value2Column).setCellValue(current.trainingTime10f[1]);
        rowTrain.createCell(value3Column).setCellValue(current.trainingTime10f[2]);
        rowTrain.createCell(value4Column).setCellValue(current.trainingTime10f[3]);
        rowTrain.createCell(value5Column).setCellValue(current.trainingTime10f[4]);
        rowTrain.createCell(value6Column).setCellValue(current.trainingTime10f[5]);
        rowTrain.createCell(value7Column).setCellValue(current.trainingTime10f[6]);
        rowTrain.createCell(value8Column).setCellValue(current.trainingTime10f[7]);
        rowTrain.createCell(value9Column).setCellValue(current.trainingTime10f[8]);
        rowTrain.createCell(value10Column).setCellValue(current.trainingTime10f[9]);
        //sheet test time 10 folds
        Row rowTest = test10Sheet.createRow((short) (rowIndex));
        rowTest.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowTest.createCell(value1Column).setCellValue(current.testTime10f[0]);
        rowTest.createCell(value2Column).setCellValue(current.testTime10f[1]);
        rowTest.createCell(value3Column).setCellValue(current.testTime10f[2]);
        rowTest.createCell(value4Column).setCellValue(current.testTime10f[3]);
        rowTest.createCell(value5Column).setCellValue(current.testTime10f[4]);
        rowTest.createCell(value6Column).setCellValue(current.testTime10f[5]);
        rowTest.createCell(value7Column).setCellValue(current.testTime10f[6]);
        rowTest.createCell(value8Column).setCellValue(current.testTime10f[7]);
        rowTest.createCell(value9Column).setCellValue(current.testTime10f[8]);
        rowTest.createCell(value10Column).setCellValue(current.testTime10f[9]);
        //sheet model size 10 folds
        Row rowSize = size10Sheet.createRow((short) (rowIndex));
        rowSize.createCell(algorithmColumn).setCellValue(current.infoclassifier.name);
        rowSize.createCell(value1Column).setCellValue(current.modelSize10f[0]);
        rowSize.createCell(value2Column).setCellValue(current.modelSize10f[1]);
        rowSize.createCell(value3Column).setCellValue(current.modelSize10f[2]);
        rowSize.createCell(value4Column).setCellValue(current.modelSize10f[3]);
        rowSize.createCell(value5Column).setCellValue(current.modelSize10f[4]);
        rowSize.createCell(value6Column).setCellValue(current.modelSize10f[5]);
        rowSize.createCell(value7Column).setCellValue(current.modelSize10f[6]);
        rowSize.createCell(value8Column).setCellValue(current.modelSize10f[7]);
        rowSize.createCell(value9Column).setCellValue(current.modelSize10f[8]);
        rowSize.createCell(value10Column).setCellValue(current.modelSize10f[9]);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();
    }

    public synchronized void writeSvmBestValues(Sheet res, InfoClassifier classif) {

        LibSVM svm = (LibSVM) classif.classifier;
        //write best parameter for SVM, C and Gamma
        Row rowZero = res.getRow(0);
        rowZero.createCell(svmBestParameterTitle).setCellValue("SVM Best Parameter Tuning");
        rowZero.createCell(svmCParameterTitle).setCellValue("C Value");
        rowZero.createCell(svmGammaParameterTitle).setCellValue("Gamma value");
        Row rowOne = res.getRow(1);
        double cvalue = classif.parametersTuned.get(0);
        double gvalue = classif.parametersTuned.get(1);
        rowOne.createCell(svmCValue).setCellValue(cvalue);
        rowOne.createCell(svmGammaValue).setCellValue(gvalue);

    }

    public synchronized boolean isAlreadyComputed4Folds(InfoClassifier ic) {

        int rowIndex = ic.id + this.rowValueOffset;
        Row row = this.sheet.getRow((short) (rowIndex));
        if (row == null) {
            return false;
        }
        Cell cell = row.getCell(2); //get name of the algo
        if (cell == null) {
            return false;
        }
        if (!cell.getStringCellValue().equals(ic.name)) {
            return false;
        }
        return true;
    }

    public synchronized boolean isAlreadyComputed10Folds(InfoClassifier ic) {

        int rowIndex = ic.id + this.rowValueOffset;
        Row row = this.sheet10.getRow((short) (rowIndex));
        if (row == null) {
            return false;
        }
        Cell cell = row.getCell(2); //get name of the algo
        if (cell == null) {
            return false;
        }
        if (!cell.getStringCellValue().equals(ic.name)) {
            return false;
        }
        return true;
    }

}