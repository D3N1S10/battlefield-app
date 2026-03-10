package com.battlefield.service;

import com.battlefield.model.BattleUnit;
import java.util.Map;
import java.util.Optional;

public interface UnitService {
    BattleUnit place(Long battlefieldId, String name, String unitType, int x, int y);
    Optional<BattleUnit> getById(Long id);
    Map<String, Object> move(Long unitId, String direction);
    void delete(Long id);
}
