package com.battlefield.repository;

import com.battlefield.model.Battlefield;
import java.util.List;
import java.util.Optional;

public interface BattlefieldRepository {
    Battlefield save(Battlefield bf);
    Optional<Battlefield> findById(Long id);
    List<Battlefield> findAll();
    void deleteById(Long id);
}
