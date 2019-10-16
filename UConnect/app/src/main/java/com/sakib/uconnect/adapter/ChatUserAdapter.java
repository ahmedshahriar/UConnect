package com.sakib.uconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sakib.uconnect.ChatActivity;
import com.sakib.uconnect.R;
import com.sakib.uconnect.model.User;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private boolean isActive;

    public ChatUserAdapter(Context context, List<User> userList, boolean isActive) {
        this.context = context;
        this.userList = userList;
        this.isActive = isActive;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_chat_item,parent,false);
        return new ChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);
        Log.d("adapter ", "onBindViewHolder: "+user.getId());
        holder.tvUserName.setText(user.getName());


        if(user.getImageUrl().equals("default")){
            holder.profilePic.setImageResource(R.drawable.blank_pro_pic);
        }
        else {
            Glide.with(context.getApplicationContext()).load(user.getImageUrl()).into(holder.profilePic);
        }

        if(isActive){
            if(user.getStatus().equals("online")){
                holder.imgStatusOff.setVisibility(View.GONE);
                holder.imgStatusOn.setVisibility(View.VISIBLE);
            }else {
                holder.imgStatusOff.setVisibility(View.VISIBLE);
                holder.imgStatusOn.setVisibility(View.GONE);
            }
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, ChatActivity.class);
                intent.putExtra("userId",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView tvUserName;
        TextView tvDateTime;
        TextView tvText;
        ImageView profilePic;
        ImageView imgStatusOff;
        ImageView imgStatusOn;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvText = itemView.findViewById(R.id.tv_message);
            tvUserName = itemView.findViewById(R.id.tv_username);
            profilePic = itemView.findViewById(R.id.iv_pro_pic);
            imgStatusOff = itemView.findViewById(R.id.iv_status_off);
            imgStatusOn = itemView.findViewById(R.id.iv_status_on);

        }

    }

}
