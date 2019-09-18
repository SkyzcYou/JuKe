package top.skyzc.juke.util;

import android.util.Log;

import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import top.skyzc.juke.model.Upgrade;
import top.skyzc.juke.model.User;

/**
*    用户提交升级时判断，传入用户的当前等级，来分析升级条件
 *   等级 0~9
*     0->1: 需要名下拥有3个子用户，并且通过上级认证和九星认证
 *    1->2: 需要一个2星用户认证
 *    2->3: 需要一个3星用户认证
 *    3->4: 需要一个4星用户认证
 *    4->5: 需要一个5星用户认证
 *    5->6: 需要一个6星用户认证
 *    6->7: 需要一个7星用户认证
 *    8->9: 需要一个9星用户认证
* */
public class UserUpgradeCase {
    private final String TAG = "UserUpgradeCase";
    private int nowLevel;
    private int toLevel;
    public UserUpgradeCase(int nowlevel) {
        this.nowLevel = nowlevel;
        this.toLevel = nowlevel + 1;
    }
    public User upgradePlanCase(){
        return null;
    }

    /** 1到8星的升级方案
     *  根据等级获取该等级的所有用户
     *  并从中随机返回一个User
     * */
    public User getRandomUserOfLevel(final int level){
        final User[] user = new User[1];
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("level", level);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    Random random = new Random();
                    int n = random.nextInt(list.size());
                    // 获取一个随机的九星级别用户
                    user[0] = list.get(n);
                } else {
                    Log.e(TAG, "随机取得" + level+ "星User失败"+e.getMessage());
                }
            }
        });
        return user[0];
    }

}
