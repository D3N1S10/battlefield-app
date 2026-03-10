package com.battlefield.repository.impl;

import com.battlefield.model.Battlefield;
import com.battlefield.repository.BattlefieldRepository;
import com.battlefield.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattlefieldRepositoryImpl implements BattlefieldRepository {

    private final DatabaseManager db;

    public BattlefieldRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Battlefield save(Battlefield bf) {
        String sql = "INSERT INTO battlefield (name, width, height) VALUES (?, ?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bf.getName());
            ps.setInt(2, bf.getWidth());
            ps.setInt(3, bf.getHeight());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) bf.setId(rs.getLong(1));
            }
            return bf;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Battlefield> findById(Long id) {
        String sql = "SELECT * FROM battlefield WHERE id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Battlefield> findAll() {
        String sql = "SELECT * FROM battlefield ORDER BY created_at DESC";
        List<Battlefield> list = new ArrayList<>();
        try (Connection c = db.getConnection();
             Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM battlefield WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Battlefield mapRow(ResultSet rs) throws SQLException {
        Battlefield bf = new Battlefield();
        bf.setId(rs.getLong("id"));
        bf.setName(rs.getString("name"));
        bf.setWidth(rs.getInt("width"));
        bf.setHeight(rs.getInt("height"));
        bf.setCreatedAt(rs.getTimestamp("created_at"));
        return bf;
    }
}
