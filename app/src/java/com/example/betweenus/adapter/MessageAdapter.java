package com.example.betweenus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.fragments.ChatListFragment;
import com.example.betweenus.model.Message;
import com.example.betweenus.model.User;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private Context context;

    private List<Message> messages;
    private Map<Integer, User> userMap;
    private int currentUserId;
    private DatabaseHelper dbHelper;

    public MessageAdapter(List<Message> messages, Map<Integer, User> userMap, int currentUserId) {
        this.messages = messages;
        this.userMap = userMap;
        this.dbHelper = new DatabaseHelper(context);
        this.currentUserId = currentUserId;
    }

    public void updateMessages(List<Message> newMessages, Map<Integer, User> newUserMap) {
        this.messages = newMessages;
        this.userMap = newUserMap;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderID() == dbHelper.currentUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        boolean isSender = getItemViewType(position) == VIEW_TYPE_SENT;
        
        User sender = userMap.get(message.getSenderID());
        int avatarId = (sender != null) ? sender.getAvatar() : 0;

        int color = ChatListFragment.getMessageColor(avatarId, isSender);

        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.messageBubble.setCardBackgroundColor(color);
            sentHolder.bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.messageBubble.setCardBackgroundColor(color);
            receivedHolder.avatarImage.setImageResource(ChatListFragment.getAvatarImage(avatarId));
            receivedHolder.bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        CardView messageBubble;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessageContent);
            messageBubble = itemView.findViewById(R.id.cardMessage);
        }

        void bind(Message message) {
            messageText.setText(message.getMessageText());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        CardView messageBubble;
        ImageView avatarImage;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessageContent);
            messageBubble = itemView.findViewById(R.id.cardMessage);
            avatarImage = itemView.findViewById(R.id.imageAvatar);
        }

        void bind(Message message) {
            messageText.setText(message.getMessageText());
        }
    }
}