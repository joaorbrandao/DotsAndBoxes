/*


----- MASTER -----

Esta classe permite criar as formas desejadas: pequenos quadrados (Dots),
rectanculos (Lines) e quadrados maiores (Boxes).

*/

import java.awt.Color;
import java.awt.Rectangle;
import java.io.*;

class Master implements Serializable{
	private static final long serialVersionUID = 1L;

	//Campos
	Rectangle shape;
	Color color;	//Define a cor do objeto a desenhar
	int locationX;
	int locationY;
	int width;
	int height;

	public Master(){
		//Inicia todos os campos
		color = Color.BLACK;
		locationX = 0;
		locationY = 0;
		width = 0;
		height = 0;
	}

	//Metodo para criar o Retangulo (Dots, Line ou Box)
	public void createShape(){
		shape = new Rectangle(locationX, locationY, width, height);
	}

	public String toString(){
		return "\nColor: " + color.toString() + "\nLocation X: " + locationX + "\nLocation Y: " + locationY + "\nWidth: " + width + "\nHeight: " + height + "\n";
	}


}
