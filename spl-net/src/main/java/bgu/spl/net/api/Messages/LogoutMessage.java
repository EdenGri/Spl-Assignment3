package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.RegistrationSystem.Database;
import bgu.spl.net.impl.RegistrationSystem.Session;
import bgu.spl.net.impl.RegistrationSystem.User;
import bgu.spl.net.impl.BGRSServer.MessagingProtocolImpl;

public class LogoutMessage implements Message {

    public LogoutMessage() {
    }

    @Override
    public Message execute(Database database, Session session) {////logout a user from the server
        User user = session.getUser();
        //checks if the user is logged in
        if (user != null && user.logout()) {

            session.setUser(null);
            session.setShouldLogout(true);
            return new AckMessage((short) 4, null);

        }
        return new ErrorMessage((short) 4);
    }
}
