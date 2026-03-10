package com.battlefield.service.impl;

import com.battlefield.model.*;
import com.battlefield.repository.BattlefieldRepository;
import com.battlefield.repository.CombatLogRepository;
import com.battlefield.repository.UnitRepository;
import com.battlefield.repository.StaticObjectRepository;
import com.battlefield.service.CombatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class CombatServiceImpl implements CombatService {

    private static final Logger log = LoggerFactory.getLogger(CombatServiceImpl.class);
    private final BattlefieldRepository bfRepo;
    private final UnitRepository unitRepo;
    private final StaticObjectRepository objRepo;
    private final CombatLogRepository combatLogRepo;

    public CombatServiceImpl(BattlefieldRepository bfRepo,
                              UnitRepository unitRepo,
                              StaticObjectRepository objRepo,
                              CombatLogRepository combatLogRepo) {
        this.bfRepo = bfRepo;
        this.unitRepo = unitRepo;
        this.objRepo = objRepo;
        this.combatLogRepo = combatLogRepo;
    }

    @Override
    public ShotResult shoot(Long attackerId, int targetX, int targetY) {
        BattleUnit attacker = unitRepo.findById(attackerId)
                .orElseThrow(() -> new NoSuchElementException("Атакующий не найден: " + attackerId));

        if (!attacker.isAlive()) {
            return ShotResult.miss("Юнит уничтожен и не может стрелять");
        }

        Position target = new Position(targetX, targetY);

        // Проверка canShootAt — учитывает дальность и сектор стрельбы
        if (!attacker.canShootAt(target)) {
            double dist = attacker.getPosition().distanceTo(target);
            if (dist > attacker.getAttackRange()) {
                return ShotResult.miss(String.format(
                        "Цель (%d,%d) вне досягаемости (дистанция=%.1f, дальность=%d)",
                        targetX, targetY, dist, attacker.getAttackRange()));
            }
            return ShotResult.miss(String.format(
                    "Цель (%d,%d) не в секторе стрельбы (%s)",
                    targetX, targetY, attacker.getDirection()));
        }

        // Проверяем попадание в юниты
        List<BattleUnit> units = unitRepo.findAliveByBattlefieldId(attacker.getBattlefieldId());
        for (BattleUnit victim : units) {
            if (victim.getId().equals(attackerId)) continue;
            if (victim.occupiesCell(targetX, targetY)) {
                int dmg = victim.takeDamage(attacker.getDamage());
                unitRepo.update(victim);

                combatLogRepo.save(attacker.getBattlefieldId(), attackerId, victim.getId(),
                        "UNIT", dmg, victim.isDestroyed(),
                        attacker.getName() + " -> " + victim.getName() + " (" + dmg + " dmg)");

                log.info("{} попал в {} на {} урона (HP: {})",
                        attacker.getName(), victim.getName(), dmg, victim.getHealth());
                return ShotResult.hitUnit(victim.getName(), dmg, victim.getHealth(), victim.isDestroyed());
            }
        }

        // Проверяем попадание в объекты
        List<StaticObject> objects = objRepo.findByBattlefieldId(attacker.getBattlefieldId());
        for (StaticObject obj : objects) {
            if (obj.isDestroyed()) continue;
            if (obj.occupiesCell(targetX, targetY)) {
                int dmg = obj.takeDamage(attacker.getDamage());
                if (dmg == 0) {
                    return ShotResult.miss("Объект '" + obj.getName() + "' неразрушим");
                }
                objRepo.update(obj);
                boolean destroyed = obj.isDestroyed();
                if (destroyed) objRepo.deleteById(obj.getId());

                combatLogRepo.save(attacker.getBattlefieldId(), attackerId, obj.getId(),
                        "OBJECT", dmg, destroyed,
                        attacker.getName() + " -> " + obj.getName() + " (" + dmg + " dmg)");

                return ShotResult.hitObject(obj.getName(), dmg, obj.getHealth(), destroyed);
            }
        }

        return ShotResult.miss(String.format("Нет целей на клетке (%d,%d)", targetX, targetY));
    }

    @Override
    public List<Map<String, Object>> getCombatLog(Long battlefieldId) {
        bfRepo.findById(battlefieldId)
                .orElseThrow(() -> new NoSuchElementException("Поле не найдено: " + battlefieldId));
        return combatLogRepo.findByBattlefieldId(battlefieldId);
    }
}
