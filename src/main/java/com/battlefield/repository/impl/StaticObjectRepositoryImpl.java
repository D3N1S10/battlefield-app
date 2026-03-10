package com.battlefield.repository.impl;

import com.battlefield.model.*;
import com.battlefield.repository.StaticObjectRepository;
import com.battlefield.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaticObjectRepositoryImpl implements StaticObjectRepository {

    private final DatabaseManager db;

    public StaticObjectRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public StaticObject save(StaticObject o) {
        String sql = "INSERT INTO static_object (battlefield_id, name, object_type, pos_x, pos_y, " +
                "size_width, size_height, is_destructible, health) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, o.getBattlefieldId());
            ps.setString(2, o.getName());
            ps.setString(3, o.getObjectType().name());
            ps.setInt(4, o.getPosition().getX());
            ps.setInt(5, o.getPosition().getY());
            ps.setInt(6, o.getSizeWidth());
            ps.setInt(7, o.getSizeHeight());
            ps.setBoolean(8, o.isDestructible());
            ps.setInt(9, o.getHealth());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) o.setId(rs.getLong(1));
            }
            return o;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<StaticObject> findById(Long id) {
        String sql = "SELECT * FROM static_object WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<StaticObject> findByBattlefieldId(Long bfId) {
        String sql = "SELECT * FROM static_object WHERE battlefield_id=?";
        List<StaticObject> list = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bfId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(StaticObject o) {
        String sql = "UPDATE static_object SET health=? WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, o.getHealth());
            ps.setLong(2, o.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM static_object WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private StaticObject mapRow(ResultSet rs) throws SQLException {
        StaticObject o = new StaticObject();
        o.setId(rs.getLong("id"));
        o.setBattlefieldId(rs.getLong("battlefield_id"));
        o.setName(rs.getString("name"));
        o.setObjectType(ObjectType.valueOf(rs.getString("object_type")));
        o.setPosition(new Position(rs.getInt("pos_x"), rs.getInt("pos_y")));
        o.setSizeWidth(rs.getInt("size_width"));
        o.setSizeHeight(rs.getInt("size_height"));
        o.setDestructible(rs.getBoolean("is_destructible"));
        o.setHealth(rs.getInt("health"));
        o.setMaxHealth(rs.getInt("health"));
        return o;
    }
}
