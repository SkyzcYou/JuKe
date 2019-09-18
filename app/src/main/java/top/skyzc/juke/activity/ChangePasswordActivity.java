package top.skyzc.juke.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import top.skyzc.juke.R;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.MyUtil;

public class ChangePasswordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        goBack();
    }
    public void goBack(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("修改密码");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button button =(Button) findViewById(R.id.go_btn);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }
    /**
     * 提供旧密码修改密码
     */
    public void updatePassword(){
        EditText old_edit = (EditText) findViewById(R.id.old_edit);
        EditText new_edit01 = (EditText) findViewById(R.id.new_edit01);
        EditText new_edit02 = (EditText) findViewById(R.id.new_edit02);

        String old = old_edit.getText().toString();
        String new01 = new_edit01.getText().toString();
        String new02 = new_edit02.getText().toString();

        if (checkGood(new01,new02)){
            //TODO 此处替换为你的旧密码和新密码
            BmobUser.updateCurrentUserPassword(old, new02, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        MyUtil.showToast(ChangePasswordActivity.this, "修改成功");
                    } else {
                        MyUtil.showToast(ChangePasswordActivity.this,"修改失败："+e.getMessage());
                    }
                }
            });
        }else {
            MyUtil.showToast(ChangePasswordActivity.this,"两次密码不一样！或者格式有误(必须大于6位)！请检查！");
        }
    }
    /*
    * 检查两次密码是否一样
    * */

    public boolean checkGood(String text01,String text02){
        if (text01.equals(text02) && text01.length() >= 6){
            return true;
        }else {
            return false;
        }
    }
}
