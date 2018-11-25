package Memo;

import Memo.myComponent.Display;
import Memo.view.MemoView;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

class Model {
    private String password;
    private MemoView memoView;

    //about password
    void setPassword(String password) {
        this.password = password;
    }

    void setPassword(char[] password) throws IOException, NoSuchAlgorithmException {
        this.password = getHashPass(new String(password));

        OutputStream fop = new FileOutputStream(new File("usr.txt"));
        OutputStreamWriter writer = new OutputStreamWriter(fop);
        writer.append(this.password);
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

    private String getDate() {
        int y, m, d, h, mi;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        mi = cal.get(Calendar.MINUTE);
        return y + "年" + m + "月" + d + "日 " + h + ":" + ((mi < 10) ? "0" + mi : mi);
    }

    int nowEdit; //when edit the new one, nowEdit value is -1
    //or nowEdit value is the id of the memo we're editing

    private int localCnt = 0, localDy[] = new int[100];
    private int removeCnt = 0, removeDy[] = new int[100];
    private LinkedList<Display> localList = new LinkedList<>(), removeList = new LinkedList<>();

    //localMemo save
    boolean save(String text) { //return true when the nowEdit value is -1
        if (nowEdit == -1) {
            for (int i = 1; i <= 100; i++) {
                File file = new File(String.format("text/local/%d", i));
                if (!file.exists()) {
                    localDy[i] = ++localCnt;
                    nowEdit = i;
                    write(text, true);
                    localList.addLast(memoView.addLocalJscoll(null, null, i));
                    break;
                }
            }
            return true;
        } else {
            write(text, true);
            Display that = localList.get(localDy[nowEdit] - 1);
            that.setLabel1(getDate());
            that.setLabel2(text);
            that.refresh();
            return false;
        }
    }

    String deleteFirstLine(String s) {
        String lines[] = s.split("\n");
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < lines.length; i++) text.append(lines[i]).append("\n");
        return text.toString();
    }

    //write text to file
    private void write(String text, boolean isLocal) {
        try {
            File file;
            if (isLocal)
                file = new File(String.format("text/local/%d.md", nowEdit));
            else
                file = new File(String.format("text/remove/%d.md", nowEdit));
            OutputStream fop = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
            writer.append(getDate()).append("\n");
            if (isLocal)
                writer.append(text);
            else
                writer.append(deleteFirstLine(text));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //read file and return String
    String readFromFile(int id, boolean isLocal) {
        nowEdit = id;
        File file;
        if (isLocal) file = new File(String.format("text/local/%d.md", id));
        else file = new File(String.format("text/remove/%d.md", id));
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        assert sc != null;
        while (sc.hasNextLine()) {
            String thisLine = sc.nextLine();
            s.append(thisLine).append("\n");
        }
        sc.close();
        return s.toString();
    }

    void localMemoInit(MemoView memoview) {
        nowEdit = -1;
        this.memoView = memoview;
        for (int i = 1; i < 100; i++) {
            File file = new File(String.format("text/local/%d.md", i));
            if (file.exists()) {
                localDy[i] = ++localCnt;
                StringBuilder s = new StringBuilder();
                try {
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        String thisLine = sc.nextLine();
                        s.append(thisLine).append("\n");
                    }
                    sc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] text_lines = s.toString().split("\n");
                localList.addLast(memoView.addLocalJscoll(text_lines[1], text_lines[0], i));
            } else localDy[i] = -1;
        }
    }

    void deleteLocal(int id) {
        localList.remove(localDy[id] - 1);
        for (int i = 1; i < 100; i++) if (localDy[i] != -1 && localDy[i] > localDy[id]) localDy[i]--;
        localCnt--;
        localDy[id] = -1;
        try {
            Files.delete(Paths.get(String.format("text/local/%d.md", id)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    void addLocalMemo() {
        nowEdit = -1;
    }

    void removeMemoInit(MemoView memoview) {
        nowEdit = -1;
        this.memoView = memoview;
        for (int i = 1; i < 100; i++) {
            File file = new File(String.format("text/remove/%d.md", i));
            if (file.exists()) {
                removeDy[i] = ++removeCnt;
                String[] text_lines = readFromFile(i, false).split("\n");
                removeList.addLast(memoView.addRemoveJscoll(text_lines[1], text_lines[0], i));
            } else removeDy[i] = -1;
        }
    }

    void upload(int id) {
        String text = readFromFile(id, true);
        serverOut.println(text);
        serverOut.println(String.valueOf((char) 0));
        //addRemoveMemo(text);
    }

    private void addRemoveMemo(String text) {
        for (int i = 1; i <= 100; i++) {
            File file = new File(String.format("text/remove/%d.md", i));
            if (!file.exists()) {
                removeDy[i] = ++removeCnt;
                nowEdit = i;
                write(text, false);
                String lines[] = text.split("\n");
                removeList.addLast(memoView.addRemoveJscoll(lines[1], null, i));
                break;
            }
        }
        memoView.frame.validate();
        memoView.frame.repaint();
    }

    void deleteRemove(int id) {
        removeList.remove(removeDy[id] - 1);
        for (int i = 1; i < 100; i++) if (removeDy[i] != -1 && removeDy[i] > removeDy[id]) removeDy[i]--;
        removeCnt--;
        removeDy[id] = -1;
        try {
            Files.delete(Paths.get(String.format("text/remove/%d.md", id)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private PrintStream serverOut;
    private BufferedReader serverIn;

    void netInit() {
        try {
            Socket connection = new Socket("127.0.0.1", 5000);
            serverIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            serverOut = new PrintStream(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread readThread = new Thread(new RemoteReader());
        readThread.start();
    }

    // an inner class to listen to server
    private class RemoteReader implements Runnable {
        @Override
        public void run() {
            String aLine;
            StringBuilder s = new StringBuilder();
            try {
                while ((aLine = serverIn.readLine())!=null){
                    if (aLine.equals(String.valueOf((char) 0))) {
                        System.out.println("recieve message: ");
                        System.out.println(s.toString());
                        addRemoveMemo(s.toString());
                    }else s.append(aLine).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}