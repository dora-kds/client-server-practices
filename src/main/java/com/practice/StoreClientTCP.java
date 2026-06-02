package com.practice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class StoreClientTCP {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5001;

    public static void main(String[] args) {
        long packetId = 1;

        while (true) {
            try (Socket socket = new Socket(HOST, PORT)) {
                System.out.println("Connected to server");

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                while (true) {
                    Message message = new Message(4, 1, "Groceries");
                    Packet packet = new Packet((byte) 1, packetId++, message);
                    byte[] packetBytes = Encoder.encode(packet);

                    out.write(packetBytes);
                    out.flush();

                    System.out.println("Packet sent. Id = " + packet.bPktId);

                    byte[] responseBuffer = new byte[1024];
                    int bytesRead = in.read(responseBuffer);

                    if (bytesRead == -1) {
                        throw new Exception("Connection lost");
                    }
                    byte[] responseData = new byte[bytesRead];
                    System.arraycopy(responseBuffer, 0, responseData, 0, bytesRead);
                    Packet responsePacket = Decoder.decode(responseData);
                    System.out.println("Server response: " + responsePacket.bMsq.message);

                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                System.out.println("Server unavailable");
                System.out.println("Trying reconnect...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}