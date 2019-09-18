package top.skyzc.juke.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import top.skyzc.juke.R;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.CheckPhoneNumber;
import top.skyzc.juke.util.CountDownTimerUtils;
import top.skyzc.juke.util.MyUtil;

public class ForgotPassWordActivity extends BaseActivity {
    private static final String TAG = "ForgotPassWordActivity";
    private CountDownTimerUtils countDownTimer;
    private EditText edit_phoneNumber, edit_verCode, edit_passord, edit_rePassword;
    private Button btn_getCode, btn_goEdit;
    private String phoneNumber, verCode, password, repassword;
    private boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_word);
        goBack();
        initView();

        // 按钮：手机号获取验证码
        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = edit_phoneNumber.getText().toString();
                // 判定手机号
                if (CheckPhoneNumber.isPhoneNumber(phoneNumber)){
                    Log.d(TAG, "手机验证码发送中。。。。");
                    sendBmobSms(phoneNumber);
                    Log.d(TAG, "手机验证码发送完毕");
                    // 发送成功进入倒计时
                    countDownTimer = new CountDownTimerUtils(btn_getCode, 60000, 1000);
                    countDownTimer.start();
                }else {
                    MyUtil.showToast(ForgotPassWordActivity.this,"手机号码有误！");
                }
            }
        });
        // 按钮：重置密码
        btn_goEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = edit_phoneNumber.getText().toString();
                verCode = edit_verCode.getText().toString();
                password = edit_passord.getText().toString();
                repassword = edit_rePassword.getText().toString();

                if (password.equals(repassword)){
                    // 重置密码
                    BmobUser.resetPasswordBySMSCode(verCode, repassword, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d(TAG,"重置成功");
                                MyUtil.showToast(ForgotPassWordActivity.this,"重置成功");
                                finish();
                            } else {
                                Log.d(TAG,"重置失败：" + e.getErrorCode() + "-" + e.getMessage());
                                MyUtil.showToast(ForgotPassWordActivity.this,"重置失败");
                            }
                        }
                    });
                }else {
                    MyUtil.showToast(ForgotPassWordActivity.this,"两次密码不相同，请检查后再试");
                }
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
     *  初始化视图
     * */
    private void initView() {
        edit_phoneNumber = (EditText) findViewById(R.id.edit_phoneNumber);
        edit_verCode = (EditText) findViewById(R.id.edit_yanzhengma);
        edit_passord = (EditText) findViewById(R.id.edit_password);
        edit_rePassword = (EditText) findViewById(R.id.edit_repassword);
        btn_getCode = (Button) findViewById(R.id.btn_getYanzhengma);
        btn_goEdit = (Button) findViewById(R.id.btn_goEdit);
    }

    /*
     *  Toolbar 初始化
     * */
    public void goBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("重置密码");
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
                    MyUtil.showToast(ForgotPassWordActivity.this, "发送验证码成功");
                    Log.d(TAG, "发送验证码成功，短信ID：" + smsId + "\n");
                } else {
                    MyUtil.showToast(ForgotPassWordActivity.this, "发送验证码失败");
                    Log.d(TAG, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n");
                }
            }
        });
    }

}
