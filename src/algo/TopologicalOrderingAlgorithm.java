package algo;

import models.Edge;
import models.Graph;
import models.Node;

import javax.swing.*;
import java.util.*;

public class TopologicalOrderingAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Graph graph;
    private Map<Node, Node> predecessors;
    private Map<Node, Integer> distances;
    private Map<Node, Node> topologicalPredecessors;
    private Map<Node, Integer> inDegree;

    private List<Node> topologicalOrder;

    private PriorityQueue<Node> barrenNodes;


    private boolean done;

    public TopologicalOrderingAlgorithm(Graph graph){

        this.graph = graph;
        predecessors = new HashMap<>();
        topologicalPredecessors = new HashMap<>();
        distances = new HashMap<>();
        inDegree = new HashMap<>();
        topologicalOrder = new ArrayList<>();

        for(Node node : graph.getNodes()){
            distances.put(node, Integer.MAX_VALUE);
        }


        safe = evaluate();
    }
    public class NodeComparator implements Comparator<Node>  {
        @Override
        public int compare(Node node1, Node node2) {
            return distances.get(node1) - distances.get(node2);
        }
    };
    private boolean evaluate(){
        if(graph.getSource()==null){
            message = "Source must be present in the graph";
            return false;
        }

        if(graph.getDestination()==null){
            message = "Destination must be present in the graph";
            return false;
        }

        for(Node node : graph.getNodes()){
            if(!graph.isNodeReachable(node)){
                message = "Graph contains unreachable nodes";
                return false;
            }
        }
        for(Edge edge : graph.getEdges()){
            if(edge.getWeight() < 0){
                message = "Network has at least a negative weighted arc. Either change its weight or use alternative algorithms such as Bellman-Ford";
                return false;
            }
        }

        return true;
    }
    private List<Edge> getNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for(Edge edge : graph.getEdges()){
            if(edge.getNodeOne()==node)
                neighbors.add(edge);
        }

        return neighbors;
    }
    private Edge getTopologicalNeighbor(Node startNode, Node endNode){
        Edge resultEdge = new Edge(startNode,endNode);
        for(Edge edge : graph.getEdges()){
            if(edge.getNodeOne()==startNode && edge.getNodeTwo() == endNode)
                resultEdge = edge;
        }
        return resultEdge;
    }

    private Node getAdjacent(Edge edge, Node node) {
        if(edge.getNodeOne()!=node && edge.getNodeTwo()!=node)
            return null;

        return node==edge.getNodeTwo()?edge.getNodeOne():edge.getNodeTwo();
    }

    public void initializeAlgorithm(){
        inDegree = new HashMap<>();
        for (Node node : graph.getNodes())
            inDegree.put(node, 0);

        for (Node node : graph.getNodes()) {
            for (Edge neighbor : getNeighbors(node)) {
                Node adjacent = getAdjacent(neighbor, node);
                inDegree.put(adjacent, inDegree.get(adjacent) + 1);
            }
        }
        for (Node node : graph.getNodes())
            if (inDegree.get(node) == 0)
                barrenNodes.add(node);
    }


    public List<Node> getTopologicalOrder() {
        while (!barrenNodes.isEmpty()) {
            Node node = barrenNodes.poll();
            topologicalOrder.add(node);
            for (Edge neighbor : getNeighbors(node)) {
                Node adjacent = getAdjacent(neighbor, node);
                int degree = inDegree.get(adjacent);
                --degree;
                if (degree == 0)
                    barrenNodes.add(adjacent);
                    topologicalPredecessors.put(adjacent,node);
                inDegree.put(adjacent, degree);
            }
        }
        return  topologicalOrder;
    }

    public void run() throws IllegalStateException {
        if (!safe) {
            throw new IllegalStateException(message);
        }
        barrenNodes = new PriorityQueue<>(graph.getNodes().size(), new NodeComparator());
        initializeAlgorithm();
        List<Node> topologicalOrder = getTopologicalOrder();

        Node source = graph.getSource();
        distances.put(source,0);

        for (Node node: topologicalOrder){
            for (Edge neighbor : getNeighbors(node)) {
                Node adjacent = getAdjacent(neighbor, node);
                if (distances.get(adjacent) > distances.get(node) + neighbor.getWeight()){
                    distances.put(adjacent,distances.get(node) + neighbor.getWeight());
                    predecessors.put(adjacent,node);
                }
            }
        }
        graph.setSolved(true);
    }
    public Integer getDestinationDistance(){
        return distances.get(graph.getDestination());
    }

    public List<Node> getDestinationPath() {
        return getPath(graph.getDestination());
    }
    public String getTopologicalOrderAsString(){
        String path = "";
        List<Node> nodeList = topologicalOrder;
        for(int i = 0; i < nodeList.toArray().length-1; i++){
            path += nodeList.toArray()[i] + "->";
        }
        path += nodeList.toArray()[nodeList.toArray().length-1];
        return path;
    }
    public String getDestinationPathAsString(){
        String path = "";
        List<Node> nodeList = getPath(graph.getDestination());
        for(int i = 0; i < nodeList.toArray().length-1; i++){
            path += nodeList.toArray()[i] + "->";
        }
        path += nodeList.toArray()[nodeList.toArray().length-1];
        return path;
    }
    public List<Node> getPath(Node node){
        List<Node> path = new ArrayList<>();

        Node current = node;
        path.add(current);
        while (current!=graph.getSource()){
            current = predecessors.get(current);
            path.add(current);
        }

        Collections.reverse(path);

        return path;
    }
}

