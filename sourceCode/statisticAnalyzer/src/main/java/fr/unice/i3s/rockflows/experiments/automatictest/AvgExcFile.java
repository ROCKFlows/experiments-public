package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvgExcFile {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;
    public String path;
    public int rowTitleIndex = 1;
    public int rowValueOffset = 3;
    int start = 2;
    public int algorithmColumn = start++;
    public int rankColumn = start++;
    public int avgAccColumn = start++;
    public int stDevAccColumn = start++;
    public int totalTimeColumn = start++;
    public int ramColumn = start++;

    public void writeFinalExcelFile(String path, List<TestResult> results) throws Exception {

        //like a query sql:
        //order by rankAccuracy,AccuracyAvg(desc)
        results.sort((TestResult r1, TestResult r2) -> {
            return Integer.compare(r1.rankAccuracy, r2.rankAccuracy);
        });

        List<TestResult> ordered = new ArrayList<>();
        //read last number of rank
        int maxRank = results.get(results.size() - 1).rankAccuracy;
        for (int i = 1; i <= maxRank; i++) {
            ordered.addAll(getOrderedByAccuracy(i, results));
        }

        writeResultExcel(ordered);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();

    }

    private List<TestResult> getOrderedByAccuracy(int rank, List<TestResult> res) {

        List<TestResult> out = new ArrayList<>();
        //get results of the selected rank
        for (int i = 0; i < res.size(); i++) {
            TestResult tr = res.get(i);
            if (tr.rankAccuracy == rank) {
                out.add(tr);
            }
        }
        //sort results according to the accuracy
        out.sort((TestResult tr1, TestResult tr2) -> {
            return Double.compare(tr2.accuracyAvg, tr1.accuracyAvg);
        });

        return out;
    }

    public AvgExcFile(String path) {
        /*
        write in the excel file all the results according to this format:
        Algorithm - Dataset Preprocessing - accuracy ... <results>
         */
        //create new excel File
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Avg");
        this.path = path;

        // Create a title row. Rows are 0 based.
        Row row = sheet.createRow((short) rowTitleIndex);
        // Create cells
        Cell cell = row.createCell(algorithmColumn);
        cell.setCellValue("Algorithm");
        row.createCell(avgAccColumn).setCellValue("Avg Accuracy");
        row.createCell(stDevAccColumn).setCellValue("St.Dev. Accuracy");
        row.createCell(rankColumn).setCellValue("Rank");
        row.createCell(totalTimeColumn).setCellValue("Avg Execution Time (ms)");
        row.createCell(ramColumn).setCellValue("Avg RAM (bytes)");
    }

    public void writeResultExcel(List<TestResult> results)
            throws FileNotFoundException, IOException {

        int num = results.size();
        for (int iii = 0; iii < num; iii++) {
            int rowIndex = iii + rowValueOffset;
            // Create a title row. Rows are 0 based.
            TestResult res = results.get(iii);
            Row row = sheet.createRow((short) (rowIndex));
            row.createCell(algorithmColumn).setCellValue(res.algoName);
            row.createCell(avgAccColumn).setCellValue(res.accuracyAvg);
            row.createCell(stDevAccColumn).setCellValue(res.accuracyStDev);
            row.createCell(rankColumn).setCellValue(res.rankAccuracy);
            row.createCell(totalTimeColumn).setCellValue((int) res.totalTimeAvg);
            row.createCell(ramColumn).setCellValue((int) res.ramAvg);
        }

    }

}
