/*
Joao Brandao
2015/2016



----- BOXES -----

This class allows that each Box has its own properties:
- Order number
- Boolean flag to check if closed
- Player number to save who have closed it
- Adjacent vertical and horizontal lines.

*/

import java.awt.Color;

public class Boxes extends Master{
	private static final long serialVersionUID = 1L;

	boolean closed;
	Lines horizontalLines[];
	Lines verticalLines[];
	int player;
	int order;

	public Boxes(){
		super();
		closed = false;
		color = Color.WHITE;
		horizontalLines = new Lines[2];
		verticalLines = new Lines[2];
	}

	//Method to check if box is closed
	public boolean checkState(){
		closed = true;
		for(int i = 0; i < horizontalLines.length; i++) {
			if(!horizontalLines[i].connected || !verticalLines[i].connected) {
				closed=false;
			}
		}
		return closed;
	}

	public String toString(){
		String string = null;
		string = super.toString() + "Order: " + order + "\nClosed: " + closed + "\nHorizontal Lines: " + horizontalLines[0].order + ", " + horizontalLines[1].order + "\nVertical Lines: " + verticalLines[0].order + ", " + verticalLines[1].order + "\n";

		for(int i = 0; i < horizontalLines.length; i++){
			string += "H Line " + horizontalLines[i].order + ": " + horizontalLines[i].connected + "\n";
			string += "V Line " + verticalLines[i].order + ": " + verticalLines[i].connected + "\n";
		}
		return string;
	}
}
