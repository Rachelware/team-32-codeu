package com.google.codeu.data;

public class User {
    
    private String email;
    private String aboutMe;
    private int level;
    
    public User(String email, String aboutMe) {
        this.email = email;
        this.aboutMe = aboutMe;
        this.level = 1;
    }

    public int getLevel() { return level; }

    public void levelUp() { this.level++; }
    
    public String getEmail(){
        return email;
    }
    
    public String getAboutMe(){
        return aboutMe;
    }
}