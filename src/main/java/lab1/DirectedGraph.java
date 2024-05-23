package lab1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;



public class DirectedGraph {
    private Map<String, GraphNode> nodeMap;
    private Map<String, java.util.List<String>> adjacencyMap;
    private Map<String, Integer> wordCountMap;
    private Map<String, Integer> edgeWeightMap;

    private Map<String, Map<String, Integer>> adjList;



    public DirectedGraph() {
        adjacencyMap = new HashMap<>();
        wordCountMap = new HashMap<>();
        edgeWeightMap = new HashMap<>();
        nodeMap = new HashMap<>();
        adjList = new HashMap<>();
    }

    public void addWord(String word) {
        if (!nodeMap.containsKey(word)) {
            nodeMap.put(word, new GraphNode(word));
            adjList.put(word, new HashMap<>());
        }
    }

    public void addEdge(String word1, String word2, int weight) {
        if (!nodeMap.containsKey(word1) || !nodeMap.containsKey(word2)) {
            throw new IllegalArgumentException("Both words must be added to the graph before adding an edge.");
        }
        adjList.get(word1).put(word2, weight);
    }

    public void showDirectedGraph(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("digraph G {\n");
            for (String node : adjList.keySet()) {
                Map<String, Integer> neighbors = adjList.get(node);
                for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                    String neighbor = entry.getKey();
                    int weight = entry.getValue();
                    writer.write(String.format("  \"%s\" -> \"%s\" [label=\"%d\"];\n", node, neighbor, weight));
                }
            }
            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 保存有向图为图形文件
    public void saveGraphToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String node : adjList.keySet()) {
                Map<String, Integer> neighbors = adjList.get(node);
                for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                    String neighbor = entry.getKey();
                    int weight = entry.getValue();
                    writer.write(String.format("%s %s %d\n", node, neighbor, weight));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DirectedGraph graph = new DirectedGraph();
        graph.addWord("A");
        graph.addWord("B");
        graph.addWord("C");

        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);

        graph.showDirectedGraph("graph.dot");
        graph.saveGraphToFile("graph.txt");
    }


    public void showLegend() {
        SwingUtilities.invokeLater(() -> {
            JFrame legendFrame = new JFrame("Legend");
            legendFrame.setSize(300, 200);
            legendFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel label1 = new JLabel("Legend:");
            JLabel label2 = new JLabel("Edges: Blue lines connecting nodes represent edges in the graph.");
            panel.add(label1);
            panel.add(label2);

            legendFrame.add(panel);
            legendFrame.setVisible(true);
        });
    }


public String queryBridgeWords(String word1, String word2) {
    StringBuilder result = new StringBuilder();

    GraphNode node1 = nodeMap.get(word1.toLowerCase());
    GraphNode node2 = nodeMap.get(word2.toLowerCase());

    if (node1 == null || node2 == null) {
        return "No " + (node1 == null ? word1 : word2) + " in the graph!";
    }

    java.util.List<String> bridgeWords = new ArrayList<>();
    for (GraphNode neighbor : node1.getNeighbors()) {
        if (neighbor.getNeighbors().contains(node2)) {
            bridgeWords.add(neighbor.getWord());
        }
    }

    if (bridgeWords.isEmpty()) {
        return "No bridge words from " + word1 + " to " + word2 + "!";
    }

    result.append("The bridge words from ").append(word1).append(" to ").append(word2).append(" are: ");
    for (int i = 0; i < bridgeWords.size(); i++) {
        result.append(bridgeWords.get(i));
        if (i < bridgeWords.size() - 1) {
            result.append(", ");
        }
    }
    return result.toString();
}

public String generateNewText(String inputText) {
    StringBuilder result = new StringBuilder();
    String[] words = inputText.split("[^a-zA-Z]+");

    for (int i = 0; i < words.length - 1; i++) {
        result.append(words[i]).append(" ");
        java.util.List<String> bridgeWords = findBridgeWords(words[i], words[i + 1]);
        if (!bridgeWords.isEmpty()) {
            Random random = new Random();
            String bridgeWord = bridgeWords.get(random.nextInt(bridgeWords.size()));
            result.append(bridgeWord).append(" ");
        }
    }
    result.append(words[words.length - 1]);
    return result.toString();
}

private java.util.List<String> findBridgeWords(String word1, String word2) {
    java.util.List<String> bridgeWords = new ArrayList<>();

    GraphNode node1 = nodeMap.get(word1.toLowerCase());
    GraphNode node2 = nodeMap.get(word2.toLowerCase());

    if (node1 != null && node2 != null) {
        for (GraphNode neighbor : node1.getNeighbors()) {
            if (neighbor.getNeighbors().contains(node2)) {
                bridgeWords.add(neighbor.getWord());
            }
        }
    }

    return bridgeWords;
}

    public String calcShortestPath(String word1, String word2) {
        GraphNode startNode = nodeMap.get(word1.toLowerCase());
        GraphNode endNode = nodeMap.get(word2.toLowerCase());

        if (startNode == null || endNode == null) {
            return "One or both words are not in the graph!";
        }

        Map<GraphNode, Integer> distances = new HashMap<>();
        Map<GraphNode, GraphNode> predecessors = new HashMap<>();
        // 创建一个比较器，用于比较节点的距离
        Comparator<GraphNode> comparator = (node1, node2) -> distances.get(node1) - distances.get(node2);

        // 创建 PriorityQueue 时传入比较器
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(comparator);

        for (GraphNode node : nodeMap.values()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }

        distances.put(startNode, 0);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            if (current == endNode) {
                break;
            }

            for (GraphNode neighbor : current.getNeighbors()) {
                int distance = distances.get(current) + 1;
                if (distance < distances.get(neighbor)) {
                    distances.put(neighbor, distance);
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (predecessors.get(endNode) == null) {
            return "No path exists between " + word1 + " and " + word2 + "!";
        }

        java.util.List<String> path = new ArrayList<>();
        GraphNode current = endNode;
        while (current != null) {
            path.add(current.getWord());
            current = predecessors.get(current);
        }
        Collections.reverse(path);

        return "The shortest path from " + word1 + " to " + word2 + " is: " + String.join(" -> ", path) +
                ". Length: " + (path.size() - 1);
    }

}
