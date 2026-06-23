package com.practice;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StoreServerUDP {
    private final int port;
    private final Processor processor = new Processor();

    public StoreServerUDP(int port) {
        this.port = port;
    }

    public void start() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                System.out.println("UDP Server started on port " + port);
                byte[] buffer = new byte[1024];
                while (!Thread.currentThread().isInterrupted()) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    byte[] data = new byte[request.getLength()];
                    System.arraycopy(request.getData(), 0, data, 0, request.getLength());
                    Packet packet = Decoder.decode(data);
                    System.out.println("Received UDP packet id: " + packet.bPktId);
                    Message responseMessage = processor.process(packet.bMsq);
                    Packet responsePacket = new Packet((byte) 0, packet.bPktId, responseMessage);
                    byte[] responseBytes = Encoder.encode(responsePacket);
                    InetAddress clientAddress = request.getAddress();
                    int clientPort = request.getPort();
                    DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                    socket.send(response);
                }
            } catch (Exception e) {
                System.err.println("UDP Server error: " + e.getMessage());
            }
        }, "UDP-Server-Thread").start();
    }

    public static void main(String[] args) {
        new StoreServerUDP(6000).start();
    }
}