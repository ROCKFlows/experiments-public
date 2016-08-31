package fr.unice.i3s.rockflows.experiments.automatictest;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Luca
 */
public class IntermediateExcelFile {

    public boolean unique = false;
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
    public int rankIntermediateAccuracyColumn = start++;
    public int rankIntermediateTrainColumn = start++;
    public int rankIntermediateTestColumn = start++;
    public int rankIntermediateSizeColumn = start++;
    public int statusIntermediateAccuracyColumn = start++;
    public int statusIntermediateTrainColumn = start++;
    public int statusIntermediateTestColumn = start++;
    public int statusIntermediateSizeColumn = start++;
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


    public IntermediateExcelFile(String path)
            throws FileNotFoundException, IOException, InvalidFormatException {

        // read existing file and then modify this one
        FileInputStream fis = new FileInputStream(path);
        workbook = (XSSFWorkbook) WorkbookFactory.create(fis);
        fis.close();
        sheet = workbook.getSheet("Results");
        sheet10 = workbook.getSheet("Results10");
        this.path = path;
    }

}