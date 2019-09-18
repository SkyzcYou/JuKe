package top.skyzc.juke.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import top.skyzc.juke.R;
import top.skyzc.juke.model.Member;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private List<Member> memberList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_name,text_phoneNumber,text_level;
        ImageView img_wechat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_name = (TextView) itemView.findViewById(R.id.text_name);
            text_phoneNumber = (TextView) itemView.findViewById(R.id.text_phoneNmuber);
            text_level = (TextView) itemView.findViewById(R.id.text_level);
            img_wechat = (ImageView) itemView.findViewById(R.id.img_wechat);
        }
    }
    public MemberAdapter(List<Member> memberList){
        this.memberList = memberList;
    }
    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.member_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder viewHolder, int i) {
        Member member = memberList.get(i);
        viewHolder.text_name.setText(member.getUsername());
        viewHolder.text_phoneNumber.setText(member.getPhoneNumber());
        viewHolder.text_level.setText(member.getLevel());
        viewHolder.img_wechat.setImageResource(member.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
