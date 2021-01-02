package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.*;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private  short opcode=-1;
    private final ByteBuffer opcodeBuffer = ByteBuffer.allocate(2);
    private final ByteBuffer courseNum = ByteBuffer.allocate(2);
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int zeroCounter = 0;

    @Override
    public Message decodeNextByte(byte nextByte) {

        if (opcode==-1) {//we read the opcode
            opcodeBuffer.put(nextByte);
            if (!opcodeBuffer.hasRemaining()) { //we read 2 bytes
                opcodeBuffer.flip();
                opcode=opcodeBuffer.getShort();

                //we are reading Logout Message
                if (opcode == 4) {
                    clearAll();
                    return new LogoutMessage();
                }
                //we are reading MyCourses Message
                else if (opcode== 11) {
                    clearAll();
                    return new MyCoursesMessage();
                }
            }
            return null;
        }

        //we are reading AdminReg Message
        if (opcode == 1) {
            return decodeNextByteAdminReg(nextByte);
        }

        //we are reading StudentReg Message
        else if (opcode == 2) {
            return decodeNextByteStudentReg(nextByte);
        }

        //we are reading LOGIN Message
        else if (opcode == 3) {
            return decodeNextByteLOGIN(nextByte);
        }

        //we are reading CourseReg Message
        else if (opcode == 5) {
            return decodeNextByteCourseReg(nextByte);
        }

        //we are reading KdamCheck Message
        else if (opcode== 6) {
            return decodeNextByteKdamCheck(nextByte);
        }

        //we are reading CourseStat Message
        else if (opcode== 7) {
            return decodeNextByteCourseStat(nextByte);
        }

        //we are reading StudentStat Message
        else if (opcode == 8) {
            return decodeNextByteStudentStat(nextByte);
        }

        //we are reading IsRegistered Message
        else if (opcode== 9) {
            return decodeNextByteIsRegistered(nextByte);
        }

        //we are reading Unregister Message
        else if (opcode== 10) {
            return decodeNextByteUnregister(nextByte);
        }


/*
        //we are reading Ack Message
        else if (opcode.getShort() == 12) {//todo needed?
            return decodeNextByteAckMessage(nextByte);
        }

        //we are reading Error Message
        else if (opcode.getShort() == 13) {//todo needed?
            return decodeNextByteErrorMessage(nextByte);
        }

 */
        return null;
    }

    public Message decodeNextByteAdminReg(byte nextByte) {
        if (nextByte == 0) {
            if (zeroCounter == 0) {
                zeroCounter++;
            } else {
                String decodedString = new String(bytes, 0, len, StandardCharsets.UTF_8);
                String[] splitString = decodedString.split("\0");
                String username = splitString[0];
                String password = splitString[1];
                clearAll();
                return new AdminRegMessage(username, password);
            }
        }
        pushByte(nextByte);
        return null;
    }


    public Message decodeNextByteStudentReg(byte nextByte) {
        if (nextByte == 0) {
            if (zeroCounter == 0) {
                zeroCounter++;
            } else {
                String decodedString = new String(bytes, 0, len, StandardCharsets.UTF_8);
                String[] splitString = decodedString.split("\0");
                String username = splitString[0];
                String password = splitString[1];
                clearAll();
                return new StudentRegMessage(username, password);
            }
        }
        pushByte(nextByte);
        return null;
    }


    public Message decodeNextByteLOGIN(byte nextByte) {
        if (nextByte == 0) {
            if (zeroCounter == 0) {
                zeroCounter++;
            } else {
                String decodedString = new String(bytes, 0, len, StandardCharsets.UTF_8);
                String[] splitString = decodedString.split("\0");
                String username = splitString[0];
                String password = splitString[1];
                clearAll();
                return new LoginMessage(username, password);
            }
        }
        pushByte(nextByte);
        return null;
    }

    public Message decodeNextByteCourseReg(byte nextByte) {
        if (courseNum.hasRemaining()) {
            courseNum.put(nextByte);
            if (!courseNum.hasRemaining()) { //we read 2 bytes
                courseNum.flip();
                CourseRegMessage output = new CourseRegMessage(courseNum.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }

    public Message decodeNextByteKdamCheck(byte nextByte) {
        if (courseNum.hasRemaining()) {
            courseNum.put(nextByte);
            if (!courseNum.hasRemaining()) { //we read 2 bytes
                courseNum.flip();
                KdamCheckMessage output = new KdamCheckMessage(courseNum.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }

    public Message decodeNextByteCourseStat(byte nextByte) {
        if (courseNum.hasRemaining()) {
            courseNum.put(nextByte);
            if (!courseNum.hasRemaining()) { //we read 2 bytes
                courseNum.flip();
                CourseStatMessage output = new CourseStatMessage(courseNum.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }

    public Message decodeNextByteStudentStat(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == 0) {
            String studentName = popString();
            clearAll();
            return new StudentStatMessage(studentName);
        }
        pushByte(nextByte);
        return null; //not a line yet
    }

    public Message decodeNextByteIsRegistered(byte nextByte) {
        if (courseNum.hasRemaining()) {
            courseNum.put(nextByte);
            if (!courseNum.hasRemaining()) { //we read 2 bytes
                courseNum.flip();
                IsRegisteredMessage output = new IsRegisteredMessage(courseNum.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }

    public Message decodeNextByteUnregister(byte nextByte) {
        if (courseNum.hasRemaining()) {
            courseNum.put(nextByte);
            if (!courseNum.hasRemaining()) { //we read 2 bytes and therefore can take the length
                courseNum.flip();
                UnregisterMessage output = new UnregisterMessage(courseNum.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }
/*
    public Message decodeNextByteAckMessage(byte nextByte) {
        if (messageOpcode.hasRemaining()) {
            messageOpcode.put(nextByte);
            if (!messageOpcode.hasRemaining()) { //we read 2 bytes and therefore can take the length
                messageOpcode.flip();
                AckMessage output = new AckMessage(messageOpcode.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }

    public Message decodeNextByteErrorMessage(byte nextByte) {
        if (messageOpcode.hasRemaining()) {
            messageOpcode.put(nextByte);
            if (!messageOpcode.hasRemaining()) { //we read 2 bytes and therefore can take the length
                messageOpcode.flip();
                ErrorMessage output = new ErrorMessage(messageOpcode.getShort());
                clearAll();
                return output;
            }
        }
        return null;
    }

 */
    //add next byte to bytes
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    //returns the fields to their default state
    private void clearAll() {
        len = 0;
        zeroCounter = 0;
        opcode=-1;
        opcodeBuffer.clear();
        courseNum.clear();
    }

    @Override
    public byte[] encode(Message message) {
        //todo check if needed
        /*
        if (message instanceof AdminRegMessage) {
            String userName = ((AdminRegMessage) message).getUsername();
            byte[] userNameBytes = userName.getBytes();
            String password = ((AdminRegMessage) message).getPassword();
            byte[] passwordBytes = password.getBytes();
            //initialize the result with the appropriate length
            // the appropriate length needs to be the sum of userName length, password length, 2 bytes for opcode and 2 bytes for 2 zero "0"
            byte[] result = new byte[userNameBytes.length + passwordBytes.length + 4];
            byte[] opcode = ByteBuffer.allocate(2).putInt(1).array();
            //add opcode to result
            System.arraycopy(opcode, 0, result, 0, opcode.length);
            //add userName to result
            System.arraycopy(userNameBytes, 0, result, opcode.length, userNameBytes.length);
            //add 0 to result
            result[opcode.length + userNameBytes.length] = 0;
            //add password to result
            System.arraycopy(userNameBytes, 0, result, opcode.length, userNameBytes.length);
            //add 0 to result
            result[opcode.length + userNameBytes.length + passwordBytes.length] = 0;
            return result;
        } else if (message instanceof StudentRegMessage) {

        } else if (message instanceof LoginMessage) {

        } else if (message instanceof LogoutMessage) {

        } else if (message instanceof CourseRegMessage) {

        } else if (message instanceof KdamCheckMessage) {

        } else if (message instanceof CourseStatMessage) {

        } else if (message instanceof StudentStatMessage) {

        } else if (message instanceof IsRegisteredMessage) {

        } else if (message instanceof UnregisterMessage) {

        } else if (message instanceof MyCoursesMessage) {

        } else*/


        byte[] opcode = createOpcode(message);
        byte[] MessageOpcode = createMessageOpcode(message);
        //the size of the opcode and the size of the other message opcode
        int outputSize = 4;
        //we encode AckMessage
        if (message instanceof AckMessage) {
            String response = ((AckMessage<String>) message).getResponse();
            byte[] responseBytes = null;
            if (response != null) {
                responseBytes = response.getBytes();
                outputSize = outputSize + responseBytes.length;
            }
            outputSize++;//for the last "0"  byte
            byte[] output = new byte[outputSize];
            //add opcode to output
            System.arraycopy(opcode, 0, output, 0, opcode.length);
            //add other message opcode to output
            System.arraycopy(MessageOpcode, 0, output, opcode.length, MessageOpcode.length);
            //add the optional part at AckMessage
            if (responseBytes != null) {
                System.arraycopy(responseBytes, 0, output, opcode.length + MessageOpcode.length, responseBytes.length);
                //add last "0"
                System.arraycopy(shortToBytes((short) 0), 0, output, opcode.length + MessageOpcode.length+responseBytes.length, 1);
            }
            else {
                //add last "0"
                System.arraycopy(shortToBytes((short) 0), 0, output, opcode.length + MessageOpcode.length, 1);
            }
            return output;
        }
        //we encode errorMessage
        else{
            byte[] output = new byte[outputSize];
            //add opcode to output
            System.arraycopy(opcode, 0, output, 0, opcode.length);
            //add other message opcode to output
            System.arraycopy(MessageOpcode, 0, output, opcode.length, MessageOpcode.length);
            return output;
        }
    }
    //encode opcode for specific message
    private byte[] createOpcode(Message message) {
        if (message instanceof AckMessage) {
            return shortToBytes((short) 12);
        } else if (message instanceof ErrorMessage) {
            return shortToBytes((short) 13);
        }
        return null;
    }
    //encode other message opcode
    private byte[] createMessageOpcode(Message message) {
        Short MessageOpcode = ((ServerToClientMessage) message).getMessageOpcode();
        return shortToBytes(MessageOpcode);
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

}
