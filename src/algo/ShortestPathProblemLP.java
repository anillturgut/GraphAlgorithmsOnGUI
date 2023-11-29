package algo;

import gurobi.*;
import models.Edge;
import models.Graph;
import models.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortestPathProblemLP {

    private boolean safe = false;
    private String message = null;

    private GRBModel model;
    private int status;

    private String resultMessage;

    private String fullResultMessage;

    private Graph graph;

    Map<Node,Node> predecessors;
    private Map<String,Double> decisionVariables;

    public ShortestPathProblemLP(Graph graph){
        this.graph = graph;
        decisionVariables = new HashMap<>();
        predecessors = new HashMap<>();

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

        try {
            // Create a new Gurobi environment
            GRBEnv env = new GRBEnv();
            env.set("OutputFlag", "0");  // Suppress Gurobi output

            // Create a new Gurobi model
            model = new GRBModel(env);
            int n = graph.getNodes().size();
            GRBVar[][] x = new GRBVar[n][n];

             for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if (isValidEdge(i,j))
                            x[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + (i+1) + "_" + (j+1));
                    }
                }
            }

            // Update the model to integrate new variables
            model.update();

            // Set the objective function
            GRBLinExpr objExpr = new GRBLinExpr();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if (isValidEdge(i,j))
                            objExpr.addTerm(getEdgeWeight(i, j), x[i][j]);
                    }
                }
            }
            model.setObjective(objExpr, GRB.MINIMIZE);

            // Add constraints
            for (int i = 0; i < n; i++) {
                GRBLinExpr exprSource = new GRBLinExpr();
                GRBLinExpr exprFlowBalance = new GRBLinExpr();
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if(isNodeIncludedInEdges(i)){
                            if (isValidEdge(i,j)){
                                if (isSourceNode(i)){
                                    exprSource.addTerm(-1.0,x[i][j]);
                                }else{
                                    exprFlowBalance.addTerm(-1.0, x[i][j]);

                                }
                            }else if(isValidEdge(j,i)){
                                exprFlowBalance.addTerm(1.0, x[j][i]);
                            }
                        }
                    }
                }
                if(isSourceNode(i))
                    model.addConstr(exprSource, GRB.EQUAL, -(n-1), "FlowBalanceConstrSource");
                else
                    model.addConstr(exprFlowBalance, GRB.EQUAL, 1, "FlowBalanceConstr_"+(i+1));
            }

            // Optimize the model
            model.optimize();

            // Check the optimization status
            status = model.get(GRB.IntAttr.Status);

            // Check the optimization status
            if (status == GRB.Status.OPTIMAL) {
                String dvResult = "";
                String dvResultFull = "";
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (x[i][j] != null) {
                            if(isValidEdge(i,j)) {
                                dvResultFull += x[i][j].get(GRB.StringAttr.VarName) + " = " + x[i][j].get(GRB.DoubleAttr.X) + "\n";
                                if (x[i][j].get(GRB.DoubleAttr.X) > 0) {
                                    dvResult += x[i][j].get(GRB.StringAttr.VarName) + " = " + x[i][j].get(GRB.DoubleAttr.X) + "\n";
                                    decisionVariables.put(x[i][j].get(GRB.StringAttr.VarName), x[i][j].get(GRB.DoubleAttr.X));
                                    String[] parts = x[i][j].get(GRB.StringAttr.VarName).split("_");
                                    predecessors.put(getNode(parts[2]),getNode(parts[1]));
                                }
                            }
                        }
                    }
                }
                resultMessage = "Optimal solution found ! \n" +
                        "Objective value (LP Primal Objective): " + model.get(GRB.DoubleAttr.ObjVal) + "\n\n" +
                        "Decision Variables (Ignoring dv with value 0): \n" + dvResult;
                fullResultMessage = "Optimal solution found ! \n" +
                        "Objective value (LP Primal Objective): " + model.get(GRB.DoubleAttr.ObjVal) + "\n\n" +
                        "Decision Variables (Ignoring dv with value 0): \n" + dvResultFull;
            } else if (status == GRB.Status.INFEASIBLE) {
                // The problem is infeasible
                resultMessage = "The problem is infeasible.";
            } else if (status == GRB.Status.UNBOUNDED) {
                // The problem is unbounded
                resultMessage = "The problem is unbounded.";
            } else {
                // Other status (e.g., GRB.Status.ERROR)
                resultMessage = "Optimization ended with status " + status;
            }

            if (model.get(GRB.IntAttr.Status) == GRB.Status.OPTIMAL) {
                System.out.println("Optimal solution found!");

            }
            // Dispose of the Gurobi environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }


        graph.setSolved(true);

    }

    public int getEdgeWeight(int indexOne, int indexTwo){
        String nodeOne = "Node " + (indexOne + 1);
        String nodeTwo = "Node " + (indexTwo + 1);

        for(Edge edge : graph.getEdges()){
            if (edge.getNodeOne().toString().equals(nodeOne) && edge.getNodeTwo().toString().equals(nodeTwo)){
                return edge.getWeight();
            }
        }
        return 0;
    }

    public boolean isValidEdge(int indexOne, int indexTwo){
        boolean flag = false;
        String nodeOne = "Node " + (indexOne + 1);
        String nodeTwo = "Node " + (indexTwo + 1);

        for(Edge edge : graph.getEdges()){
            if (edge.getNodeOne().toString().equals(nodeOne) && edge.getNodeTwo().toString().equals(nodeTwo)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isNodeIncludedInEdges(int indexNode){
        boolean flag = false;
        String node = "Node " + (indexNode + 1);
        for(Edge edge : graph.getEdges()){
            if (edge.getNodeOne().toString().equals(node) || edge.getNodeTwo().toString().equals(node)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean isSourceNode(int indexOne){
        boolean flag = false;
        String nodeOne = "Node " + (indexOne + 1);

        if(graph.getSource().toString().equals(nodeOne))
            flag = true;
        return flag;
    }

    public String getOptimizationResult() throws GRBException {
        return resultMessage;
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

    public Node getNode(String index){
        String nodeName = "Node " + index;
        for (Node node : graph.getNodes()){
            if(node.toString().equals(nodeName)){
                return node;
            }
        }
        return null;
    }
    public int getEdgeWeightInPath(Node one, Node two){
        for (Edge edge : graph.getEdges()){
            if (edge.getNodeOne().equals(one) && edge.getNodeTwo().equals(two))
                return edge.getWeight();
        }
        return 0;
    }
    public int getDestinationDistance(){
        int distance = 0;
        List<Node> nodeList = getPath(graph.getDestination());
        for (int i = 0; i < nodeList.size() - 1; i++){
            distance += getEdgeWeightInPath(nodeList.get(i), nodeList.get(i + 1));
        }
        return distance;
    }
    public String getLogResultMessage(){
        return  fullResultMessage;
    }
}
