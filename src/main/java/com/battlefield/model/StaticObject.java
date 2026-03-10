package com.battlefield.model;

/** Статический объект на поле боя (стена, камень, здание, дерево). */
public class StaticObject extends GameObject implements Damageable {

    private ObjectType objectType;
    private boolean destructible;
    private int health;
    private int maxHealth;

    public StaticObject() {}

    public StaticObject(Long battlefieldId, String name, ObjectType type, int x, int y) {
        super(battlefieldId, name, x, y, type.getDefaultWidth(), type.getDefaultHeight());
        this.objectType = type;
        this.destructible = type.isDestructible();
        this.health = type.getDefaultHealth();
        this.maxHealth = type.getDefaultHealth();
    }

    // ===== Damageable implementation =====

    @Override
    public int getHealth() { return health; }

    @Override
    public int getMaxHealth() { return maxHealth; }

    @Override
    public int takeDamage(int amount) {
        if (!destructible) return 0;
        int actual = Math.min(amount, health);
        health -= actual;
        return actual;
    }

    @Override
    public boolean isDestroyed() {
        return destructible && health <= 0;
    }

    // ===== Getters / Setters =====

    public ObjectType getObjectType() { return objectType; }
    public void setObjectType(ObjectType objectType) { this.objectType = objectType; }

    public boolean isDestructible() { return destructible; }
    public void setDestructible(boolean destructible) { this.destructible = destructible; }

    public void setHealth(int health) { this.health = health; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
}
