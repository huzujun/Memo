package Memo.myComponent;

import javax.swing.*;

public class myButton extends JButton {
    public myButton(Icon var1){
        super(var1);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setOpaque(false);
    }
}
