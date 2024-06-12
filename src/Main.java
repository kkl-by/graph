package src;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Main {
    private static src.Graph graph;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graph Operations");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JButton loadButton = new JButton("Load Text File");
        loadButton.setBounds(50, 20, 200, 30);
        frame.add(loadButton);

        JButton showGraphButton = new JButton("Show Graph");
        showGraphButton.setBounds(50, 60, 200, 30);
        frame.add(showGraphButton);

        JButton bridgeWordsButton = new JButton("Find Bridge Words");
        bridgeWordsButton.setBounds(50, 100, 200, 30);
        frame.add(bridgeWordsButton);

        JButton newTextButton = new JButton("Generate New Text");
        newTextButton.setBounds(50, 140, 200, 30);
        frame.add(newTextButton);

        JButton shortestPathButton = new JButton("Shortest Path");
        shortestPathButton.setBounds(50, 180, 200, 30);
        frame.add(shortestPathButton);

        JButton randomWalkButton = new JButton("Random Walk");
        randomWalkButton.setBounds(50, 220, 200, 30);
        frame.add(randomWalkButton);

        JTextArea outputArea = new JTextArea();
        outputArea.setBounds(300, 20, 450, 500);
        frame.add(outputArea);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    graph = Graph.loadGraphFromFile(file);
                    outputArea.setText("Graph loaded from file: " + file.getName());
                }
            }
        });

        showGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph != null) {
                    try {
                        // 可能发生IOException的代码
                        String pngFilePath = Graph.showDirectedGraph(graph);
                        outputArea.setText("Graph loaded from file: " + pngFilePath);
                        JFrame frame = new JFrame("Graph Visualization");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        ImageIcon icon = new ImageIcon(pngFilePath);
                        JLabel label = new JLabel(icon);
                        frame.getContentPane().add(label);
                        frame.pack();
                        frame.setVisible(true);
                    } catch (IOException | InterruptedException e2) {
                        // 处理异常的代码
                        e2.printStackTrace();
                    }

                } else {
                    outputArea.setText("No graph loaded.\n");
                }
            }
        });

        bridgeWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph != null) {
                    String word1 = JOptionPane.showInputDialog("Enter first word:");
                    String word2 = JOptionPane.showInputDialog("Enter second word:");
                    String result = graph.queryBridgeWords(word1, word2);
                    outputArea.setText(result);
                } else {
                    outputArea.setText("No graph loaded.");
                }
            }
        });

        newTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph != null) {
                    String inputText = JOptionPane.showInputDialog("Enter text:");
                    String result = graph.generateNewText(inputText);
                    outputArea.setText(result);
                } else {
                    outputArea.setText("No graph loaded.");
                }
            }
        });

        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph != null) {
                    String word1 = JOptionPane.showInputDialog("Enter first word:");
                    String word2 = JOptionPane.showInputDialog("Enter second word:");
                    String result = graph.calcShortestPath(word1, word2);
                    outputArea.setText(result);
                } else {
                    outputArea.setText("No graph loaded.");
                }
            }
        });

        randomWalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph != null) {
                    String result = graph.randomWalk();
                    outputArea.setText(result);
                } else {
                    outputArea.setText("No graph loaded.");
                }
            }
        });

        frame.setVisible(true);
    }
}