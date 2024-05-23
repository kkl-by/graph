package lab1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI extends JFrame {
    private DirectedGraph graph;
    private JTextField filenameField;
    private JTextArea resultArea;
    private JTextField word1Field;
    private JTextField word2Field;

    public GUI(DirectedGraph graph) {
        this.graph = graph;
        setTitle("Directed Graph GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JPanel panel = new JPanel(new GridLayout(3, 1));

        // 文件名输入
        JPanel filePanel = new JPanel();
        filePanel.add(new JLabel("Enter filename:"));
        filenameField = new JTextField(20);
        filePanel.add(filenameField);
        JButton saveButton = new JButton("Save Graph");
        saveButton.addActionListener(new SaveGraphListener());
        filePanel.add(saveButton);
        panel.add(filePanel);


        // 操作面板
        JPanel controlPanel = new JPanel(new GridLayout(4, 1));

        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.add(new JLabel("Enter word 1:"));
        word1Field = new JTextField(10);
        queryPanel.add(word1Field);
        queryPanel.add(new JLabel("Enter word 2:"));
        word2Field = new JTextField(10);
        queryPanel.add(word2Field);
        JButton queryButton = new JButton("Query Bridge Words");
        queryButton.addActionListener(new QueryBridgeWordsListener());
        queryPanel.add(queryButton);
        controlPanel.add(queryPanel);

        JPanel newTextPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextArea inputTextArea = new JTextArea(3, 20);
        newTextPanel.add(new JLabel("Enter text:"));
        newTextPanel.add(new JScrollPane(inputTextArea));
        JButton generateButton = new JButton("Generate New Text");
        generateButton.addActionListener(new GenerateNewTextListener(inputTextArea));
        newTextPanel.add(generateButton);
        controlPanel.add(newTextPanel);

        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField startField = new JTextField(10);
        JTextField endField = new JTextField(10);
        pathPanel.add(new JLabel("Enter start word:"));
        pathPanel.add(startField);
        pathPanel.add(new JLabel("Enter end word:"));
        pathPanel.add(endField);
        JButton pathButton = new JButton("Calculate Shortest Path");
        pathButton.addActionListener(new CalcShortestPathListener(startField, endField));
        pathPanel.add(pathButton);
        controlPanel.add(pathPanel);




        JButton legendButton = new JButton("Show Legend");
        legendButton.addActionListener(new ShowLegendListener());
        controlPanel.add(legendButton);

        panel.revalidate();
        panel.repaint();
//        legendButton.addActionListener(e -> graph.showLegend());
//        panel.add(legendButton);



        panel.add(controlPanel, BorderLayout.WEST);

        // 结果显示区域
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane);

        add(panel);
        setVisible(true);
    }

    private class SaveGraphListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filename = filenameField.getText();
            graph.saveGraphToFile(filename);
            resultArea.setText("Graph saved to file: " + filename);
        }
    }

    private class LoadGraphListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filename = filenameField.getText();
            graph.showDirectedGraph(filename);
            resultArea.setText("Graph loaded from file: " + filename);
        }
    }

    private class QueryBridgeWordsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String word1 = word1Field.getText();
            String word2 = word2Field.getText();
            String result = graph.queryBridgeWords(word1, word2);
            resultArea.setText(result);
        }
    }

    private class GenerateNewTextListener implements ActionListener {
        private JTextArea inputTextArea;

        public GenerateNewTextListener(JTextArea inputTextArea) {
            this.inputTextArea = inputTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String inputText = inputTextArea.getText();
            String newText = graph.generateNewText(inputText);
            resultArea.setText("Generated Text:\n" + newText);
        }
    }

    private class CalcShortestPathListener implements ActionListener {
        private JTextField startField;
        private JTextField endField;

        public CalcShortestPathListener(JTextField startField, JTextField endField) {
            this.startField = startField;
            this.endField = endField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String startWord = startField.getText();
            String endWord = endField.getText();
            String result = graph.calcShortestPath(startWord, endWord);
            resultArea.setText(result);
        }
    }

    // 添加显示图例的监听器
    private class ShowLegendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            graph.showLegend();
            // 这里显示图例
            JOptionPane.showMessageDialog(GUI.this, "Legend:\n" +
                    "Edges: Blue lines connecting nodes represent edges in the graph.");
        }
    }
}
