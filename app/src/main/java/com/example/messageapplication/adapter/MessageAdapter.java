package com.example.messageapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ItemChatBinding;
import com.example.messageapplication.listener.IClickItemMessageListener;
import com.example.messageapplication.model.UserMessage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final Context context;
    private final List<UserMessage> list;
    private IClickItemMessageListener listener;

    public MessageAdapter(Context context, List<UserMessage> list, IClickItemMessageListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding chatBinding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MessageViewHolder(chatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        UserMessage userMessage = list.get(position);
        if (userMessage == null) {
            return;
        }

        Uri profileImageUri = Uri.parse(userMessage.getImageResource());
        try {
            // Lấy InputStream của URI và chuyển thành Bitmap
            InputStream inputStream = context.getContentResolver().openInputStream(profileImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.chatBinding.avatarMessage.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("ProfileImageError", "File not found: " + e.getMessage());
        }

        holder.chatBinding.tvMessageTime.setText(userMessage.getMessageTime());
        holder.chatBinding.tvUsername.setText(userMessage.getUserName());
        holder.chatBinding.tvLastMessageContent.setText(userMessage.getLastMessage());

        holder.chatBinding.itemChat.setOnClickListener(v -> {
            listener.onClick(userMessage);
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatBinding chatBinding;

        public MessageViewHolder(ItemChatBinding chatBinding) {
            super(chatBinding.getRoot());
            this.chatBinding = chatBinding;
        }
    }
}
