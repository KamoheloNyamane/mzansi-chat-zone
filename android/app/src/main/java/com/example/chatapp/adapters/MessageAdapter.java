package com.example.chatapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.database.Message;
import com.example.chatapp.utils.Constants;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final Context context;
    private final List<Message> messages;
    private String currentUser = Constants.CURRENT_USER;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getContent());

        if (holder.senderTextView != null) {
            String sender = message.getSender();
            if (sender == null || sender.isEmpty()) {
                sender = "Unknown";
            }
            holder.senderTextView.setText(sender);
        }

        // Distinct styling for incoming vs outgoing messages
        boolean isOutgoing = message.getSender() != null && message.getSender().equalsIgnoreCase(currentUser);
        if (holder.itemView instanceof LinearLayout) {
            LinearLayout container = (LinearLayout) holder.itemView;
            if (isOutgoing) {
                container.setGravity(Gravity.END);
                holder.messageTextView.setBackgroundColor(Color.parseColor("#DCF8C6"));
            } else {
                container.setGravity(Gravity.START);
                holder.messageTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public void addMessage(Message message) {
        if (messages != null && message != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public final TextView messageTextView;
        public final TextView senderTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            senderTextView = itemView.findViewById(R.id.message_sender);
        }
    }
}
