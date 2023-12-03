package algo;

import gui.MainWindow;
import models.Edge;
import models.Graph;
import models.Node;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
public class FloydWarshallAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Graph graph;

    private MainWindow mainWindow;
    private Map<Node, Node> predecessors;
    private Map<Node, Integer> distances;
    private DefaultTableModel loggedTableModel;

    private int maxValue;


    private boolean evaluate(){
        for(Node node : graph.getNodes()){
            if(!graph.isNodeReachable(node)){
                message = "Graph contains unreachable nodes";
                return false;
            }
        }

        return true;
    }
    public FloydWarshallAlgorithm(Graph graph, MainWindow mainWindow){
        this.graph = graph;
        predecessors = new HashMap<>();
        distances = new HashMap<>();
        this.maxValue = 50000;
        initializeNodeDistances();
        this.mainWindow = mainWindow;

        safe = evaluate();
    }
    private List<Edge> getNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for(Edge edge : graph.getEdges()){
            if(edge.getNodeOne()==node)
                neighbors.add(edge);
        }

        return neighbors;
    }
    private Node getAdjacent(Edge edge, Node node) {
        if(edge.getNodeOne()!=node && edge.getNodeTwo()!=node)
            return null;

        return node==edge.getNodeTwo()?edge.getNodeOne():edge.getNodeTwo();
    }
    public void initializeNodeDistances(){
        for(Node startNode : graph.getNodes()){
            distances = new HashMap<>();
            predecessors = new HashMap<>();
            for(Node endNode : graph.getNodes()){
                if (startNode.toString().equals(endNode.toString())){
                    distances.put(endNode, 0);
                }else if (getNeighbors(startNode).isEmpty()){
                    distances.put(endNode, maxValue);
                }else{
                    for(Edge neighbor : getNeighbors(startNode)) {
                        Node adjacent = getAdjacent(neighbor, startNode);
                        if (adjacent != null && adjacent.toString().equals(endNode.toString())){
                            distances.put(endNode, neighbor.getWeight());
                            predecessors.put(endNode,startNode);
                        } else if (!distances.containsKey(endNode)){
                            distances.put(endNode, maxValue);
                        }
                    }
                }
            }
            startNode.setDistancesToAllNodes(distances);
            startNode.setPredecessorFW(predecessors);
        }
    }

    public void run() throws IllegalStateException {
        if (!safe) {
            throw new IllegalStateException(message);
        }

        for (Node k: graph.getNodes()){
            Map<Node, Integer> distanceK = k.getDistancesToAllNodes();
            Map<Node, Node> predecessorK = k.getPredecessorFW();
            for (Node i: graph.getNodes()){
                Map<Node, Integer> distanceI = i.getDistancesToAllNodes();
                Map<Node, Node> predecessorI = i.getPredecessorFW();
                for (Node j: graph.getNodes()){
                    if((distanceI.get(j) > distanceI.get(k) + distanceK.get(j)) && (distanceI.get(k) + distanceK.get(j) > Integer.MIN_VALUE)){
                        distanceI.put(j,distanceI.get(k) + distanceK.get(j));
                        predecessorI.put(j,predecessorK.get(j));
                    }
                }
            }
        }
        graph.setSolved(true);
    }
    public void getDistanceMatrix(){
        String[] columnNames = new String[graph.getNodes().size()+1];
        columnNames[0] = "Node Name";
        int index = 1;
        for (Node node: graph.getNodes()){
            columnNames[index] = node.toString();
            index++;
        }
        Object[][] data = new Object[graph.getNodes().size()][graph.getNodes().size()+1];

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        int rowIndex = 0;
        for (Node node : graph.getNodes()){
            int columnIndex = 1;
            Map<Node, Integer> nodeDistances = node.getDistancesToAllNodes();
            model.setValueAt(node.toString(), rowIndex, 0);
            for (Node nodeKey: graph.getNodes()){
                int distance = nodeDistances.get(nodeKey);
                if (distance > 10000){
                        model.setValueAt("INF", rowIndex, columnIndex);
                }else{
                    model.setValueAt(nodeDistances.get(nodeKey), rowIndex, columnIndex);
                }
                columnIndex++;
            }
            rowIndex++;
        }

        loggedTableModel = model;

        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        // Create a JPanel to hold the table
        JPanel panel = new JPanel();
        panel.add(scrollPane);

        // Show the matrix in a JOptionPane message dialog
        JOptionPane.showMessageDialog(mainWindow, panel,
                "Shortest Path Distances From Each Node", JOptionPane.PLAIN_MESSAGE);
    }
    public Integer getDestinationDistance(){
        return distances.get(graph.getDestination());
    }

    public List<Node> getDestinationPath() {
        return getPath(graph.getSource(),graph.getDestination());
    }

    public DefaultTableModel getLoggedTableModel(){return loggedTableModel;}

    public List<Node> getPath(Node sourceNode, Node destinationNode){
        List<Node> path = new ArrayList<>();

        Node current = destinationNode;
        path.add(current);
        while (current!=graph.getSource()){
            current = sourceNode.getPredecessorFW().get(current);
            path.add(current);
        }

        Collections.reverse(path);

        return path;
    }

}
