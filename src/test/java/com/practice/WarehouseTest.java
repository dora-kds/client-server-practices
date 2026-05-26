package com.practice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    @Test
    public void addAmountTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.addAmount("Гречка", 10);
        assertEquals(10, warehouse.getAmount("Гречка"));
    }

    @Test
    public void removeAmountTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.addAmount("Гречка", 20);
        warehouse.removeAmount("Гречка", 5);
        assertEquals(15, warehouse.getAmount("Гречка"));
    }

    @Test
    public void setPriceTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setPrice("Гречка", 50.0);
        assertEquals(50.0, warehouse.getPrice("Гречка"));
    }

    @Test
    public void addGroupTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.addGroup("Крупи");
        assertNotNull(warehouse.getGroupProducts("Крупи"));
    }

    @Test
    public void addProductToGroupTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.addProductToGroup("Крупи", "Гречка");
        assertTrue(warehouse.getGroupProducts("Крупи").contains("Гречка"));
    }
}