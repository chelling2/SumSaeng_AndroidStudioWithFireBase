package com.example.myapplication;

public class InquireChatMessage {
    private String message;
    private String senderId;
    private String userId;
    private String nickname;
    private MessageType type;
    private String senderEmail;
    private long timestamp;

    public InquireChatMessage() {
        // 기본 생성자
    }

    // 생성자에 timestamp 파라미터 추가
    public InquireChatMessage(String message, String senderId, String userId, String nickname, MessageType type, String senderEmail, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.userId = userId;
        this.nickname = nickname;
        this.type = type;
        this.senderEmail = senderEmail;
        this.timestamp = timestamp;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public enum MessageType {
        USER, ADMIN , TEXT
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    public String getUserId() { // userId getter 추가
        return userId;
    }

    public void setUserId(String userId) { // userId setter 추가
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public MessageType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

}


