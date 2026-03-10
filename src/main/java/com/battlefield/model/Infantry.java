package com.battlefield.model;

/** Пехота: 1x1, быстрая, слабая. */
public class Infantry extends BattleUnit implements Movable {

    public Infantry() {}

    public Infantry(Long battlefieldId, String name, int x, int y) {
        super(battlefieldId, name, UnitType.INFANTRY, x, y, 1, 1,
              50, 2, 3, 10, Direction.ALL);
    }

    @Override
    public Position move(Direction dir) {
        Position newPos = position.move(dir, speed);
        this.position = newPos;
        this.direction = dir;
        return newPos;
    }
}
