package com.battlefield.repository.impl;

import com.battlefield.model.*;
import com.battlefield.repository.UnitRepository;
import com.battlefield.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UnitRepositoryImpl implements UnitRepository {

    private final DatabaseManager db;

    public UnitRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public BattleUnit save(BattleUnit u) {
        String sql = "INSERT INTO battle_unit (battlefield_id, name, unit_type, pos_x, pos_y, " +
                "size_width, size_height, health, max_health, speed, attack_range, damage, " +
                "direction, is_alive) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, u.getBattlefieldId());
            ps.setString(2, u.getName());
            ps.setString(3, u.getUnitType().name());
            ps.setInt(4, u.getPosition().getX());
            ps.setInt(5, u.getPosition().getY());
            ps.setInt(6, u.getSizeWidth());
            ps.setInt(7, u.getSizeHeight());
            ps.setInt(8, u.getHealth());
            ps.setInt(9, u.getMaxHealth());
            ps.setInt(10, u.getSpeed());
            ps.setInt(11, u.getAttackRange());
            ps.setInt(12, u.getDamage());
            ps.setString(13, u.getDirection().name());
            ps.setBoolean(14, u.isAlive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getLong(1));
            }
            return u;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<BattleUnit> findById(Long id) {
        String sql = "SELECT * FROM battle_unit WHERE id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<BattleUnit> findByBattlefieldId(Long bfId) {
        return query("SELECT * FROM battle_unit WHERE battlefield_id=?", bfId);
    }

    @Override
    public List<BattleUnit> findAliveByBattlefieldId(Long bfId) {
        return query("SELECT * FROM battle_unit WHERE battlefield_id=? AND is_alive=TRUE", bfId);
    }

    private List<BattleUnit> query(String sql, Long bfId) {
        List<BattleUnit> list = new ArrayList<>();
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
    public void update(BattleUnit u) {
        String sql = "UPDATE battle_unit SET pos_x=?, pos_y=?, health=?, direction=?, is_alive=? WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, u.getPosition().getX());
            ps.setInt(2, u.getPosition().getY());
            ps.setInt(3, u.getHealth());
            ps.setString(4, u.getDirection().name());
            ps.setBoolean(5, u.isAlive());
            ps.setLong(6, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM battle_unit WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Восстанавливает правильный подкласс через UnitFactory. */
    private BattleUnit mapRow(ResultSet rs) throws SQLException {
        UnitType type = UnitType.valueOf(rs.getString("unit_type"));
        Long bfId = rs.getLong("battlefield_id");
        String name = rs.getString("name");
        int x = rs.getInt("pos_x");
        int y = rs.getInt("pos_y");

        BattleUnit u = UnitFactory.create(type, bfId, name, x, y);
        u.setId(rs.getLong("id"));
        u.setPosition(new Position(x, y));
        u.setSizeWidth(rs.getInt("size_width"));
        u.setSizeHeight(rs.getInt("size_height"));
        u.setHealth(rs.getInt("health"));
        u.setMaxHealth(rs.getInt("max_health"));
        u.setSpeed(rs.getInt("speed"));
        u.setAttackRange(rs.getInt("attack_range"));
        u.setDamage(rs.getInt("damage"));
        u.setDirection(Direction.valueOf(rs.getString("direction")));
        u.setAlive(rs.getBoolean("is_alive"));
        return u;
    }
}
