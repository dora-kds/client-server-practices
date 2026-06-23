package com.practice;

import java.io.IOException;

public class Main {
    private static final int HTTP_PORT = 8185;
    private static final int TCP_PORT = 5001;
    private static final int UDP_PORT = 6000;
    private static final String DB_NAME = "store.db";

    public static void main(String[] args) {
        ProductDB productDB = new SQLiteProdDB(DB_NAME);
        try {
            HttpServerManager httpServer = new HttpServerManager(HTTP_PORT, productDB);
            httpServer.start();
        } catch (IOException e) {
            System.err.println("Failed to start HTTP Server: " + e.getMessage());
        }
        StoreServerTCP tcpServer = new StoreServerTCP(TCP_PORT);
        tcpServer.start();
        StoreServerUDP udpServer = new StoreServerUDP(UDP_PORT);
        udpServer.start();
        System.out.println("All servers are running concurrently.");
    }
}