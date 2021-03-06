package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.RegistrationSystem.*;

import java.nio.charset.StandardCharsets;

public class StudentRegMessage implements Message {
    private String username;
    private String password;

    //constructor
    public StudentRegMessage(String username, String password) {
        super();
        this.username = username;
        this.password = password;

    }

    @Override
    public Message execute(Database database, Session session) {//register a student in the service
        User user = session.getUser();
        //check if the user not loggedIn
        if (user == null) {
            User student = new Student(username, password);
            User toAdd = database.UserReg(username, student);
            if (toAdd == null) {
                return new AckMessage((short) 2, null);
            }
        }
        return new ErrorMessage((short) 2);

    }
}

