package com.social.messenger.adapter;

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
import com.social.messenger.R;
import com.social.messenger.dto.ChatItem;
import com.social.messenger.dto.GroupDTO;
import com.social.messenger.models.User;
import com.social.messenger.ui.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatItem> chatList;
    private List<GroupDTO> groupDTOList;
    private boolean isPersonal = true; // Mặc định là hiển thị chat cá nhân

    public ChatAdapter() {}

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if (isPersonal) {
            ChatItem chat = chatList.get(position);

            holder.userName.setText(chat.getUser().getFullName());
            holder.lastMessage.setText(chat.getLastMessage());

            Date date = new Date(chat.getTimestamp());
            String formattedTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
            holder.timestamp.setText(formattedTime);

            holder.unreadCount.setVisibility(chat.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
            holder.unreadCount.setText(String.valueOf(chat.getUnreadCount()));
            Glide.with(context)
                    .load(chat.getUser().getAvatarUrl())  // link ảnh avatar
                    .placeholder(R.drawable.ic_default_avatar) // ảnh mặc định khi chưa load xong// ảnh hiển thị nếu load lỗi
                    .into(holder.avatarImage);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user", chat.getUser());
                context.startActivity(intent);
            });
        } else {
            GroupDTO group = groupDTOList.get(position);

            holder.userName.setText(group.getName());
            Log.d("GroupDTO", "Group Name: " + group.getName());
//            holder.lastMessage.setText(group.getLastMessage());
            holder.lastMessage.setText("");

            Date date = new Date(group.getTimestamp());
            String formattedTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
//            holder.timestamp.setText(formattedTime);
            holder.timestamp.setText("");

            holder.unreadCount.setVisibility(group.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
            holder.unreadCount.setText(String.valueOf(group.getUnreadCount()));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("groupId", group.getId());
                intent.putExtra("groupName", group.getName());
                intent.putExtra("IS_GROUP_CHAT", true);

                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return isPersonal
                ? (chatList != null ? chatList.size() : 0)
                : (groupDTOList != null ? groupDTOList.size() : 0);
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

    // Setter cho chat cá nhân
    public void setChatList(List<ChatItem> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    // Setter cho group
    public void setGroupDTOList(List<GroupDTO> groupDTOList) {
        this.groupDTOList = groupDTOList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Đặt chế độ hiển thị: true = cá nhân, false = nhóm
    public void setPersonal(boolean isPersonal) {
        this.isPersonal = isPersonal;
    }
}
