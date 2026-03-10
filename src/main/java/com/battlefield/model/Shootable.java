package com.battlefield.model;

/**
 * Интерфейс для юнитов, способных стрелять.
 */
public interface Shootable {

    int getAttackRange();

    int getDamage();

    Direction getFireDirection();

    /**
     * Проверяет, может ли юнит выстрелить по указанной позиции.
     * Учитывает дальность и сектор стрельбы.
     */
    boolean canShootAt(Position target);
}
