/*


2015/2016


----- PLAYER -----

Esta classe permite criar caracteristicas de cada jogador.

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
