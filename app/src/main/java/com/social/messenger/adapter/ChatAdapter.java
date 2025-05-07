package com.social.messenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.social.messenger.R;
import com.social.messenger.models.ChatMessage;
import com.social.messenger.ui.ChatActivity;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatMessage> chatList;

    public ChatAdapter(Context context, List<ChatMessage> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chat = chatList.get(position);

        holder.userName.setText(chat.getUserName());
        holder.lastMessage.setText(chat.getLastMessage());
        holder.timestamp.setText(chat.getTimestamp());

        holder.unreadCount.setVisibility(chat.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
        holder.unreadCount.setText(String.valueOf(chat.getUnreadCount()));

        // Tạm thời không load ảnh
        // holder.avatarImage.setImageResource(R.drawable.ic_default_avatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage, timestamp, unreadCount;
        ImageView avatarImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.text_view_user_name);
            lastMessage = itemView.findViewById(R.id.text_view_last_message);
            timestamp = itemView.findViewById(R.id.text_view_timestamp);
            unreadCount = itemView.findViewById(R.id.text_view_unread_count);
            avatarImage = itemView.findViewById(R.id.image_view_profile_picture);
        }
    }
}
