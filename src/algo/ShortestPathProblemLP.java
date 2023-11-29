package algo;

import gurobi.*;
import models.Edge;
import models.Graph;
import models.Node;

public class ShortestPathProblemLP {

    private boolean safe = false;
    private String message = null;

    private Graph graph;

    public ShortestPathProblemLP(Graph graph){
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
            GRBModel model = new GRBModel(env);
            int n = graph.getNodes().size();
            GRBVar[][] x = new GRBVar[n][n];
            GRBVar[] w = new GRBVar[n];

            for (int i = 0; i < n; i++) {
                w[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "w_" + i);

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        x[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
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
                        objExpr.addTerm(1.0, x[i][j]);
                    }
                }
            }
            model.setObjective(objExpr, GRB.MINIMIZE);

            // Add constraints
            for (int i = 0; i < n; i++) {
                GRBLinExpr expr1 = new GRBLinExpr();
                GRBLinExpr expr2 = new GRBLinExpr();

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        expr1.addTerm(1.0, x[i][j]);
                        expr2.addTerm(1.0, x[j][i]);
                    }
                }

                model.addConstr(expr1, GRB.EQUAL, n - 1, "constraint1_" + i);
                model.addConstr(expr2, GRB.EQUAL, 1.0, "constraint2_" + i);
            }

            // Optimize the model
            model.optimize();

            // Dispose of the Gurobi environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }


        graph.setSolved(true);

    }
}
