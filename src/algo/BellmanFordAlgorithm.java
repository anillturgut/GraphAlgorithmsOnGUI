package algo;

import models.Edge;
import models.Graph;
import models.Node;

import javax.swing.*;
import java.util.*;
public class BellmanFordAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Graph graph;
    private Map<Node, Node> predecessors;
    private Map<Node, Integer> distances;

    public BellmanFordAlgorithm(Graph graph){
        this.graph = graph;
        predecessors = new HashMap<>();
        distances = new HashMap<>();

        for(Node node : graph.getNodes()){
            distances.put(node, Integer.MAX_VALUE);
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

    public void run() throws IllegalStateException {
        if(!safe) {
            throw new IllegalStateException(message);
        }
        Node source = graph.getSource();
        distances.put(source, 0);

        boolean done = false;

        while (!done){

            done = true;

            for(Edge edge: graph.getEdges()){
                if(distances.get(edge.getNodeTwo()) > distances.get(edge.getNodeOne()) + edge.getWeight()){
                    distances.put(edge.getNodeTwo(),distances.get(edge.getNodeOne()) + edge.getWeight());
                    predecessors.put(edge.getNodeTwo(),edge.getNodeOne());
                    done = false;
                }
            }
            isNegativeCycleDetected();
        }
        graph.setSolved(true);

    }
    public void isNegativeCycleDetected(){
        for(Edge edge: graph.getEdges()){
            if( distances.get(edge.getNodeOne())  != Integer.MAX_VALUE &&
                    (distances.get(edge.getNodeTwo()) > distances.get(edge.getNodeOne()) + edge.getWeight())){
                JOptionPane.showMessageDialog( null, "Negative Cycle Detected!");
            }
        }
    }
    public Integer getDestinationDistance(){
        return distances.get(graph.getDestination());
    }

    public List<Node> getDestinationPath() {
        return getPath(graph.getDestination());
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
