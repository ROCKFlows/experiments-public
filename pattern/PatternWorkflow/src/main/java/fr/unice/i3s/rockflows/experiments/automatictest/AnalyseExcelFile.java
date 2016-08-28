package fr.unice.i3s.rockflows.experiments.automatictest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import fr.unice.i3s.rockflows.experiments.datamining.Dataset;
import fr.unice.i3s.rockflows.experiments.datamining.InfoClassifier;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import fr.unice.i3s.rockflows.experiments.datamining.ResWorkflow;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;    
import weka.classifiers.functions.LibSVM;

/**
 *
 * @author Luca
 */
public class AnalyseExcelFile {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet; 
    public String path;
    public int start = 2;
    public int algorithmName = start++;
    public int idPreProcColumn = start++;
    public int accuracy = start++;
    public int accuracyRank = start++;
    public int trainingTime = start++;    
    public int trainingTimeRank = start++;    
    public int totalTime = start++;
    public int totalTimeRank = start++;
    public int modelSize = start++;
    public int modelSizeRank = start++;           
    
    public AnalyseExcelFile(String path)
                throws FileNotFoundException, IOException, InvalidFormatException {
        
        //check if the file already exists, maybe it contains already the results
        //for some classifiers
        File excFile = new File(path);
        if(excFile.exists() && !excFile.isDirectory()) {
            // read existing file and then modify this one
            FileInputStream fis = new FileInputStream(path);
            workbook = (XSSFWorkbook)WorkbookFactory.create(fis);
            fis.close();
            sheet = workbook.getSheet("StatisticAnalysis");
        }
        this.path = path;
    }               
    
    public void readValues(List<ResWorkflow> classifiers){
        
        Iterator<Row> rows = sheet.rowIterator();
        //skip titles
        rows.next();
        //read values
        while(rows.hasNext()){
            Row row = rows.next();
            //read algorithm name
            String name = row.getCell(algorithmName).getStringCellValue();
            int preProcId = (int)row.getCell(idPreProcColumn).getNumericCellValue();
            //get classifier by name
            ResWorkflow cls = getWorkflow(name, preProcId, classifiers);
            //set values of this row to the read classifier
            double accuracy2 = row.getCell(this.accuracy).getNumericCellValue();
            double accRank = row.getCell(this.accuracyRank).getNumericCellValue();
            double time = row.getCell(this.totalTime).getNumericCellValue();
            double ram = row.getCell(this.modelSize).getNumericCellValue();
            cls.accuracy.add(accuracy2);
            cls.rankAccuracy.add(accRank);
            cls.time.add(time);
            cls.ram.add(ram);
        }
    }
    
    public ResWorkflow getWorkflow(String classifierName, int preProcId, List<ResWorkflow> workflows){
        
        int num = workflows.size();
        for(int i = 0; i < num; i++){
            ResWorkflow cls = workflows.get(i);
            if(cls.preProcId == preProcId){
                if(cls.classifierName.equals(classifierName) 
                        || classifierName.equals("1-vs-1 " + cls.classifierName)){
                    return cls;
                }                
            }
        }
        return null;
    }
    
}