/*
Joao Brandao
2015/2016



----- LINES -----

This class allows that each Line as its own properties:
- Order number
- Boolean flag to check if closed
- Number to set if horizontal ou vertical.

*/
import java.awt.Color;

public class Lines extends Master{
	private static final long serialVersionUID = 1L;

	int order;
	boolean connected;
	int type;				//horizontal (0) | vertical (1)

	public Lines(){
		super();
		connected = false;
		color = Color.WHITE;
	}

	public String toString(){
		String string = super.toString();

		string += "Connected: " + connected + "\nOrder: " + order;

		return string;
	}
}
