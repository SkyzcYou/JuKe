package top.skyzc.juke.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import top.skyzc.juke.R;
import top.skyzc.juke.model.Upgrade;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.CheckPhoneNumber;
import top.skyzc.juke.util.MyUtil;

public class Menu01Activity extends BaseActivity {
    private final String TAG = "Menu01Activity";
    private EditText edit_myInvID,edit_hePhoneNumber,edit_heName,edit_hePassword,edit_heRePassword,edit_heWechat;
    private Button btn_goRegister;
    private String heInvID,hePhoneNumber,heName,hePassword,heRePassword,heWechat;

    private User nowUser = BmobUser.getCurrentUser(User.class);
    private User nineUser;
    private String nowUserAcc,nowUserPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu01);
        goBack();
        initView();
        initEven();
    }
    /*
    * 初始化View
    * */
    private void initView() {
        edit_myInvID = (EditText) findViewById(R.id.edit_myInvID);
        //设置 不可编辑
        edit_myInvID.setKeyListener(null);
        Intent intent = getIntent();
        edit_myInvID .setText(intent.getStringExtra("myInvId"));

        edit_myInvID = findViewById(R.id.edit_myInvID);
        edit_hePhoneNumber = findViewById(R.id.edit_hePhone);
        edit_heName = findViewById(R.id.edit_heName);
        edit_hePassword = findViewById(R.id.edit_hePassword);
        edit_heRePassword = findViewById(R.id.edit_reHePassword);
        edit_heWechat = findViewById(R.id.edit_heWechat);
        btn_goRegister = findViewById(R.id.btn_goRegister);

    }
    /*
    * 初始化事件
    * */
    private void initEven() {
        btn_goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取得数据
                heInvID = edit_myInvID.getText().toString();
                hePhoneNumber = edit_hePhoneNumber.getText().toString();
                heName = edit_heName.getText().toString();
                hePassword = edit_hePassword.getText().toString();
                heRePassword = edit_heRePassword.getText().toString();
                heWechat = edit_heWechat.getText().toString();
                // 判定手机号
                if (CheckPhoneNumber.isPhoneNumber(hePhoneNumber)){
                    // 校验两次密码格式
                    if (hePassword.equals(heRePassword) && hePassword.length() >= 6){
                        //注册
                        register(heInvID,hePhoneNumber,heName,hePassword,heWechat);
                    }else {
                        MyUtil.showToast(Menu01Activity.this,"两次密码必须一样，且大于6位");
                    }
                }else {
                    MyUtil.showToast(Menu01Activity.this,"手机号不合法！");
                }
            }
        });
    }
    /*
    *   Bomb 用户注册
    * */
    private void register(String heInvID,String hePhoneNumber,String heName,String hePassword,String heWechat) {
        Log.d(TAG,"注册之前的User:"+BmobUser.getCurrentUser(User.class).getUsername());
        // 注册
        User user = new User();
        user.setHeInvID(heInvID);
        user.setUsername(heName);
        user.setMobilePhoneNumber(hePhoneNumber);
        user.setMobilePhoneNumberVerified(true);
        user.setPassword(hePassword);
        user.setLevel(0);
        user.setUpUser(nowUser);
        user.setInvID("ck"+getRandom());
        user.setWechat(heWechat);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    Toast.makeText(Menu01Activity.this, "注册成功！", Toast.LENGTH_LONG).show();
                    // 一旦注册成功，就将该用户加入 Upgrade 表，并给他指定上级用户和(九星)升级用户
                    final Upgrade upgrade = new Upgrade();
                    upgrade.setNowUser(user);
                    upgrade.setUpUser(nowUser);
                    // 随机取得一个九星用户
                    //查询所有九星会员随机抽取一个
                    BmobQuery<User> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("level", 9);
                    bmobQuery.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null) {
                                Random random = new Random();
                                int n = random.nextInt(list.size());
                                // 获取一个随机的九星级别用户
                                nineUser  = list.get(n);
                                upgrade.setMyNineUser(nineUser);
                                upgrade.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null){
                                            Log.e(TAG, "取得 九星上级的成功，Upgrade表生成成功");
                                        }else {
                                            Log.e(TAG, "取得 九星上级的成功，但是Upgrade表生成失败");
                                        }
                                    }
                                });
                            } else {
                                Log.e(TAG, "随机取得 九星上级的失败"+e.getMessage());
                            }
                        }
                    });
                    // 退出登录，清空本地缓存
                    BmobUser.logOut();
                    // Log.d(TAG,"注册之后的User:"+BmobUser.getCurrentUser(User.class).getUsername());
                } else {
                    Toast.makeText(Menu01Activity.this, "注册失败！！！错误信息：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        // 重新登录
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String acc = preferences.getString("acc","");
        String pwd = preferences.getString("pwd","");
        Log.d(TAG,"获取到信息=>acc:" + acc + " pwd:" + pwd);
        login(acc,pwd);
    }
    /*
     * 登录模块
     * */
    public void login(String acc,String pwd) {

        final User user = new User();
        user.setUsername(acc);
        user.setPassword(pwd);

        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    //获取当前用户对象
                    User nowUser = BmobUser.getCurrentUser(User.class);
                    Log.d(TAG,nowUser.getUsername() + " 登录成功");
                } else {
                    Log.d(TAG,nowUser.getUsername() + " 重新登录失败");
                }
            }
        });
    }
    /*
     * 6位随机数
     * */
    public String getRandom(){
        String sources = "0123456789"; // 加上一些字母，就可以生成pc站的验证码了
        Random rand = new Random();
        StringBuffer flag = new StringBuffer();
        for (int j = 0; j < 6; j++)
        {
            flag.append(sources.charAt(rand.nextInt(9)) + "");
        }
        //System.out.println(flag.toString());
        return flag.toString();
    }
    /*
     *   左上角返回按钮
     * */
    public void goBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("创客新零售会员注册");
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
