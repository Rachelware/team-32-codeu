package com.google.codeu.data;

import java.util.ArrayList;

public class Puzzle {

    int level;
    String answer;

    /* Constructor */
    public Puzzle(int level, String answer) {
        this.answer = answer;
        this.level = level;
    }

    public String getAnswer() {
        return this.answer;
    }
    public int getLevel() {
        return this.level;
    }
}
