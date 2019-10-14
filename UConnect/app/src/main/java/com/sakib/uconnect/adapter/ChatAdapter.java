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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sakib.uconnect.ChatActivity;
import com.sakib.uconnect.R;
import com.sakib.uconnect.model.Chat;
import com.sakib.uconnect.model.User;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Chat> chatList;
    private String imageUrl;
    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.view_user_list_item,parent,false);
//        return new ChatAdapter.ViewHolder(view);
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        final Chat chat = chatList.get(position);

        holder.tvMessageBody.setText(chat.getMessage());

        if(imageUrl.equals("default")){
            if(holder.profilePic!=null){
                holder.profilePic.setImageResource(R.drawable.blank_pro_pic);
            }

        }
        else {
            Glide.with(context).load(imageUrl).into(holder.profilePic);
        }


    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView tvMessageBody;
        public ImageView profilePic;
        public TextView  messageTime;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            tvMessageBody = itemView.findViewById(R.id.text_message_body);
            profilePic = itemView.findViewById(R.id.image_message_profile);
            messageTime = itemView.findViewById(R.id.text_message_time);

        }

    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
}
