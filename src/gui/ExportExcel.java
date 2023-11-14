package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Edge;
import models.Graph;
import models.Node;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
public class ExportExcel {
    private Graph graph;

    private GraphPanel graphPanel;

    public ExportExcel(Graph graph, GraphPanel graphPanel){
        this.graph = graph;
        this.graphPanel = graphPanel;
    }
    public void exportExcel(String destinationPath){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Network");

        // Create header row with node names
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Node Name");

        int colIndex = 1;
        for (Node node : graph.getNodes()) {
            headerRow.createCell(colIndex++).setCellValue("Node " + node.getId());
        }

        // Create data rows
        int rowIndex = 1;
        for (Node node : graph.getNodes()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue("Node " + node.getId());

            // Fill in the matrix values
            colIndex = 1;
            for (Node otherNode : graph.getNodes()) {
                int edgeCellValue = getEdgeCellValue(node,otherNode);
                if(edgeCellValue == 0){
                    row.createCell(colIndex++).setCellValue(0);
                }else if(edgeCellValue == -1){
                    row.createCell(colIndex++).setCellValue("INF");
                }else{
                    row.createCell(colIndex++).setCellValue(edgeCellValue);
                }

            }
        }
        // Write the workbook content to a file
        try (FileOutputStream fileOut = new FileOutputStream(destinationPath)) {
            workbook.write(fileOut);
            JOptionPane.showMessageDialog(null, "Network matrix exported successfully!", "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting network matrix!", "Export Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private List<Edge> getNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for(Edge edge : graph.getEdges()){
            if(edge.getNodeOne()==node)
                neighbors.add(edge);
        }

        return neighbors;
    }
    public int getEdgeCellValue(Node one, Node two){
        for(Edge edge : getNeighbors(one)){
            if (edge.getNodeTwo().equals(two)) {
                return edge.getWeight();
            }
        }
        if (one.equals(two))
            return 0;
        return -1;
    }



}
