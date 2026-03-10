package com.battlefield.model;

/** Снайпер: 1x1, хрупкий, очень дальнобойный. */
public class Sniper extends BattleUnit implements Movable {

    public Sniper() {}

    public Sniper(Long battlefieldId, String name, int x, int y) {
        super(battlefieldId, name, UnitType.SNIPER, x, y, 1, 1,
              30, 2, 10, 40, Direction.ALL);
    }

    @Override
    public Position move(Direction dir) {
        Position newPos = position.move(dir, speed);
        this.position = newPos;
        this.direction = dir;
        return newPos;
    }
}
