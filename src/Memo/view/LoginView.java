package Memo.view;

import Memo.Controller;
import layout.TableLayout;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;

public class LoginView implements View {
    private JFrame frame;
    private Controller controller;

    public LoginView(Controller controller) {
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
        JLabel label = new JLabel("请输入您的密码", SwingConstants.CENTER);
        JPasswordField passwordField = new JPasswordField();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setTitle("登录");
        frame.setLocationRelativeTo(null);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        label.setFont(new Font("mono", Font.BOLD, 25));
        label.setAlignmentX(0.5F);
        panel.add(label);
        panel.add(passwordField);
        JLabel fail = new JLabel("密码错误, 请重试");
        fail.setFont(new Font("mono", Font.ITALIC, 20));
        fail.setForeground(Color.red);
        passwordField.addActionListener(e -> {
            try {
                if (controller.authenticate(passwordField.getPassword())) {
                    frame.setVisible(false);
                    controller.openMemo();
                } else {
                    panel.add(fail);
                    fail.setAlignmentX(0.5F);
                    frame.revalidate();
                    frame.repaint();
                }
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
        });

        double size[][] = {{0.25, 0.5, 0.25}, {0.45, 0.1, 0.45}};
        frame.setLayout(new TableLayout(size));
        frame.getContentPane().add(panel, "1, 1");
        frame.setVisible(true);
    }

}

