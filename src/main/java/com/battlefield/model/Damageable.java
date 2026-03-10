package com.battlefield.model;

/**
 * Интерфейс для объектов, которые могут получать урон.
 */
public interface Damageable {

    int getHealth();

    int getMaxHealth();

    /**
     * Нанести урон объекту.
     * @param amount количество входящего урона
     * @return фактически нанесённый урон
     */
    int takeDamage(int amount);

    boolean isDestroyed();
}
