package com.battlefield.service;

import com.battlefield.model.ShotResult;
import java.util.List;
import java.util.Map;

public interface CombatService {
    ShotResult shoot(Long attackerId, int targetX, int targetY);
    List<Map<String, Object>> getCombatLog(Long battlefieldId);
}
