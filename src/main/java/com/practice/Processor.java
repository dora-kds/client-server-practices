package com.practice;

import java.util.List;
import java.util.Optional;

public class Processor {
    private final ProductDB productDB;

    public Processor() {
        this.productDB = new SQLiteProdDB("store.db");
    }

    public Processor(ProductDB productDB) {
        this.productDB = productDB;
    }

    public synchronized Message process(Message message) {
        String[] data = message.message.split(";");
        try {
            switch (message.cType) {
                case 1 -> {
                    List<Product> list = productDB.getAll();
                    StringBuilder sb = new StringBuilder("ALL_PRODUCTS:");
                    for (Product p : list) {
                        sb.append("\n").append(p.toString());
                    }
                    return new Message(100, 0, sb.toString());
                }

                case 2 -> {
                    int id = Integer.parseInt(data[0]);
                    Optional<Product> product = productDB.getById(id);
                    return product.map(p -> new Message(100, 0, "FOUND:" + p))
                            .orElseGet(() -> new Message(100, 0, "ERROR:NOT_FOUND"));
                }

                case 3 -> {
                    String name = data[0];
                    String category = data[1];
                    int amount = Integer.parseInt(data[2]);
                    double price = Double.parseDouble(data[3]);

                    Product product = new Product(name, category, amount, price);
                    int id = productDB.insert(product);
                    return new Message(100, 0, "CREATED_ID:" + id);
                }

                case 4 -> {
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String category = data[2];
                    int amount = Integer.parseInt(data[3]);
                    double price = Double.parseDouble(data[4]);

                    Product product = new Product(id, name, category, amount, price);
                    boolean updated = productDB.update(product);
                    return new Message(100, 0, updated ? "UPDATED" : "ERROR:NOT_FOUND");
                }

                case 5 -> {
                    int id = Integer.parseInt(data[0]);
                    boolean deleted = productDB.deleteById(id);
                    return new Message(100, 0, deleted ? "DELETED" : "ERROR:NOT_FOUND");
                }

                case 6 -> {
                    String name = "null".equals(data[0]) ? null : data[0];
                    String category = "null".equals(data[1]) ? null : data[1];
                    Integer minAmount = "null".equals(data[2]) ? null : Integer.parseInt(data[2]);
                    Integer maxAmount = "null".equals(data[3]) ? null : Integer.parseInt(data[3]);
                    Double minPrice = "null".equals(data[4]) ? null : Double.parseDouble(data[4]);
                    Double maxPrice = "null".equals(data[5]) ? null : Double.parseDouble(data[5]);

                    int page = Integer.parseInt(data[6]);
                    int size = Integer.parseInt(data[7]);

                    List<Product> result = productDB.findWithFilters(
                            name, category,
                            minAmount, maxAmount,
                            minPrice, maxPrice,
                            page, size
                    );

                    StringBuilder sb = new StringBuilder("SEARCH_RESULTS:");
                    for (Product p : result) {
                        sb.append("\n").append(p.toString());
                    }
                    return new Message(100, 0, sb.toString());
                }

                default -> {
                    return new Message(100, 0, "ERROR:UNKNOWN_COMMAND");
                }
            }

        } catch (Exception e) {
            return new Message(100, 0, "ERROR:" + e.getMessage());
        }
    }
}