package com.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warehouse {
    private final Map<String, Integer>
            productAmounts = new HashMap<>();

    private final Map<String, Double>
            productPrices = new HashMap<>();

    private final Map<String, List<String>>
            groups = new HashMap<>();

    public synchronized void addGroup(String groupName) {
        groups.putIfAbsent( groupName, new ArrayList<>());
    }

    public synchronized void addProductToGroup(String groupName, String productName) {
        groups.putIfAbsent(groupName, new ArrayList<>());
        groups.get(groupName).add(productName);
    }

    public synchronized void addAmount(String productName, int amount) {
        productAmounts.put(productName, productAmounts.getOrDefault(productName, 0) + amount);
    }

    public synchronized void removeAmount(String productName, int amount) {
        productAmounts.put(productName, productAmounts.getOrDefault(productName, 0) - amount);
    }

    public synchronized int getAmount(String productName) {
        return productAmounts.getOrDefault(productName, 0);
    }

    public synchronized void setPrice(String productName, double price) {
        productPrices.put(productName, price);
    }

    public synchronized double getPrice(String productName) {
        return productPrices.getOrDefault(productName, 0.0);
    }

    public synchronized List<String> getGroupProducts(String groupName) {
        return groups.getOrDefault(groupName, new ArrayList<>());
    }
}
