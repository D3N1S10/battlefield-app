package com.battlefield.model;

/**
 * Интерфейс для юнитов, способных перемещаться по полю боя.
 */
public interface Movable {

    int getSpeed();

    /**
     * Переместить юнит в указанном направлении.
     * @param direction направление движения
     * @return новая позиция после перемещения
     */
    Position move(Direction direction);
}
