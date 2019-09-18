package top.skyzc.juke.model;

import cn.bmob.v3.BmobObject;

public class Examine extends BmobObject {
    private User sendUser;
    private User upUser;
    private User nineUser;
    private boolean upUserStatus;
    private boolean nineUserStatus;
    private boolean allStatus;

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    private boolean isUser;

    public User getSendUser() {
        return sendUser;
    }

    public void setSendUser(User sendUser) {
        this.sendUser = sendUser;
    }

    public User getUpUser() {
        return upUser;
    }

    public void setUpUser(User upUser) {
        this.upUser = upUser;
    }

    public User getNineUser() {
        return nineUser;
    }

    public void setNineUser(User nineUser) {
        this.nineUser = nineUser;
    }

    public boolean isUpUserStatus() {
        return upUserStatus;
    }

    public void setUpUserStatus(boolean upUserStatus) {
        this.upUserStatus = upUserStatus;
    }

    public boolean isNineUserStatus() {
        return nineUserStatus;
    }

    public void setNineUserStatus(boolean nineUserStatus) {
        this.nineUserStatus = nineUserStatus;
    }

    public boolean isAllStatus() {
        return allStatus;
    }

    public void setAllStatus(boolean allStatus) {
        this.allStatus = allStatus;
    }
}
