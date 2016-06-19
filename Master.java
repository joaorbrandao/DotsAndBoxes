/*
Joao Brandao
2015/2016



----- MASTER -----

This class allows to create shapes: squares (Dots),
rectangles (Lines) e bigger squares (Boxes).

*/

import java.awt.Color;
import java.awt.Rectangle;
import java.io.*;

class Master implements Serializable{
	private static final long serialVersionUID = 1L;

	//Campos
	Rectangle shape;
	Color color;	//Define object color
	int locationX;
	int locationY;
	int width;
	int height;

	public Master(){
		//initialize fields
		color = Color.BLACK;
		locationX = 0;
		locationY = 0;
		width = 0;
		height = 0;
	}

	//Method to create a rectangle (Dots, Line ou Box)
	public void createShape(){
		shape = new Rectangle(locationX, locationY, width, height);
	}

	public String toString(){
		return "\nColor: " + color.toString() + "\nLocation X: " + locationX + "\nLocation Y: " + locationY + "\nWidth: " + width + "\nHeight: " + height + "\n";
	}


}
