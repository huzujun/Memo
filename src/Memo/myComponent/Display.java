package Memo.myComponent;

import Memo.Controller;
import layout.TableLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Calendar;

/**
 * 一个显示备忘录信息的 Content
 */
public class Display extends Content {
    private JLabel label1 = new JLabel(), label2 = new JLabel();
    private Frame parent;

    /**
     * @param frame 父frame
     * @param controller 父controller
     * @param id 父id
     * @param textArea 父textArea
     * @param save 父save
     * @param overview 缩略
     * @param memo 父Content
     * @param dateInfo 日期信息
     * @param isLocal 是否是本地备忘录，若为网络备忘录，则值为true
     */
    public Display(Frame frame, Controller controller, int id, JTextArea textArea, JButton save,
                   String overview, Content memo, String dateInfo, boolean isLocal) {
        parent = frame;
        this.setMaximumSize(new Dimension(350, 50));
        this.setLayout(new BorderLayout(0, 0));
        JButton jButton = new JButton();
        jButton.setLayout(new BorderLayout());
        if (dateInfo != null) setLabel1(dateInfo);
        else setLabel1(getDateInfo());
        if (overview != null) setLabel2(overview);
        else setLabel2(textArea.getText());
        label1.setFont(new Font("mono", Font.PLAIN, 18));
        label2.setFont(new Font("mono", Font.PLAIN, 18));
        jButton.add(BorderLayout.NORTH, label1);
        jButton.add(BorderLayout.SOUTH, label2);

        jButton.setBackground(Color.white);
        jButton.setBorder(null);
        jButton.addActionListener(e -> {
            if (isLocal) {
                controller.setNowEdit(id);
                save.setEnabled(true);
            } else save.setEnabled(false);
            textArea.setText(controller.readFromFile(id, isLocal));
            textArea.setCaretPosition(0);
            controller.setUrl(id, isLocal);
            controller.changeToWrite();
        });
        this.add("Center", jButton);
        Content r = new Content();
        double size[][] = {{TableLayout.FILL}, {0.5, 0.5}};
        r.setLayout(new TableLayout(size));
        myButton delete = new myButton(new ImageIcon("img/close-button.png"));
        delete.addActionListener(e -> {
            controller.delete(id, isLocal);
            memo.remove(this);
            memo.revalidate();
            refresh();
        });
        r.add(delete, "0, 0");
        if (isLocal) {
            myButton upload = new myButton(new ImageIcon("img/upload-button.png"));
            upload.addActionListener(e -> controller.upload(id));
            r.add(upload, "0, 1");
        }
        this.add("East", r);
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }

    /**
     * 刷新界面
     */
    public void refresh() {
        this.revalidate();
        parent.revalidate();
        parent.repaint();
    }

    /**
     * @return 表示当前时间的字符串
     */
    private String getDateInfo() {
        int y, m, d, h, mi;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        mi = cal.get(Calendar.MINUTE);
        return y + "年" + m + "月" + d + "日 " + h + ":" + ((mi < 10) ? "0" + mi : mi);
    }

    /**
     * @param dateInfo 日期信息
     */
    public void setLabel1(String dateInfo) {
        label1.setText(dateInfo);
    }

    /**
     * @param text 文本信息
     */
    public void setLabel2(String text) {
        String lines[] = text.split("\n");
        String overview = lines[0];
        label2.setText(overview);
    }
}
