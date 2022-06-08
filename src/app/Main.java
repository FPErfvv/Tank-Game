package app;
import javax.swing.JFrame;


public class Main  {
    static JFrame f = new JFrame("Tank Game");

    public static void main(String[] args) throws Exception {
        MainPanel mainPanel = new MainPanel(f);
        f.add(mainPanel);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500,500);
        f.setVisible(true);        
    }
}
