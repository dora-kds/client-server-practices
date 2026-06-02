package com.practice;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StoreServerUDP {
    private static final int PORT = 6000;
    private static final Processor processor = new Processor();
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server started on port " + PORT);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                socket.receive(request);
                byte[] data = new byte[request.getLength()];
                System.arraycopy(request.getData(), 0, data, 0, request.getLength());

                Packet packet = Decoder.decode(data);
                System.out.println("Received packet id: " + packet.bPktId);

                Message responseMessage = processor.process(packet.bMsq);
                Packet responsePacket = new Packet((byte) 0, packet.bPktId, responseMessage);
                byte[] responseBytes = Encoder.encode(responsePacket);
                InetAddress clientAddress = request.getAddress();
                int clientPort = request.getPort();
                DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);

                socket.send(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}