package com.social.messenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.social.messenger.R;
import com.social.messenger.models.User;

import java.util.List;

public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                FriendshipAdapter extends RecyclerView.Adapter<FriendshipAdapter.ViewHolder> {


    private final String action;
    private List<User> userList;
    private List<String> selectedUsers;
    private OnAddFriendClickListener listener;
    private OnBtnXoaClickListener btnXoaClickListener;

    public interface OnAddFriendClickListener {
        void onAddFriendClicked(User user);
    }
    public interface OnBtnXoaClickListener {
        void onBtnXoaClickListener(User user);
    }

    public FriendshipAdapter(List<User> userList, String action, OnAddFriendClickListener listener) {
        this.userList = userList;
        this.listener = listener;
        this.action = action;
    }
    public FriendshipAdapter(List<User> userList, String action, List<String> selectedUsers, OnAddFriendClickListener listener) {
        this.userList = userList;
        this.listener = listener;
        this.selectedUsers = selectedUsers;
        this.action = action;
    }
    public FriendshipAdapter(List<User> userList, String action, OnAddFriendClickListener listener, OnBtnXoaClickListener btnXoaClickListener) {
        this.userList = userList;
        this.listener = listener;
        this.btnXoaClickListener = btnXoaClickListener;
        this.action = action;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewName.setText(user.getFullName());
        holder.btnAddFriend.setText(action);
        if (action.equals("Chấp nhận")) {
            holder.btnXoa.setVisibility(View.VISIBLE);
        } else {
            holder.btnXoa.setVisibility(View.GONE);
        }
        if(selectedUsers != null) {
            if (selectedUsers.contains(user.getId())) {
                holder.btnAddFriend.setText("Bỏ chọn");
            } else {
                holder.btnAddFriend.setText("Thêm");
            }
        }
        // Nếu có avatar:
        // Glide.with(holder.itemView.getContext())
        //     .load(user.getAvatarUrl())
        //     .into(holder.imageViewAvatar);

        holder.btnAddFriend.setOnClickListener(v -> listener.onAddFriendClicked(user));
        holder.btnXoa.setOnClickListener(v -> btnXoaClickListener.onBtnXoaClickListener(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAvatar;
        TextView textViewName;
        MaterialButton btnAddFriend, btnXoa;

        ViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = itemView.findViewById(R.id.image_view_profile_picture);
            textViewName = itemView.findViewById(R.id.text_view_user_name);
            btnAddFriend = itemView.findViewById(R.id.button_add_friend);
            btnXoa = itemView.findViewById(R.id.button_xoa);
        }
    }
    public void updateData(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged();
    }

}
