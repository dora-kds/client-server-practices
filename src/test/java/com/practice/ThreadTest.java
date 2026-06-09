//package com.practice;
//
//import org.junit.jupiter.api.Test;
//import java.util.concurrent.ArrayBlockingQueue;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ThreadTest {
//    static class FakeReceiver {
//        private final byte[] data;
//
//        public FakeReceiver(byte[] data) {
//            this.data = data;
//        }
//
//        public byte[] receiveMessage() {
//            return data;
//        }
//    }
//
//    static class FakeSender {
//        public void sendMessage(byte[] msg) {
//            System.out.println("SENT MESSAGE SIZE: " + msg.length);
//        }
//    }
//
//    @Test
//    public void systemMultiThreadTest() throws Exception {
//        ArrayBlockingQueue<byte[]> rawQueue = new ArrayBlockingQueue<>(100);
//        ArrayBlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(100);
//        ArrayBlockingQueue<Message> processedQueue = new ArrayBlockingQueue<>(100);
//        ArrayBlockingQueue<byte[]> outputQueue = new ArrayBlockingQueue<>(100);
//
//        Processor processor = new Processor();
//        FakeSender sender = new FakeSender();
//
//        Message testMessage = new Message(3, 1, "Гречка;10");
//        Packet packet = new Packet((byte) 1, 1L, testMessage);
//        byte[] encodedPacket = Encoder.encode(packet);
//        FakeReceiver receiver = new FakeReceiver(encodedPacket);
//
//        rawQueue.put(receiver.receiveMessage());
//
//        Thread receiverThread = new Thread(() -> {
//            try {
//                byte[] data = rawQueue.take();
//                messageQueue.put(Decoder.decode(data).bMsq);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        Thread decoderThread = new Thread(() -> {
//            try {
//                Message msg = messageQueue.take();
//                processedQueue.put(msg);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        Thread processorThread = new Thread(() -> {
//            try {
//                Message msg = processedQueue.take();
//                Message result = processor.process(msg);
//                outputQueue.put(Encoder.encode(new Packet((byte) 1, 2L, result)));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        Thread senderThread = new Thread(() -> {
//            try {
//                byte[] result = outputQueue.take();
//                sender.sendMessage(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        receiverThread.start();
//        decoderThread.start();
//        processorThread.start();
//        senderThread.start();
//
//        receiverThread.join();
//        decoderThread.join();
//        processorThread.join();
//        senderThread.join();
//
//        assertTrue(true);
//    }
//}