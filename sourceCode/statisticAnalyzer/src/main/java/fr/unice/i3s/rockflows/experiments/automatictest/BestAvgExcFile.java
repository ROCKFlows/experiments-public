package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.TestResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class BestAvgExcFile {

    public XSSFWorkbook workbook;
    public XSSFSheet sheetAvg;
    public XSSFSheet sheetBestPrep;
    public String path;
    public int rowTitleIndex = 1;
    public int rowValueOffset = 3;
    int startAvg = 2;
    public int algorithmColumn = startAvg++;
    public int rankColumn = startAvg++;
    public int avgAccColumn = startAvg++;
    public int stDevAccColumn = startAvg++;
    public int totalTimeColumn = startAvg++;
    public int ramColumn = startAvg++;

    int startBestPrep = 3;
    public int p0Column = startBestPrep++;
    public int p1Column = startBestPrep++;
    public int p2Column = startBestPrep++;
    public int p3Column = startBestPrep++;
    public int p4Column = startBestPrep++;
    public int p5Column = startBestPrep++;
    public int p6Column = startBestPrep++;
    public int p7Column = startBestPrep++;
    public int p8Column = startBestPrep++;
    public int p9Column = startBestPrep++;
    public int p10Column = startBestPrep++;
    public int p11Column = startBestPrep++;
    public int p12Column = startBestPrep++;

    public void writeAvgSorted(String path, List<TestResult> results, int numDatasets)
            throws Exception {

        //like a query sql:
        //order by rankAccuracy,AccuracyAvg(desc)
        results.sort((TestResult r1, TestResult r2) -> {
            return Double.compare(r2.accuracyAvg, r1.accuracyAvg);
        });

        writeResultExcel(results, numDatasets);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();

    }

    public void writeTimeSorted(String path, List<TestResult> results, int numDatasets)
            throws Exception {

        //like a query sql:
        //order by rankAccuracy,AccuracyAvg(desc)
        results.sort((TestResult r1, TestResult r2) -> {
            return Double.compare(r2.totalTimeAvg, r1.totalTimeAvg);
        });

        writeResultExcel(results, numDatasets);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();

    }

    public void writeRamSorted(String path, List<TestResult> results, int numDatasets)
            throws Exception {

        //like a query sql:
        //order by rankAccuracy,AccuracyAvg(desc)
        results.sort((TestResult r1, TestResult r2) -> {
            return Double.compare(r2.modelSizeAvg, r1.modelSizeAvg);
        });

        writeResultExcel(results, numDatasets);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();

    }

    public BestAvgExcFile(String path) {
        /*
        write in the excel file all the results according to this format:
        Algorithm - Dataset Preprocessing - accuracy ... <results>
         */
        //create new excel File
        workbook = new XSSFWorkbook();
        sheetAvg = workbook.createSheet("Avg");
        sheetBestPrep = workbook.createSheet("BestPrep");
        this.path = path;

        Row row2 = sheetBestPrep.createRow((short) rowTitleIndex - 1);
        row2.createCell(p11Column).setCellValue("#Best Pre-Processing IDs");

        // Create a title row. Rows are 0 based.
        Row row = sheetAvg.createRow((short) rowTitleIndex);
        // Create cells
        Cell cell = row.createCell(algorithmColumn);
        cell.setCellValue("Algorithm");
        row.createCell(avgAccColumn).setCellValue("Avg Accuracy");
        row.createCell(stDevAccColumn).setCellValue("St.Dev. Accuracy");
        row.createCell(rankColumn).setCellValue("Rank");
        row.createCell(totalTimeColumn).setCellValue("Avg Execution Time (ms)");
        row.createCell(ramColumn).setCellValue("Avg RAM (bytes)");

        // Create a title row. Rows are 0 based.
        Row row3 = sheetBestPrep.createRow((short) rowTitleIndex);
        // Create cells
        Cell cell3 = row3.createCell(algorithmColumn);
        cell3.setCellValue("Algorithm");
        row3.createCell(p0Column).setCellValue("p0");
        row3.createCell(p1Column).setCellValue("p1");
        row3.createCell(p2Column).setCellValue("p2");
        row3.createCell(p3Column).setCellValue("p3");
        row3.createCell(p4Column).setCellValue("p4");
        row3.createCell(p5Column).setCellValue("p5");
        row3.createCell(p6Column).setCellValue("p6");
        row3.createCell(p7Column).setCellValue("p7");
        row3.createCell(p8Column).setCellValue("p8");
        row3.createCell(p9Column).setCellValue("p9");
        row3.createCell(p10Column).setCellValue("p10");
        row3.createCell(p11Column).setCellValue("p11");
        row3.createCell(p12Column).setCellValue("p12");
    }

    public void writeResultExcel(List<TestResult> results, int numDatasets)
            throws FileNotFoundException, IOException {

        int num = results.size();
        for (int iii = 0; iii < num; iii++) {
            int rowIndex = iii + rowValueOffset;
            // Create a title row. Rows are 0 based.
            TestResult res = results.get(iii);
            Row row = sheetAvg.createRow((short) (rowIndex));
            row.createCell(algorithmColumn).setCellValue(res.infoclassifier.name);
            row.createCell(stDevAccColumn).setCellValue(res.accuracyStDev);
            row.createCell(avgAccColumn).setCellValue(res.accuracyAvg);
            row.createCell(rankColumn).setCellValue(res.rankAccuracy);
            row.createCell(totalTimeColumn).setCellValue((int) res.totalTimeAvg);
            row.createCell(ramColumn).setCellValue((int) res.modelSizeAvg);

            Row row2 = sheetBestPrep.createRow((short) (rowIndex));
            row2.createCell(algorithmColumn).setCellValue(res.infoclassifier.name);
            row2.createCell(p0Column).setCellValue(res.contPreProc[0]);
            row2.createCell(p1Column).setCellValue(res.contPreProc[1]);
            row2.createCell(p2Column).setCellValue(res.contPreProc[2]);
            row2.createCell(p3Column).setCellValue(res.contPreProc[3]);
            row2.createCell(p4Column).setCellValue(res.contPreProc[4]);
            row2.createCell(p5Column).setCellValue(res.contPreProc[5]);
            row2.createCell(p6Column).setCellValue(res.contPreProc[6]);
            row2.createCell(p7Column).setCellValue(res.contPreProc[7]);
            row2.createCell(p8Column).setCellValue(res.contPreProc[8]);
            row2.createCell(p9Column).setCellValue(res.contPreProc[9]);
            row2.createCell(p10Column).setCellValue(res.contPreProc[10]);
            row2.createCell(p11Column).setCellValue(res.contPreProc[11]);
            row2.createCell(p12Column).setCellValue(res.contPreProc[12]);
        }

    }

}
