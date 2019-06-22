package com.google.codeu.data;

import java.security.Timestamp;

public class User {
    
    private String email;
    private String aboutMe;
    private int level;
    private long timestamp;
    
    public User(String email, String aboutMe, int level) {
        this.email = email;
        this.aboutMe = aboutMe;
        this.level = level;
        this.timestamp = System.currentTimeMillis();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int lev) {
        this.level = lev;
    }

    public void levelUp() {
        this.level++;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public String getAboutMe(){
        return this.aboutMe;
    }
}