package bgu.spl.net.impl.RegistrationSystem;

import java.util.LinkedList;

public abstract class User {
    private String username;
    private String password;
    private volatile boolean isLoggedIn;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        isLoggedIn = false;
    }

    public String getName(){
        return username;
    }
    public void login(){
        isLoggedIn = true;
    }
    public void logout(){
        isLoggedIn = false;
    }
    public String getPassword() {
        return password;
    }
    public boolean isLoggedIn(){return isLoggedIn;}

}
