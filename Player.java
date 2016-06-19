/*
Joao Brandao
2015/2016



----- PLAYER -----

Tthis class allows that each player has its own properties:
- If it is his turn
- Id number
- Color for lines
- Color for Boxes
- Score

*/

import java.awt.Color;
import java.awt.*;
import java.net.*;
import java.io.*;

class Player implements Serializable{
	private static final long serialVersionUID = 1L;

	boolean active;
	int number;
	String name;
	Color color;
	Color color_box;
	int score;

	public Player(){
		active = false;
		score = 0;
		name = null;
	}

	public String toString(){
		String string = "Number: " + number + "\nName: " + name + "\nActive: " + active + "\nScore: " + score + "\n";
		return string;
	}
}
