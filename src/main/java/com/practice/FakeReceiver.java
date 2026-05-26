package com.practice;

import java.util.Random;

public class FakeReceiver
        implements Receiver {

    private final Random
            random = new Random();

    @Override
    public byte[] receiveMessage() {
        int command = random.nextInt(6) + 1;

        Message message = new Message(command, 1, "Гречка;10");
        Packet packet = new Packet((byte) 1, System.currentTimeMillis(), message);

        try {
            return Encoder.encode(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
