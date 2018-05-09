package com.example.basimahmad.smartjournalism.model;

/**
 * Created by basim on 09/05/2018.
 */

public class User {
    private String name;
    private String img;

    public User() {
    }

    public User(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
