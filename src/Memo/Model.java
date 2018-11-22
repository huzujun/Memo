package Memo;

import Memo.myComponent.Display;
import Memo.view.MemoView;

import java.io.*;
import java.math.BigInteger;
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

    void localMemoInit(MemoView memoview) {
        nowEdit = -1;
        this.memoView = memoview;
        for (int i = 1; i < 100; i++) {
            File file = new File(String.format("text/%d", i));
            StringBuilder s = new StringBuilder();
            if (file.exists()) {
                dy[i] = ++cnt;
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
            }else dy[i] = -1;
        }
    }

    int nowEdit; //when edit the new one, nowEdit value is -1
    //or nowEdit value is the id of the memo we're editing

    private int cnt = 0, dy[] = new int[100];
    private LinkedList<Display> localList = new LinkedList<>(), removeList = new LinkedList<>();
    boolean save(String text) { //return true when the nowEdit value is -1
        if (nowEdit == -1) {
            for (int i = 1; i <= 100; i++) {
                File file = new File(String.format("text/%d", i));
                if (!file.exists()) {
                    dy[i] = ++cnt;
                    nowEdit = i;
                    write(text);
                    localList.addLast(memoView.addLocalJscoll(null, null, i));
                    break;
                }
            }
            return true;
        } else {
            write(text);
            Display that = localList.get(dy[nowEdit] - 1);
            that.setLabel1(getDate());
            that.setLabel2(text);
            that.refresh();
            return false;
        }
    }
    private void write(String text){
        try {
            File file = new File(String.format("text/%d", nowEdit));
            OutputStream fop = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
            writer.append(getDate()).append("\n");
            writer.append(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getDate(){
        int y, m, d, h, mi;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        h = cal.get(Calendar.HOUR_OF_DAY);
        mi = cal.get(Calendar.MINUTE);
        return y + "年" + m + "月" + d + "日 " + h + ":" + ((mi < 10) ? "0" + mi : mi);
    }
    void deleteLocal(int id){
        localList.remove(dy[id]-1);
        for (int i=1; i<100; i++) if (dy[i]!=-1 && dy[i] > dy[id]) dy[i]--;
        cnt --;
        dy[id] = -1;
        try {
            Files.delete(Paths.get(String.format("text/%d", id)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    void deleteRemove(int id){
        removeList.remove(dy[id]-1);
        for (int i=1; i<100; i++) if (dy[i]!=-1 && dy[i] > dy[id]) dy[i]--;
        cnt --;
        dy[id] = -1;
        try {
            Files.delete(Paths.get(String.format("text/%d", id)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    void create() {
        nowEdit = -1;
    }

    String readFromFile(int id) {
        nowEdit = id;
        File file = new File(String.format("text/%d", id));
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        assert sc != null;
        if (sc.hasNextLine()) sc.nextLine();
        while (sc.hasNextLine()) {
            String thisLine = sc.nextLine();
            s.append(thisLine).append("\n");
        }
        sc.close();
        return s.toString();
    }

    void upload(int id) {

    }

    boolean authenticate(char[] input) throws NoSuchAlgorithmException {
        return getHashPass(new String(input)).equals(password);
    }

    private String getHashPass(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plainText.getBytes());

        return new BigInteger(1, md.digest()).toString(16);
    }
}
