package com.battlefield.model;

/** Турель: 1x1, неподвижная (speed=0), стреляет во все стороны. */
public class Turret extends BattleUnit {

    public Turret() {}

    public Turret(Long battlefieldId, String name, int x, int y) {
        super(battlefieldId, name, UnitType.TURRET, x, y, 1, 1,
              120, 0, 6, 25, Direction.ALL);
    }
    // НЕ реализует Movable — турель неподвижна!
}
