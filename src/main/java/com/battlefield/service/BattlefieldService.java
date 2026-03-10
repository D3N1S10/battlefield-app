package com.battlefield.service;

import com.battlefield.model.Battlefield;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BattlefieldService {
    Battlefield create(String name, int width, int height);
    Optional<Battlefield> getById(Long id);
    List<Battlefield> getAll();
    void delete(Long id);
    Map<String, Object> getState(Long id);
}
