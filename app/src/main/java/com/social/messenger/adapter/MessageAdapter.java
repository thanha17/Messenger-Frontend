package com.social.messenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.social.messenger.R;
import com.social.messenger.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.isSentByUser()) {
            holder.cardReceived.setVisibility(View.GONE);
            holder.cardSent.setVisibility(View.VISIBLE);
            holder.textSent.setText(message.getContent());
        } else {
            holder.cardSent.setVisibility(View.GONE);
            holder.cardReceived.setVisibility(View.VISIBLE);
            holder.textReceived.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardSent, cardReceived;
        TextView textSent, textReceived;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSent = itemView.findViewById(R.id.card_view_sent_message);
            cardReceived = itemView.findViewById(R.id.card_view_received_message);
            textSent = itemView.findViewById(R.id.text_view_sent_message);
            textReceived = itemView.findViewById(R.id.text_view_received_message);
        }
    }
}
