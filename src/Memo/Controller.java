package Memo;

import Memo.view.LoginView;
import Memo.view.MemoView;
import Memo.view.UploadView;
import Memo.view.View;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


/**
 *
 */
public class Controller {
    private Model model;
    private View view;

    /**
     * @param model 设定当前的 model
     */
    void setModel(Model model) {
        this.model = model;
    }

    /**
     * @param view 设定当前的界面
     */
    void setView(View view) {
        this.view = view;
    }

    /**
     * @param input 用户输入的密码
     * @return 输入的密码是否正确
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public boolean authenticate(char[] input) throws NoSuchAlgorithmException {
        return model.authenticate(input);
    }

    private UploadView uploadView;

    /**
     * 打开备忘录界面
     */
    public void openMemo() {
        view = new MemoView(this);
    }

    /**
     * 打开登录界面
     */
    public void openLogin() {
        view = new LoginView(this);
    }

    /**
     * @param text 保存文本
     * @return 是否是新建的文件
     */
    public boolean save(String text) {
        return model.save(text);
    }

    /**
     * @param id 设定当前正在编辑备忘录的序号
     */
    public void setNowEdit(int id) {
        model.nowEdit = id;
    }

    /**
     * 新建本地备忘录
     */
    public void addLocalMemo() {
        model.addLocalMemo();
    }

    /**
     * @param memoView 备忘录界面
     */
    public void MemoInit(MemoView memoView) {
        view = memoView;
        model.localMemoInit(memoView);
        model.removeMemoInit(memoView);
    }

    private int id;

    /**
     * 设定密码
     *
     * @param password 用户输入的密码
     * @throws IOException              IOException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public void setPassword(char[] password) throws IOException, NoSuchAlgorithmException {
        model.setPassword(password);
    }

    /**
     * 删除本地备忘录
     *
     * @param id 备忘录序号
     */
    private void deleteLocal(int id) {
        model.deleteLocal(id);
    }

    /**
     * 删除远程备忘录
     *
     * @param id 备忘录序号
     */
    private void deleteRemove(int id) {
        model.deleteRemove(id);
    }

    /**
     * 删除备忘录
     *
     * @param id    备忘录序号
     * @param local 是否是本地的
     */
    public void delete(int id, boolean local) {
        if (local) deleteLocal(id);
        else deleteRemove(id);
    }

    /**
     * @param id      文件序号
     * @param isLocal 是否是本地备忘录
     * @return 文本内容
     */
    public String readFromFile(int id, boolean isLocal) {
        return model.deleteFirstLine(model.readFromFile(id, isLocal));
    }

    /**
     * 设定 html 地址
     *
     * @param id      备忘录序号
     * @param isLocal 是否是本地备忘录
     */
    public void setUrl(int id, boolean isLocal) {
        model.setUrl(id, isLocal);
    }

    /**
     * 切换成写模式
     */
    public void changeToWrite() {
        model.changeToWrite();
    }

    /**
     * 上传
     * @param id 备忘录序号
     */
    public void upload(int id) {
        this.id = id;
        uploadView = new UploadView(this);
    }

    /**
     * @param text 服务端地址
     */
    public void setNet(String text) {
        if (model.setNet(text)) {
            uploadView.jLabel.setText("上传成功");
            uploadView.jLabel.setForeground(new Color(29, 158, 0));
        } else {
            uploadView.jLabel.setText("上传失败");
            uploadView.jLabel.setForeground(Color.red);
        }
        model.upload(id);
    }


    /**
     * 网络初始化
     */
    void netInit() {
        model.netInit();
    }
}
