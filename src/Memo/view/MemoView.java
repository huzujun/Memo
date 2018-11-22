package Memo.view;

import Memo.Controller;
import Memo.myComponent.Content;
import Memo.myComponent.Display;
import Memo.myComponent.myButton;
import layout.TableLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MemoView implements View {
    private JFrame frame = new JFrame();
    private Controller controller;

    public MemoView(Controller controller) {
        this.controller = controller;
        controller.localMemoInit(this);
        initComponents();
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    private JTextArea textArea = new JTextArea();
    private void initComponents() {
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
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                int code = ke.getKeyCode();
                int modifiers = ke.getModifiers();
                if (code == KeyEvent.VK_ENTER && modifiers == KeyEvent.SHIFT_MASK) {
                    if (controller.save(textArea.getText())) {
                        frame.revalidate();
                        frame.repaint();
                    }
                }
                if (code == KeyEvent.VK_N && modifiers == KeyEvent.CTRL_MASK) {
                    textArea.setText("");
                    controller.create();
                }
            }
        });
        save.addActionListener(e -> {
            if (controller.save(textArea.getText())) {
                frame.revalidate();
                frame.repaint();
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

        myButton local_more = new myButton(new ImageIcon("img/menu-button.png"));
        p1.add(local_more, "1, 0");

        p1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        up.add("North", p1);

        removeMemo.setLayout(new BoxLayout(removeMemo, BoxLayout.Y_AXIS));
        localMemo.setLayout(new BoxLayout(localMemo, BoxLayout.Y_AXIS));

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
            controller.create();
        });
        up.add("South", create);

        Content p2 = new Content();
        p2.setLayout(new TableLayout(ss));
        p2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
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

    private Content localMemo = new Content(), removeMemo = new Content();
    public Display addLocalJscoll(String overview, String dateInfo, int id){
        Display display = new Display(frame, controller, id, textArea, overview, localMemo, dateInfo, false);
        localMemo.add("North", display);
        frame.revalidate();
        frame.repaint();
        return display;
    }
    public Display addRemoveJscoll(String overview, String dateInfo, int id){
        Display display = new Display(frame, controller, id, textArea, overview, localMemo, dateInfo, true);
        removeMemo.add("North", display);
        frame.revalidate();
        frame.repaint();
        return display;
    }
}
