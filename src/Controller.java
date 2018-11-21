import com.sun.org.apache.xpath.internal.operations.Mod;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class Controller {
    Controller() throws IOException {
        File file = new File("usr.txt");
        View view = View.getInstance();
/*        if (file.exists()) {
            Scanner sc = new Scanner(file);
            String passWord = null;
            if (sc.hasNextLine())
                passWord = sc.nextLine();
            Model model = Model.getInstance();
            model.setPassword(passWord);
            view.login();
        } else {
            view.register();
        }*/
        view.mono();
        Model model = Model.getInstance();
        model.initServer();
    }
}
