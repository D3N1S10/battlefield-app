package com.battlefield.model;

import java.sql.Timestamp;

/** Поле боя — прямоугольная сетка клеток. */
public class Battlefield {

    private Long id;
    private String name;
    private int width;
    private int height;
    private Timestamp createdAt;

    public Battlefield() {}

    public Battlefield(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public boolean containsCell(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
