package com.practice;

public class Message {

    public int cType;
    public int bUserId;
    public String message;

    public Message() {
    }

    public Message(int cType, int bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }
}