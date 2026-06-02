package com.practice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StoreServerTCP {
    private static final int PORT = 5001;
    private static final Processor processor = new Processor();
    public static void main(String[] args) {
        try ( ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> {
                    try {
                        InputStream in = clientSocket.getInputStream();
                        OutputStream out = clientSocket.getOutputStream();
                        byte[] buffer = new byte[1024];

                        while (true) {
                            int bytesRead = in.read(buffer);
                            if (bytesRead == -1) {
                                break;
                            }
                            byte[] data = new byte[bytesRead];
                            System.arraycopy(buffer, 0, data, 0, bytesRead);

                            Packet requestPacket = Decoder.decode(data);
                            System.out.println("Received packet id: " + requestPacket.bPktId);
                            Message responseMessage = processor.process(requestPacket.bMsq);
                            Packet responsePacket = new Packet((byte) 0, requestPacket.bPktId, responseMessage);
                            byte[] responseBytes = Encoder.encode(responsePacket);

                            out.write(responseBytes);
                            out.flush();
                        }
                    } catch (Exception e) {
                        System.out.println("Client disconnected");
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}