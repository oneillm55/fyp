package com.example.fyp.UserFolder;

public class User {

    String usernname;
    String email;
    String password;
    String imageID;

    public User(String usernname, String email, String password,String imageChanged) {
        this.usernname = usernname;
        this.email = email;
        this.password = password;
        this.imageID = imageChanged;
    }


    public User() {
    }



    public String getUsernname() {
        return usernname;
    }

    public void setUsernname(String usernname) {
        this.usernname = usernname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
}
