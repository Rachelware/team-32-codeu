package com.google.codeu.data;

import java.util.ArrayList;

public class Puzzle {

    public enum Puzzle_Type {
        LOCATION,
        TEXT,
        PICTURE,
        MULTI_TEXT
    }

    int level;
    String answer;
    Puzzle_Type type;
    ArrayList<Stat> stats;

    /* Constructor */
    public Puzzle(int level, Puzzle_Type type, String answer) {
        this.type = type;
        this.level = level;
        this.type = type;
        this.stats = new ArrayList<>();
    }

    public String getAnswer() {
        return this.answer;
    }

    public Puzzle.Puzzle_Type getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    public ArrayList<Stat> getStats() {
        return this.stats;
    }

    public void setStats(ArrayList<Stat> stats) {
        this.stats = stats;
    }
}
