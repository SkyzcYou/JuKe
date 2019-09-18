package top.skyzc.juke.model;

import cn.bmob.v3.BmobObject;

public class Level extends BmobObject {
    private User user;
    private Integer level;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
