package com.battlefield.repository.impl;

import com.battlefield.repository.CombatLogRepository;
import com.battlefield.util.DatabaseManager;

import java.sql.*;
import java.util.*;

public class CombatLogRepositoryImpl implements CombatLogRepository {

    private final DatabaseManager db;

    public CombatLogRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public void save(Long bfId, Long attackerId, Long targetId,
                     String targetType, int damage, boolean destroyed, String description) {
        String sql = "INSERT INTO combat_log (battlefield_id, attacker_id, target_id, " +
                "target_type, damage_dealt, is_destroyed, description) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bfId);
            ps.setLong(2, attackerId);
            ps.setLong(3, targetId);
            ps.setString(4, targetType);
            ps.setInt(5, damage);
            ps.setBoolean(6, destroyed);
            ps.setString(7, description);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Map<String, Object>> findByBattlefieldId(Long bfId) {
        String sql = "SELECT * FROM combat_log WHERE battlefield_id=? ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bfId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("battlefieldId", rs.getLong("battlefield_id"));
                    row.put("attackerId", rs.getLong("attacker_id"));
                    row.put("targetId", rs.getLong("target_id"));
                    row.put("targetType", rs.getString("target_type"));
                    row.put("damageDealt", rs.getInt("damage_dealt"));
                    row.put("destroyed", rs.getBoolean("is_destroyed"));
                    row.put("description", rs.getString("description"));
                    row.put("createdAt", rs.getTimestamp("created_at").toString());
                    list.add(row);
                }
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
