package com.practice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProductDBTest {
    private ProductDB db;

    @BeforeEach
    void setUp() {
        db = new SQLiteProdDB(":memory:");
    }

    @Test
    void testCreateRead() {
        Product p = new Product("Молоко", "Продукти", 10, 25.5);
        int id = db.insert(p);
        assertTrue(id > 0);

        Product fromDb = db.getById(id).orElseThrow();
        assertEquals("Молоко", fromDb.getName());
        assertEquals("Продукти", fromDb.getCategory());
        assertEquals(10, fromDb.getAmount());
        assertEquals(25.5, fromDb.getPrice());
    }

    @Test
    void testUpdate() {
        int id = db.insert(new Product("Хліб", "Продукти", 5, 10));
        Product updated = new Product(id, "Хліб білий", "Продукти", 7, 12);
        assertTrue(db.update(updated));

        Product fromDb = db.getById(id).orElseThrow();
        assertEquals("Хліб білий", fromDb.getName());
        assertEquals(7, fromDb.getAmount());
        assertEquals(12, fromDb.getPrice());
    }

    @Test
    void testDelete() {
        int id = db.insert(new Product("Вода", "Напої", 20, 15));
        assertTrue(db.deleteById(id));
        assertTrue(db.getById(id).isEmpty());
    }

    @Test
    void testFilters() {
        db.insert(new Product("Ноутбук Asus", "Електроніка", 5, 20000));
        db.insert(new Product("Ноутбук HP", "Електроніка", 3, 18000));
        db.insert(new Product("Сік", "Напої", 50, 30));
        List<Product> result = db.findWithFilters(
                "Ноутбук",
                "Електроніка",
                null, null,
                null, null,
                1, 10
        );
        assertEquals(2, result.size());
    }

    @Test
    void testPagination() {
        ProductDB db = new SQLiteProdDB(":memory:");
        for (int i = 1; i <= 5; i++) {
            db.insert(new Product("P" + i, "Cat", i, 10));
        }
        var page1 = db.findWithFilters(null, null, null, null, null, null, 1, 2);
        var page2 = db.findWithFilters(null, null, null, null, null, null, 2, 2);

        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
    }
}