package com.example.designstudioiitr.wassup;

/**
 * Created by Design Studio IITR on 31-03-2018.
 */

public class User {

    String name, status, image;

    public User() {

    }

    public User(String name, String status, String image) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
