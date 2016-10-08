/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.automatictest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author lupin
 */
public class ExcelUtils {
 
    /**
     * Make the given {@link XSSFCell} blank
     * @param cell 
     * the cell to make blank
     */
    public static void emptyCell(final XSSFCell cell) {
        cell.setCellType(Cell.CELL_TYPE_BLANK);
    }    
    
    /**
    * Checks if the value of a given {@link XSSFCell} is empty.
    * 
    * @param cell
    *            The {@link XSSFCell}.
    * @return {@code true} if the {@link XSSFCell} is empty. {@code false}
    *         otherwise.
    */
    public static boolean isCellEmpty(final XSSFCell cell) {
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return true;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
            return true;
        }
        return false;
    }
   
    /**
    * Checks if the value of a given {@link XSSFRow} is empty.
    * 
    * @param row
    *            The {@link XSSFRow}.
    * @return {@code true} if the {@link XSSFRow} is empty. {@code false}
    *         otherwise.
    */    
    public static boolean isRowEmpty(final XSSFRow row) {
        if(row == null){
            return true;
        }
        return false;
    }
   
    /**
    * Get the double value contianed in a given {@link XSSFCell}
    * 
    * @param cell
    *            The {@link XSSFCell}.
    * @return the double value  if the {@link XSSFCell} if the type of the cell
    * is {@code Cell.CELL_TYPE_NUMERIC}, 
    * otherwise {@code Double.NEGATIVE_INFINITY}
    */    
    public static double getDoubleValue(final XSSFCell cell){
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return cell.getNumericCellValue();
        }
        return Double.NEGATIVE_INFINITY;
    }

    /**
    * Get the int value contianed in a given {@link XSSFCell}
    * 
    * @param cell
    *            The {@link XSSFCell}.
    * @return the int value  if the {@link XSSFCell} if the type of the cell
    * is {@code Cell.CELL_TYPE_NUMERIC}, 
    * otherwise {@code Integer.MIN_VALUE}
    */        
    public static int getIntValue(final XSSFCell cell){
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return (int)cell.getNumericCellValue();
        }
        return Integer.MIN_VALUE;
    }
    
    public static String getStringValue(final XSSFCell cell){
        if(cell.getCellType() == Cell.CELL_TYPE_STRING){
            return cell.getStringCellValue();
        }
        return cell.getRawValue();
    }        
    
    
    public static void moveColumns(XSSFSheet sheet) throws Exception{
        
        Row first = sheet.iterator().next();
        Cell firstCell = first.cellIterator().next();
        int firstIndex = firstCell.getColumnIndex();        
        if(firstIndex > 2){
            int difference = firstIndex - 2;
            for(int i = 0; i < difference; i++){
                deleteColumn(sheet, 0);
            }
        }
    }    
    
    /**
    * Given a sheet, this method deletes a column from a sheet and moves
    * all the columns to the right of it to the left one cell.
    *
    * Note, this method will not update any formula references.
    *
    * @param sheet
    * @param column
    */
    private static void deleteColumn( Sheet sheet, int columnToDelete ){
        int maxColumn = 0;
        for (int iii = 0; iii < sheet.getLastRowNum()+1; iii++){
            Row row = sheet.getRow(iii );
            // if no row exists here; then nothing to do; next!
            if ( row == null ){
                continue;
            }

            // if the row doesn't have this many columns then we are good; next!
            int lastColumn = row.getLastCellNum();
            if ( lastColumn > maxColumn ){
                maxColumn = lastColumn;
            }                

            if ( lastColumn < columnToDelete ){
                continue;
            }                

            for ( int x=columnToDelete+1; x < lastColumn + 1; x++ ){
                Cell oldCell = row.getCell(x-1);
                if ( oldCell != null ){
                    row.removeCell( oldCell );
                }                        
                Cell nextCell = row.getCell( x );
                if ( nextCell != null ){
                    Cell newCell    = row.createCell( x-1, nextCell.getCellType() );
                    cloneCell(newCell, nextCell);
                }
            }
        }

        // Adjust the column widths
        for ( int ccc=0; ccc < maxColumn; ccc++ ){
            sheet.setColumnWidth(ccc, sheet.getColumnWidth(ccc+1) );
        }
    }


    /*
     * Takes an existing Cell and merges all the styles and forumla
     * into the new one
     */
    private static void cloneCell( Cell cnew, Cell cold ){

        cnew.setCellType(cold.getCellType());

        switch ( cnew.getCellType() ){
            case Cell.CELL_TYPE_BLANK:{
                cnew.setCellValue(cold.getNumericCellValue());
                break;
            }                      
            case Cell.CELL_TYPE_STRING:{
                cnew.setCellValue(cold.getStringCellValue() );
                break;
            }
        }
    }        
}
