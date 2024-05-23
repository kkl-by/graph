package lab1;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filename = selectedFile.getAbsolutePath();

            DirectedGraph graph = new DirectedGraph();

            // 读取文件，构建图

            try {

                FileReader fileReader = new FileReader(filename);
                BufferedReader br = new BufferedReader(fileReader);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("[^a-zA-Z]+");
                    for (String word : words) {
                        graph.addWord(word);
                    }
                    for (int i = 0; i < words.length - 1; i++) {
                        graph.addEdge(words[i], words[i + 1]);
                    }
                }
                br.close();
                fileReader.close();
            } catch (IOException e) {
                System.out.println("发生了 I/O 异常：" + e.getMessage());
                e.printStackTrace();
                return; // 发生异常时退出
            }


            // 创建 GUI 界面并显示
            GUI gui = new GUI(graph);
            gui.setVisible(true);
        }
    }
}
