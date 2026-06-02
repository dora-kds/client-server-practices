package com.practice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreUDPTest {
    @Test
    public void udpAckLogicTest() throws Exception {

        Message request = new Message(3, 1, "Гречка;10");
        Packet packet = new Packet((byte) 1, 42L, request);

        byte[] encoded = Encoder.encode(packet);
        Packet received = Decoder.decode(encoded);

        Processor processor = new Processor();
        Message response = processor.process(received.bMsq);
        Packet ack = new Packet((byte) 0, received.bPktId, response);

        assertEquals(packet.bPktId, ack.bPktId);
        assertNotNull(ack.bMsq);
        assertEquals(response.message, ack.bMsq.message);
    }
}