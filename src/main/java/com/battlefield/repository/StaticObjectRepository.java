package com.battlefield.repository;

import com.battlefield.model.StaticObject;
import java.util.List;
import java.util.Optional;

public interface StaticObjectRepository {
    StaticObject save(StaticObject obj);
    Optional<StaticObject> findById(Long id);
    List<StaticObject> findByBattlefieldId(Long bfId);
    void update(StaticObject obj);
    void deleteById(Long id);
}
