package com.battlefield.model;

/**
 * Абстрактный базовый класс для всех объектов на поле боя.
 * Инкапсулирует общие свойства: ID, позиция, размер на сетке.
 */
public abstract class GameObject {

    protected Long id;
    protected Long battlefieldId;
    protected String name;
    protected Position position;
    protected int sizeWidth;
    protected int sizeHeight;

    protected GameObject() {}

    protected GameObject(Long battlefieldId, String name, int x, int y, int w, int h) {
        this.battlefieldId = battlefieldId;
        this.name = name;
        this.position = new Position(x, y);
        this.sizeWidth = w;
        this.sizeHeight = h;
    }

    /**
     * Проверяет, занимает ли этот объект указанную клетку.
     */
    public boolean occupiesCell(int x, int y) {
        return x >= position.getX() && x < position.getX() + sizeWidth
            && y >= position.getY() && y < position.getY() + sizeHeight;
    }

    // ===== Getters / Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBattlefieldId() { return battlefieldId; }
    public void setBattlefieldId(Long battlefieldId) { this.battlefieldId = battlefieldId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public int getSizeWidth() { return sizeWidth; }
    public void setSizeWidth(int sizeWidth) { this.sizeWidth = sizeWidth; }

    public int getSizeHeight() { return sizeHeight; }
    public void setSizeHeight(int sizeHeight) { this.sizeHeight = sizeHeight; }
}
