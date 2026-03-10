package com.battlefield.service;

import com.battlefield.model.StaticObject;
import java.util.Optional;

public interface StaticObjectService {
    StaticObject place(Long battlefieldId, String name, String objectType, int x, int y);
    Optional<StaticObject> getById(Long id);
    void delete(Long id);
}
