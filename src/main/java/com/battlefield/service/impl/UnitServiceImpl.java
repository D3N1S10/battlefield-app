package com.battlefield.service.impl;

import com.battlefield.model.*;
import com.battlefield.repository.BattlefieldRepository;
import com.battlefield.repository.UnitRepository;
import com.battlefield.repository.StaticObjectRepository;
import com.battlefield.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UnitServiceImpl implements UnitService {

    private static final Logger log = LoggerFactory.getLogger(UnitServiceImpl.class);
    private final BattlefieldRepository bfRepo;
    private final UnitRepository unitRepo;
    private final StaticObjectRepository objRepo;

    public UnitServiceImpl(BattlefieldRepository bfRepo,
                            UnitRepository unitRepo,
                            StaticObjectRepository objRepo) {
        this.bfRepo = bfRepo;
        this.unitRepo = unitRepo;
        this.objRepo = objRepo;
    }

    @Override
    public BattleUnit place(Long bfId, String name, String unitType, int x, int y) {
        Battlefield bf = bfRepo.findById(bfId)
                .orElseThrow(() -> new NoSuchElementException("Поле не найдено: " + bfId));

        UnitType type = UnitType.valueOf(unitType.toUpperCase());
        BattleUnit unit = UnitFactory.create(type, bfId, name, x, y);

        // Проверка границ поля
        if (!bf.containsCell(x, y) ||
            !bf.containsCell(x + unit.getSizeWidth() - 1, y + unit.getSizeHeight() - 1)) {
            throw new IllegalArgumentException("Юнит выходит за границы поля");
        }

        // Проверка коллизий
        checkCollision(bfId, x, y, unit.getSizeWidth(), unit.getSizeHeight(), null);

        unitRepo.save(unit);
        log.info("Размещён юнит '{}' ({}) на ({},{}) поля {}", name, type, x, y, bfId);
        return unit;
    }

    @Override
    public Optional<BattleUnit> getById(Long id) { return unitRepo.findById(id); }

    @Override
    public Map<String, Object> move(Long unitId, String directionStr) {
        BattleUnit unit = unitRepo.findById(unitId)
                .orElseThrow(() -> new NoSuchElementException("Юнит не найден: " + unitId));

        if (!unit.isAlive()) {
            return Map.of("success", false, "message", "Юнит уничтожен");
        }

        // Полиморфная проверка: только юниты с Movable могут двигаться
        if (!(unit instanceof Movable movable)) {
            return Map.of("success", false, "message",
                    "Юнит '" + unit.getName() + "' не может двигаться (неподвижный)");
        }

        Direction dir = Direction.valueOf(directionStr.toUpperCase());
        if (dir == Direction.ALL) {
            return Map.of("success", false, "message", "ALL — не направление для движения");
        }

        Battlefield bf = bfRepo.findById(unit.getBattlefieldId())
                .orElseThrow(() -> new NoSuchElementException("Поле не найдено"));

        // Вычисляем новую позицию
        Position newPos = unit.getPosition().move(dir, movable.getSpeed());

        // Ограничиваем границами поля
        int nx = Math.max(0, Math.min(newPos.getX(), bf.getWidth() - unit.getSizeWidth()));
        int ny = Math.max(0, Math.min(newPos.getY(), bf.getHeight() - unit.getSizeHeight()));
        newPos = new Position(nx, ny);

        // Проверка коллизий на новой позиции
        try {
            checkCollision(unit.getBattlefieldId(), nx, ny,
                    unit.getSizeWidth(), unit.getSizeHeight(), unit.getId());
        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", "Путь заблокирован: " + e.getMessage());
        }

        // Применяем перемещение
        unit.setPosition(newPos);
        unit.setDirection(dir);
        unitRepo.update(unit);

        log.info("Юнит '{}' переместился на {}", unit.getName(), newPos);
        return Map.of("success", true, "newX", nx, "newY", ny,
                "message", "Юнит перемещён на " + newPos);
    }

    @Override
    public void delete(Long id) { unitRepo.deleteById(id); }

    private void checkCollision(Long bfId, int x, int y, int w, int h, Long excludeId) {
        List<BattleUnit> units = unitRepo.findAliveByBattlefieldId(bfId);
        List<StaticObject> objects = objRepo.findByBattlefieldId(bfId);

        for (int cx = x; cx < x + w; cx++) {
            for (int cy = y; cy < y + h; cy++) {
                for (BattleUnit u : units) {
                    if (excludeId != null && u.getId().equals(excludeId)) continue;
                    if (u.occupiesCell(cx, cy)) {
                        throw new IllegalArgumentException(
                                String.format("Клетка (%d,%d) занята юнитом '%s'", cx, cy, u.getName()));
                    }
                }
                for (StaticObject o : objects) {
                    if (!o.isDestroyed() && o.occupiesCell(cx, cy)) {
                        throw new IllegalArgumentException(
                                String.format("Клетка (%d,%d) занята объектом '%s'", cx, cy, o.getName()));
                    }
                }
            }
        }
    }
}
