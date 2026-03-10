package com.battlefield.model;

/**
 * Фабрика для создания боевых юнитов по типу (паттерн Factory Method).
 */
public class UnitFactory {

    public static BattleUnit create(UnitType type, Long battlefieldId, String name, int x, int y) {
        return switch (type) {
            case TANK      -> new Tank(battlefieldId, name, x, y);
            case INFANTRY  -> new Infantry(battlefieldId, name, x, y);
            case ARTILLERY -> new Artillery(battlefieldId, name, x, y);
            case TURRET    -> new Turret(battlefieldId, name, x, y);
            case SNIPER    -> new Sniper(battlefieldId, name, x, y);
            case APC       -> new APC(battlefieldId, name, x, y);
        };
    }
}
