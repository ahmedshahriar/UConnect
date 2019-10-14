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

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    private Context context;
    private List<User> userList;

    public UserListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_user_list_item,parent,false);
        return new ViewHolder(view);
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
            Glide.with(context).load(user.getImageUrl()).into(holder.profilePic);
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


        public TextView tvUserName;
        public ImageView profilePic;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            tvUserName = itemView.findViewById(R.id.user_tv_username);
            profilePic = itemView.findViewById(R.id.iv_pro_pic);

        }

    }
}
