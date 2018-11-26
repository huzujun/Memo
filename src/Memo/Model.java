package Memo;

import Memo.myComponent.Display;
import Memo.view.MemoView;
import conglin.test.TestMarkdownProcessor;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 */
class Model {
    private String password;
    private MemoView memoView;

    /**
     * @param password 密码
     */
    //about password
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param password 密码
     */
    void setPassword(char[] password) throws IOException, NoSuchAlgorithmException {
        this.password = getHashPass(new String(password));

        OutputStream fop = new FileOutputStream(new File("usr.txt"));
        OutputStreamWriter writer = new OutputStreamWriter(fop);
        writer.append(this.password);
        writer.append("\n");
        writer.close();
        fop.close();
    }

    /**
     * @param input 用户输入的密码
     * @return 是否匹配
     */
    boolean authenticate(char[] input) throws NoSuchAlgorithmException {
        return getHashPass(new String(input)).equals(password);
    }

    /**
     * @param plainText 源文本
     * @return md5加密后的文本
     */
    private String getHashPass(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plainText.getBytes());

        return new BigInteger(1, md.digest()).toString(16);
    }

    /**
     * @return 说明当前时间的字符串
     */
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

    int nowEdit; //when edit the new one, nowEdit value is 0
    //or nowEdit value is the id of the memo we're editing

    private int localCnt = 0, localDy[] = new int[100];
    private int removeCnt = 0, removeDy[] = new int[100];
    private LinkedList<Display> localList = new LinkedList<>(), removeList = new LinkedList<>();
    /**
     * 初始化网路
     */
    private String netUrl = "127.0.0.1";

    /**
     * @param s 文本内容
     * @return 文本内容删除第一行后的字符串
     */
    String deleteFirstLine(String s) {
        String lines[] = s.split("\n");
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < lines.length; i++) text.append(lines[i]).append("\n");
        return text.toString();
    }

    private Socket connection;

    /**
     * 保存或创建文件
     * @param text 文本内容
     * @return 是否是本地文件
     */
    //localMemo save
    boolean save(String text) { //return true when the nowEdit value is 0
        if (nowEdit == 0) {
            for (int i = 1; i <= 100; i++) {
                File file = new File(String.format("text/local/%d.md", i));
                if (!file.exists()) {
                    localDy[i] = ++localCnt;
                    nowEdit = i;
                    write(text, true);
                    localList.addLast(memoView.addLocalJscoll(null, null, i));
                    break;
                }
            }
            return true;
        } else if (nowEdit > 0) {
            write(text, true);
            Display that = localList.get(localDy[nowEdit] - 1);
            that.setLabel1(getDate());
            that.setLabel2(text);
            that.refresh();
            return false;
        } else {
            write(text, false);
            Display that = removeList.get(removeDy[-nowEdit] - 1);
            that.setLabel1(getDate());
            that.setLabel2(text);
            that.refresh();
            return true;
        }
    }

    /**
     * @param id 备忘录序号
     * @param isLocal 是否是本地备忘录
     * @return 文件里的文本内容
     */
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

    /**
     * @param id      备忘录序号
     * @param isLocal 是否是本地备忘录
     */
    void setUrl(int id, boolean isLocal) {
        if (isLocal) memoView.url = String.format("text/local/%d.md.html", id);
        else memoView.url = String.format("text/remove/%d.md.html", id);
    }

    private String deleteSecondLine(String s) {
        String lines[] = s.split("\n");
        StringBuilder text = new StringBuilder();
        text.append(lines[0]).append("\n");
        for (int i = 2; i < lines.length; i++) text.append(lines[i]).append("\n");
        return text.toString();
    }

    /**
     * 文本转化成本地文件
     * @param text 文本内容
     * @param isLocal 是否是本地文本
     */
    //write text to file
    private void write(String text, boolean isLocal) {
        try {
            File file;
            if (isLocal)
                file = new File(String.format("text/local/%d.md", nowEdit));
            else
                file = new File(String.format("text/remove/%d.md", -nowEdit));
            OutputStream fop = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
            writer.append(getDate()).append("\n");
            writer.append(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (isLocal)
                TestMarkdownProcessor.process(String.format("text/local/%d.md", nowEdit));
            else
                TestMarkdownProcessor.process(String.format("text/remove/%d.md", -nowEdit));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * 加载本地备忘录
     * @param memoview 备忘录界面
     */
    void localMemoInit(MemoView memoview) {
        nowEdit = 0;
        this.memoView = memoview;
        memoView.port = String.valueOf(connection.getLocalPort());
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
            } else localDy[i] = 0;
        }
    }

    /**
     * 删除本地备忘录
     * @param id 备忘录编号
     */
    void deleteLocal(int id) {
        localList.remove(localDy[id] - 1);
        for (int i = 1; i < 100; i++) if (localDy[i] != 0 && localDy[i] > localDy[id]) localDy[i]--;
        localCnt--;
        localDy[id] = 0;
        try {
            Files.delete(Paths.get(String.format("text/local/%d.md", id)));
            Files.delete(Paths.get(String.format("text/local/%d.md.html", id)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 创建本地备忘录
     */
    void addLocalMemo() {
        nowEdit = 0;
    }

    /**
     * 加载本地的网路备忘录
     * @param memoview 备忘录界面
     */
    void removeMemoInit(MemoView memoview) {
        nowEdit = 0;
        this.memoView = memoview;
        for (int i = 1; i < 100; i++) {
            File file = new File(String.format("text/remove/%d.md", i));
            if (file.exists()) {
                removeDy[i] = ++removeCnt;
                String[] text_lines = readFromFile(i, false).split("\n");
                removeList.addLast(memoView.addRemoveJscoll(text_lines[1], text_lines[0], i));
            } else removeDy[i] = 0;
        }
    }

    /**
     * 将本地备忘录上传到服务器
     * @param id 本地备忘录编号
     */
    void upload(int id) {
        String text = deleteFirstLine(readFromFile(id, true));
        serverOut.println(text);
        serverOut.println(String.valueOf((char) 0));
        //addRemoveMemo(text);
    }

    /**
     * 切换为写模式
     */
    void changeToWrite() {
        memoView.changeToWrite();
    }

    private PrintStream serverOut;
    private BufferedReader serverIn;

    /**
     * 新建远程备忘录
     * @param text 文本
     */
    private void addRemoveMemo(String text) {
        for (int i = 1; i <= 100; i++) {
            File file = new File(String.format("text/remove/%d.md", i));
            if (!file.exists()) {
                removeDy[i] = ++removeCnt;
                nowEdit = -i;
                write(deleteSecondLine(text), false);
                String lines[] = text.split("\n");
                removeList.addLast(memoView.addRemoveJscoll(lines[1], null, i));
                break;
            }
        }
        memoView.frame.validate();
        memoView.frame.repaint();
    }

    /**
     * 删除远程备忘录
     * @param id 备忘录序号
     */
    void deleteRemove(int id) {
        removeList.remove(removeDy[id] - 1);
        for (int i = 1; i < 100; i++) if (removeDy[i] != 0 && removeDy[i] > removeDy[id]) removeDy[i]--;
        removeCnt--;
        removeDy[id] = 0;
        try {
            Files.delete(Paths.get(String.format("text/remove/%d.md", id)));
            Files.delete(Paths.get(String.format("text/remove/%d.md.html", id)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    void netInit() {
        try {
            connection = new Socket(netUrl, 5000);
            serverIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            serverOut = new PrintStream(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread readThread = new Thread(new RemoteReader());
        readThread.start();
    }

    boolean setNet(String netUrl) {
        this.netUrl = netUrl;
        InetAddress ad;
        try {
            ad = InetAddress.getByName(netUrl);
        } catch (UnknownHostException e) {
            return false;
        }
        try {
            assert ad != null;
            boolean state = ad.isReachable(1000);
            if (!state) return false;
        } catch (IOException e) {
            return false;
        }
        try {
            connection = new Socket(netUrl, 5000);
            memoView.port = String.valueOf(connection.getLocalPort());
            serverIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            serverOut = new PrintStream(connection.getOutputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    /**
     * 读入服务器备忘录进程
     */
    // an inner class to listen to server
    private class RemoteReader implements Runnable {
        @Override
        public void run() {
            String aLine;
            StringBuilder s = new StringBuilder();
            try {
                while ((aLine = serverIn.readLine()) != null) {
                    if (aLine.equals(String.valueOf((char) 0))) {
                        System.out.println("recieve message: ");
                        System.out.println(s.toString());
                        addRemoveMemo(s.toString());
                        s = new StringBuilder();
                    }else s.append(aLine).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}