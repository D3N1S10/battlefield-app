package com.battlefield.repository;

import java.util.List;
import java.util.Map;

public interface CombatLogRepository {
    void save(Long bfId, Long attackerId, Long targetId, String targetType,
              int damage, boolean destroyed, String description);
    List<Map<String, Object>> findByBattlefieldId(Long bfId);
}
