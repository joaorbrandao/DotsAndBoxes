/*



----- BOXES -----

Esta classe permite que cada Box tenha as suas proprias
caracteristicas como um numero que informa a sua ordem,
uma variavel boolean para identificar se a Box se encontra
fechada, o jogador que a fechou e dois vetores para armazenar
as linhas horizontais e verticais a ela adjacentes.

*/

import java.awt.Color;

public class Boxes extends Master{
	private static final long serialVersionUID = 1L;

	boolean closed;				//Para registar se a caixa foi fechada
	Lines horizontalLines[];	//Para registar as linhas horizontais adjacentes a caixa
	Lines verticalLines[];		//Para registar as linhas verticais adjacentes a caixa
	int player;					//Para registar o jogador que fechou a caixa
	int order;					//Para ordenar as caixas

	public Boxes(){
		super();
		closed = false;
		color = Color.WHITE;
		horizontalLines = new Lines[2];
		verticalLines = new Lines[2];
	}

	//Metodo para verificar o estado da caixa e fechar caso nao esteja fechada
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
