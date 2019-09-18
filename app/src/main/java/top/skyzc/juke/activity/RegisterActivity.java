package top.skyzc.juke.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import top.skyzc.juke.R;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.CheckPhoneNumber;
import top.skyzc.juke.util.CountDownTimerUtils;
import top.skyzc.juke.util.MyUtil;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    private EditText edit_username, edit_phoneNumber, edit_verCode, edit_password, edit_repassword;
    private Button btn_getVerCode, btn_goRegister;
    private String phoneNumber, verCode, name, password, rePassword;
    private CountDownTimerUtils countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        goBack();
        initView();
    }

    private void initView() {
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_phoneNumber = (EditText) findViewById(R.id.edit_phoneNumber);
        edit_verCode = (EditText) findViewById(R.id.edit_yanzhengma);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_repassword = (EditText) findViewById(R.id.edit_repassword);
        btn_getVerCode = (Button) findViewById(R.id.btn_getYanzhengma);
        btn_goRegister = (Button) findViewById(R.id.btn_goRegister);
        //发送验证码
        btn_getVerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = edit_phoneNumber.getText().toString();
                // 判定手机号
                if (CheckPhoneNumber.isPhoneNumber(phoneNumber)) {
                    Log.d(TAG, "手机验证码发送中。。。。");
                    sendBmobSms(phoneNumber);
                    Log.d(TAG, "手机验证码发送完毕");
                    // 发送成功进入倒计时
                    countDownTimer = new CountDownTimerUtils(btn_getVerCode, 60000, 1000);
                    countDownTimer.start();
                } else {
                    MyUtil.showToast(RegisterActivity.this, "手机号码有误！");
                }
            }
        });
        btn_goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = edit_phoneNumber.getText().toString();
                verCode = edit_verCode.getText().toString();
                name = edit_username.getText().toString();
                password = edit_password.getText().toString();
                rePassword = edit_repassword.getText().toString();

                if (password.equals(rePassword)) {
                    signOrLogin(phoneNumber, verCode, name, password);
                } else {
                    MyUtil.showToast(RegisterActivity.this, "密码格式有误！请检查：两次密码必须一样且大于6位");
                }
            }
        });
    }

    public void goBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("注册账号");
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
     *   检查手机号是否合法
     */
//    private boolean checkPhoneNumber(String phoneNumber) {
//        if (phoneNumber.matches(CheckPhoneNumber.REGEX_MOBILE)) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /*
     *  Bmob发送短信接口
     * */
    public void sendBmobSms(String phoneNumber) {
        /**
         * TODO template 如果是自定义短信模板，此处替换为你在控制台设置的自定义短信模板名称；如果没有对应的自定义短信模板，则使用默认短信模板。
         */
        BmobSMS.requestSMSCode(phoneNumber, "JukeSMS", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    MyUtil.showToast(RegisterActivity.this, "发送验证码成功");
                    Log.d(TAG, "发送验证码成功，短信ID：" + smsId + "\n");
                } else {
                    MyUtil.showToast(RegisterActivity.this, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                    Log.d(TAG, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                }
            }
        });
    }

    /**
     * 一键注册或登录的同时保存其他字段的数据
     *
     * @param phone
     * @param code
     */
    private void signOrLogin(String phone, String code, String name, String password) {
        User user = new User();
        //设置手机号码（必填）
        user.setMobilePhoneNumber(phone);
        //设置用户名，如果没有传用户名，则默认为手机号码
        user.setUsername(name);
        //设置用户密码
        user.setPassword(password);
        user.setMobilePhoneNumberVerified(true);
        user.setLevel(0);
        user.setInvID("ck"+ getRandom());
        user.signOrLogin(code, new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    MyUtil.showToast(RegisterActivity.this, "注册成功：" + user.getUsername());
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    MyUtil.showToast(RegisterActivity.this, "注册失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
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
            System.out.println(flag.toString());
            return flag.toString();
    }



}
