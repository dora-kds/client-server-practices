package com.practice;

import java.util.List;
import java.util.Optional;

public interface ProductDB {
    int insert(Product product);
    Optional<Product> getById(int id);
    List<Product> getAll();
    boolean update(Product product);
    boolean deleteById(int id);
    boolean validateUser(String login, String password);
    List<Product> findWithFilters(
            String name,
            String category,
            Integer minAmount,
            Integer maxAmount,
            Double minPrice,
            Double maxPrice,
            int page,
            int pageSize
    );
}
