package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;
import bgu.spl.net.impl.RegistrationSystem.Course;
import bgu.spl.net.impl.RegistrationSystem.Database;

public class KdamCheckMessage implements Message {
    private int courseNum;
    public KdamCheckMessage(int courseNum){

        this.courseNum=courseNum;
    }

    @Override
    public Message execute(Database database) {
        Course course = database.getCourses().get(courseNum);
        if (course!=null){
            return new AckMessage((short)6,course.getKdamCourses());
        }
        return new ErrorMessage((short)6);
    }
}
