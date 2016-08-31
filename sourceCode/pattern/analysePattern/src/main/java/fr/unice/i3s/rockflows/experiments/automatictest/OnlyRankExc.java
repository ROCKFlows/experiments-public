package fr.unice.i3s.rockflows.experiments.automatictest;

import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnlyRankExc {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;
    public String path;
    public boolean alreadyExisting = false;
    //indices of rows and columns of the file
    public int rowNumTrainingInstances = 0;
    public int columnTitleNumTrainingInstances = 2;
    public int columnNumTrainingInstances = 3;
    public int rowNumTestInstances = 1;
    public int columnTitleNumTestInstances = 2;
    public int columnNumTestInstances = 3;
    public int rowTitleIndex = 1;
    public int rowValueOffset = 3;
    int start = 2;
    public int algorithmColumn = start++;
    public int accuracyColumn = start++;
    public int rankAccuracy = start++;
    public int trainingTimeColumn = start++;
    public int rankTrain = start++;
    public int totalTimeColumn = start++;
    public int rankTime = start++;
    public int modelSizeColumn = start++;
    public int rankSize = start++;

    public void writeFinalExcelFile(String path, List<TestResult> results, boolean folds4)
            throws Exception {

        //like a query sql:
        //order by rankAccuracy,AccuracyAvg(desc)
        results.sort((TestResult r1, TestResult r2) -> {
            return Integer.compare(r1.rankAccuracy, r2.rankAccuracy);
        });

        List<TestResult> ordered = new ArrayList<>();
        //read last number of rank
        int maxRank = results.get(results.size() - 1).rankAccuracy;
        for (int i = 1; i <= maxRank; i++) {
            ordered.addAll(getOrderedByAccuracy(i, results, folds4));
        }

        writeResultExcel(ordered, folds4);

        //write file in output
        FileOutputStream fileOut = new FileOutputStream(path);
        workbook.write(fileOut);
        fileOut.close();

        //move columns if they are shifted (bug ApachePOI)
        // read existing file and then modify this one        
        FileInputStream fis = new FileInputStream(path);
        XSSFWorkbook finalFile = (XSSFWorkbook) WorkbookFactory.create(fis);
        fis.close();
        ExcelUtils.moveColumns(finalFile.getSheet("StatisticAnalysis"));

        //write file in output
        fileOut = new FileOutputStream(path);
        finalFile.write(fileOut);
        fileOut.close();
    }

    private List<TestResult> getOrderedByAccuracy(int rank, List<TestResult> res, boolean folds4) {

        List<TestResult> out = new ArrayList<>();
        //get results of the selected rank
        for (int i = 0; i < res.size(); i++) {
            TestResult tr = res.get(i);
            if (tr.rankAccuracy == rank) {
                out.add(tr);
            }
        }
        //sort results according to the accuracy
        if (folds4) {
            out.sort((TestResult tr1, TestResult tr2) -> {
                return Double.compare(tr2.accuracyAvg, tr1.accuracyAvg);
            });
        } else {
            out.sort((TestResult tr1, TestResult tr2) -> {
                return Double.compare(tr2.accuracyAvg10, tr1.accuracyAvg10);
            });
        }

        return out;
    }

    public OnlyRankExc(String path) {
        /*
        write in the excel file all the results according to this format:
        Algorithm - Dataset Preprocessing - accuracy ... <results>
         */
        //create new excel File
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("StatisticAnalysis");
        this.path = path;

        // Create a title row. Rows are 0 based.
        Row row = sheet.createRow((short) rowTitleIndex);
        // Create cells
        Cell cell = row.createCell(algorithmColumn);
        cell.setCellValue("Algorithm");
        row.createCell(accuracyColumn).setCellValue("Accuracy Avg");
        row.createCell(trainingTimeColumn).setCellValue("Training Time (ms)");
        row.createCell(totalTimeColumn).setCellValue("Total Time (ms)");
        row.createCell(rankAccuracy).setCellValue("Rank Accuracy");
        row.createCell(rankTrain).setCellValue("Rank Training Time");
        row.createCell(rankTime).setCellValue("Rank Total Time");
        row.createCell(rankSize).setCellValue("Rank Model Size");
        row.createCell(modelSizeColumn).setCellValue("Trained Model Size (bytes)");
    }

    public void writeResultExcel(List<TestResult> results, boolean folds4)
            throws FileNotFoundException, IOException {

        int num = results.size();
        for (int iii = 0; iii < num; iii++) {
            int rowIndex = iii + rowValueOffset;
            // Create a title row. Rows are 0 based.
            TestResult res = results.get(iii);
            Row row = sheet.createRow((short) (rowIndex));
            row.createCell(algorithmColumn).setCellValue(res.algoName);
            if (folds4) {
                row.createCell(accuracyColumn).setCellValue(res.accuracyAvg);
                row.createCell(trainingTimeColumn).setCellValue(res.trainingTimeAvg);
                row.createCell(modelSizeColumn).setCellValue(res.modelSizeAvg);
            } else {
                row.createCell(accuracyColumn).setCellValue(res.accuracyAvg10);
                row.createCell(trainingTimeColumn).setCellValue(res.trainingTimeAvg10);
                row.createCell(modelSizeColumn).setCellValue(res.modelSizeAvg10);
            }
            row.createCell(totalTimeColumn).setCellValue(res.sumTime);
            row.createCell(rankAccuracy).setCellValue(res.rankAccuracy);
            row.createCell(rankTrain).setCellValue(res.rankTrainingTime);
            row.createCell(rankTime).setCellValue(res.rankTotalTime);
            row.createCell(rankSize).setCellValue(res.rankSize);
        }

    }

}
