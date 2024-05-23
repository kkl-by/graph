package lab1;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {
    private String word;
    private List<GraphNode> neighbors;

    public GraphNode(String word) {
        this.word = word.toLowerCase();
        this.neighbors = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public List<GraphNode> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(GraphNode neighbor) {
        neighbors.add(neighbor);
    }
}