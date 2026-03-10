package com.battlefield.repository;

import com.battlefield.model.BattleUnit;
import java.util.List;
import java.util.Optional;

public interface UnitRepository {
    BattleUnit save(BattleUnit unit);
    Optional<BattleUnit> findById(Long id);
    List<BattleUnit> findByBattlefieldId(Long bfId);
    List<BattleUnit> findAliveByBattlefieldId(Long bfId);
    void update(BattleUnit unit);
    void deleteById(Long id);
}
