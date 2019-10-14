package com.sakib.uconnect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sakib.uconnect.R;
import com.sakib.uconnect.model.User;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public ChatUserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_chat_item,parent,false);
        return new ChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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


        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvText = itemView.findViewById(R.id.tv_message);
            tvUserName = itemView.findViewById(R.id.tv_username);
            profilePic = itemView.findViewById(R.id.iv_pro_pic);

        }

    }

}
