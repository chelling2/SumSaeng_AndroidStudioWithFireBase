package com.example.myapplication;

public class UserAccount {
    private String idToken; // Firebase Uid 고유 토큰
    private String emailId; // 이메일아이디
    private String password; // 비밀번호

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    private String money;
    private String nickname;

    private String verified;

    private String education;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    private String rating;
    private int point;
    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getEducation() { return education; }

    public void setEducation(String education) { this.education = education; }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserAccount(){ }

    public String getIdToken() {return idToken;}

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {return emailId;}

    public void setEmailId(String emailId) {this.emailId = emailId;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}
}