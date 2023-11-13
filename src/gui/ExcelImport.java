package gui;

import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExcelImport {

    private String filePath;

    public ExcelImport(String filePath){
        this.filePath = filePath;
    }

    public void getExcelFromPath(){
        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath))) {
            // Create a Workbook from the Excel file
            Workbook workbook = WorkbookFactory.create(fileInputStream);

            // Assuming you have only one sheet; if not, loop through sheets
            Sheet sheet = workbook.getSheetAt(0);

            // Create a DefaultTableModel to hold the data for the JTable
            DefaultTableModel tableModel = new DefaultTableModel();

            // Add column headers to the table model
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                tableModel.addColumn(cell.toString());
            }

            // Add rows to the table model
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Object[] rowData = new Object[row.getLastCellNum()];
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    rowData[j] = (cell != null) ? cell.toString() : "";
                }
                tableModel.addRow(rowData);
            }

            // Create a JTable with the populated table model
            JTable table = new JTable(tableModel);

            // Create a JScrollPane to contain the JTable
            JScrollPane scrollPane = new JScrollPane(table);

            // Show the message dialog with the JTable
            JOptionPane.showMessageDialog(null, scrollPane, "Excel Table", JOptionPane.PLAIN_MESSAGE);

            // Close the workbook
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
