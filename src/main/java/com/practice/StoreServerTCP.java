package com.practice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StoreServerTCP {
    private final int port;
    private final Processor processor = new Processor();

    public StoreServerTCP(int port) {
        this.port = port;
    }

    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("TCP Server started on port " + port);
                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (Exception e) {
                System.err.println("TCP Server error: " + e.getMessage());
            }
        }, "TCP-Server-Thread").start();
    }

    private void handleClient(Socket clientSocket) {
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
            System.out.println("Client disconnected from TCP");
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        new StoreServerTCP(5001).start();
    }
}