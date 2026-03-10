package com.battlefield.model;

/** Результат выстрела (DTO). */
public class ShotResult {

    private boolean hit;
    private String targetName;
    private String targetType;
    private int damageDealt;
    private int remainingHealth;
    private boolean destroyed;
    private String message;

    private ShotResult() {}

    public static ShotResult miss(String msg) {
        ShotResult r = new ShotResult();
        r.hit = false;
        r.message = msg;
        return r;
    }

    public static ShotResult hitUnit(String name, int dmg, int remaining, boolean destroyed) {
        ShotResult r = new ShotResult();
        r.hit = true;
        r.targetName = name;
        r.targetType = "UNIT";
        r.damageDealt = dmg;
        r.remainingHealth = remaining;
        r.destroyed = destroyed;
        r.message = destroyed
            ? String.format("Юнит '%s' уничтожен! Урон: %d", name, dmg)
            : String.format("Юнит '%s' получил %d урона, HP: %d", name, dmg, remaining);
        return r;
    }

    public static ShotResult hitObject(String name, int dmg, int remaining, boolean destroyed) {
        ShotResult r = new ShotResult();
        r.hit = true;
        r.targetName = name;
        r.targetType = "OBJECT";
        r.damageDealt = dmg;
        r.remainingHealth = remaining;
        r.destroyed = destroyed;
        r.message = destroyed
            ? String.format("Объект '%s' разрушен! Урон: %d", name, dmg)
            : String.format("Объект '%s' получил %d урона, HP: %d", name, dmg, remaining);
        return r;
    }

    public boolean isHit() { return hit; }
    public String getTargetName() { return targetName; }
    public String getTargetType() { return targetType; }
    public int getDamageDealt() { return damageDealt; }
    public int getRemainingHealth() { return remainingHealth; }
    public boolean isDestroyed() { return destroyed; }
    public String getMessage() { return message; }
}
