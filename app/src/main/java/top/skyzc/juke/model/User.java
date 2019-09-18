package top.skyzc.juke.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;

/*
* User 类属性包括
*   username password mobilePhoneNumber
*   level invID
* */
public class User extends BmobUser {

    /*
    private String username;
    private String password;
    private String mobilePhoneNumber;
    */
    private Integer level;
    private String invID;
    private String wechat;
    private String heInvID;

    private User upUser;

    public String getHeInvID() {
        return heInvID;
    }

    public void setHeInvID(String heInvID) {
        this.heInvID = heInvID;
    }

    public User getUpUser() {
        return upUser;
    }

    public void setUpUser(User upUser) {
        this.upUser = upUser;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }



    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getInvID() {
        return invID;
    }

    public void setInvID(String invID) {
        this.invID = invID;
    }

}
