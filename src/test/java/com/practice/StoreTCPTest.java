package com.practice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTCPTest {
    @Test
    public void tcpEncodeDecodeProcessFlowTest() throws Exception {

        Message request = new Message(3, 1, "Гречка;10");
        Packet packet = new Packet((byte) 1, 1L, request);

        byte[] encoded = Encoder.encode(packet);
        Packet decoded = Decoder.decode(encoded);

        Processor processor = new Processor();
        Message response = processor.process(decoded.bMsq);

        assertNotNull(response);
        assertTrue(response.message.contains("OK") || response.message.contains("15"));
    }
}
