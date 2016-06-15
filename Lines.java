/*


----- LINES -----

Esta classe permite que cada Line tenha as suas proprias
caracteristicas como um numero que informa a sua ordem,
uma variavel boolean para identificar se a Line se encontra
conectada e o tipo, podendo este ultimo ser horizontal ou
vertical.

*/
import java.awt.Color;

public class Lines extends Master{
	private static final long serialVersionUID = 1L;

	int order;				//Para ordenar as linhas para debug na atribuicao destas as caixas
	boolean connected;		//Para registar se a linha est selecionada
	int type;				//Para identificar se e linha horizontal (0) ou vertical (1)

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
