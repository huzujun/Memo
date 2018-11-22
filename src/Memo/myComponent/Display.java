package Memo.myComponent;

import Memo.Controller;
import layout.TableLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Calendar;

public class Display extends Content {
    private JLabel label1 = new JLabel(), label2 = new JLabel();
    private Frame parent;
    public Display(Frame file, Controller controller, int id, JTextArea textArea,
                   String overview, Content localMemo, String dateInfo, boolean local){
        parent = file;
        this.setMaximumSize(new Dimension(350, 50));
        this.setLayout(new BorderLayout(0, 0));
        JButton jButton = new JButton();
        jButton.setLayout(new BorderLayout());
        if (overview != null) setLabel1(dateInfo); else setLabel1(getDateInfo());
        if (overview != null) setLabel2(overview); else setLabel2(textArea.getText());
        label1.setFont(new Font("mono", Font.PLAIN, 18));
        label2.setFont(new Font("mono", Font.PLAIN, 18));
        jButton.add(BorderLayout.NORTH, label1);
        jButton.add(BorderLayout.SOUTH, label2);

        jButton.setBackground(Color.white);
        jButton.setBorder(null);
        jButton.addActionListener(e -> {
            controller.setNowEdit(id);
            textArea.setText(controller.readFromFile(id));
        });
        this.add("Center", jButton);
        Content r = new Content();
        double size[][] = {{TableLayout.FILL}, {0.5, 0.5}};
        r.setLayout(new TableLayout(size));
        myButton delete = new myButton(new ImageIcon("img/close-button.png"));
        delete.addActionListener(e -> {
            controller.delete(id, local);
            localMemo.remove(this);
            localMemo.revalidate();
            refresh();
        });
        r.add(delete, "0, 0");
        myButton upload = new myButton(new ImageIcon("img/upload-button.png"));
        upload.addActionListener(e -> controller.upload(id));
        if (local) r.add(upload, "0, 1");
        this.add("East", r);
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }
    public void refresh(){
        this.revalidate();
        parent.revalidate();
        parent.repaint();
    }
    private String getDateInfo(){
        int y, m, d, h, mi;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        mi = cal.get(Calendar.MINUTE);
        return y + "年" + m + "月" + d + "日 " + h + ":" + ((mi < 10) ? "0" + mi : mi);
    }
    public void setLabel1(String dateInfo){
        label1.setText(dateInfo);
    }
    public void setLabel2(String text){
        String lines[] = text.split("\n");
        String overview = lines[0];
        label2.setText(overview);
    }
}
