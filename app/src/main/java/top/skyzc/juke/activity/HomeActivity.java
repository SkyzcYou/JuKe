package top.skyzc.juke.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.stx.xhb.xbanner.XBanner;
import com.sunfusheng.marqueeview.MarqueeView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.skyzc.juke.R;
import top.skyzc.juke.model.Examine;
import top.skyzc.juke.model.UpUserLevel;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.MyUtil;

public class HomeActivity extends BaseActivity {
    private final String TAG = "HomeActivity";
    private MarqueeView marqueeView;
    private XBanner xBanner;

    private TextView textPhoneNumber, textUsername, textLevel, textInvId;
    private String myInvId, heInvId, nowLevel, canUpLevel;
    private User nowUser;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                MyUtil.showToast(HomeActivity.this, "自检升级成功！共查询到有效状态 Fxxk 条");
            }
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        xBanner.startAutoPlay();
        marqueeView.startFlipping();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // 初始化跑马灯文字
        initNotice();
        // 初始化XBanner
        xBanner = findViewById(R.id.banner_home);
        initXBanner();
        // 权限验证：自动更新需要写入SD卡
        MyUtil.verifyStoragePermissions(this);

        //初始化tView
        initView();

        //初始化用户
        initUser();
        // 更新本地User
        updateUser();

        /**每次进入Home，进行自检升级
         升级条件是 查询 把当前 User 作为 sendUser 传入 Examine表
         查询 upUserStatus 状态为 true 的一共有几条
         有几条就升几级
         * */
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                selfChecking();
            }
        }).start();*/
    }

    /**
     * 初始化 View
     */
    private void initView() {
        textPhoneNumber = findViewById(R.id.text_phoneNmuber);
        textUsername = findViewById(R.id.text_username);
        textLevel = findViewById(R.id.text_level);
        textInvId = findViewById(R.id.text_inv);
        // 调用App更新接口进行更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(HomeActivity.this);
    }

    /**
     * 同步控制台数据到缓存
     */
    private void updateUser() {
        BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {
                    final User myUser = BmobUser.getCurrentUser(User.class);
                    Log.d(TAG, myUser.getUsername() + ": 更新用户本地缓存信息成功：" + myUser.getUsername());
                    initUser();
                } else {
                    Log.e(TAG, "更新用户本地缓存信息失败：" + e.getMessage());
                }
            }
        });
    }


    /**
     * 初始化界面用户信息
     */
    private void initUser() {
        nowUser = BmobUser.getCurrentUser(User.class);

        textPhoneNumber.setText("手机号：" + nowUser.getMobilePhoneNumber());
        textUsername.setText("姓名：" + nowUser.getUsername());
        // 等级判定
        int level = nowUser.getLevel();
        textLevel.setText("等级：" + MyUtil.getShowLevelofLevel(level));
        textInvId.setText("邀请码：" + nowUser.getInvID());

        myInvId = nowUser.getInvID();
        heInvId = nowUser.getHeInvID();
        nowLevel = MyUtil.getShowLevelofLevel(level);
        if (level == 9) {
            canUpLevel = "顶级会员";
        } else {
            canUpLevel = MyUtil.getShowLevelofLevel(level+1);
        }
        Log.d(TAG, "用户名：" + nowUser.getUsername() + "电话号码：" + nowUser.getMobilePhoneNumber() + "等级：" + nowLevel);
    }


    /**
     * 跑马灯文字
     */
    private void initNotice() {
        //设置跑马灯文字
        marqueeView = findViewById(R.id.tv_marquee);
        List<String> info = new ArrayList<>();
        info.add("欢迎加入九星创客！");
        info.add("欢迎大家关注我哦！");
        marqueeView.startWithList(info);
        marqueeView.startWithList(info, R.anim.anim_bottom_in, R.anim.anim_top_out);
    }

    /**
     * [自检升级selfChecking()]
     * 每次进入Home，进行自检升级
     * 升级条件是 查询 把当前 User 作为 sendUser 传入 Examine表
     * 查询 upUserStatus 状态为 true 的一共有几条
     * 有几条就升几级
     * */
    /*private void selfChecking() {
        User user = BmobUser.getCurrentUser(User.class);
        Log.d(TAG, "现在用户为：" + user.getUsername());
        BmobQuery<Examine> querySend = new BmobQuery<>();
        querySend.addWhereEqualTo("sendUser", user);
        BmobQuery<Examine> queryUpStatus = new BmobQuery<>();
        queryUpStatus.addWhereEqualTo("upUserStatus", true);
        BmobQuery<Examine> queryNineStatus = new BmobQuery<>();
        queryNineStatus.addWhereEqualTo("nineUserStatus", true);

        List<BmobQuery<Examine>> queryArrayList = new ArrayList<BmobQuery<Examine>>();
        queryArrayList.add(querySend);
        queryArrayList.add(queryUpStatus);
        queryArrayList.add(queryNineStatus);

        BmobQuery<Examine> andQuery = new BmobQuery<>();
        andQuery.and(queryArrayList);
        andQuery.setCachePolicy(BmobQuery.CachePolicy.IGNORE_CACHE);
        andQuery.count(Examine.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "自检升级成功！共查询到有效状态" + integer + "条");
                    //MyUtil.showToast(HomeActivity.this, "自检升级成功！共查询到有效状态" + integer + "条");
                    upLevelUser(integer);
                    mHandler.sendEmptyMessage(1);
                } else {
                    Log.d(TAG, "自检升级失败！" + e.getMessage() + "," + e.getErrorCode());
                    //MyUtil.showToast(HomeActivity.this, "自检升级失败！" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }*/

    /**
     * 更新用户操作并同步更新本地的用户信息
     */
    private void upLevelUser(int newLevel) {
        final User user = BmobUser.getCurrentUser(User.class);
        user.setUsername(null);
        user.setLevel(newLevel);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUtil.showToast(HomeActivity.this, "更新用户信息成功：" + user.getUsername());
                } else {
                    //MyUtil.showToast(HomeActivity.this, "更新用户信息失败：" + e.getMessage());
                    Log.e("更新用户信息失败：", e.getMessage());
                }
            }
        });
    }


    /*
     *   XBanner轮播图
     * */
    private void initXBanner() {
        //创建集合
        final List<Integer> imgesUrl = new ArrayList<>();
        imgesUrl.add(R.drawable.banner0003);
        imgesUrl.add(R.drawable.banner0002);
        imgesUrl.add(R.drawable.banner0001);

        xBanner.setData(imgesUrl, null);
        xBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                Glide.with(HomeActivity.this).load(imgesUrl.get(position)).into((ImageView) view);
            }
        });
    }


    /*
     * 设置页面
     * */
    public void setting(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    /*
     * menu_one
     * */
    public void menu_one(View view) {
        Intent intent = new Intent(HomeActivity.this, Menu01Activity.class);
        intent.putExtra("myInvId", myInvId);
        startActivity(intent);
    }

    /*
     * menu_two
     * */
    public void menu_two(View view) {
        Intent intent = new Intent(HomeActivity.this, Menu02Activity.class);
        intent.putExtra("myInvId", myInvId);
        startActivity(intent);
    }

    /*
     * menu_there
     * */
    public void menu_there(View view) {
        Intent intent = new Intent(HomeActivity.this, Menu03Activity.class);
        startActivity(intent);
    }

    /*
     * menu_four
     * */
    public void menu_four(View view) {
        Intent intent = new Intent(HomeActivity.this, Menu04Activity.class);
        intent.putExtra("nowLevel", nowLevel);
        intent.putExtra("canUpLevel", canUpLevel);
        intent.putExtra("heInvId", heInvId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xBanner.stopAutoPlay();
        marqueeView.stopFlipping();
    }

    public void showTest(View view) {
        //updateUser();
        /*selfChecking();*/
        String cloudCodeName = "UpUserLevel";
        JSONObject params = new JSONObject();
        try {
            params.put("objectId",BmobUser.getCurrentUser(User.class).getObjectId());
            params.put("level",5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncCustomEndpoints asyncCustomEndpoints = new AsyncCustomEndpoints();
        asyncCustomEndpoints.callEndpoint(cloudCodeName, params, new CloudCodeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null){
                    Log.i(TAG,"result="+ o.toString());
                }else {
                    Log.i(TAG,"BmobException = "+e);
                }
            }
        });
    }
}
