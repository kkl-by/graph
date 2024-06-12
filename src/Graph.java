package src;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collections;

public class Graph {
    private Map<String, Map<String, Integer>> adjList;

    public Graph() {
        this.adjList = new HashMap<>();
    }

    public void addEdge(String from, String to) {
        adjList.putIfAbsent(from, new HashMap<>());
        Map<String, Integer> edges = adjList.get(from);
        edges.put(to, edges.getOrDefault(to, 0) + 1);
    }

    public static Graph loadGraphFromFile(File file) {
        Graph graph = new Graph();
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String modifiedString = content.replaceAll("[^a-zA-Z0-9\s]", "");
            String[] words = modifiedString.split("\\W+");
            for (int i = 0; i < words.length - 1; i++) {
                graph.addEdge(words[i].toLowerCase(), words[i + 1].toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }

    public static String showDirectedGraph(Graph graph) throws IOException, InterruptedException {
        String dotPath = "dot";

        StringBuilder dotFormat = new StringBuilder();
        dotFormat.append("digraph G {\n");

        for (Map.Entry<String, Map<String, Integer>> entry : graph.adjList.entrySet()) {
            String from = entry.getKey();
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                String to = edge.getKey();
                int weight = edge.getValue();
                dotFormat.append(String.format("  \"%s\" -> \"%s\" [label=\"%d\"];%n", from, to, weight));
            }
        }

        dotFormat.append("}\n");

        Path imageDir = Paths.get("src", "image");
        if (!Files.exists(imageDir)) {
            Files.createDirectories(imageDir);
        }

        String dotFilePath = "src/image/graph.dot";
        String pngFilePath = "src/image/graph.png";

        try (BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dotFilePath), StandardCharsets.UTF_8))) {
            fileWriter.write(dotFormat.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 使用 ProcessBuilder 构建命令和参数
        ProcessBuilder processBuilder = new ProcessBuilder(dotPath, "-Tpng", dotFilePath, "-o", pngFilePath);

        // 合并错误流到标准输出流
        processBuilder.redirectErrorStream(true);

        // 启动进程
        Process process = processBuilder.start();

        // 等待进程完成
        process.waitFor();

        // 返回生成的 PNG 文件路径
        return pngFilePath;    }



    public String queryBridgeWords(String word1, String word2) {
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }
        Set<String> bridgeWords = new HashSet<>();
        for (String word3 : adjList.get(word1).keySet()) {
            if (adjList.containsKey(word3) && adjList.get(word3).containsKey(word2)) {
                bridgeWords.add(word3);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }
        return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords) + ".";
    }

    public String generateNewText(String inputText) {
        String[] words = inputText.split("\\s+");
        StringBuilder newText = new StringBuilder(words[0]);
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            Set<String> bridgeWords = new HashSet<>();
            if (adjList.containsKey(word1)) {
                for (String word3 : adjList.get(word1).keySet()) {
                    if (adjList.containsKey(word3) && adjList.get(word3).containsKey(word2)) {
                        bridgeWords.add(word3);
                    }
                }
            }
            if (!bridgeWords.isEmpty()) {
                String[] bridges = bridgeWords.toArray(new String[0]);
                newText.append(" ").append(bridges[rand.nextInt(bridges.length)]);
            }
            newText.append(" ").append(word2);
        }
        return newText.toString();
    }

    public String calcShortestPath(String word1, String word2) {
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        for (String node : adjList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(word1, 0);
        queue.add(word1);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(word2)) break;
            for (Map.Entry<String, Integer> neighbor : adjList.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    queue.add(neighbor.getKey());
                }
            }
        }

        if (distances.get(word2) == Integer.MAX_VALUE) {
            return word1 + " is not reachable from " + word2;
        }

        List<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return "Shortest path: " + String.join(" -> ", path) + " with distance " + distances.get(word2);
    }

    public String randomWalk() {
        if (adjList.isEmpty()) return "The graph is empty!";
        SecureRandom rand = new SecureRandom();
        List<String> nodes = new ArrayList<>(adjList.keySet());
        String current = nodes.get(rand.nextInt(nodes.size()));
        StringBuilder walk = new StringBuilder(current);
        Set<String> visitedEdges = new HashSet<>();
        while (true) {
            Map<String, Integer> edges = adjList.get(current);
            if (edges == null || edges.isEmpty()) break;
            List<String> possibleEdges = new ArrayList<>(edges.keySet());
            String nextNode = possibleEdges.get(rand.nextInt(possibleEdges.size()));
            String edge = current + "->" + nextNode;
            if (visitedEdges.contains(edge)) break;
            visitedEdges.add(edge);
            walk.append(" -> ").append(nextNode);
            current = nextNode;
        }
        return walk.toString();
    }


}
