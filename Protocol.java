/*
Joao Brandao
2015/2016



----- PROTOCOL -----

This class allows the communication between
clients and server.

*/

import java.awt.*;
import java.net.*;
import java.io.*;

public class Protocol implements Serializable{
    private static final long serialVersionUID = 1L;

    Object arg1 = null;
    Object arg2 = null;
    Object arg3 = null;
    Object arg4 = null;
    String state = null;

    //Method to send info
    public void send(ObjectOutputStream out) throws Exception
    {
        out.writeObject(this);
        out.flush();
        out.reset();
    }

    //Method to receive info
    public static Protocol receive(ObjectInputStream in) throws Exception
    {
        return (Protocol)in.readObject();
    }
}
