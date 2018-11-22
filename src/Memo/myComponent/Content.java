package Memo.myComponent;

import javax.swing.*;
import java.awt.*;

public class Content extends JPanel {
    public Content() {
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.repaint();
        //this.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
    }

}