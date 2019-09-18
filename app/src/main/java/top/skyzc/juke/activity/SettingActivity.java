package top.skyzc.juke.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import top.skyzc.juke.R;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.ActivityManage;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.MyUtil;
import top.skyzc.juke.util.UserUpgradeCase;

public class SettingActivity extends BaseActivity {
    private TextView text_version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        goBack();
        initView();
    }

    private void initView() {
        text_version = findViewById(R.id.text_version);
        text_version.setText(getVersionName(SettingActivity.this));
    }

    public void goBack(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
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

    /*
    * 退出登录
    * */
    public void loginOut(View view) {
        BmobUser.logOut();
        // 清除本地信息
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.clear();
        Log.d("SettingActivity","清楚本地信息成功。。。。即将退出！");
        MyUtil.showToast(SettingActivity.this,"退出登录成功！");
        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
        startActivity(intent);
        ActivityManage.finishAll();
    }
    /*
    * 修改密码
    * */
    public void editPro(View view) {
        Intent intent = new Intent(SettingActivity.this,ChangePasswordActivity.class);
        startActivity(intent);
    }
    /*
    *
    * */
    public String getVersionName(Context context){
        String versionName = "";
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public void test(View view) {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("level", 9);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    Random random = new Random();
                    int n = random.nextInt(list.size());
                    // 获取一个随机的九星级别用户
                     MyUtil.showToast(SettingActivity.this,list.get(n).getUsername());
                } else {
                    Log.e("TAG", "随机取得" + 9+ "星User失败"+e.getMessage());
                }
            }
        });
    }
}
