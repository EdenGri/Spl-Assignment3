package bgu.spl.net.impl.RegistrationSystem;

public class Session {
    User user;
    public Session(User user){
        this.user=user;
    }

    public User getUser(){
        return user;
    }
    public void setUser(User user){
        user=user;
    }
}