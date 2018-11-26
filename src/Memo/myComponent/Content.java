package Memo.myComponent;

import javax.swing.*;
import java.awt.*;

/**
 * 白色的 Jpanel
 */
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