import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Scanner;

import layout.TableLayout;

class View {
    private static View view = new View();
    static View getInstance() {
        return view;
    }
    int dy[] = new int[100], cnt;

    private View() {
    }
    void viewInit() throws FileNotFoundException {
        removeMemo.setLayout(new BoxLayout(removeMemo, BoxLayout.Y_AXIS));
        localMemo.setLayout(new BoxLayout(localMemo, BoxLayout.Y_AXIS));
        for (int i=1; i<=100; i++){
            File file = new File(String.format("text/%d", i));
            StringBuilder s = new StringBuilder();
            if (file.exists()){
                dy[++cnt]=i;
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()){
                    String thisLine = sc.nextLine();
                    s.append(thisLine).append("\n");
                }
                sc.close();
                String[] text_lines = s.toString().split("\n");
                addLocalJscoll(text_lines[0]);
            }
        }
    }

    private Model model = Model.getInstance();

    void register() {
        JFrame frame = new JFrame();
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
                model.setPassword(passwordField.getPassword());
                login();
                frame.setVisible(false);
            } catch (IOException | NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
        });

        double size[][] = {{0.25, 0.5, 0.25}, {0.45, 0.1, 0.45}};
        frame.setLayout(new TableLayout(size));
        frame.getContentPane().add(panel, "1, 1");
        frame.setVisible(true);
    }

    void login() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel("请输入您的密码", SwingConstants.CENTER);
        JPasswordField passwordField = new JPasswordField();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setVisible(true);
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
                if (model.authenticate(passwordField.getPassword())) {
                    mono();
                    frame.setVisible(false);
                } else {
                    panel.add(fail);
                    fail.setAlignmentX(0.5F);
                    frame.revalidate();
                    frame.repaint();
                }
            } catch (NoSuchAlgorithmException | IOException e1) {
                e1.printStackTrace();
            }
        });

        double size[][] = {{0.25, 0.5, 0.25}, {0.45, 0.1, 0.45}};
        frame.setLayout(new TableLayout(size));
        frame.getContentPane().add(panel, "1, 1");
    }
    private JFrame frame = new JFrame();
    private JTextArea textArea = new JTextArea();
    private int nowEdit = -1;
    void mono() throws IOException {
        frame = new JFrame();
        frame.setSize(1200, 900);
        frame.setTitle("备忘录");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double size[][] = {{0.3, 0.7}, {TableLayout.FILL}};
        frame.setLayout(new TableLayout(size));
        Content right = new Content(), left = new Content();

        frame.getContentPane().add(left, "0, 0");
        frame.getContentPane().add(right, "1, 0");
        //divide the whole frame to two part: left and right

        //right Part
        right.setLayout(new BorderLayout(0, 0));
        JButton save = new JButton("保存");
        textArea.setFont(new Font("mono", Font.PLAIN, 20));
        textArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        textArea.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent ke )
            {
                //System.out.println("key pressed fired");
                int code = ke.getKeyCode();
                int modifiers = ke.getModifiers();
                if(code == KeyEvent.VK_ENTER && modifiers == KeyEvent.SHIFT_MASK)
                {
                    if (nowEdit == -1) {
                        try {
                            model.addLocalMemo(textArea.getText());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        textArea.setText("");
                    }else {
                        try {
                            model.save(textArea.getText(), nowEdit);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        save.addActionListener(e -> {
            if (nowEdit == -1) {
                try {
                    model.addLocalMemo(textArea.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                textArea.setText("");
            }else {
                try {
                    model.save(textArea.getText(), nowEdit);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        right.add("Center", textArea);

        save.setFont(new Font("mono", Font.BOLD, 25));
        save.setBackground(Color.getHSBColor(255, 255, 70));
        right.add("South", save);

        //left part
        double size_left[][] = {{TableLayout.FILL}, {0.5, 0.5}};
        left.setLayout(new TableLayout(size_left));
        Content up = new Content();
        Content down = new Content();
        left.setBackground(Color.WHITE);
        left.add(up, "0, 0");
        left.add(down, "0, 1");
        //divide the panel to two part: up and down
        up.setLayout(new BorderLayout(0, 0));
        Content p1 = new Content();
        double ss[][] = {{0.765, TableLayout.FILL}, {TableLayout.FILL}};
        p1.setLayout(new TableLayout(ss));
        JLabel local_label = new JLabel(" 本地备忘录");
        ImageIcon icon = new ImageIcon("img/wavy.gif", "wavy-line border icon"); //20x22
        local_label.setFont(new Font("mono", Font.BOLD, 25));
        p1.add(local_label, "0, 0");

        JButton local_more = new JButton(new ImageIcon("img/menu-button.png"));
        local_more.setBorderPainted(false);
        local_more.setContentAreaFilled(false);
        local_more.setFocusPainted(false);
        local_more.setOpaque(false);
        p1.add(local_more, "1, 0");

        //p1.setBorder(BorderFactory.createMatteBorder(-1, -1, -1, -1, icon));
        p1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        up.add("North", p1);

        //Scroll pane
        JScrollPane jscrollPane1 = new JScrollPane(localMemo,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        up.add("Center", jscrollPane1);

        JButton create = new JButton("新建");
        create.setFont(new Font("mono", Font.BOLD, 25));
        create.setForeground(Color.white);
        create.setBackground(new Color(29, 158, 0));
        create.addActionListener(e -> {
            textArea.setText("");
            nowEdit = -1;
        });
        up.add("South", create);

        Content p2 = new Content();
        p2.setLayout(new TableLayout(ss));
        p2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        //p2.setBorder(BorderFactory.createMatteBorder(-1, -1, -1, -1, icon));
        down.setLayout(new BorderLayout(0, 0));
        JLabel remove_label = new JLabel(" 网络备忘录");
        remove_label.setFont(new Font("mono", Font.BOLD, 25));
        JButton remove_more = new JButton(new ImageIcon("img/menu-button.png"));
        remove_more.setBorderPainted(false);
        remove_more.setContentAreaFilled(false);
        remove_more.setFocusPainted(false);
        remove_more.setOpaque(false);
        p2.add(remove_label, "0, 0");
        p2.add(remove_more, "1, 0");

        //Scroll pane
        JScrollPane jscrollPane2 = new JScrollPane(removeMemo,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        down.add("Center", jscrollPane2);

        down.add("North", p2);
        JLabel ipLable = new JLabel("局域网地址:   127.0.0.1:5000");
        ipLable.setFont(new Font("mono", Font.ITALIC, 20));
        down.add("South", ipLable);
        ipLable.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        down.setVisible(true);
        frame.setVisible(true);
    }

    public class Content extends JPanel {
        Content() {
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
    private Content localMemo = new Content(), removeMemo = new Content();
    void addLocalJscoll(String overview){
        Content p = new Content();
        p.setPreferredSize(new Dimension(350, 50));
        p.setMaximumSize(new Dimension(350, 50));
        p.setLayout(new BorderLayout(0, 0));
        int y, m, d, h, mi;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        mi = cal.get(Calendar.MINUTE);
        JButton jButton = new JButton();
        jButton.setLayout(new BorderLayout());
        JLabel label1 = new JLabel(y + "年" + m + "月" + d + "日 " + h + ":" + ((mi < 10) ? "0" + mi: mi));
        JLabel label2 = new JLabel(overview);
        label1.setFont(new Font("mono", Font.PLAIN, 18));
        label2.setFont(new Font("mono", Font.PLAIN, 18));
        jButton.add(BorderLayout.NORTH, label1);
        jButton.add(BorderLayout.SOUTH, label2);

        jButton.setBackground(Color.white);
        jButton.setBorder(null);
        int tmp = cnt;
        jButton.addActionListener(e -> {
            nowEdit = tmp;
            try {
                textArea.setText(model.readFromFile(dy[nowEdit]));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        p.add("Center", jButton);
        Content r = new Content();
        double size[][] = {{TableLayout.FILL}, {0.5, 0.5}};
        r.setLayout(new TableLayout(size));
        JButton delete = new JButton(new ImageIcon("img/close-button.png"));
        delete.setBorderPainted(false);
        delete.setContentAreaFilled(false);
        delete.setFocusPainted(false);
        delete.setOpaque(false);
        delete.addActionListener(e -> {
            try {
                Files.delete(Paths.get(String.format("text/%d", dy[tmp])));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            localMemo.remove(p);
            localMemo.revalidate();
            frame.revalidate();
            frame.repaint();
            cnt--;
        });
        r.add(delete, "0, 0");
        JButton upload = new JButton(new ImageIcon("img/upload-button.png"));
        upload.setBorderPainted(false);
        upload.setContentAreaFilled(false);
        upload.setFocusPainted(false);
        upload.setOpaque(false);
        upload.addActionListener(e -> {
            try {
                model.upload(dy[tmp]);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        r.add(upload, "0, 1");
        p.add("East", r);
        p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        localMemo.add("North", p);
    }
    void addRemoveJscoll(String overview){
        Content p = new Content();
        p.setPreferredSize(new Dimension(350, 50));
        p.setMaximumSize(new Dimension(350, 50));
        p.setLayout(new BorderLayout(0, 0));
        int y, m, d, h, mi;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        mi = cal.get(Calendar.MINUTE);
        JButton jButton = new JButton();
        jButton.setLayout(new BorderLayout());
        JLabel label1 = new JLabel(y + "年" + m + "月" + d + "日 " + h + ":" + ((mi < 10) ? "0" + mi: mi));
        JLabel label2 = new JLabel(overview);
        label1.setFont(new Font("mono", Font.PLAIN, 18));
        label2.setFont(new Font("mono", Font.PLAIN, 18));
        jButton.add(BorderLayout.NORTH, label1);
        jButton.add(BorderLayout.SOUTH, label2);

        jButton.setBackground(Color.white);
        jButton.setBorder(null);
        int tmp = cnt;
        jButton.addActionListener(e -> {
            nowEdit = tmp;
            try {
                textArea.setText(model.readFromFile(dy[nowEdit]));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        p.add("Center", jButton);
        Content r = new Content();
        double size[][] = {{TableLayout.FILL}, {0.5, 0.5}};
        r.setLayout(new TableLayout(size));
        JButton delete = new JButton(new ImageIcon("img/close-button.png"));
        delete.setBorderPainted(false);
        delete.setContentAreaFilled(false);
        delete.setFocusPainted(false);
        delete.setOpaque(false);
        delete.addActionListener(e -> {
            try {
                Files.delete(Paths.get(String.format("text/%d", dy[tmp])));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            localMemo.remove(p);
            localMemo.revalidate();
            frame.revalidate();
            frame.repaint();
            cnt--;
        });
        r.add(delete, "0, 0");
        p.add("East", r);
        p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        removeMemo.add("North", p);
    }
}
