package gui;

import algo.*;
import models.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;

public class MainWindow extends JPanel {

    private Graph graph;
    private GraphPanel graphPanel;

    private JComboBox<String> comboBox;

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
        final JButton importExcel = new JButton();
        setupIcon(importExcel, "excel");
        importExcel.setToolTipText("Import excel to draw network");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DrawUtils.parseColor("#DDDDDD"));
        buttonPanel.add(reset);
        buttonPanel.add(run);
        buttonPanel.add(info);
        buttonPanel.add(importExcel);
        buttonPanel.add(algorithm, BorderLayout.BEFORE_FIRST_LINE);
        comboBox = new JComboBox<>();
        comboBox.setBackground(DrawUtils.parseColor("#DDDDDD"));
        comboBox.setPreferredSize(new Dimension(100,30));
        comboBox.setVisible(false);
        comboBox.setToolTipText("Select an algorithm");
        buttonPanel.add(comboBox, BorderLayout.CENTER);
        buttonPanel.add(personal);


        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.reset();
            }
        });

        info.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Click on empty space to create new node\n" +
                                "Drag from node to node to create an edge\n" +
                                "Click on edges to set the weight\n\n" +
                                "Combinations:\n" +
                                "Shift + Left Click       :    Set node as source\n" +
                                "Shift + Right Click     :    Set node as destination\n" +
                                "Ctrl  + Drag               :    Reposition Node\n" +
                                "Ctrl  + Click                :    Get Path of Node\n" +
                                "Ctrl  + Shift + Click   :    Delete Node/Edge\n");
            }
        });

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBox.getSelectedItem() == "Dijkstra's"){
                    DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph);
                    try {
                        dijkstraAlgorithm.run();
                        graphPanel.setPath(dijkstraAlgorithm.getDestinationPath(), comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(null,
                                "Shortest Path: " + dijkstraAlgorithm.getDestinationPathAsString() + "\n"
                                        + "              Total Distance: " + dijkstraAlgorithm.getDestinationDistance());
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    }
                } else if (comboBox.getSelectedItem() == "Bread-First-Search"){
                    BreadthFirstSearchAlgorithm breadthFirstSearchAlgorithm = new BreadthFirstSearchAlgorithm(graph);
                    try {
                        breadthFirstSearchAlgorithm.run();
                        graphPanel.setPath(breadthFirstSearchAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(null,
                                "Traversed Path: " + breadthFirstSearchAlgorithm.getDestinationPathAsString() + "\n");
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }
                }else if (comboBox.getSelectedItem() == "Depth-First-Search"){
                    DepthFirstSearchAlgorithm depthFirstSearchAlgorithm = new DepthFirstSearchAlgorithm(graph);
                    try {
                        depthFirstSearchAlgorithm.run();
                        graphPanel.setPath(depthFirstSearchAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(null,
                                "Traversed Path: " + depthFirstSearchAlgorithm.getDestinationPathAsString() + "\n");
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }
                } else if (comboBox.getSelectedItem() == "Bellman-Ford") {
                    BellmanFordAlgorithm bellmanFordAlgorithm = new BellmanFordAlgorithm(graph);
                    try {
                        bellmanFordAlgorithm.run();

                        graphPanel.setPath(bellmanFordAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(null,
                                "Shortest Path: " + bellmanFordAlgorithm.getDestinationPathAsString() + "\n"
                                        + "              Total Distance: " + bellmanFordAlgorithm.getDestinationDistance());

                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }
                }else if (comboBox.getSelectedItem() == "Floyd-Warshall") {
                    FloydWarshallAlgorithm floydWarshallAlgorithm = new FloydWarshallAlgorithm(graph);
                    try {
                        floydWarshallAlgorithm.run();
                        floydWarshallAlgorithm.getDistanceMatrix();
                        if (graph.getDestination() != null){
                            graphPanel.setPath(floydWarshallAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        }else{
                            JOptionPane.showMessageDialog(null,
                                    "Since there is no destination defined, path is not seen.");
                        }
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "Augmenting-Path") {
                    AugmentingPathAlgorithm augmentingPathAlgorithm = new AugmentingPathAlgorithm(graph);
                    try {
                        augmentingPathAlgorithm.run();
                        JOptionPane.showMessageDialog(null,
                                "Maximum flow that can be sent from "+graph.getSource().toString() +
                                        " to " + graph.getDestination().toString() + " : "
                                        + augmentingPathAlgorithm.getMaxFlow() + "\n" +
                                        "Edge's residual/original capacities: " + "\n" +
                                        augmentingPathAlgorithm.getResidualCapacitiesAsString(graph.getEdges()));
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "Capacity-Scaling") {
                    CapacityScalingAlgorithm capacityScalingAlgorithm = new CapacityScalingAlgorithm(graph);
                    try {
                        capacityScalingAlgorithm.run();
                        JOptionPane.showMessageDialog(null,
                                "Maximum flow that can be sent from "+graph.getSource().toString() +
                                        " to " + graph.getDestination().toString() + " : "
                                        + capacityScalingAlgorithm.getMaxFlow() + "\n" +
                                        "Scaling Phases (Δ) : " +
                                        capacityScalingAlgorithm.getScalingPhasesAsString() + "\n" +
                                        "Edge's residual/original capacities: " + "\n" +
                                        capacityScalingAlgorithm.getResidualCapacitiesAsString(graph.getEdges()));
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                }else if (comboBox.getSelectedItem() == "PreFlow-Push") {
                    PreFlowPushAlgorithm preflowPushAlgorithm = new PreFlowPushAlgorithm(graph);
                    try {
                        preflowPushAlgorithm.run();
                        JOptionPane.showMessageDialog(null,
                                "Maximum flow that can be sent from "+graph.getSource().toString() +
                                        " to " + graph.getDestination().toString() + " : "
                                        + preflowPushAlgorithm.getMaxFlow(graph.getDestination()) + "\n" +
                                        "Edge's flow/capacity information : " + "\n" +
                                        preflowPushAlgorithm.getEdgeFlowAsString(graph.getEdges()));
                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }catch (NullPointerException npe){}
                } else if (comboBox.getSelectedItem() == "Topological-Ordering") {
                    TopologicalOrderingAlgorithm topologicalOrderingAlgorithm = new TopologicalOrderingAlgorithm(graph);
                    try {
                        topologicalOrderingAlgorithm.run();
                        graphPanel.setPath(topologicalOrderingAlgorithm.getDestinationPath(),comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(null,
                                "Topological Order: " + topologicalOrderingAlgorithm.getTopologicalOrderAsString() + "\n" +
                                "Shortest Path: " + topologicalOrderingAlgorithm.getDestinationPathAsString() + "\n"
                                        + "              Total Distance: " + topologicalOrderingAlgorithm.getDestinationDistance());

                    } catch (IllegalStateException ise) {
                        JOptionPane.showMessageDialog(null, ise.getMessage());
                    } catch (OutOfMemoryError ome) {
                        JOptionPane.showMessageDialog(null, "Memory error, control it.");
                    }
                } else if (comboBox.getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Please select an algorithm !" );
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            comboBox.getSelectedItem()+" algorithm has not been developed yet." );
                }
            }
        });

        personal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
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

                // Show open dialog; this method does not return until the dialog is closed
                int result = fileChooser.showOpenDialog(MainWindow.this);

                // Check if the user chose a file
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null,
                            "Selected File Path: " + selectedFile.toString());
                    // Do something with the selected file, e.g., open or process it
                }
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
                        "Topological-Ordering", "Bellman-Ford", "Floyd-Warshall", "Augmenting-Path",
                        "Capacity-Scaling", "PreFlow-Push"};
                for (int index = 0; index < selection.length; index++) {
                    comboBox.addItem(selection[index]);
                }

                firstTimeSwitch = false;
            }
            comboBox.setVisible(!comboBox.isVisible());
        }
    }
}
