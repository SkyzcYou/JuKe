package top.skyzc.juke.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import top.skyzc.juke.R;
import top.skyzc.juke.adapter.MemberAdapter;
import top.skyzc.juke.model.Member;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;
import top.skyzc.juke.util.MyUtil;

public class Menu02Activity extends BaseActivity {
    private static List<Member> memberList = new ArrayList<>();
    private String myInvId;
//
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu02);
        goBack();
        initView();

        initMember();

    }

    private void initView() {

    }

    @SuppressLint("LongLogTag")
    private void initMember() {
        //获取当前用户InvID
        Intent intent = getIntent();
        myInvId = intent.getStringExtra("myInvId");
        // 加载downUser列表
        memberList.clear();
        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("heInvID", myInvId);
        categoryBmobQuery.order("-createdAt");
        categoryBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    //MyUtil.showToast(Menu02Activity.this, "查询成功：" + object.size()+ "==="+object.get(0).getUsername());
                    //Log.d("查询成功","查询成功：" + object.size()+ "==="+object.get(0).getUsername());
                    for (int i = 0 ; i < object.size();i++){
                        // 等级判定
                        int level = object.get(i).getLevel();
                        String [] levels = {"普通","一星","二星","三星","四星","五星","六星","七星","八星","九星"};

                        Member member = new Member("姓名："+object.get(i).getUsername(),"手机号："+ object.get(i).getMobilePhoneNumber(),
                                "等级："+levels[level]+ "会员",R.drawable.head);
                        memberList.add(member);
                        Log.d("memberList测试",memberList.get(i).getPhoneNumber());
                    }
                } else {
                    Log.e("BMOB", e.toString());
                    //MyUtil.showToast(Menu02Activity.this, e.getMessage());
                    //MyUtil.showToast(Menu02Activity.this, e.getMessage());
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Menu02Activity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                Log.d("memberList大小", String.valueOf(memberList.size()));
                TextView allSize = findViewById(R.id.tv_allSize);
                allSize.setText("一星及一星以上人数：" + memberList.size());
                MemberAdapter adapter = new MemberAdapter(memberList);
                recyclerView.setAdapter(adapter);
            }

        });


    }

    public void goBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("我的团队");
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
    /**
     * name为football的类别
     */
    private void equal() {
        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("heInvID", "ck140472");
        categoryBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    MyUtil.showToast(Menu02Activity.this, "查询成功：" + object.size()+ "==="+object.get(0).getUsername());
                    Log.d("查询成功","查询成功：" + object.size()+ "==="+object.get(0).getUsername());
                } else {
                    Log.e("BMOB", e.toString());
                    MyUtil.showToast(Menu02Activity.this, e.getMessage());
                }
            }
        });
    }
}
