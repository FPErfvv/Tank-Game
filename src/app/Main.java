package app;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Toolkit;


public class Main  {
    static final JFrame f = new JFrame("Tank Game");

    public static void main(String[] args) throws Exception {
        MainPanel mainPanel = new MainPanel(f);
        f.add(mainPanel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setExtendedState (JFrame.MAXIMIZED_BOTH);
        //f.setMinimumSize(new Dimension(250,250));
        f.setVisible(true);        
    }
}
