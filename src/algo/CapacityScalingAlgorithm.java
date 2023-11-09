package algo;

import models.Graph;
import models.Node;

import java.util.*;

import models.Edge;
import models.Graph;
import models.Node;
public class CapacityScalingAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Graph graph;

    private List<Node> labeled;

    private List<Node> marked;
    private Map<Node, Node> predecessors;

    private int scalingPhaseDelta;

    private int MaxFlow;

    public CapacityScalingAlgorithm(Graph graph){
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
            if(edge.getNodeOne()==node && edge.getWeight() >= scalingPhaseDelta)
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

        int U = getMaxEdgeResidual(graph.getEdges());

        scalingPhaseDelta = (int)Math.floor((Math.log(U) / Math.log(2)));

        int delta = (int) Math.pow(2,scalingPhaseDelta);

        while (delta >= 1 ){
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
            delta = delta / 2;
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
        int flow =  getMinEdgeResidual(edgesInPath);

        for (Edge graphEdge: graph.getEdges()){
            if (edgesInPath.contains(graphEdge)){
                graphEdge.setResidual(graphEdge.getResidual() - flow);
            }
        }
        Collections.reverse(path);
        MaxFlow += flow;

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
    public int getMaxEdgeResidual(List<Edge> edges){
        int maxValue = 0;
        for (Edge edge: edges){
            if (edge.getResidual() > maxValue){
                maxValue = edge.getResidual();
            }
        }
        return maxValue;
    }
    public String getResidualCapacitiesAsString(List<Edge> edges){
        String residualCapacities = "";
        for(Edge edge: edges){
            String edgeCapacity = edge.toString() + " :  (";
            edgeCapacity += edge.getResidual() + "/" + edge.getWeight() + ")";
            residualCapacities += edgeCapacity + "\n";
        }
        return residualCapacities;
    }
}
