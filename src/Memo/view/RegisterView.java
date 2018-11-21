package Memo.view;

import Memo.Controller;
import layout.TableLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RegisterView implements View{
    private JFrame frame;
    private Controller controller;

    public RegisterView(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void initComponents() {
        frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel("请输入您的初始密码", SwingConstants.CENTER);
        JPasswordField passwordField = new JPasswordField();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setTitle("新用户注册");
        frame.setLocationRelativeTo(null);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        label.setFont(new Font("mono", Font.BOLD, 25));
        label.setAlignmentX(0.5F);
        panel.add(label);
        panel.add(passwordField);
        passwordField.addActionListener(e -> {
            try {
                controller.setPassword(passwordField.getPassword());
                frame.setVisible(false);
                controller.openLogin();
            } catch (NoSuchAlgorithmException | IOException e1) {
                e1.printStackTrace();
            }
        });

        double size[][] = {{0.25, 0.5, 0.25}, {0.45, 0.1, 0.45}};
        frame.setLayout(new TableLayout(size));
        frame.getContentPane().add(panel, "1, 1");
        frame.setVisible(true);
    }
}
