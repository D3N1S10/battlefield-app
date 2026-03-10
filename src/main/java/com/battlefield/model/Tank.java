package com.battlefield.model;

/** Танк: 2x2 клетки, подвижный, стреляет во все стороны. */
public class Tank extends BattleUnit implements Movable {

    public Tank() {}

    public Tank(Long battlefieldId, String name, int x, int y) {
        super(battlefieldId, name, UnitType.TANK, x, y, 2, 2,
              150, 3, 5, 30, Direction.ALL);
    }

    @Override
    public Position move(Direction dir) {
        Position newPos = position.move(dir, speed);
        this.position = newPos;
        this.direction = dir;
        return newPos;
    }
}
