package com.example.fyp.UserFolder;

public class User {

    String usernname;
    String email;
    String password;

    public User(String usernname, String email, String password) {
        this.usernname = usernname;
        this.email = email;
        this.password = password;
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

}
