package algo;

import models.Edge;
import models.Graph;
import models.Node;

import java.util.*;

public class BreadthFirstSearchAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Graph graph;
    private Map<Node, Node> predecessors;
    private Map<Node, Integer> distances;

    private HashSet<Node> unmarked;
    private HashSet<Node> marked;


    public BreadthFirstSearchAlgorithm(Graph graph){
        this.graph = graph;
        unmarked = new HashSet<>();
        marked = new HashSet<>();

        marked = new HashSet<>();

        safe = evaluate();
    }

    private HashSet<Node> getUnmarked(Graph graph){
        for(Node node: graph.getNodes()){
            if(node != graph.getSource()){
                unmarked.add(node);
            }
        }
        return  unmarked;
    }

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

        return true;
    }

    private Node getAdjacent(Edge edge, Node node) {
        if(edge.getNodeOne()!=node && edge.getNodeTwo()!=node)
            return null;

        return node==edge.getNodeTwo()?edge.getNodeOne():edge.getNodeTwo();
    }

    private List<Edge> getNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for(Edge edge : graph.getEdges()){
            //if(edge.getNodeOne()==node ||edge.getNodeTwo()==node)
            if(edge.getNodeOne()==node)
                neighbors.add(edge);
        }

        return neighbors;
    }

    public void run() throws IllegalStateException {
        if(!safe) {
            throw new IllegalStateException(message);
        }
        HashSet<Node> unmarkedList = getUnmarked(graph);
        Node source = graph.getSource();
        marked.add(source);

        while (unmarkedList.size() != 0){

            Node selectedNode = unmarkedList.stream().findFirst().get();

            for (Edge neighbor : getNeighbors(selectedNode)) {
                Node adjacent = getAdjacent(neighbor, selectedNode);
                if (adjacent == null)
                    continue;
                marked.add(adjacent);
            }

        }

        graph.setSolved(true);

    }
}
