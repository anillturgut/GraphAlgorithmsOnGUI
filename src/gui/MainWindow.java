package gui;

import algo.*;
import gurobi.GRBException;
import models.Graph;
import org.apache.poi.ss.usermodel.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.io.File;

public class MainWindow extends JPanel {

    private Graph graph;
    private GraphPanel graphPanel;

    private JComboBox<String> comboBox;

    private boolean loggingEnabled = false;

    public MainWindow() {
        super.setLayout(new BorderLayout());
        setGraphPanel();
    }

    private void setGraphPanel() {
        graph = new Graph();
        graphPanel = new GraphPanel(graph);
        graphPanel.setPreferredSize(new Dimension(9000, 4096));

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(graphPanel);
        scroll.setPreferredSize(new Dimension(750, 500));
        scroll.getViewport().setViewPosition(new Point(4100, 0));
        add(scroll, BorderLayout.CENTER);
        setTopPanel();
        setButtons();
    }

    private void setTopPanel() {
        JLabel info = new JLabel("Graph & Network Algorithm Visualiser");
        info.setForeground(new Color(230, 220, 250));
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 204));
        panel.add(info);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(panel, BorderLayout.NORTH);
    }

    private void setButtons() {
        JButton run = new JButton();
        setupIcon(run, "run");
        run.setToolTipText("Run the model with the selected algorithm");
        JButton reset = new JButton();
        setupIcon(reset, "reset");
        reset.setToolTipText("Clear the network");
        JButton info = new JButton();
        setupIcon(info, "info");
        info.setToolTipText("Button & network information");
        JButton algorithm = new JButton();
        setupIcon(algorithm, "algorithm");
        algorithm.setToolTipText("Click to select an algorithm to solve the network problem");
        algorithm.addActionListener(new ButtonListener());
        JButton personal = new JButton();
        setupIcon(personal, "boun");
        personal.setToolTipText("My Information");
        JButton importExcel = new JButton();
        setupIcon(importExcel, "excel");
        importExcel.setToolTipText("Import excel to draw network");
        final JButton exportExcel = new JButton();
        setupIcon(exportExcel, "export");
        exportExcel.setToolTipText("Export current network to the excel file");
        JCheckBox loggingCheckBox = new JCheckBox("Enable Logging");
        loggingCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        loggingCheckBox.setToolTipText("Check if you want to log the results on your Documents/ folder");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DrawUtils.parseColor("#DDDDDD"));
        buttonPanel.add(reset);
        buttonPanel.add(run);
        buttonPanel.add(info);
        buttonPanel.add(importExcel);
        buttonPanel.add(exportExcel);
        buttonPanel.add(algorithm, BorderLayout.BEFORE_FIRST_LINE);
        comboBox = new JComboBox<>();
        comboBox.setBackground(DrawUtils.parseColor("#DDDDDD"));
        comboBox.setPreferredSize(new Dimension(100,30));
        comboBox.setVisible(false);
        comboBox.setToolTipText("Select an algorithm");
        buttonPanel.add(comboBox, BorderLayout.CENTER);
        buttonPanel.add(personal);
        buttonPanel.add(loggingCheckBox);


        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.reset();
            }
        });

        info.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainWindow.this,
                        "Click on empty space to create new node\n" +
                                "Drag from node to node to create an edge\n" +
                                "Click on edges to set the weight\n\n" +
                                "Combinations:\n" +
                                "Shift + Left Click       :    Set node as source\n" +
                                "Shift + Right Click     :    Set node as destination\n" +
                                "Ctrl  + Drag               :    Reposition Node\n" +
                                "Ctrl  + Shift + Click   :    Delete Node/Edge\n");
            }
        });

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long startTime = System.nanoTime();
                if (comboBox.getSelectedItem() == "Dijkstra's"){
                    DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph);
                    try {
                        dijkstraAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        graphPanel.setPath(dijkstraAlgorithm.getDestinationPath(), comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Shortest Path: " + dijkstraAlgorithm.getDestinationPathAsString() + "\n"
                                        + "              Total Distance: " + dijkstraAlgorithm.getDestinationDistance());
                        if (loggingEnabled){
                            LogActions logDijkstra = new LogActions(graph,graphPanel, loggingEnabled,
                                                                    dijkstraAlgorithm.getDestinationPathAsString(),"Dijkstra's Algorithm",
                                                                    dijkstraAlgorithm.getDestinationDistance(),elapsedTime);
                            logDijkstra.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    }
                } else if (comboBox.getSelectedItem() == "Bread-First-Search"){
                    BreadthFirstSearchAlgorithm breadthFirstSearchAlgorithm = new BreadthFirstSearchAlgorithm(graph);
                    try {
                        breadthFirstSearchAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        graphPanel.setPath(breadthFirstSearchAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Traversed Path: " + breadthFirstSearchAlgorithm.getDestinationPathAsString() + "\n");
                        if (loggingEnabled){
                            LogActions logBFS = new LogActions(graph,graphPanel,loggingEnabled,
                                                                breadthFirstSearchAlgorithm.getDestinationPathAsString(),
                                                                "Breadth First Search Algorithm",elapsedTime);
                            logBFS.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }
                }else if (comboBox.getSelectedItem() == "Depth-First-Search"){
                    DepthFirstSearchAlgorithm depthFirstSearchAlgorithm = new DepthFirstSearchAlgorithm(graph);
                    try {
                        depthFirstSearchAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        graphPanel.setPath(depthFirstSearchAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Traversed Path: " + depthFirstSearchAlgorithm.getDestinationPathAsString() + "\n");
                        if (loggingEnabled){
                            LogActions logDFS = new LogActions(graph,graphPanel,loggingEnabled,
                                    depthFirstSearchAlgorithm.getDestinationPathAsString(),
                                    "Depth First Search Algorithm",elapsedTime);
                            logDFS.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }
                } else if (comboBox.getSelectedItem() == "Bellman-Ford") {
                    BellmanFordAlgorithm bellmanFordAlgorithm = new BellmanFordAlgorithm(graph);
                    try {
                        bellmanFordAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        graphPanel.setPath(bellmanFordAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Shortest Path: " + bellmanFordAlgorithm.getDestinationPathAsString() + "\n"
                                        + "              Total Distance: " + bellmanFordAlgorithm.getDestinationDistance());
                        if (loggingEnabled){
                            LogActions logBellman = new LogActions(graph,graphPanel,loggingEnabled,
                                    bellmanFordAlgorithm.getDestinationPathAsString(),"Bellman Ford Algorithm",
                                    bellmanFordAlgorithm.getDestinationDistance(),elapsedTime);
                            logBellman.log();
                        }

                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }
                }else if (comboBox.getSelectedItem() == "Floyd-Warshall") {
                    FloydWarshallAlgorithm floydWarshallAlgorithm = new FloydWarshallAlgorithm(graph, MainWindow.this);
                    try {
                        floydWarshallAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        floydWarshallAlgorithm.getDistanceMatrix();
                        if (graph.getDestination() != null){
                            graphPanel.setPath(floydWarshallAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        }else{
                            JOptionPane.showMessageDialog(MainWindow.this,
                                    "Since there is no destination defined, path is not seen.");
                        }
                        if (loggingEnabled){
                            LogActions logFloyd = new LogActions(graph,graphPanel,loggingEnabled,
                                    floydWarshallAlgorithm.getLoggedTableModel(),"Floyd Warshall Algorithm",elapsedTime);
                            logFloyd.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "ShortestPath-LP") {
                    ShortestPathProblemLP shortestPathProblemLP = new ShortestPathProblemLP(graph);
                    try {
                        shortestPathProblemLP.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        graphPanel.setPath(shortestPathProblemLP.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(MainWindow.this,
                                        "Xij: Flow across (i,j) \n" +
                                shortestPathProblemLP.getOptimizationResult() + "\n" +
                                        "Result from Dual: \n" +
                                        "Shortest Path: " + shortestPathProblemLP.getDestinationPathAsString() + "\n"
                                        + "Total Distance: " + shortestPathProblemLP.getDestinationDistance());
                        if (loggingEnabled){
                            LogActions logLP = new LogActions(graph,graphPanel,loggingEnabled,
                                    shortestPathProblemLP.getDestinationPathAsString(),"Shortest Path Problem - LP Optimization",
                                    shortestPathProblemLP.getDestinationDistance(),elapsedTime, shortestPathProblemLP.getLogResultMessage());
                            logLP.log();
                        }

                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }catch (NullPointerException npe){} catch (GRBException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (comboBox.getSelectedItem() == "Augmenting-Path") {
                    AugmentingPathAlgorithm augmentingPathAlgorithm = new AugmentingPathAlgorithm(graph);
                    try {
                        graphPanel.setPath(null,comboBox.getSelectedItem().toString());
                        augmentingPathAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Maximum flow that can be sent from "+graph.getSource().toString() +
                                        " to " + graph.getDestination().toString() + " : "
                                        + augmentingPathAlgorithm.getMaxFlow() + "\n" +
                                        "Edge's residual/original capacities: " + "\n" +
                                        augmentingPathAlgorithm.getResidualCapacitiesAsString(graph.getEdges()));
                        if (loggingEnabled){
                            LogActions logAugmentingPath = new LogActions(graph,graphPanel,loggingEnabled,
                                    augmentingPathAlgorithm.getMaxFlow(), augmentingPathAlgorithm.getResidualCapacitiesAsString(graph.getEdges()),
                                    "Augmenting Path Algorithm",elapsedTime);
                            logAugmentingPath.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "Capacity-Scaling") {
                    CapacityScalingAlgorithm capacityScalingAlgorithm = new CapacityScalingAlgorithm(graph);
                    try {
                        graphPanel.setPath(null,comboBox.getSelectedItem().toString());
                        capacityScalingAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Maximum flow that can be sent from "+graph.getSource().toString() +
                                        " to " + graph.getDestination().toString() + " : "
                                        + capacityScalingAlgorithm.getMaxFlow() + "\n" +
                                        "Scaling Phases (Δ) : " +
                                        capacityScalingAlgorithm.getScalingPhasesAsString() + "\n" +
                                        "Edge's residual/original capacities: " + "\n" +
                                        capacityScalingAlgorithm.getResidualCapacitiesAsString(graph.getEdges()));
                        if (loggingEnabled){
                            LogActions logCapacityScaling = new LogActions(graph,graphPanel,loggingEnabled,
                                    capacityScalingAlgorithm.getMaxFlow(), capacityScalingAlgorithm.getResidualCapacitiesAsString(graph.getEdges()),
                                    "Capacity Scaling Algorithm",elapsedTime);
                            logCapacityScaling.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "PreFlow-Push") {
                    PreFlowPushAlgorithm preflowPushAlgorithm = new PreFlowPushAlgorithm(graph);
                    try {
                        graphPanel.setPath(null,comboBox.getSelectedItem().toString());
                        preflowPushAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Maximum flow that can be sent from "+graph.getSource().toString() +
                                        " to " + graph.getDestination().toString() + " : "
                                        + preflowPushAlgorithm.getMaxFlow(graph.getDestination()) + "\n" +
                                        "Edge's flow/capacity information : " + "\n" +
                                        preflowPushAlgorithm.getEdgeFlowAsString(graph.getEdges()));
                        if (loggingEnabled){
                            LogActions logPreFlow = new LogActions(graph,graphPanel,loggingEnabled,
                                    preflowPushAlgorithm.getMaxFlow(graph.getDestination()), preflowPushAlgorithm.getEdgeFlowAsString(graph.getEdges()),
                                    "PreFlow Push Algorithm",elapsedTime);
                            logPreFlow.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "MaximumFlow-LP") {
                    MaximumFlowProblemLP maximumFlowProblemLP = new MaximumFlowProblemLP(graph);
                    try {
                        graphPanel.setPath(null,comboBox.getSelectedItem().toString());
                        maximumFlowProblemLP.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        JOptionPane.showMessageDialog(MainWindow.this,
                                maximumFlowProblemLP.getOptimizationResult());
                        if (loggingEnabled){
                            LogActions logMaxFlowLP = new LogActions(graph,graphPanel,loggingEnabled,
                                    "Maximum Flow Problem - LP Optimization",elapsedTime,
                                    maximumFlowProblemLP.getOptimizationResult());
                            logMaxFlowLP.log();
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                } else if (comboBox.getSelectedItem() == "Topological-Ordering") {
                    TopologicalOrderingAlgorithm topologicalOrderingAlgorithm = new TopologicalOrderingAlgorithm(graph);
                    try {
                        topologicalOrderingAlgorithm.run();
                        long endTime = System.nanoTime();
                        long elapsedTimeInNanos = endTime - startTime;
                        double elapsedTime = elapsedTimeInNanos / 1e9;
                        graphPanel.setPath(topologicalOrderingAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Topological Order: " + topologicalOrderingAlgorithm.getTopologicalOrderAsString() + "\n" +
                                "Shortest Path: " + topologicalOrderingAlgorithm.getDestinationPathAsString() + "\n"
                                        + "              Total Distance: " + topologicalOrderingAlgorithm.getDestinationDistance());

                        if (loggingEnabled){
                            LogActions logTopological = new LogActions(graph,graphPanel, loggingEnabled,
                                    topologicalOrderingAlgorithm.getDestinationPathAsString(),"Topological Ordering Algorithm",
                                    topologicalOrderingAlgorithm.getDestinationDistance(),elapsedTime);
                            logTopological.log();
                        }

                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(MainWindow.this, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Memory error, control it.");
                    }
                } else if (comboBox.getSelectedItem() == null){
                    JOptionPane.showMessageDialog(MainWindow.this, "Please select an algorithm !" );
                }
                else {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            comboBox.getSelectedItem()+" algorithm has not been developed yet." );
                }
            }
        });

        personal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainWindow.this,
                        "Anil Turgut <----->  2022702072\n" +
                                "Industrial Engineering MSc Student\n" +
                                "Bogazici University\n\n" +
                                "GitHub: https://github.com/anillturgut");
            }
        });

        importExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select network excel file");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xls", "xlsx");
                fileChooser.setFileFilter(filter);

                // Show open dialog; this method does not return until the dialog is closed
                int result = fileChooser.showOpenDialog(MainWindow.this);

                // Check if the user chose a file
                if (result == JFileChooser.APPROVE_OPTION) {
                    File filePath = fileChooser.getSelectedFile();
                    /*
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Selected File Path: " + selectedFile.toString());*/
                    // Do something with the selected file, e.g., open or process it
                    ExcelImport excelImport = new ExcelImport(graph,graphPanel,filePath.toString());
                    excelImport.getExcelFromPath();
                }
            }
        });
        exportExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                ExportExcel excel = new ExportExcel(graph,graphPanel);
                fileChooser.setDialogTitle("Select Destination Path");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int userSelection = fileChooser.showSaveDialog(MainWindow.this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String destinationPath = fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx";
                    excel.exportExcel(destinationPath);
                }
            }
        });

        // Add an ActionListener to the checkbox
        loggingCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loggingEnabled = loggingCheckBox.isSelected();
            }
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupIcon(JButton button, String img) {
        try {
            Image icon = ImageIO.read(getClass().getResource(
                    "/resources/" + img + ".png"));
            ImageIcon imageIcon = new ImageIcon(icon);
            button.setIcon(imageIcon);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ButtonListener implements ActionListener {

        private boolean firstTimeSwitch = true;

        @Override
        public void actionPerformed(ActionEvent event) {
            if (firstTimeSwitch) {
                String[] selection = {"Dijkstra's", "Bread-First-Search","Depth-First-Search",
                        "Topological-Ordering", "Bellman-Ford", "Floyd-Warshall", "ShortestPath-LP",
                        "Augmenting-Path", "Capacity-Scaling", "PreFlow-Push","MaximumFlow-LP"};
                for (int index = 0; index < selection.length; index++) {
                    comboBox.addItem(selection[index]);
                }

                firstTimeSwitch = false;
            }
            comboBox.setVisible(!comboBox.isVisible());
        }
    }
}
