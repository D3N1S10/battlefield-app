package com.battlefield.model;

/**
 * Абстрактный боевой юнит. Наследует GameObject, реализует Damageable и Shootable.
 * Конкретные типы юнитов (Tank, Infantry, ...) наследуют этот класс.
 */
public abstract class BattleUnit extends GameObject implements Damageable, Shootable {

    protected UnitType unitType;
    protected int health;
    protected int maxHealth;
    protected int speed;
    protected int attackRange;
    protected int damage;
    protected Direction direction;
    protected boolean alive;

    protected BattleUnit() {}

    protected BattleUnit(Long battlefieldId, String name, UnitType type,
                         int x, int y, int w, int h,
                         int health, int speed, int range, int dmg, Direction dir) {
        super(battlefieldId, name, x, y, w, h);
        this.unitType = type;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.attackRange = range;
        this.damage = dmg;
        this.direction = dir;
        this.alive = true;
    }

    // ===== Damageable implementation =====

    @Override
    public int getHealth() { return health; }

    @Override
    public int getMaxHealth() { return maxHealth; }

    @Override
    public int takeDamage(int amount) {
        int actual = Math.min(amount, health);
        health -= actual;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
        return actual;
    }

    @Override
    public boolean isDestroyed() { return !alive; }

    // ===== Shootable implementation =====

    @Override
    public int getAttackRange() { return attackRange; }

    @Override
    public int getDamage() { return damage; }

    @Override
    public Direction getFireDirection() { return direction; }

    @Override
    public boolean canShootAt(Position target) {
        if (!alive) return false;
        double dist = position.distanceTo(target);
        if (dist > attackRange) return false;
        if (direction == Direction.ALL) return true;

        int dx = Integer.compare(target.getX() - position.getX(), 0);
        int dy = Integer.compare(target.getY() - position.getY(), 0);
        return dx == direction.getDx() && dy == direction.getDy();
    }

    // ===== Additional methods =====

    public boolean isMovable() { return speed > 0; }

    // ===== Getters / Setters =====

    public UnitType getUnitType() { return unitType; }
    public void setUnitType(UnitType unitType) { this.unitType = unitType; }

    public void setHealth(int health) { this.health = health; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public void setAttackRange(int attackRange) { this.attackRange = attackRange; }
    public void setDamage(int damage) { this.damage = damage; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}
