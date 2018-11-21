import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.*;
import java.util.Scanner;

class Model {
    private static Model model = new Model();
    private Socket connection;
    private PrintStream serverOut;
    private BufferedReader serverIn;
    private Model(){
    }
    static Model getInstance(){
        return model;
    }
    private static String password;
    void initServer() throws IOException {
        connection = new Socket("127.0.0.1", 5000);
        serverIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        serverOut = new PrintStream(connection.getOutputStream());
        Thread readThread = new Thread(new RemoteReader());
        readThread.start();
    }
    String getPassword() {
        return password;
    }
    void setPassword(String password) {
        Model.password = password;
    }
    void setPassword(char[] password) throws IOException, NoSuchAlgorithmException {
        Model.password = getHashPass(new String(password));

        OutputStream fop = new FileOutputStream(new File("usr.txt"));
        OutputStreamWriter writer = new OutputStreamWriter(fop);
        writer.append(Model.password);
        writer.append("\n");
        writer.close();
        fop.close();
    }

    boolean authenticate(char[] input) throws NoSuchAlgorithmException {
        return getHashPass(new String(input)).equals(password);
    }

    private String getHashPass(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plainText.getBytes());

        return new BigInteger(1, md.digest()).toString(16);
    }

    void save(String text, int id) throws IOException {
        File file = new File(String.format("text/%d", id));
        OutputStream fop = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
        writer.append(text);
        writer.close();
    }


    void addLocalMemo(String text) throws IOException {
        View view = View.getInstance();
        for (int i=1; i<=100; i++) {
            File file = new File(String.format("text/%d", i));
            if (!file.exists()) {
                view.dy[++view.cnt] = i;
                OutputStream fop = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
                writer.append(text);
                writer.close();
                String[] text_lines = text.split("\n");
                view.addLocalJscoll(text_lines[0]);
                break;
            }
        }
    }

    void upload(int id) throws FileNotFoundException {
        serverOut.println(id);
    }

    String readFromFile(int id) throws FileNotFoundException {
        File file = new File(String.format("text/%d", id));
        Scanner sc = new Scanner(file);
        StringBuilder s = new StringBuilder();
        while (sc.hasNextLine()){
            String thisLine = sc.nextLine();
            s.append(thisLine).append("\n");
        }
        sc.close();
        return s.toString();
    }
    private class RemoteReader implements Runnable {
        @Override
        public void run() {
            String aLine;
            try {
                while ((aLine = serverIn.readLine())!=null) {
                    int id = Integer.valueOf(aLine);
                    String text = readFromFile(id);
                    View view = View.getInstance();
                    System.out.println(id);
                    String[] text_lines = text.split("\n");
                    view.addRemoveJscoll(text_lines[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
