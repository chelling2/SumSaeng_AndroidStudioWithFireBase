package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private String currentUserUid;
    private String recipientUid;
    private List<ChatMessage> messages;

    public ChatAdapter(Context context, String currentUserUid, String recipientUid) {
        this.context = context;
        this.currentUserUid = currentUserUid;
        this.recipientUid = recipientUid;
        this.messages = new ArrayList<>();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_bubble, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.messageTextView = convertView.findViewById(R.id.messageTextView);
            viewHolder.chatImage = convertView.findViewById(R.id.chat_uploadImage);
            viewHolder.dateView = convertView.findViewById(R.id.chat_date);
            viewHolder.timeView_sender = convertView.findViewById(R.id.chat_time_sender);
            viewHolder.timeView_receiver = convertView.findViewById(R.id.chat_time_receiver);
            viewHolder.image_timeView_sender = convertView.findViewById(R.id.image_chat_time_sender);
            viewHolder.image_timeView_receiver = convertView.findViewById(R.id.image_chat_time_receiver);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatMessage message = getItem(position);

        int backgroundColor;
        int alignmentRuleMessage = 0;

        if (message != null && currentUserUid != null && message.getSenderId() != null) {
            if (message.getSenderId().equals(currentUserUid)) {
                backgroundColor = ContextCompat.getColor(context, R.color.yellow2);
                alignmentRuleMessage = RelativeLayout.ALIGN_PARENT_END;
                viewHolder.chatImage.setVisibility(View.GONE);

                if ("image".equals(message.getType())) {
                    viewHolder.messageTextView.setVisibility(View.GONE);
                    viewHolder.chatImage.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                            700 ,700
                    );
                    imageParams.addRule(alignmentRuleMessage);
                    viewHolder.chatImage.setLayoutParams(imageParams);

                    // 이미지 로드 시에 Glide를 사용하여 이미지 크기를 제한하지 않음
                    Glide.with(context)
                            .load(Uri.parse(message.getContent()))
                            .into(viewHolder.chatImage);

                    // 이미지뷰 크기 조절
                    ViewGroup.LayoutParams layoutParams = viewHolder.chatImage.getLayoutParams();
                    layoutParams.width = 450; // 원하는 너비 설정
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT; // 높이는 자동으로 조절
                    viewHolder.chatImage.setLayoutParams(layoutParams);


                    // 텍스트 메시지의 시간 설정
                    viewHolder.timeView_sender.setVisibility(View.GONE);
                    viewHolder.timeView_receiver.setVisibility(View.GONE);
                    viewHolder.image_timeView_sender.setVisibility(View.VISIBLE);
                    viewHolder.image_timeView_receiver.setVisibility(View.VISIBLE);

                    if (position < messages.size() - 1) {
                        ChatMessage nextMessage = getItem(position + 1);
                        if (!isSameMinute(message.getTimestamp(), nextMessage.getTimestamp())) {
                            viewHolder.image_timeView_sender.setText(formatTime(message.getTimestamp()));
                            viewHolder.image_timeView_receiver.setText(formatTime(nextMessage.getTimestamp()));
                        } else {
                            viewHolder.image_timeView_sender.setVisibility(View.GONE);
                            viewHolder.image_timeView_receiver.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.image_timeView_sender.setText(formatTime(message.getTimestamp()));
                        viewHolder.image_timeView_receiver.setVisibility(View.GONE);
                    }

                } else {
                    viewHolder.messageTextView.setVisibility(View.VISIBLE);
                    viewHolder.chatImage.setVisibility(View.GONE);
                    viewHolder.messageTextView.setText(message.getMessage());
                    viewHolder.messageTextView.setBackgroundColor(backgroundColor);

                    // 텍스트 메시지의 시간 설정
                    viewHolder.image_timeView_sender.setVisibility(View.GONE);
                    viewHolder.image_timeView_receiver.setVisibility(View.GONE);
                    viewHolder.timeView_sender.setVisibility(View.VISIBLE);
                    viewHolder.timeView_receiver.setVisibility(View.VISIBLE);

                    if (position < messages.size() - 1) {
                        ChatMessage nextMessage = getItem(position + 1);
                        if (!isSameMinute(message.getTimestamp(), nextMessage.getTimestamp())) {
                            viewHolder.timeView_sender.setText(formatTime(message.getTimestamp()));
                            viewHolder.timeView_receiver.setText(formatTime(nextMessage.getTimestamp()));
                        } else {
                            viewHolder.timeView_sender.setVisibility(View.GONE);
                            viewHolder.timeView_receiver.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.timeView_sender.setText(formatTime(message.getTimestamp()));
                        viewHolder.timeView_receiver.setVisibility(View.GONE);
                    }
                }
            } else {
                backgroundColor = ContextCompat.getColor(context, R.color.white);
                alignmentRuleMessage = RelativeLayout.ALIGN_PARENT_START;
                viewHolder.chatImage.setVisibility(View.GONE);

                if ("image".equals(message.getType())) {
                    viewHolder.messageTextView.setVisibility(View.GONE);
                    viewHolder.chatImage.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                            700 ,700
                    );
                    imageParams.addRule(alignmentRuleMessage);
                    viewHolder.chatImage.setLayoutParams(imageParams);

                    Glide.with(context)
                            .load(Uri.parse(message.getContent()))
                            .override(450, ViewGroup.LayoutParams.WRAP_CONTENT)
                            .into(viewHolder.chatImage);

                    // 이미지 메시지의 시간 설정
                    viewHolder.timeView_sender.setVisibility(View.GONE);
                    viewHolder.timeView_receiver.setVisibility(View.GONE);
                    viewHolder.image_timeView_sender.setVisibility(View.VISIBLE);
                    viewHolder.image_timeView_receiver.setVisibility(View.VISIBLE);

                    if (position < messages.size() - 1) {
                        ChatMessage nextMessage = getItem(position + 1);
                        if (!isSameMinute(message.getTimestamp(), nextMessage.getTimestamp())) {
                            viewHolder.image_timeView_sender.setText(formatTime(message.getTimestamp()));
                            viewHolder.image_timeView_receiver.setText(formatTime(nextMessage.getTimestamp()));
                        } else {
                            viewHolder.image_timeView_sender.setVisibility(View.GONE);
                            viewHolder.image_timeView_receiver.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.image_timeView_receiver.setText(formatTime(message.getTimestamp()));
                        viewHolder.image_timeView_sender.setVisibility(View.GONE);
                    }

                } else {
                    viewHolder.messageTextView.setVisibility(View.VISIBLE);
                    viewHolder.chatImage.setVisibility(View.GONE);
                    viewHolder.messageTextView.setText(message.getMessage());
                    viewHolder.messageTextView.setBackgroundColor(backgroundColor);

                    // 텍스트 메시지의 시간 설정
                    viewHolder.image_timeView_sender.setVisibility(View.GONE);
                    viewHolder.image_timeView_receiver.setVisibility(View.GONE);
                    viewHolder.timeView_sender.setVisibility(View.VISIBLE);
                    viewHolder.timeView_receiver.setVisibility(View.VISIBLE);

                    if (position < messages.size() - 1) {
                        ChatMessage nextMessage = getItem(position + 1);
                        if (!isSameMinute(message.getTimestamp(), nextMessage.getTimestamp())) {
                            viewHolder.timeView_sender.setText(formatTime(message.getTimestamp()));
                            viewHolder.timeView_receiver.setText(formatTime(nextMessage.getTimestamp()));
                        } else {
                            viewHolder.timeView_sender.setVisibility(View.GONE);
                            viewHolder.timeView_receiver.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.timeView_sender.setText(formatTime(message.getTimestamp()));
                        viewHolder.timeView_receiver.setText(formatTime(message.getTimestamp()));
                    }

                }
            }
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.addRule(alignmentRuleMessage);
        viewHolder.messageTextView.setLayoutParams(params);


        // 날짜 표시 여부를 날짜가 달라질 때로 수정
        if (position == 0 || !isSameDay(message.getTimestamp(), getItem(position - 1).getTimestamp())) {
            viewHolder.dateView.setVisibility(View.VISIBLE);
            viewHolder.dateView.setText(formatDate(message.getTimestamp()));

            RelativeLayout.LayoutParams dateLayoutParams = (RelativeLayout.LayoutParams) viewHolder.dateView.getLayoutParams();
            dateLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            dateLayoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.messageTextView);
            dateLayoutParams.setMargins(0, -100, 0, 0); // 여기에서 간격 조정

            // messageTextView 아래에 마진 추가
            RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) viewHolder.messageTextView.getLayoutParams();
            messageLayoutParams.setMargins(0, 150, 0, 0); // 여기에서 간격 조정
            viewHolder.messageTextView.setLayoutParams(messageLayoutParams);
            viewHolder.chatImage.setLayoutParams(messageLayoutParams);
            viewHolder.dateView.setLayoutParams(dateLayoutParams);
        } else {
            viewHolder.dateView.setVisibility(View.GONE);
        }

        // 이미지 눌렀을 때 크게 보기에 하는 메서드
        viewHolder.chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미지를 크게 보여줄 다이얼로그를 띄웁니다.
                showImageDialog(message.getContent());
            }
        });

        return convertView;
    }

    // 이미지를 크게 보여주는 다이얼로그를 만드는 메서드
    private void showImageDialog(String imageUrl) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.image_dialog, null);
        dialogBuilder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.dialogImageView);
        Button backButton = dialogView.findViewById(R.id.backButton); // 뒤로 가기 버튼 추가

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        AlertDialog alertDialog = dialogBuilder.create();

        // 뒤로 가기 버튼 클릭 처리
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // 다이얼로그 닫기
            }
        });

        alertDialog.show();
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

    private static class ViewHolder {
        TextView messageTextView;
        ImageView chatImage;
        TextView dateView;
        TextView timeView_sender;
        TextView timeView_receiver;
        TextView image_timeView_sender;
        TextView image_timeView_receiver;
    }
}