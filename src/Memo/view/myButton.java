package Memo.view;

import javax.swing.*;

class myButton extends JButton {
    myButton(Icon var1){
        super(var1);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setOpaque(false);
    }
}
