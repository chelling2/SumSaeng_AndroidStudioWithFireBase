package com.example.myapplication;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InquireAdapter extends RecyclerView.Adapter<InquireAdapter.MyViewHolder> {
    private List<InquireChatMessage> messageList;
    private String currentUserUid;


    public InquireAdapter(List<InquireChatMessage> messageList, String currentUserUid) {
        this.messageList = messageList;
        this.currentUserUid = currentUserUid;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderTextView;
        TextView timeTextView_sender;
        TextView timeTextView_receiver;
        TextView dateTextView;

        ImageView imgSender;
        ImageView imgReceiver;

        ConstraintLayout constraintLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tv_chat);
            senderTextView = itemView.findViewById(R.id.tv_sender);
            timeTextView_sender = itemView.findViewById(R.id.tv_time_sender);
            timeTextView_receiver = itemView.findViewById(R.id.tv_time_receiver);
            dateTextView = itemView.findViewById(R.id.tv_date);
            imgSender = itemView.findViewById(R.id.img_sender);
            imgReceiver = itemView.findViewById(R.id.img_receiver);
            constraintLayout = itemView.findViewById(R.id.item_constraint_layout);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inquire_chat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        InquireChatMessage chatMessage = messageList.get(position);

        // 날짜 표시 로직 수정
        boolean showDate = true;
        if (position > 0) {
            InquireChatMessage prevMessage = messageList.get(position - 1);
            showDate = !isSameDay(chatMessage.getTimestamp(), prevMessage.getTimestamp());
        }


        holder.messageTextView.setText(chatMessage.getMessage());

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.messageTextView.getLayoutParams();

        boolean isCurrentUser = currentUserUid != null && currentUserUid.equals(chatMessage.getSenderId());

        // 시간 표시 로직 추가
        boolean showTime = true;
        if (position < messageList.size() - 1) {
            InquireChatMessage nextMessage = messageList.get(position + 1);
            if (isSameMinute(chatMessage.getTimestamp(), nextMessage.getTimestamp())) {
                showTime = false;
            }
        }

        holder.senderTextView.setVisibility(View.GONE);
        holder.imgSender.setVisibility(View.GONE);
        holder.imgReceiver.setVisibility(View.GONE);

        if (isCurrentUser) {
            if ("ADMIN".equals(chatMessage.getType())) {
                holder.messageTextView.setGravity(Gravity.END);
                Log.d("InquireAdapter", "U: Admin message");
            } else if ("USER".equals(chatMessage.getType())) {
                holder.messageTextView.setGravity(Gravity.START);
                Log.d("InquireAdapter", "User message");
            } else {
                holder.messageTextView.setBackgroundResource(R.drawable.inquire_sender_message_bg);
            }

            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            if ("USER".equals(chatMessage.getType())) {
                holder.messageTextView.setGravity(Gravity.START);
                Log.d("InquireAdapter", "Other user sent user message");
            } else if ("ADMIN".equals(chatMessage.getType())) {
                holder.messageTextView.setGravity(Gravity.END);
                Log.d("InquireAdapter", "A: Admin message");
            } else {
                holder.messageTextView.setBackgroundResource(R.drawable.inquire_receiver_message_bg);
            }

            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        }

        params.topMargin = 20;
        params.bottomMargin = 20;
        holder.messageTextView.setLayoutParams(params);

        ConstraintLayout.LayoutParams dateLayoutParams = (ConstraintLayout.LayoutParams) holder.dateTextView.getLayoutParams();
        dateLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 메시지 위에 날짜가 표시되도록 설정
        holder.dateTextView.setLayoutParams(dateLayoutParams);

        if (showDate) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(formatDate(chatMessage.getTimestamp()));

            // 날짜 텍스트와 메시지 간격 조정
            ConstraintLayout.LayoutParams messageLayoutParams = (ConstraintLayout.LayoutParams) holder.messageTextView.getLayoutParams();
            messageLayoutParams.topMargin = 170; // 조절 가능한 값으로 설정
            holder.messageTextView.setLayoutParams(messageLayoutParams);

            dateLayoutParams.topToBottom = holder.messageTextView.getId(); // 메시지 아래에 날짜가 표시되도록 설정
            dateLayoutParams.topMargin = 20; // 조절 가능한 값으로 설정
            holder.dateTextView.setLayoutParams(dateLayoutParams);

        } else {
            holder.dateTextView.setVisibility(View.GONE);
        }


        if (showTime) {
            holder.timeTextView_sender.setVisibility(View.VISIBLE);
            holder.timeTextView_receiver.setVisibility(View.VISIBLE);
            holder.timeTextView_sender.setText(formatTime(chatMessage.getTimestamp()));
            holder.timeTextView_receiver.setText(formatTime(chatMessage.getTimestamp()));

//            ConstraintLayout.LayoutParams timeLayoutParams = (ConstraintLayout.LayoutParams) holder.timeTextView_sender.getLayoutParams();
            if (isCurrentUser) { // 현재 유저가 메시지를 보낸 경우
                if ("USER".equals(chatMessage.getType())) {
                    // 유저가 보낸 메시지의 경우, 시간을 메시지 왼쪽에 위치
                    holder.timeTextView_sender.setVisibility(View.VISIBLE);
                    holder.timeTextView_receiver.setVisibility(View.GONE);
                } else {
                    // 관리자가 보낸 메시지의 경우, 시간을 메시지 오른쪽에 위치
                    holder.timeTextView_sender.setVisibility(View.GONE);
                    holder.timeTextView_receiver.setVisibility(View.VISIBLE);
                }
            } else { // 현재 유저가 메시지를 받은 경우
                if ("USER".equals(chatMessage.getType())) {
                    // 유저가 보낸 메시지의 경우, 시간을 메시지 오른쪽에 위치
                    holder.timeTextView_sender.setVisibility(View.GONE);
                    holder.timeTextView_receiver.setVisibility(View.VISIBLE);
                } else {
                    // 관리자가 보낸 메시지의 경우, 시간을 메시지 왼쪽에 위치
                    holder.timeTextView_sender.setVisibility(View.VISIBLE);
                    holder.timeTextView_receiver.setVisibility(View.GONE);
                }
            }
        } else {
            holder.timeTextView_receiver.setVisibility(View.GONE);
            holder.timeTextView_sender.setVisibility(View.GONE);
        }
    }

    // 같은 시간인지 확인하는 메서드 추가
    private boolean isSameMinute(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
                && cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)
                && cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }

    // 같은 날짜인지 확인하는 메서드 추가
    private boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    // 시간 포맷 메서드 추가
    private String formatTime(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    // 날짜 포맷 메서드 추가
    private String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy - MM - dd", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
