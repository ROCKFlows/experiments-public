/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.datamining.ResWorkflow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author lupin
 */
public class PatternStatisticExc {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;
    public String path;
    public int start = 2;
    public int algorithmName = start++;
    public int idPreProcColumn = start++;
    //public int compatible = start++;    
    public int avgAccRank = start++;
    public int stdAccRank = start++;
    public int avgAcc = start++;
    public int stdAcc = start++;
    public int numBestRank = start++;
    public int percCompatible = start++;

    public PatternStatisticExc(String path)
            throws FileNotFoundException, IOException, InvalidFormatException {

        this.path = path;
        //create workbook, sheet, titles:
        this.workbook = new XSSFWorkbook();
        this.sheet = this.workbook.createSheet("Statistics");
        Row title = this.sheet.createRow(1);
        title.createCell(algorithmName).setCellValue("Algorithm");
        title.createCell(idPreProcColumn).setCellValue("Id Pre-Processing");
        //title.createCell(compatible).setCellValue("Compatible");
        title.createCell(avgAccRank).setCellValue("Avg Rank");
        title.createCell(stdAccRank).setCellValue("St.dev Rank");
        title.createCell(avgAcc).setCellValue("Avg Accuracy");
        title.createCell(stdAcc).setCellValue("St.dev Accuracy");
        title.createCell(numBestRank).setCellValue("#Best (Rank = 1)");
        title.createCell(percCompatible).setCellValue("% compatible");
    }

    //classifiers are already sorted
    public void writeValues(List<ResWorkflow> classifiers, int total)
            throws Exception {

        int index = 3; //first row of results        
        int num = classifiers.size();
        for (int i = 0; i < num; i++) {
            ResWorkflow res = classifiers.get(i);
            Row row = this.sheet.createRow(index++);
            row.createCell(algorithmName).setCellValue(res.classifierName);
            row.createCell(idPreProcColumn).setCellValue(res.preProcId);
            row.createCell(percCompatible).setCellValue(res.accuracy.size() + " / " + total);
            if (res.compatible) {
                //row.createCell(compatible).setCellValue("y");
                row.createCell(avgAccRank).setCellValue(res.avgAccRank);
                row.createCell(stdAccRank).setCellValue(res.stDevAccRank);
                row.createCell(avgAcc).setCellValue(res.avgAccuracy);
                row.createCell(stdAcc).setCellValue(res.stDevAccuracy);
                row.createCell(numBestRank).setCellValue(res.numBestRank);
            }
            /*
            else{
                row.createCell(compatible).setCellValue("n");
            }
            */
        }
        //write file in output
        FileOutputStream fileOut = new FileOutputStream(this.path);
        this.workbook.write(fileOut);
        fileOut.close();
    }

    public ResWorkflow getClassifierByName(String name, List<ResWorkflow> classifiers) {

        int num = classifiers.size();
        for (int i = 0; i < num; i++) {
            ResWorkflow cls = classifiers.get(i);
            if (cls.classifierName.equals(name) || name.equals("1-vs-1 " + cls.classifierName)) {
                return cls;
            }
        }
        return null;
    }

}
