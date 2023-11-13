package models;

public class Edge {
    private Node one;
    private Node two;
    private int weight = 1;
    private int residual;
    private int flow;
    private boolean isActive = true;
    public Edge(Node one, Node two){
        this.one = one;
        this.two = two;
    }

    public Node getNodeOne(){
        return one;
    }

    public Node getNodeTwo(){
        return two;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public int getWeight(){
        return weight;
    }

    public boolean hasNode(Node node){
        return one==node || two==node;
    }

    public boolean equals(Edge edge) {
        return (one ==edge.one && two ==edge.two) || (one ==edge.two && two ==edge.one) ;
    }

    public int getResidual() {
        return residual;
    }

    public void setResidual(int residual) {
        this.residual = residual;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Edge ~ "
                + getNodeOne().getId() + " - "
                + getNodeTwo().getId();
    }
}
