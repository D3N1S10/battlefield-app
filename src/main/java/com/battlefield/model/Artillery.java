package com.battlefield.model;

/** Артиллерия: 2x1, медленная, дальнобойная, стреляет только на NORTH. */
public class Artillery extends BattleUnit implements Movable {

    public Artillery() {}

    public Artillery(Long battlefieldId, String name, int x, int y) {
        super(battlefieldId, name, UnitType.ARTILLERY, x, y, 2, 1,
              80, 1, 8, 50, Direction.NORTH);
    }

    @Override
    public Position move(Direction dir) {
        Position newPos = position.move(dir, speed);
        this.position = newPos;
        // Артиллерия НЕ меняет направление стрельбы при движении
        return newPos;
    }
}
