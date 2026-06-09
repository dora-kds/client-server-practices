package com.practice;

public class Product {
    private Integer id;
    private String name;
    private String category;
    private int amount;
    private double price;

    public Product(String name,
                   String category,
                   int amount,
                   double price) {
        this(null, name, category, amount, price);
    }

    public Product(Integer id,
                   String name,
                   String category,
                   int amount,
                   double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }
}
