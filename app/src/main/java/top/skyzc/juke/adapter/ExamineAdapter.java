package top.skyzc.juke.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import top.skyzc.juke.R;
import top.skyzc.juke.model.ExamineRv;

public class ExamineAdapter extends RecyclerView.Adapter<ExamineAdapter.MyViewHolder> implements View.OnClickListener{

    private List<ExamineRv> list;//数据源
    private Context context;//上下文

    public ExamineAdapter(List<ExamineRv> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.examine_item,parent,false);
        return new MyViewHolder(view);
    }

    //绑定
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExamineRv examineRv = list.get(position);
        holder.ivIcon.setBackgroundResource(examineRv.getImageUrl());
        holder.tvUsername.setText(examineRv.getUsername());
        holder.tvphoneNumber.setText(examineRv.getPhoneNumber());
        holder.tvLevel.setText(examineRv.getLevel());

        holder.itemView.setTag(position);
        holder.btnOk.setTag(position);
        holder.btnBad.setTag(position);
    }

    //有多少个item？
    @Override
    public int getItemCount() {
        return list.size();
    }

    //创建MyViewHolder继承RecyclerView.ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivIcon;
        private TextView tvUsername, tvphoneNumber,tvLevel;
        private Button btnOk, btnBad;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.img_head);
            tvUsername = itemView.findViewById(R.id.text_name);
            tvphoneNumber = itemView.findViewById(R.id.text_phoneNmuber);
            tvLevel = itemView.findViewById(R.id.text_level);
            btnOk = itemView.findViewById(R.id.btn_ok);
            btnBad = itemView.findViewById(R.id.btn_bad);

            // 为ItemView添加点击事件
            itemView.setOnClickListener(ExamineAdapter.this);
            btnOk.setOnClickListener(ExamineAdapter.this);
            btnBad.setOnClickListener(ExamineAdapter.this);
        }

    }

    //=======================以下为item中的button控件点击事件处理===================================

    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    }

    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, ViewName viewName, int position);
        void onItemLongClick(View v);
    }

    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener  listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
                case R.id.recyclerView:
                    mOnItemClickListener.onItemClick(v, ViewName.PRACTISE, position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
            }
        }
    }
}
