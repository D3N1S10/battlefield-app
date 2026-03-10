package com.battlefield.model;

public enum ObjectType {
    WALL(true, 50, 3, 1),
    ROCK(false, -1, 1, 1),
    BUILDING(true, 200, 3, 3),
    TREE(true, 20, 1, 1);

    private final boolean destructible;
    private final int defaultHealth;
    private final int defaultWidth;
    private final int defaultHeight;

    ObjectType(boolean destructible, int health, int width, int height) {
        this.destructible = destructible;
        this.defaultHealth = health;
        this.defaultWidth = width;
        this.defaultHeight = height;
    }

    public boolean isDestructible() { return destructible; }
    public int getDefaultHealth() { return defaultHealth; }
    public int getDefaultWidth() { return defaultWidth; }
    public int getDefaultHeight() { return defaultHeight; }
}
