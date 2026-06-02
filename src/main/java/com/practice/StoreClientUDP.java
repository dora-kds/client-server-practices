package com.practice;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class StoreClientUDP {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6000;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(2000);

            Message message = new Message(4, 1, "Groceries");
            Packet packet = new Packet((byte) 1, 1, message);

            byte[] packetBytes = Encoder.encode(packet);
            InetAddress address = InetAddress.getByName(HOST);
            DatagramPacket request = new DatagramPacket(packetBytes, packetBytes.length, address, PORT);
            byte[] buffer = new byte[1024];
            boolean acknowledged = false;

            while (!acknowledged) {
                try {
                    socket.send(request);
                    System.out.println("UDP packet sent. Id = " + packet.bPktId);
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

                    socket.receive(response);

                    byte[] responseData = new byte[response.getLength()];
                    System.arraycopy(response.getData(), 0, responseData, 0, response.getLength());
                    Packet responsePacket = Decoder.decode(responseData);

                    if (responsePacket.bPktId == packet.bPktId) {
                        acknowledged = true;
                        System.out.println("ACK received for packet " + packet.bPktId);

                        System.out.println("Server response: " + responsePacket.bMsq.message);
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout. Resending packet " + packet.bPktId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}