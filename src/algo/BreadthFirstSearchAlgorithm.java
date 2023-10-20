package algo;

import models.Edge;
import models.Graph;
import models.Node;

import java.util.*;

public class BreadthFirstSearchAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Map<Node, Integer> distances;
    private Graph graph;
    private Map<Node, Node> predecessors;

    private HashSet<Node> traversedNodes;
    private PriorityQueue<Node> marked;


    public class NodeComparator implements Comparator<Node>  {
        @Override
        public int compare(Node node1, Node node2) {
            return distances.get(node1) - distances.get(node2);
        }
    };
    public BreadthFirstSearchAlgorithm(Graph graph){
        this.graph = graph;
        marked = new PriorityQueue<>();
        predecessors = new HashMap<>();
        traversedNodes = new HashSet<Node>();
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

        for(Node node : graph.getNodes()){
            if(!graph.isNodeReachable(node)){
                message = "Graph contains unreachable nodes";
                return false;
            }
        }

        return true;
    }

    private Node getAdjacent(Edge edge, Node node) {
        if(edge.getNodeOne()!=node)
            return null;

        return node==edge.getNodeTwo()?edge.getNodeOne():edge.getNodeTwo();
    }

    private List<Edge> getNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for(Edge edge : graph.getEdges()){
            if(edge.getNodeOne()==node)
                neighbors.add(edge);
        }

        return neighbors;
    }

    public void run() throws IllegalStateException {
        if(!safe) {
            throw new IllegalStateException(message);
        }
        marked = new PriorityQueue<>(graph.getNodes().size(), new NodeComparator());
        traversedNodes = new HashSet<>(graph.getNodes().size());
        Node source = graph.getSource();
        marked.add(source);
        traversedNodes.add(source);


        while (!marked.isEmpty()){

            Node selectedNode = marked.peek();

            for (Edge neighbor : getNeighbors(selectedNode)) {
                Node adjacent = getAdjacent(neighbor, selectedNode);
                if (adjacent != null && !marked.contains(adjacent)){
                    distances.put(adjacent, neighbor.getWeight());
                    marked.add(adjacent);
                    traversedNodes.add(adjacent);
                    predecessors.put(adjacent, selectedNode);
                }
            }
            marked.remove(selectedNode);
        }
        graph.setSolved(true);
    }

    public List<Node> getDestinationPath() {
        return getPath(graph.getDestination());
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
    public String getDestinationPathAsString(){
        String path = "";
        List<Node> nodeList = getPath(graph.getDestination());
        for(int i = 0; i < nodeList.toArray().length-1; i++){
            path += nodeList.toArray()[i] + "->";
        }
        path += nodeList.toArray()[nodeList.toArray().length-1];
        return path;
    }
}
