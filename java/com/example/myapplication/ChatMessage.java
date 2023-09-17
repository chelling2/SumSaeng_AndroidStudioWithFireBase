package com.example.myapplication;

import android.util.Base64;

// ChatMessage.java
public class ChatMessage {
    private String senderId;
    private String recipientId;
    private String message;
    private long timestamp;

    private String type;
    private String content;

    public ChatMessage() {
        // Firebase의 데이터 스냅샷에서 객체를 생성하기 위해 빈 생성자 필요
    }

    public ChatMessage(String senderId, String recipientId, String message) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }


    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getContentAsBytes() {
        if ("image".equals(type)) {
            try {
                return Base64.decode(content, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
