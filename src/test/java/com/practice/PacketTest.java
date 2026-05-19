package com.practice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PacketTest {
    @Test
    public void encodeDecodeTest() throws Exception {
        Message message = new Message(1, 100, "Hello world");
        Packet packet = new Packet((byte) 1, 999L, message);

        byte[] encoded = Encoder.encode(packet);
        Packet decoded = Decoder.decode(encoded);

        assertEquals(packet.bSrc, decoded.bSrc);
        assertEquals(packet.bPktId, decoded.bPktId);
        assertEquals(packet.bMsq.cType, decoded.bMsq.cType);
        assertEquals(packet.bMsq.bUserId, decoded.bMsq.bUserId);
        assertEquals(packet.bMsq.message, decoded.bMsq.message);
    }
}