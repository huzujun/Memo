package Memo.view;

import Memo.Controller;
import layout.TableLayout;

import javax.swing.*;
import java.awt.*;

public class UploadView implements View {
    public JLabel jLabel = new JLabel("", SwingConstants.CENTER);
    private Controller controller;
    private JTextField textField = new JTextField("127.0.0.1");

    public UploadView(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void initComponents() {
        JFrame frame = new JFrame();

        frame.setSize(600, 250);
        frame.setTitle("飞鸽传书");
        frame.setLocationRelativeTo(null);

        JButton jButton = new JButton("点击上传");
        jButton.addActionListener(e -> controller.setNet(textField.getText()));

        jButton.setFont(new Font("mono", Font.BOLD, 25));
        jButton.setBackground(Color.getHSBColor(255, 255, 70));

        double size[][] = {{0.2, 0.6, 0.2}, {0.2, 0.2, 0.2, 0.2, 0.2}};
        frame.setLayout(new TableLayout(size));
        frame.getContentPane().add(textField, "1, 1");
        frame.getContentPane().add(jButton, "1, 2");
        frame.getContentPane().add(jLabel, "1, 3");
        frame.setVisible(true);
    }
}
