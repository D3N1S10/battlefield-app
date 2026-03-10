package com.battlefield.service.impl;

import com.battlefield.model.*;
import com.battlefield.repository.BattlefieldRepository;
import com.battlefield.repository.UnitRepository;
import com.battlefield.repository.StaticObjectRepository;
import com.battlefield.service.BattlefieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BattlefieldServiceImpl implements BattlefieldService {

    private static final Logger log = LoggerFactory.getLogger(BattlefieldServiceImpl.class);
    private final BattlefieldRepository bfRepo;
    private final UnitRepository unitRepo;
    private final StaticObjectRepository objRepo;

    public BattlefieldServiceImpl(BattlefieldRepository bfRepo,
                                   UnitRepository unitRepo,
                                   StaticObjectRepository objRepo) {
        this.bfRepo = bfRepo;
        this.unitRepo = unitRepo;
        this.objRepo = objRepo;
    }

    @Override
    public Battlefield create(String name, int width, int height) {
        if (width < 5 || width > 100 || height < 5 || height > 100) {
            throw new IllegalArgumentException("Размер поля: от 5x5 до 100x100");
        }
        Battlefield bf = bfRepo.save(new Battlefield(name, width, height));
        log.info("Создано поле '{}' ({}x{}), id={}", name, width, height, bf.getId());
        return bf;
    }

    @Override
    public Optional<Battlefield> getById(Long id) { return bfRepo.findById(id); }

    @Override
    public List<Battlefield> getAll() { return bfRepo.findAll(); }

    @Override
    public void delete(Long id) {
        bfRepo.deleteById(id);
        log.info("Удалено поле id={}", id);
    }

    @Override
    public Map<String, Object> getState(Long id) {
        Battlefield bf = bfRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Поле не найдено: " + id));
        List<BattleUnit> units = unitRepo.findByBattlefieldId(id);
        List<StaticObject> objects = objRepo.findByBattlefieldId(id);

        Map<String, Object> state = new LinkedHashMap<>();
        state.put("battlefield", bf);
        state.put("units", units);
        state.put("objects", objects);
        state.put("aliveUnits", units.stream().filter(BattleUnit::isAlive).count());
        state.put("destroyedUnits", units.stream().filter(u -> !u.isAlive()).count());
        return state;
    }
}
