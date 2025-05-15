package com.social.messenger.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.social.messenger.R;
import com.social.messenger.dto.ChatMess;
import com.social.messenger.models.GroupMessage;
import com.social.messenger.models.Message;
import com.social.messenger.utils.SharedPrefManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private List<GroupMessage> GroupMessage;
    private String currentUserId ;
    private boolean isGroupChat = false;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }
    public MessageAdapter(List<GroupMessage> messages, String currentUserId, boolean isGroupChat) {
        this.GroupMessage = messages;
        this.currentUserId = currentUserId;
        this.isGroupChat = isGroupChat;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMess msg = new ChatMess();
        if (isGroupChat) {
            GroupMessage gmsg = GroupMessage.get(position);
            msg.setText(gmsg.getText());
            msg.setFileUrl(gmsg.getFileUrl());
            msg.setType(gmsg.getType());
            msg.setSenderId(gmsg.getSenderId());
            msg.setGroupId(gmsg.getGroupId());
            msg.setTimestamp(gmsg.getTimestamp());
            Log.d("TAG", "onBindViewHolder: " + msg.getText());
        }
        else {
            Message mmsg = messages.get(position);
            msg.setText(mmsg.getText());
            msg.setFileUrl(mmsg.getFileUrl());
            msg.setType(mmsg.getType());
            msg.setSenderId(mmsg.getSenderId());
            msg.setReceiverId(mmsg.getReceiverId());
            msg.setTimestamp(mmsg.getTimestamp());
            msg.setStatus(mmsg.getStatus());
            Log.d("TAG", "onBindViewHolder: " + msg.getText());
        }
        boolean isSent = msg.getSenderId().equals(currentUserId);
        Log.d("TAG", "onBindViewHolder: " + isSent + " " + msg.getSenderId() + " " + currentUserId);

        String type = msg.getType(); // text, image, video
//        String type = "text"; // text, image, video

        if (isSent) {
            holder.sentCard.setVisibility(View.VISIBLE);
            holder.receivedCard.setVisibility(View.GONE);

            // Reset visibility
            holder.sentText.setVisibility(View.GONE);
            holder.sentImage.setVisibility(View.GONE);
            holder.sentVideo.setVisibility(View.GONE);

            switch (type) {
                case "text":
                    holder.sentText.setVisibility(View.VISIBLE);
                    holder.sentText.setText(msg.getText());
                    break;

                case "image":
                    Log.d("TAG", "onBindViewHolder: " + msg.getFileUrl());
                    holder.sentImage.setVisibility(View.VISIBLE);
                    Glide.with(holder.itemView.getContext())
                            .load(msg.getFileUrl()) // URL ảnh
                            .into(holder.sentImage);
                    break;

                case "video":
                    Log.d("VideoView", "Video URL: " + msg.getFileUrl());

                    holder.sentVideo.setVisibility(View.VISIBLE);
                    holder.sentVideo.setVideoURI(Uri.parse(msg.getFileUrl()));

                    MediaController mediaController = new MediaController(holder.sentVideo.getContext());
                    mediaController.setAnchorView(holder.sentVideo);
                    holder.sentVideo.setMediaController(mediaController);
                    holder.sentVideo.requestFocus();
                    holder.sentVideo.start();
                    break;
            }

        } else {
            holder.receivedCard.setVisibility(View.VISIBLE);
            holder.sentCard.setVisibility(View.GONE);

            // Reset visibility
            holder.receivedText.setVisibility(View.GONE);
            holder.receivedImage.setVisibility(View.GONE);
            holder.receivedVideo.setVisibility(View.GONE);

            switch (type) {
                case "text":
                    holder.receivedText.setVisibility(View.VISIBLE);
                    holder.receivedText.setText(msg.getText());
                    break;

                case "image":
                    Log.d("TAG", "onBindViewHolder: " + msg.getFileUrl());
                    holder.receivedImage.setVisibility(View.VISIBLE);
                    Glide.with(holder.itemView.getContext())
                            .load(msg.getFileUrl())
                            .into(holder.receivedImage);
                    break;

                case "video":
                    Log.d("VideoView", "Video URL: " + msg.getFileUrl());

                    holder.receivedVideo.setVisibility(View.VISIBLE);
                    holder.receivedVideo.setVideoURI(Uri.parse(msg.getFileUrl()));

                    MediaController mediaController = new MediaController(holder.receivedVideo.getContext());
                    mediaController.setAnchorView(holder.receivedVideo);
                    holder.receivedVideo.setMediaController(mediaController);
                    holder.receivedVideo.requestFocus();
                    holder.receivedVideo.start();
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isGroupChat) {
            return GroupMessage.size();
        }
        else return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView sentCard, receivedCard;
        TextView sentText, receivedText;
        ImageView sentImage, receivedImage;
        VideoView sentVideo, receivedVideo;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentCard = itemView.findViewById(R.id.card_view_sent_message);
            receivedCard = itemView.findViewById(R.id.card_view_received_message);
            sentText = itemView.findViewById(R.id.text_view_sent_message);
            receivedText = itemView.findViewById(R.id.text_view_received_message);
            sentImage = itemView.findViewById(R.id.image_view_sent);
            receivedImage = itemView.findViewById(R.id.image_view_received);
            sentVideo = itemView.findViewById(R.id.video_view_sent);
            receivedVideo = itemView.findViewById(R.id.video_view_received);
        }
    }
}
