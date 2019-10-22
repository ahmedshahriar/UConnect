package com.sakib.uconnect.adapter;

import android.content.Context;
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
import com.sakib.uconnect.R;
import com.sakib.uconnect.model.Chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        if(holder.profilePic!=null){
            if(imageUrl.equals("default")){
                    holder.profilePic.setImageResource(R.drawable.blank_pro_pic);
            }
            else {
                Glide.with(context.getApplicationContext()).load(imageUrl).into(holder.profilePic);
            }
        }
        if(position == chatList.size()-1){

            if(chat.isSeen()){
                holder.tvSeen.setText("seen");
            }else {
                holder.tvSeen.setText("delivered");
            }

        }
        else {
            holder.tvSeen.setVisibility(View.GONE);
        }
        String time = chat.getCreatedAt();
        Date displayDateTime =null;
        SimpleDateFormat formatter1=new SimpleDateFormat("MMM d, yyyy hh:mm:ss aaa", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
        try {
            displayDateTime =formatter1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (displayDateTime != null) {
            holder.messageTime.setText(targetFormat.format(displayDateTime));
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
        private TextView tvSeen;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            tvMessageBody = itemView.findViewById(R.id.text_message_body);
            profilePic = itemView.findViewById(R.id.image_message_profile);
            messageTime = itemView.findViewById(R.id.text_message_time);
            tvSeen = itemView.findViewById(R.id.tv_seen);

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
