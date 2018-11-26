package Memo.view;

import Memo.Controller;
import Memo.myComponent.Content;
import Memo.myComponent.Display;
import Memo.myComponent.myButton;
import layout.TableLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 备忘录界面
 */
public class MemoView implements View {
    public JFrame frame = new JFrame();
    private Controller controller;
    private JButton save = new JButton("保存");

    public MemoView(Controller controller) {
        this.controller = controller;
        controller.MemoInit(this);
        initComponents();
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    private JTextArea textArea = new JTextArea();
    private Content right = new Content();
    public String port = "";
    /**
     * 初始化组件
     */
    private void initComponents() {
        frame.setSize(1200, 900);
        frame.setTitle("备忘录");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double size[][] = {{0.3, 0.7}, {TableLayout.FILL}};
        frame.setLayout(new TableLayout(size));
        Content left = new Content();

        changeToWrite();

        frame.getContentPane().add(left, "0, 0");
        frame.getContentPane().add(right, "1, 0");
        //divide the whole frame to two part: left and right

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
            controller.addLocalMemo();
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
        JLabel ipLable = new JLabel("局域网地址:   127.0.0.1:" + port);

        ipLable.setFont(new Font("mono", Font.ITALIC, 20));
        down.add("South", ipLable);
        ipLable.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        down.setVisible(true);
        frame.setVisible(true);
    }

    private Content localMemo = new Content(), removeMemo = new Content();

    /**
     * @param overview 缩略
     * @param dateInfo 日期信息
     * @param id 第几个备忘录
     * @return 记录一个备忘录的Display
     */
    public Display addLocalJscoll(String overview, String dateInfo, int id) {
        Display display = new Display(frame, controller, id, textArea, save, overview, localMemo, dateInfo, true);
        localMemo.add("North", display);
        frame.revalidate();
        frame.repaint();
        return display;
    }

    /**
     * @param overview 缩略
     * @param dateInfo 日期信息
     * @param id 第几个备忘录
     * @return 记录一个备忘录的Display
     */
    public Display addRemoveJscoll(String overview, String dateInfo, int id) {
        Display display = new Display(frame, controller, id, textArea, save, overview, removeMemo, dateInfo, false);
        removeMemo.add("North", display);
        frame.revalidate();
        frame.repaint();
        return display;
    }

    /**
     * 转为写模式
     */
    public void changeToWrite(){
        right.removeAll();
        right.repaint();
        right.revalidate();
        right.setLayout(new BorderLayout(0, 0));
        textArea.setFont(new Font("mono", Font.PLAIN, 20));
        textArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        textArea.setLineWrap(true);        //激活自动换行功能
        textArea.setWrapStyleWord(true);
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
                    controller.addLocalMemo();
                }
            }
        });
        save.addActionListener(e -> {
            if (controller.save(textArea.getText())) {
                frame.revalidate();
                frame.repaint();
            }
        });
        save.setEnabled(true);
        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        right.add("Center", scroll);

        double size_previewPanel[][] = {{0.5, 0.5}, {TableLayout.FILL}};
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new TableLayout(size_previewPanel));
        JButton write = new JButton("书写模式"), read = new JButton("阅读模式");
        write.addActionListener(e -> changeToWrite());
        read.addActionListener(e -> changeToRead());
        write.setBackground(new Color(31, 158, 255));
        read.setBackground(Color.white);
        previewPanel.add(write, "0, 0");
        previewPanel.add(read, "1, 0");
        right.add("North", previewPanel);
        save.setFont(new Font("mono", Font.BOLD, 25));
        save.setBackground(Color.getHSBColor(255, 255, 70));
        right.add("South", save);

        right.repaint();
        right.revalidate();
    }

    /**
     * html地址
     */
    public String url = "1.html";
    private JEditorPane editorPane = new JEditorPane();
    /**
     * 转为阅读模式
     */
    private void changeToRead(){
        right.removeAll();
        right.repaint();
        right.revalidate();

        right.setLayout(new BorderLayout(0, 0));
        editorPane.setEditable(false);
        JScrollPane scroll = new JScrollPane(editorPane,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet css = kit.getStyleSheet();
        css.addRule("h1 {\n" +
                "    padding-bottom: .3em;\n" +
                "    font-size: 2.0em;\n" +
                "    line-height: 1.2;\n" +
                "}");
        css.addRule("h2 {\n" +
                "   padding-bottom: .3em;\n" +
                "    font-size: 1.5em;\n" +
                "    line-height: 1.225;\n" +
                "}");
        css.addRule("h3 {\n" +
                "    font-size: 1.5em;\n" +
                "    line-height: 1.43;\n" +
                "}");
        css.addRule("h4 {\n" +
                "    font-size: 1.25em;\n" +
                "}");
        css.addRule("h5 {\n" +
                "    font-size: 1em;\n" +
                "}");
        css.addRule("h6 {\n" +
                "   font-size: 1em;\n" +
                "    color: #777;\n" +
                "}");
        css.addRule("h1,\n" +
                "h2,\n" +
                "h3,\n" +
                "h4,\n" +
                "h5,\n" +
                "h6 {\n" +
                "    position: relative;\n" +
                "    margin-top: 1rem;\n" +
                "    margin-bottom: 1rem;\n" +
                "    font-weight: bold;\n" +
                "    line-height: 1.4;\n" +
                "    cursor: text;\n" +
                "}");
        css.addRule("body {\n" +
                "    font-family: \"Open Sans\",\"Clear Sans\",\"Helvetica Neue\",Helvetica,Arial,sans-serif;\n" +
                "    color: rgb(51, 51, 51);\n" +
                "    line-height: 1.6;\n" +
                "}");
        css.addRule("ul, ol{font-size: 1.12em;}");
        css.addRule("p {\n" +
                "    font-size: 1.12em; margin: 0.8em 0.5em;\n" +
                "    line-height: 1.5em;\n" +
                "}");
        css.addRule("body {\n" +
                "    font-family: \"Open Sans\",\"Clear Sans\",\"Helvetica Neue\",Helvetica,Arial,sans-serif;\n" +
                "    color: rgb(51, 51, 51);\n" +
                "    line-height: 1.6;\n" +
                "}");
        Document doc = kit.createDefaultDocument();
        editorPane.setDocument(doc);
        editorPane.setEditorKit(kit);
        try {
            FileReader fr = new FileReader(new File(url));
            editorPane.setContentType("text/html");
            editorPane.read(fr, "Test");
        } catch (IOException e) {
            e.printStackTrace();
        }

        right.add("Center", scroll);
        save.setEnabled(false);

        double size_previewPanel[][] = {{0.5, 0.5}, {TableLayout.FILL}};
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new TableLayout(size_previewPanel));
        JButton write = new JButton("书写模式"), read = new JButton("阅读模式");
        write.addActionListener(e -> changeToWrite());
        read.addActionListener(e -> changeToRead());
        write.setBackground(Color.white);
        read.setBackground(new Color(31, 158, 255));
        previewPanel.add(write, "0, 0");
        previewPanel.add(read, "1, 0");
        right.add("North", previewPanel);
        save.setFont(new Font("mono", Font.BOLD, 25));
        save.setBackground(Color.getHSBColor(255, 255, 70));
        right.add("South", save);

        right.repaint();
        right.revalidate();
    }

}