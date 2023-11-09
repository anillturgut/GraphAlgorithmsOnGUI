package algo;

import models.Edge;
import models.Graph;
import models.Node;

import java.util.*;
public class AugmentingPathAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Graph graph;

    private List<Node> labeled;

    private List<Node> marked;
    private Map<Node, Node> predecessors;

    private int MaxFlow;

    public AugmentingPathAlgorithm(Graph graph){
        this.graph = graph;
        predecessors = new HashMap<>();
        labeled = new ArrayList<>();
        marked = new ArrayList<>();
        for (Edge edge: graph.getEdges()){
            edge.setResidual(edge.getWeight());
        }
        safe = evaluate();
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

    public void run() throws IllegalStateException {
        if (!safe) {
            throw new IllegalStateException(message);
        }
        Node destination = graph.getDestination();
        labeled.add(destination);

        while(labeled.contains(destination)){
            labeled.clear();
            predecessors.clear();
            marked.clear();
            Node source = graph.getSource();
            marked.add(source);
            while (!marked.isEmpty() && !labeled.contains(destination)){
                Node selectedNode = marked.get(0);
                marked.remove(selectedNode);
                for (Edge neighbor : getNeighbors(selectedNode)) {
                    if(neighbor.getResidual() > 0){
                        Node adjacent = getAdjacent(neighbor, selectedNode);
                        if (!labeled.contains(adjacent)){
                            labeled.add(adjacent);
                            predecessors.put(adjacent,selectedNode);
                            marked.add(adjacent);
                        }
                    }
                }
            }
            if (labeled.contains(destination)){
                AugmentPath(predecessors);
            }
        }
        graph.setSolved(true);
    }
    public int getMaxFlow(){return MaxFlow;}
    public void AugmentPath(Map<Node, Node> predecessors){
        List<Node> path = new ArrayList<>();
        List<Edge> edgesInPath = new ArrayList<>();
        Node end = graph.getDestination();
        path.add(end);
        while (end!=graph.getSource()){
            Node start = end;
            end = predecessors.get(start);
            for (Edge edge: getNeighbors(end)){
                if (edge.getNodeTwo().equals(start)){
                    edgesInPath.add(edge);
                }
            }
            path.add(end);
        }
        int delta = getMinEdgeResidual(edgesInPath);

        for (Edge graphEdge: graph.getEdges()){
            if (edgesInPath.contains(graphEdge)){
                graphEdge.setResidual(graphEdge.getResidual() - delta);
            }
        }
        Collections.reverse(path);
        MaxFlow += delta;

    }
    public int getMinEdgeResidual(List<Edge> edges){
        int minValue = Integer.MAX_VALUE;
        for (Edge edge: edges){
            if (edge.getResidual() < minValue){
                minValue = edge.getResidual();
            }
        }
        return minValue;

    }
}
