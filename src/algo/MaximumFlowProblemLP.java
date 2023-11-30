package algo;

import gurobi.*;
import models.Edge;
import models.Graph;
import models.Node;

import java.util.HashMap;
import java.util.Map;

public class MaximumFlowProblemLP {

    private boolean safe = false;
    private String message = null;

    private GRBModel model;
    private int status;
    private String resultMessage;
    private Graph graph;

    public MaximumFlowProblemLP(Graph graph){
        this.graph = graph;

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
                            x[i][j] = model.addVar(0.0, getEdgeWeight(i,j), 0.0, GRB.CONTINUOUS, "x_" + (i+1) + "_" + (j+1));
                    }
                }
            }

            // Update the model to integrate new variables
            GRBVar V = model.addVar(0.0,GRB.CONTINUOUS,0.0,GRB.CONTINUOUS,"V");
            model.update();


            // Set the objective function
            GRBLinExpr objExpr = new GRBLinExpr();
            objExpr.addTerm(1,V);
            model.setObjective(objExpr, GRB.MAXIMIZE);

            // Add constraints
            for (int i = 0; i < n; i++) {
                GRBLinExpr exprSource = new GRBLinExpr();
                GRBLinExpr exprFlowBalance = new GRBLinExpr();
                GRBLinExpr exprDest = new GRBLinExpr();
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        if(isNodeIncludedInEdges(i)){
                            if (isValidEdge(i,j)){
                                if (isSourceNode(i)){
                                    exprSource.addTerm(1.0,x[i][j]);
                                } else if (isDestinationNode(i)) {
                                    exprDest.addTerm(-1.0,x[i][j]);
                                } else{
                                    exprFlowBalance.addTerm(1.0, x[i][j]);

                                }
                            }else if(isValidEdge(j,i)){
                                if (isSourceNode(i)){
                                    exprSource.addTerm(-1.0, x[j][i]);
                                } else if (isDestinationNode(i)) {
                                    exprDest.addTerm(1.0, x[j][i]);
                                }else {
                                    exprFlowBalance.addTerm(-1.0, x[j][i]);
                                }
                            }
                        }
                    }
                }
                if(isSourceNode(i))
                    model.addConstr(exprSource, GRB.EQUAL, V , "FlowBalanceConstrSource");
                else if (isDestinationNode(i)) {
                    model.addConstr(exprDest, GRB.EQUAL, V , "FlowBalanceConstrDest");
                } else
                    model.addConstr(exprFlowBalance, GRB.EQUAL, 0, "FlowBalanceConstr_"+(i+1));
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
                                }
                            }
                        }
                    }
                }
                resultMessage = "Maximum Flow Problem : \n" +
                        "Optimal solution found ! \n" +
                        "Objective value (LP Primal Objective): " + model.get(GRB.DoubleAttr.ObjVal) + "\n\n" +
                        "Decision Variables (Ignoring dv with value 0): \n" + dvResult;
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
    public boolean isDestinationNode(int indexOne){
        boolean flag = false;
        String nodeOne = "Node " + (indexOne + 1);

        if(graph.getDestination().toString().equals(nodeOne))
            flag = true;
        return flag;
    }
    public String getOptimizationResult(){
        return resultMessage;
    }

}
