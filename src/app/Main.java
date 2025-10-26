package app;

import javax.swing.JFrame;

import java.awt.Dimension;

public class Main {
    static final JFrame f = new JFrame("Tank Game - Singleplayer");

    public static void main(String[] args) throws Exception {
        MainPanel mainPanel = new MainPanel(f);
        f.add(mainPanel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setMinimumSize(new Dimension(800, 600));
        f.setVisible(true);
        
        mainPanel.run();
    }
}
