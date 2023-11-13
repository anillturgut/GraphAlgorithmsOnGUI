package gui;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import models.Edge;
import models.Graph;
import models.Node;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExcelImport {

    private Graph graph;

    private String filePath;

    private List<String> nodeNames;

    private GraphPanel graphPanel;

    private DefaultTableModel tableModel;

    public ExcelImport(Graph graph, GraphPanel graphPanel, String filePath){
        this.filePath = filePath;
        this.graphPanel = graphPanel;
        this.graph = graph;
        tableModel = new DefaultTableModel();
        nodeNames = new ArrayList<>();
    }

    public void getExcelFromPath(){
        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath))) {
            // Create a Workbook from the Excel file
            Workbook workbook = WorkbookFactory.create(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);
            graphPanel.reset();

            // Add column headers to the table model
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                String str = cell.toString();
                if(!str.contains("Name")){
                    nodeNames.add(cell.toString());
                }
            }
            createNodeFromExcel();

            createEdgeFromExcel(getNetworkMatrixFromExcel(sheet,headerRow));

            graphPanel.repaint();


            // Close the workbook
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNodeFromExcel(){
        int x = 4200;
        int y = 180;
        int ratio = 0;
        for (String str : nodeNames){
            int id = Character.getNumericValue(str.charAt(str.length() - 1));
            Node node = new Node(id);
            node.setCoord(x,y);
            graph.addNode(node);
            if (ratio % 2 == 0){
                x+=150;
                y+=150;
            }else{
                x+=180;
                y-=150;
            }
            ratio++;
        }
    }
    public void createEdgeFromExcel(DefaultTableModel tableModel){
        int rowCount = tableModel.getRowCount();
        int columnCount = tableModel.getColumnCount();

        for (int i = 0; i < rowCount; i++) {
            for (int j = 1; j < columnCount; j++) {
                Object cellValue = tableModel.getValueAt(i, j);
                if(!(cellValue.equals("INF")) && !(cellValue.equals("0.0"))){
                    int intCellValue = (int)Double.parseDouble(cellValue.toString());
                    Node one = graph.getNodes().get(i);
                    Node two = graph.getNodes().get(j-1);
                    graph.addEdge(createEdge(one,two,intCellValue));
                }
            }
        }

    }

    public DefaultTableModel getNetworkMatrixFromExcel(Sheet sheet, Row headerRow){
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
        return tableModel;
    }
    public Edge createEdge(Node one, Node two, int weight){
        Edge edge = new Edge(one,two);
        edge.setWeight(weight);
        return edge;
    }

}
