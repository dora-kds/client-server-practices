package com.practice;

import org.junit.jupiter.api.*;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpServerManagerTest {
    private static HttpServerManager httpServer;
    private static ProductDB productDB;
    private static HttpClient client;
    private static String jwtToken;
    private static int createdProductId;

    @BeforeAll
    public static void setup() throws Exception {
        File dbFile = new File("test_store.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        productDB = new SQLiteProdDB("test_store.db");
        httpServer = new HttpServerManager(8185, productDB);
        httpServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    public static void teardown() {
        httpServer.stop();
        File dbFile = new File("test_store.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("POST /login")
    public void testLoginSuccess() throws Exception {
        String loginJson = """
            {
              "login": "admin",
              "password": "admin123"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/login"))
                .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        jwtToken = HttpServerManager.extractJsonField(response.body(), "token");
        assertNotNull(jwtToken);
        assertFalse(jwtToken.isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("GET /products/1 - Unauthorized")
    public void testGetProductUnauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products/1"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode());
    }

    @Test
    @Order(3)
    @DisplayName("PUT /products - Create Product")
    public void testCreateProduct() throws Exception {
        String productJson = """
            {
              "name": "Laptop",
              "category": "Electronics",
              "amount": 10,
              "price": 999.99
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products"))
                .header("Authorization", "Bearer " + jwtToken)
                .PUT(HttpRequest.BodyPublishers.ofString(productJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        String idStr = HttpServerManager.extractJsonField(response.body(), "id");
        createdProductId = Integer.parseInt(idStr);
        assertTrue(createdProductId > 0);
    }

    @Test
    @Order(4)
    @DisplayName("GET /products/{id}")
    public void testGetProduct() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products/" + createdProductId))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Laptop"));
    }

    @Test
    @Order(5)
    @DisplayName("GET /products/{id} - Not Found")
    public void testGetProductNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products/9999"))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    @Order(6)
    @DisplayName("POST /products/{id} - Update Product")
    public void testUpdateProduct() throws Exception {
        String updateJson = """
            {
              "name": "Laptop Pro",
              "category": "Electronics",
              "amount": 5,
              "price": 1299.99
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products/" + createdProductId))
                .header("Authorization", "Bearer " + jwtToken)
                .POST(HttpRequest.BodyPublishers.ofString(updateJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("UPDATED"));
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /products/{id}")
    public void testDeleteProduct() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products/" + createdProductId))
                .header("Authorization", "Bearer " + jwtToken)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("DELETED"));
        HttpRequest checkRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8185/products/" + createdProductId))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();
        HttpResponse<String> checkResponse = client.send(checkRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, checkResponse.statusCode());
    }
}