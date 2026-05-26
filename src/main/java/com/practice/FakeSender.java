package com.practice;

import java.util.Arrays;

public class FakeSender extends Sender{
    @Override
    public void sendMessage(byte[] message){
        System.out.println(Arrays.toString(message));
    }
}
