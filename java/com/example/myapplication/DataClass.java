package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class DataClass {


    private String dataTitle;
    private String dataDesc;
//    private String dataImage;
    private List<String> dataImage;
    private String key;

    private String userId;
    private String education;
    private int price;//new
    private  boolean transaction;

    public void setUploaderId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String dataTitle, String dataDesc, List<String> dataImage, String userId, String education, int price,boolean transaction) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataImage = dataImage;
        this.userId = userId;
        this.education = education;
        this.price = price;
        this.transaction = transaction;

    }


    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }


//    public String getDataImage() {
//        return dataImage;
//    }
//
//    public void setDataImage(String dataImage) {
//        this.dataImage = dataImage;
//    }

    public DataClass(){

    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<String> getDataImage() {
        if (dataImage == null) {
            return new ArrayList<>();
        }
        return dataImage;
    }


    public void setDataImage(List<String> dataImage) {
        this.dataImage = dataImage;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }
}
