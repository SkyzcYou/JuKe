package top.skyzc.juke.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import top.skyzc.juke.R;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.MyUtil;

public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";
    private EditText edt_id, edt_password;
    private String id, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "16676baa23cda129e4feeed2e1d89064","BMOB");

        MyUtil.verifyStoragePermissions(this);
        //检测是否登录
        checkIsLogin();
    }

    /*
     * 登录检测模块
     * checkIsLogin()
     * */
    private void checkIsLogin() {
        if (BmobUser.isLogin()) {
            //跳转页面
            Log.d(TAG, "已检测到用户登录：" + BmobUser.getCurrentUser(User.class).getUsername());
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            //
        }
    }

    /*
     * 登录模块
     * */
    public void login(View view) {
        edt_id = findViewById(R.id.edt_id);
        edt_password = findViewById(R.id.edt_password);

        id = edt_id.getText().toString();
        password = edt_password.getText().toString();

        final User user = new User();
        user.setUsername(id);
        user.setPassword(password);

        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    //获取当前用户对象
                    User nowUser = BmobUser.getCurrentUser(User.class);
                    MyUtil.showToast(MainActivity.this, nowUser.getUsername() + " 登录成功");
                    ///跳转到主页
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    // 将信息保存
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("acc", id);
                    editor.putString("pwd", password);
                    editor.apply();
                    Log.d(TAG, "信息保存成功！");
                } else {
                    MyUtil.showToast(MainActivity.this, "登录失败！！请检查用户名和密码！" + e.getMessage());
                }
            }
        });
    }

    /*
     * 注册模块
     * */
    public void registered(View view) {
        // 跳转到注册页面
        MyUtil.showToast(MainActivity.this, "未打开注册通道！请找朋友帮忙注册或联系客服！");
        //Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        //startActivity(intent);
    }

    /*
     * 重置密码模块
     * */
    public void forget(View view) {
        // 跳转到重置密码模块
        Intent intent = new Intent(MainActivity.this, ForgotPassWordActivity.class);
        startActivity(intent);
    }
}
