package Memo;

import Memo.view.LoginView;
import Memo.view.MemoView;
import Memo.view.RegisterView;
import Memo.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static boolean hasConnet = false;
    private Model model;
    private View view;
    private Controller controller;

    private Main(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        this.view = beginView();
        init();
    }

    /**
     * @return 起始的界面
     */
    private View beginView() {
        File file = new File("usr.txt");
        if (file.exists()) {
            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String passWord = null;
            assert sc != null;
            if (sc.hasNextLine())
                passWord = sc.nextLine();
            model.setPassword(passWord);
            return new LoginView(controller);
        } else {
            return new RegisterView(controller);
        }
    }

    public static void main(String[] args) {
        if (args.length!=0 && args[0].equals("yes")) hasConnet = true;
        new Main(new Model(), new Controller());
    }

    private void init() {
        controller.setView(view);
        controller.setModel(model);
        if (hasConnet) controller.netInit();
    }
}
