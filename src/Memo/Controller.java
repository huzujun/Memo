package Memo;

import Memo.view.LoginView;
import Memo.view.MemoView;
import Memo.view.View;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Controller {
    private Model model;

    private View view;

    void setView(View view) {
        this.view = view;
    }
    public boolean authenticate(char[] input) throws NoSuchAlgorithmException {
        return model.authenticate(input);
    }
    public void setPassword(char[] password) throws IOException, NoSuchAlgorithmException {
        model.setPassword(password);
    }
    public void openMemo(){
        view = new MemoView(this);
    }

    public void openLogin(){
        view = new LoginView(this);
    }
    public boolean save(String text){
        return model.save(text);
    }
    void setModel(Model model) {
        this.model = model;
    }
    public int getId(){
        return model.nowEdit;
    }
    public void setNowEdit(int id){
        model.nowEdit = id;
    }
    public void create(){
        model.create();
    }
    public void localMemoInit(MemoView memoView){
        view = memoView;
        model.localMemoInit(memoView);
    }
    private void deleteLocal(int id){
        model.deleteLocal(id);
    }
    private void deleteRemove(int id){
        model.deleteRemove(id);
    }
    public void delete(int id, boolean local){
        if (local) deleteLocal(id); else deleteRemove(id);
    }
    public String readFromFile(int id){
        return model.readFromFile(id);
    }
    public void upload(int id){
        model.upload(id);
    }
}
