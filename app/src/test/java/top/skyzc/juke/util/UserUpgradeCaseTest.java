package top.skyzc.juke.util;

import android.util.Log;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import top.skyzc.juke.model.User;

import static org.junit.Assert.*;

public class UserUpgradeCaseTest {

    @Test
    public void upgradePlanCase() {
        String username = new UserUpgradeCase(9).getRandomUserOfLevel(9).getUsername();
        System.out.println(username);
    }

    @Test
    public void getRandomUserOfLevel() {
        final User[] user = new User[1];
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("level", 9);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    Random random = new Random();
                    int n = random.nextInt(list.size());
                    // 获取一个随机的九星级别用户
                    user[0] = list.get(n);
                    System.out.println(user[0].getUsername());
                } else {
                    Log.e("TAG", "随机取得" + 9+ "星User失败"+e.getMessage());
                }
            }
        });
    }
}