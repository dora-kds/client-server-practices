package com.practice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServerManager {
    private final int port;
    private final ProductDB productDB;
    private HttpServer server;

    public HttpServerManager(int port, ProductDB productDB) {
        this.port = port;
        this.productDB = productDB;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/login", new LoginHandler());
        var productsContext = server.createContext("/products", new ProductsHandler());
        productsContext.setAuthenticator(new AuthHandler());
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Server started on port " + port);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, """
                    {"error": "Method Not Allowed"}
                    """);
                return;
            }
            String body = readBody(exchange);
            String login = extractJsonField(body, "login");
            String password = extractJsonField(body, "password");
            if (productDB.validateUser(login, password)) {
                String token = JwtProvider.generateToken(login);
                String jsonResponse = """
                    {"token": "%s"}
                    """.formatted(token);
                sendResponse(exchange, 200, jsonResponse);
            } else {
                sendResponse(exchange, 401, """
                    {"error": "Invalid login or password"}
                    """);
            }
        }
    }

    private class ProductsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Integer id = extractIdFromPath(path);
            try {
                switch (method.toUpperCase()) {
                    case "GET":
                        if (id == null) {
                            sendResponse(exchange, 400, """
                                {"error": "Missing or invalid product ID"}
                                """);
                            return;
                        }
                        Optional<Product> productOpt = productDB.getById(id);
                        if (productOpt.isPresent()) {
                            Product p = productOpt.get();
                            String json = """
                                {
                                  "id": %d,
                                  "name": "%s",
                                  "category": "%s",
                                  "amount": %d,
                                  "price": %.2f
                                }
                                """.formatted(p.getId(), p.getName(), p.getCategory(), p.getAmount(), p.getPrice());
                            sendResponse(exchange, 200, json);
                        } else {
                            sendResponse(exchange, 404, """
                                {"error": "Product not found"}
                                """);
                        }
                        break;

                    case "PUT":
                        String putBody = readBody(exchange);
                        String name = extractJsonField(putBody, "name");
                        String category = extractJsonField(putBody, "category");
                        int amount = Integer.parseInt(extractJsonField(putBody, "amount"));
                        double price = Double.parseDouble(extractJsonField(putBody, "price"));
                        Product newProduct = new Product(0, name, category, amount, price);
                        int newId = productDB.insert(newProduct);
                        String createdResponse = """
                            {
                              "status": "CREATED",
                              "id": %d
                            }
                            """.formatted(newId);
                        sendResponse(exchange, 201, createdResponse);
                        break;

                    case "POST":
                        if (id == null) {
                            sendResponse(exchange, 400, """
                                {"error": "Missing or invalid product ID"}
                                """);
                            return;
                        }
                        String postBody = readBody(exchange);
                        String uName = extractJsonField(postBody, "name");
                        String uCategory = extractJsonField(postBody, "category");
                        int uAmount = Integer.parseInt(extractJsonField(postBody, "amount"));
                        double uPrice = Double.parseDouble(extractJsonField(postBody, "price"));
                        Product updatedProduct = new Product(id, uName, uCategory, uAmount, uPrice);
                        productDB.update(updatedProduct);
                        sendResponse(exchange, 200, """
                            {"status": "UPDATED"}
                            """);
                        break;

                    case "DELETE":
                        if (id == null) {
                            sendResponse(exchange, 400, """
                                {"error": "Missing or invalid product ID"}
                                """);
                            return;
                        }
                        productDB.deleteById(id);
                        sendResponse(exchange, 200, """
                            {"status": "DELETED"}
                            """);
                        break;

                    default:
                        sendResponse(exchange, 405, """
                            {"error": "Method Not Allowed"}
                            """);
                }
            } catch (Exception e) {
                String errorJson = """
                    {
                      "error": "Internal Server Error",
                      "message": "%s"
                    }
                    """.formatted(e.getMessage());
                sendResponse(exchange, 500, errorJson);
            }
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static String extractJsonField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\\s*\"?([^,\"}]+)\"?");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private static Integer extractIdFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            try {
                return Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}