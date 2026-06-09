package com.practice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteProdDB implements ProductDB {
    private final Connection connection;

    public SQLiteProdDB(String dbName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            initTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initTable() {
        try (Statement st = connection.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS product(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        price REAL NOT NULL
                    )
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insert(Product product) {
        String sql = "INSERT INTO product(name, category, amount, price) VALUES(?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getAmount());
            ps.setDouble(4, product.getPrice());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Product> getById(int id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM product WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> getAll() {
        List<Product> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM product")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE product SET name=?, category=?, amount=?, price=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getAmount());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM product WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> findWithFilters(
            String name,
            String category,
            Integer minAmount,
            Integer maxAmount,
            Double minPrice,
            Double maxPrice,
            int page,
            int pageSize) {

        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }

        if (category != null) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        if (minAmount != null) {
            sql.append(" AND amount >= ?");
            params.add(minAmount);
        }

        if (maxAmount != null) {
            sql.append(" AND amount <= ?");
            params.add(maxAmount);
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(map(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("amount"),
                rs.getDouble("price")
        );
    }
}
