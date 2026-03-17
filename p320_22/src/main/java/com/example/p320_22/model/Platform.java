package com.example.p320_22.model;

public class Platform {
    private int id;
    private String type;

    public Platform(int id, String type) {
        this.id = id;
        this.type = type;
    }

    /** Getters */
    public int getId() { return this.id; }
    public String getType() { return this.type; }
}
