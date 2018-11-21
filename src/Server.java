import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
    private ArrayList<PrintWriter> clientOutputStreams;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        ClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Receive message: " + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server().go();
    }

    private void go() {
        clientOutputStreams = new ArrayList<>();
        try{
            ServerSocket serverSock = new ServerSocket(5000);
            while (true){
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connetion");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void tellEveryone(String message){
        for (PrintWriter clientOutputStream : clientOutputStreams) {
            try {
                clientOutputStream.println(message);
                clientOutputStream.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
