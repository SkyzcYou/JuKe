package top.skyzc.juke.model;

import cn.bmob.v3.BmobObject;

public class Upgrade extends BmobObject {
    private User nowUser;
    private User upUser;
    private User myNineUser;

    public User getNowUser() {
        return nowUser;
    }

    public void setNowUser(User nowUser) {
        this.nowUser = nowUser;
    }

    public User getMyNineUser() {
        return myNineUser;
    }

    public void setMyNineUser(User myNineUser) {
        this.myNineUser = myNineUser;
    }

    public User getUpUser() {
        return upUser;
    }

    public void setUpUser(User upUser) {
        this.upUser = upUser;
    }
}
