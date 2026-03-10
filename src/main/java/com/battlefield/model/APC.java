package com.battlefield.model;

/** БТР: 2x1, быстрый, слабое вооружение, крепкий. */
public class APC extends BattleUnit implements Movable {

    public APC() {}

    public APC(Long battlefieldId, String name, int x, int y) {
        super(battlefieldId, name, UnitType.APC, x, y, 2, 1,
              100, 4, 3, 15, Direction.ALL);
    }

    @Override
    public Position move(Direction dir) {
        Position newPos = position.move(dir, speed);
        this.position = newPos;
        this.direction = dir;
        return newPos;
    }
}
