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

    public void envia(ObjectOutputStream out) throws Exception
    {
        out.writeObject(this);
        out.flush();
        out.reset();
    }

    public static Protocol recebe(ObjectInputStream in) throws Exception
    {
        return (Protocol)in.readObject();
    }
}
