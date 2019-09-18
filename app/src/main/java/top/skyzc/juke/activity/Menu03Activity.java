package top.skyzc.juke.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import top.skyzc.juke.R;
import top.skyzc.juke.adapter.ExamineAdapter;
import top.skyzc.juke.model.Examine;
import top.skyzc.juke.model.ExamineRv;
import top.skyzc.juke.model.Level;
import top.skyzc.juke.model.User;
import top.skyzc.juke.util.BaseActivity;

public class Menu03Activity extends BaseActivity {
    private final static String TAG = "Menu03Activity";
    private RecyclerView recyclerView;
    private ExamineAdapter adapter;
    // 储存部分数据，用户显示视图
    private final List<ExamineRv> examineListRv = new ArrayList<>();
    // 储存一个完整的 Examine 对象
    private final List<Examine> examineList = new ArrayList<>();

    private Button btn_wait, btn_old;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //Toast.makeText(Menu03Activity.this, "查询成功！刷新UI、", Toast.LENGTH_SHORT).show();
                recyclerView.setLayoutManager(new LinearLayoutManager(Menu03Activity.this, LinearLayoutManager.VERTICAL, false));
                adapter = new ExamineAdapter(examineListRv, Menu03Activity.this);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(MyItemClickListener);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu03);
        goBack();

        initView();
        initEven();

        // 查询是异步操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询数据，查询完毕发送 msg
                initExamineRv();
            }
        }).start();


    }

    /**
     * 初始化View
     */
    private void initView() {
        btn_wait = findViewById(R.id.btn_wait);
        btn_old = findViewById(R.id.btn_old);
        recyclerView = findViewById(R.id.recyclerView);
    }

    /**
     * 顶部两个按钮
     */
    private void initEven() {
        btn_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        btn_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initExamineRv() {
        // 当前User
        User user = BmobUser.getCurrentUser(User.class);
        Log.d(TAG, "当前User:" + user.getUsername());
        /**
         * 1.查询upUser有自己,并且 upStatus=false 的 sendUser 信息
         * */
        BmobQuery<Examine> eqUp1 = new BmobQuery<>();
        eqUp1.addWhereEqualTo("upUser", user);
        BmobQuery<Examine> eqUp2 = new BmobQuery<>();
        eqUp2.addWhereEqualTo("upUserStatus", false);
        // 组装
        List<BmobQuery<Examine>> andUpQuerys = new ArrayList<BmobQuery<Examine>>();
        andUpQuerys.add(eqUp1);
        andUpQuerys.add(eqUp2);
        // 查询组装条件
        BmobQuery<Examine> eqUpQuery = new BmobQuery<Examine>();
        eqUpQuery.and(andUpQuerys);
        eqUpQuery.include("sendUser");
        eqUpQuery.order("-createdAt");
        eqUpQuery.findObjects(new FindListener<Examine>() {
            @Override
            public void done(List<Examine> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Log.d(TAG, "查询upUser字段成功-我目前作为直线上级要审核" + list.size() + "条订单：");
                        for (int i = 0; i < list.size(); i++) {
                            String[] levels = {"普通", "一星", "二星", "三星", "四星", "五星", "六星", "七星", "八星", "九星"};
                            User sendUser = list.get(i).getSendUser();
                            String level = "等级：" + levels[sendUser.getLevel()] + "会员";
                            Log.d(TAG, "他的ObjectId:" + sendUser.getObjectId() + "\n他的Username：" + sendUser.getUsername() + "他的等级：" + level);
                            examineListRv.add(new ExamineRv("姓名：" + sendUser.getUsername(), "手机号：" + sendUser.getMobilePhoneNumber(), "等级：" + levels[sendUser.getLevel()] + "会员", R.drawable.head));
                            examineList.add(list.get(i));
                            mHandler.sendEmptyMessage(1);
                        }
                    } else {
                        Log.d(TAG, "查询upUser字段成功-我目前作为直线上级没有审核订单：");
                    }
                } else {
                    Log.d(TAG, "查询 upUser 字段失败：" + e.getMessage());
                }
            }
        });
        /**
         * 2.查询nineUser有自己,并且 nineStatus=false 的 sendUser 信息
         * */
        BmobQuery<Examine> eqNine1 = new BmobQuery<>();
        eqNine1.addWhereEqualTo("nineUser", user);
        BmobQuery<Examine> eqNine2 = new BmobQuery<>();
        eqNine2.addWhereEqualTo("nineUserStatus", false);
        // 组装
        List<BmobQuery<Examine>> andNineQuerys = new ArrayList<BmobQuery<Examine>>();
        andNineQuerys.add(eqNine1);
        andNineQuerys.add(eqNine2);

        BmobQuery<Examine> eqNineQuery = new BmobQuery<>();
        eqNineQuery.and(andNineQuerys);
        eqNineQuery.include("sendUser");
        eqNineQuery.order("-createdAt");
        eqNineQuery.findObjects(new FindListener<Examine>() {
            @Override
            public void done(List<Examine> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Log.d(TAG, "查询nineUser字段成功-我目前作为九星上级要审核" + list.size() + "条订单：");
                        for (int i = 0; i < list.size(); i++) {
                            String[] levels = {"普通", "一星", "二星", "三星", "四星", "五星", "六星", "七星", "八星", "九星"};
                            User sendUser = list.get(i).getSendUser();
                            String level = "等级：" + levels[sendUser.getLevel()] + "会员";
                            Log.d(TAG, "他的ObjectId:" + sendUser.getObjectId() + "\n他的Username：" + sendUser.getUsername() + "他的等级：" + level);
                            examineListRv.add(new ExamineRv("姓名：" + sendUser.getUsername(), "手机号：" + sendUser.getMobilePhoneNumber(), "等级：" + levels[sendUser.getLevel()] + "会员", R.drawable.head));
                            examineList.add(list.get(i));
                            mHandler.sendEmptyMessage(1);
                        }
                    } else {
                        Log.d(TAG, "查询nineUser字段成功-我目前作为九星上级没有审核订单：");
                    }
                } else {
                    Log.d(TAG, "查询 nineUser 字段失败：" + e.getMessage());
                }
            }
        });


    }


    /**
     * item＋item里的控件点击监听事件
     */
    private ExamineAdapter.OnItemClickListener MyItemClickListener = new ExamineAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, ExamineAdapter.ViewName viewName, final int position) {
            switch (v.getId()) {
                case R.id.btn_ok:
                    // 点击同意=》 获取该Item对应的 sendUser(已经存入examineList),修改
                    if (BmobUser.getCurrentUser(User.class).getObjectId().equals(examineList.get(position).getUpUser().getObjectId())) {
                        Log.d(TAG, "我现在是：" + examineList.get(position).getSendUser().getUsername() + "的直线上级");
                        // 查询
                        // 当前为 直线上级，修改 当前的 examine 对象的 upUserStatus 的值为 true
                        examineList.get(position).setUpUserStatus(true);
                        examineList.get(position).setUser(true);
                        examineList.get(position).update(examineList.get(position).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d(TAG, "已通过");


                                    // 调用云函数，更新examineList.get(position).getSendUser()的等级
                                    String cloudCodeName = "UpUserLevel";
                                    String objectId = examineList.get(position).getSendUser().getObjectId();
                                    String level = String.valueOf(examineList.get(position).getSendUser().getLevel()+1);
                                    JSONObject params = new JSONObject();
                                    try {
                                        params.put("objectId",objectId);
                                        params.put("level",level);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
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

                                    // 删除 当前 这个examine，和examineRv
                                    examineList.remove(position);
                                    examineListRv.remove(position);
                                    mHandler.sendEmptyMessage(1);
                                } else {
                                    Log.d(TAG, "提交错误：" + e.getMessage());
                                }
                            }
                        });
                    } else if (BmobUser.getCurrentUser(User.class).getObjectId().equals(examineList.get(position).getNineUser().getObjectId())) {
                        Log.d(TAG, "我现在是：" + examineList.get(position).getSendUser().getUsername() + "的九星上级");
                        // 当前为 九星上级，修改 当前的 examine 对象的 nineUserStatus 的值为 true
                        examineList.get(position).setNineUserStatus(true);
                        examineList.get(position).update(examineList.get(position).getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d(TAG, "以通过");

                                    examineList.remove(position);
                                    examineListRv.remove(position);
                                    mHandler.sendEmptyMessage(1);
                                } else {
                                    Log.d(TAG, "提交错误：" + e.getMessage());
                                }
                            }
                        });
                    }
                    break;
                case R.id.btn_bad:
                    Log.d(TAG, "你点击了拒绝按钮" + (position + 1));
                    break;
                default:
                    Log.d(TAG, "你点击了item按钮" + (position + 1));
                    // Toast.makeText(Menu03Activity.this,"你点击了item按钮"+(position+1),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        @Override
        public void onItemLongClick(View v) {

        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /*
     *   左上角返回按钮
     * */
    public void goBack() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("审核升级");
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
