package com.google.codeu.data;

import java.util.UUID;

public class Stat {

    public enum Stat_Type {
        DURATION,
        ATTEMPTS,
        CONTRIBUTION
    }

    private User user;
    private Stat_Type type;
    private double value;
    private int level;
    private UUID id;

    /* Constructor */
    public Stat(User user, Stat_Type type, double value, int level) {
        this.user = user;
        this.type = type;
        this.value = value;
        this.level = level;
        this.id = java.util.UUID.randomUUID();
    }

    public User getUser() {
        return this.user;
    }

    public String getId() {
        return this.id.toString();
    }

    public Stat_Type getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    public double getValue() {
        return this.value;
    }
}
