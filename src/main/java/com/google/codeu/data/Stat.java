package com.google.codeu.data;

import java.util.UUID;

public class Stat {

    public enum Stat_Type {
        DURATION,
        ATTEMPTS,
        CONTRIBUTION
    }

    private String user_email;
    private Stat_Type type;
    private double value;
    private int level;
    private UUID id;

    /* Constructor */
    public Stat(String user_email, Stat_Type type, double value, int level) {
        this.user_email = user_email;
        this.type = type;
        this.value = value;
        this.level = level;
        this.id = java.util.UUID.randomUUID();
    }

    public String getUser() {
        return this.user_email;
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

    public void incrementValue() {
        this.value = this.value + 1;
    }
}
