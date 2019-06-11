package com.example.barnaclebot.chantel.model;

import com.example.barnaclebot.chantel.model.Data;
import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    // variable name should be same as in the json response from php
    @SerializedName("success")
    boolean success;
    @SerializedName("message")
    String message;
    @SerializedName("status")
    int status;
    @SerializedName("data")
    private Data data;


    public String getMessage() {
        return message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setData(Data data){
        this.data = data;
    }

    public Data getData(){
        return data;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }

    @Override
    public String toString(){
        return
                "Response{" +
                        "data = '" + data + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }


}
