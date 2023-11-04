package models;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Node {
    private Point coord = new Point();
    private int id;
    private java.util.List<Node> path;
    private Map<Node, Integer> distancesToAllNodes;

    private Map<Node, Node> predecessorFW;

    public Node(){}

    public Node(int id){
        this.id = id;
    }

    public Node(Point p){
        this.coord = p;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCoord(int x, int y){
        coord.setLocation(x, y);
    }

    public Point getCoord(){
        return coord;
    }

    public void setPath(List<Node> path) {
        this.path = path;
    }

    public List<Node> getPath() {
        return path;
    }

    public int getX(){
        return (int) coord.getX();
    }

    public int getY(){
        return (int) coord.getY();
    }

    public int getId(){
        return id;
    }

    public Map<Node, Integer> getDistancesToAllNodes() {
        return distancesToAllNodes;
    }

    public void setDistancesToAllNodes(Map<Node, Integer> distancesToAllNodes) {
        this.distancesToAllNodes = distancesToAllNodes;
    }

    public Map<Node, Node> getPredecessorFW() {
        return predecessorFW;
    }

    public void setPredecessorFW(Map<Node, Node> predecessorFW) {
        this.predecessorFW = predecessorFW;
    }

    @Override
    public String toString() {
        return "Node " + id;
    }
}
