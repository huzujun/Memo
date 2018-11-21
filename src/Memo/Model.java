package Memo;

import Memo.view.MemoView;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        for (int i = 1; i <= 100; i++) {
            File file = new File(String.format("text/%d", i));
            StringBuilder s = new StringBuilder();
            if (file.exists()) {
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
                memoView.addLocalJscoll(text_lines[0], i);
            }
        }
    }

    int nowEdit; //when edit the new one, nowEdit value is -1
    //or nowEdit value is the id of the memo we're editing

    boolean save(String text) { //return true when the nowEdit value is -1
        if (nowEdit == -1) {
            try {
                for (int i = 1; i <= 100; i++) {
                    File file = new File(String.format("text/%d", i));
                    if (!file.exists()) {
                        OutputStream fop = new FileOutputStream(file);
                        OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
                        writer.append(text);
                        writer.close();
                        String[] text_lines = text.split("\n");
                        memoView.addLocalJscoll(text_lines[0], i);
                        nowEdit = i;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            try {
                File file = new File(String.format("text/%d", nowEdit));
                OutputStream fop = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
                writer.append(text);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
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
        while (sc.hasNextLine()) {
            String thisLine = sc.nextLine();
            s.append(thisLine).append("\n");
        }
        sc.close();
        return s.toString();
    }

    boolean authenticate(char[] input) throws NoSuchAlgorithmException {
        return getHashPass(new String(input)).equals(password);
    }

    void upload(int id) {

    }

    private String getHashPass(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plainText.getBytes());

        return new BigInteger(1, md.digest()).toString(16);
    }
}
