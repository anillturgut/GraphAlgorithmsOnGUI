package algo;

import java.util.*;

import models.Edge;
import models.Graph;
import models.Node;
public class PreFlowPushAlgorithm {

    private boolean safe = false;
    private String message = null;

    private Map<Node, Integer> distances;
    private Graph graph;

    public PreFlowPushAlgorithm(Graph graph){
        this.graph = graph;
        this.distances = new HashMap<>();
        for (Edge edge: graph.getEdges()){
            edge.setResidual(edge.getWeight());
            edge.setFlow(0);
        }
        for (Node node: graph.getNodes()){
            node.setExcess(0);
            distances.put(node,0);
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
        preprocess(source);
        // While there are active nodes, push or relabel
        Node activeNode = getActiveNode();
        while (activeNode != null) {
            if (!push(activeNode)) {
                relabel(activeNode);
            }
            activeNode = getActiveNode();
        }
        graph.setSolved(true);
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

    /*
     * Push:
     * - Push flow from node with excess flow to those below it
     * - Decreases excess flow of pushing node
     * - Increases excess flow of pushed node
     * - Increases flow along the edge connecting them
     * - Updates (or creates) reverse edge along nodes
     */
    private boolean push(Node n) {
        for (Edge e : getNeighbors(n)) {
            if ((distances.get(n)> distances.get(e.getNodeTwo())) && (e.getFlow() != e.getWeight())) {
                int flow = Math.min(e.getWeight() - e.getFlow(), n.getExcess());
                int excessFlowOne = n.getExcess();
                excessFlowOne -= flow;
                n.setExcess(excessFlowOne);
                int excessFlowTwo = e.getNodeTwo().getExcess();
                excessFlowTwo += flow;
                e.getNodeTwo().setExcess(excessFlowTwo);
                int flowEdge = e.getFlow();
                flowEdge += flow;
                e.setFlow(flowEdge);
                updateReverseEdge(e, flow);
                return true;
            }
        }
        return false;
    }
    /*
     * Relabel:
     * - Finds adjacent node with lower height
     * - Sets height of node to one greater than that
     */
    private void relabel(Node n){
        int minDistance = Integer.MAX_VALUE;
        for (Edge e: getNeighbors(n)){
            if ((e.getFlow() != e.getWeight()) && (distances.get(e.getNodeTwo()) < minDistance)) {
                minDistance = distances.get(e.getNodeTwo());
                distances.put(n, minDistance + 1);
            }
        }
    }
    /*
     * Active Node:
     * - A node (excluding source and sink) with excess flow
     */
    private Node getActiveNode() {
        for (Node node : graph.getNodes()){
            if ((node.getExcess() > 0) && !node.equals(graph.getDestination())){
                return node;
            }
        }
        return null;
    }
    /*
     * Active Node:
     * - A node (excluding source and sink) with excess flow
     */
    public void preprocess(Node source){
        distances.put(source,graph.getNodes().size());

        for(Edge edge: getNeighbors(source)){
            edge.setFlow(edge.getWeight());
            Node adjacent = getAdjacent(edge,source);
            if (adjacent != null){
                int adjacentExcess = adjacent.getExcess();
                adjacentExcess += edge.getFlow();
                adjacent.setExcess(adjacentExcess);
                adjacent.addEdge(source,-edge.getFlow(),0);
            }
        }
    }
    /*
     * Reverse Edge
     * - Used by the residual graph to allocate flow
     * - Represented here by negative flow and zero capacity
     */
    private void updateReverseEdge(Edge edge, int flow){
        for(Edge e: getNeighbors(edge.getNodeTwo())){
            if(e.getNodeTwo().equals(edge.getNodeOne())){
                int edgeFlow = e.getFlow();
                edgeFlow -= flow;
                e.setFlow(edgeFlow);
            }
        }
        edge.getNodeTwo().addEdge(edge.getNodeOne(),-flow,0);
    }
    public int getMaxFlow(Node destination){ return destination.getExcess();}

    public String getEdgeFlowAsString(List<Edge> edges){
        String edgeFlows = "";
        edges.sort(Comparator.comparing(Edge::toString));
        for(Edge edge : edges){
            String flow = edge.toString() + " :  (";
            flow += edge.getFlow() + "/" + edge.getWeight() + ")";
            if (edge.getFlow() == edge.getWeight()){
                flow += "   - Full";
            }else if (!(edge.getFlow() == edge.getWeight()) && edge.getFlow() > 0){
                flow += "   - Partially used";
            } else {
                flow += "   - Not used";
            }
            edgeFlows += flow + "\n";
        }
        return edgeFlows;
    }
}
