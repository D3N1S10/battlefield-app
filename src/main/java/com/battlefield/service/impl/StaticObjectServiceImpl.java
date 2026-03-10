package com.battlefield.service.impl;

import com.battlefield.model.*;
import com.battlefield.repository.BattlefieldRepository;
import com.battlefield.repository.StaticObjectRepository;
import com.battlefield.repository.UnitRepository;
import com.battlefield.service.StaticObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class StaticObjectServiceImpl implements StaticObjectService {

    private static final Logger log = LoggerFactory.getLogger(StaticObjectServiceImpl.class);
    private final BattlefieldRepository bfRepo;
    private final UnitRepository unitRepo;
    private final StaticObjectRepository objRepo;

    public StaticObjectServiceImpl(BattlefieldRepository bfRepo,
                                    UnitRepository unitRepo,
                                    StaticObjectRepository objRepo) {
        this.bfRepo = bfRepo;
        this.unitRepo = unitRepo;
        this.objRepo = objRepo;
    }

    @Override
    public StaticObject place(Long bfId, String name, String objectType, int x, int y) {
        Battlefield bf = bfRepo.findById(bfId)
                .orElseThrow(() -> new NoSuchElementException("Поле не найдено: " + bfId));

        ObjectType type = ObjectType.valueOf(objectType.toUpperCase());
        StaticObject obj = new StaticObject(bfId, name, type, x, y);

        if (!bf.containsCell(x, y) ||
            !bf.containsCell(x + obj.getSizeWidth() - 1, y + obj.getSizeHeight() - 1)) {
            throw new IllegalArgumentException("Объект выходит за границы поля");
        }

        // Проверка коллизий
        List<BattleUnit> units = unitRepo.findAliveByBattlefieldId(bfId);
        List<StaticObject> objects = objRepo.findByBattlefieldId(bfId);
        for (int cx = x; cx < x + obj.getSizeWidth(); cx++) {
            for (int cy = y; cy < y + obj.getSizeHeight(); cy++) {
                for (BattleUnit u : units) {
                    if (u.occupiesCell(cx, cy))
                        throw new IllegalArgumentException(
                            "Клетка (" + cx + "," + cy + ") занята юнитом '" + u.getName() + "'");
                }
                for (StaticObject o : objects) {
                    if (!o.isDestroyed() && o.occupiesCell(cx, cy))
                        throw new IllegalArgumentException(
                            "Клетка (" + cx + "," + cy + ") занята объектом '" + o.getName() + "'");
                }
            }
        }

        objRepo.save(obj);
        log.info("Размещён объект '{}' ({}) на ({},{}) поля {}", name, type, x, y, bfId);
        return obj;
    }

    @Override
    public Optional<StaticObject> getById(Long id) { return objRepo.findById(id); }

    @Override
    public void delete(Long id) { objRepo.deleteById(id); }
}
