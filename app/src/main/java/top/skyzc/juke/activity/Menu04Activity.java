package top.skyzc.juke.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import top.skyzc.juke.R;
import top.skyzc.juke.model.Examine;
import top.skyzc.juke.model.Upgrade;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.MyDialog;
import top.skyzc.juke.util.MyUtil;

public class Menu04Activity extends BaseActivity {
    private final String TAG = "Menu04Activity";
    private Window window;
    private MyDialog mMyDialog;
    private Button btn_goUp, btn_copy01, btn_copy02;
    private TextView text_canUpTip, text_canUpLevel, text_nowLevel, text_weChat01, text_weChat02, text_cancel, text_commit,text_twoWechatHello;
    private String nowLevel, canUpLevel;

    private Examine examine;
    private User sendUser;
    private User upUser,nineUser;

    /*
    *   1.判定用户是否已经提交：查询当前用户的Examine表AllStatus字段，有false则说明当前有正在审核，按钮钮显示审核中
    *
    *
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu04);
        goBack();
        sendUser = BmobUser.getCurrentUser(User.class);
        initView();
        initLevel();
        initEven();

        examine = new Examine();
        examine.setSendUser(sendUser);
    }


    private void initView() {
        text_canUpTip = findViewById(R.id.text_canUpStartTip);
        text_canUpLevel = findViewById(R.id.text_canUpStart);
        text_nowLevel = findViewById(R.id.text_nowLevel);
        btn_goUp = findViewById(R.id.btn_goUp);

        View view = getLayoutInflater().inflate(R.layout.my_dialog, null);
        mMyDialog = new MyDialog(Menu04Activity.this, 0, 0, view, R.style.DialogTheme);
        mMyDialog.setCancelable(true);
        initUpNine();

        Log.d(TAG,"当前用户：" + sendUser.getUsername());
    }

    /*
    *   获取当前账户的升级信息
    */
    @SuppressLint("ResourceAsColor")
    private void initLevel() {
        Intent intent = getIntent();
        nowLevel = intent.getStringExtra("nowLevel");
        canUpLevel = intent.getStringExtra("canUpLevel");
        Log.d("获取等级：", nowLevel + canUpLevel);
        text_nowLevel.setText(nowLevel);
        if (canUpLevel.equals("顶级会员")) {
            text_canUpTip.setText("您已经成为:");
            text_canUpLevel.setText("顶级会员");
            btn_goUp.setText("已经成为顶级会员");
            btn_goUp.setEnabled(false);
            text_canUpLevel.setTextColor(R.color.themeColor);
        } else {
            text_canUpLevel.setText(canUpLevel);
        }
    }
    /*
     *   初始化 上级和九星用户 并填入 Examine 表
     * */
    public void initUpNine(){


        // 更新 直线上级的微信和九星上级的微信
        Window window = mMyDialog.getWindow();
        text_weChat01 = window.findViewById(R.id.text_oneWechat);
        text_weChat02 = window.findViewById(R.id.text_twoWechat);


        /*if (sendUser.getLevel().equals(0)){}else {
            text_twoWechatHello.setVisibility(View.GONE);
            text_weChat02.setVisibility(View.GONE);
            btn_copy02.setVisibility(View.GONE);
            Log.e("TAG","已隐藏");
        }*/

        // 根据 upgrade 表取得 上级的微信 和九星微信
        BmobQuery<Upgrade> upgradeBmobQuery = new BmobQuery<>();
        upgradeBmobQuery.addWhereEqualTo("nowUser", sendUser);
        upgradeBmobQuery.include("upUser,myNineUser");
        upgradeBmobQuery.findObjects(new FindListener<Upgrade>() {
            @Override
            public void done(List<Upgrade> list, BmobException e) {
                if (e == null) {

                    // 获得上级微信并填写
                    /**
                    *  0 读取Upgrade表
                     *  1-8 使用云函数随机获取
                    * */
                    if (sendUser.getLevel() == 0){
                        upUser  = list.get(0).getUpUser();
                        examine.setUpUser(upUser);
                        text_weChat01.setText(upUser.getWechat());
                    }else if (0<sendUser.getLevel() && sendUser.getLevel()<9){
                        //查询所有九星会员随机抽取一个
                        BmobQuery<User> bmobQuery = new BmobQuery<>();
                        bmobQuery.addWhereEqualTo("level", sendUser.getLevel()+1);
                        bmobQuery.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                if (e == null) {
                                    Random random = new Random();
                                    int n = random.nextInt(list.size());
                                    // 获取一个随机的九星级别用户
                                    upUser  = list.get(n);
                                    examine.setUpUser(upUser);
                                    text_weChat01.setText(upUser.getWechat());
                                } else {
                                    Log.e(TAG, "随机取得上级的失败"+e.getMessage());
                                }
                            }
                        });
                    }
                    // 获得九星微信并填写
                    nineUser  = list.get(0).getMyNineUser();
                    examine.setNineUser(nineUser);
                    text_weChat02.setText(nineUser.getWechat());
                } else {
                    text_weChat01.setText("获取失败");
                    text_weChat02.setText("获取失败");
                }
            }
        });

    }

    private void initEven() {
        btn_goUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                window = mMyDialog.getWindow();

                text_cancel = window.findViewById(R.id.text_cancel);
                text_commit = window.findViewById(R.id.text_commit);
                text_weChat01 = window.findViewById(R.id.text_oneWechat);
                text_weChat02 = window.findViewById(R.id.text_twoWechat);
                btn_copy01 = window.findViewById(R.id.btn_copy01);
                btn_copy02 = window.findViewById(R.id.btn_copy02);
                text_twoWechatHello = window.findViewById(R.id.text_twoWechatHello);

                if (sendUser.getLevel() != 0){
                    text_twoWechatHello.setVisibility(View.GONE);
                    text_weChat02.setVisibility(View.GONE);
                    btn_copy02.setVisibility(View.GONE);
                }

                mMyDialog.show();


                //取消
                text_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMyDialog.cancel();
                    }
                });
                //提交
                text_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        *   判定是否提交，只能提交一次
                        *   查询 examine 表，若 sendUser 有当前User 则已提交
                        * */
                        BmobQuery<Examine> query = new BmobQuery<>();
                        query.addWhereEqualTo("sendUser",sendUser);
                        BmobQuery<Examine> queryStatus = new BmobQuery<>();
                        query.addWhereEqualTo("upUserStatus",false);
                        List<BmobQuery<Examine>> queryArrayList = new ArrayList<BmobQuery<Examine>>();
                        queryArrayList.add(query);
                        queryArrayList.add(queryStatus);

                        BmobQuery<Examine> andQuery = new BmobQuery<>();
                        andQuery.and(queryArrayList);
                        andQuery.findObjects(new FindListener<Examine>() {
                            @Override
                            public void done(List<Examine> list, BmobException e) {
                                if (e == null){
                                    // 校检成功，判断
                                    if (list.size() > 0 ){
                                        //表中已经有数据，不可提交
                                        mMyDialog.cancel();
                                        MyUtil.showToast(Menu04Activity.this,"已经提交过了哟，请等待审核！");
                                    }else {
                                        // 可以提交
                                        mMyDialog.cancel();
                                        // 提交数据
                                        // 把当前账号信息推送到 examine
                                        examine.setUpUserStatus(false);
                                        examine.setNineUserStatus(false);
                                        examine.setUser(false);
                                        examine.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e  == null){
                                                    Log.d(TAG,"一个审核信息已经提交,审核编号为："+ s );
                                                    MyUtil.showToast(Menu04Activity.this, "提交申请成功");
                                                }else {
                                                    Log.d(TAG,"Sorry! 审核信息提交失败" + e.getMessage());
                                                    MyUtil.showToast(Menu04Activity.this, "提交申请失败！！");
                                                }
                                            }
                                        });
                                    }
                                }else {
                                    // 校检失败
                                    Log.d(TAG,"校检失败。。。");
                                    MyUtil.showToast(Menu04Activity.this,"提交失败！用户校检失败！请联系管理员\n错误代码：" + e.getMessage());
                                }
                            }

                        });
                    }
                });
                btn_copy01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //剪贴板对象
                        ClipboardManager myClipboard;
                        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData myClip;
                        myClip = ClipData.newPlainText("weChat01", text_weChat01.getText().toString());
                        myClipboard.setPrimaryClip(myClip);
                        MyUtil.showToast(Menu04Activity.this, "微信号：" + text_weChat01.getText().toString() + "-复制成功");
                    }
                });
                btn_copy02.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //剪贴板对象
                        ClipboardManager myClipboard;
                        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData myClip;
                        myClip = ClipData.newPlainText("weChat02", text_weChat02.getText().toString());
                        myClipboard.setPrimaryClip(myClip);

                        MyUtil.showToast(Menu04Activity.this, "微信号：" + text_weChat02.getText().toString() + "-复制成功");
                    }
                });
            }
        });

    }


    /**
    *  左上角返回箭头
    * */
    public void goBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("申请认证");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
