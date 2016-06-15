/*

2015/2016


----- FIRST PAGE -----

Esta classe permite apresentar uma primeira janela para
serem inseridos os nomes dos jogadores e selecionado o
numero de pontos no tabuleiro.

*/



import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;


public class FirstPage extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	//Labels
	private JLabel label0;
	private JLabel label1;
	private JLabel label2;
	
	
	//Text Fields
	private JTextField playerOneName;
	private JTextField playerTwoName;
	
	//Button
	private JButton validateNames;
	
	public JPanel panel;
	public Main auxMain;
	
	public String playerName;
	
	public FirstPage(){
		super("Dots & Boxes"); //Escreve o nome do jogo na parte superior da tabela
		

		//Define o tamanho da janela assim como a cor de fundo
		setSize(500, 600);
       	setResizable(false);
       	setBackground(Color.WHITE);
    
       	//Dots & Boxes Label
       	label0 = new JLabel("Dots & Boxes");
       	label0.setFont(new Font(label0.getName(), Font.PLAIN, 80));
       	
       	//Player 1 Label + Text Field
       	label1 = new JLabel("Name: ");
       	playerOneName = new JTextField(35);
       	playerOneName.setEditable(true);       	
       	
       	//Start Game button
       	validateNames = new JButton("Start Game!");
       	validateNames.addActionListener(this);
       	
       	//Cria painel para adicionar componentes a janela
       	panel = new JPanel();
       	panel.setBackground(Color.WHITE);
       	panel.setLayout(new FlowLayout());
       	panel.add(label0);
       	panel.add(label1);
       	panel.add(playerOneName);
       	panel.add(validateNames);
       	
       	add(panel);		//Adiciona o painel a janela
       	
       	addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we) {
            	dispose();
			}
		});
       	
		//Mostra a janela
		setVisible(true);
	}
	
	//Metodo para iniciar novo jogo
	public void newGame(){
		setVisible(false);
		dispose();
		new Main(playerName).setVisible(true);
	}
	
    
	
	/*
	  --------------------------------------
	  -              EVENTS                -
	  --------------------------------------
	*/
	//JButton EVENTS
	public void actionPerformed(ActionEvent arg0) {
		if(playerOneName.getText().equals("") == false){
				playerName = playerOneName.getText();
				
				newGame();
		}
		//System.out.println(playerOneName.getText() + "\n" + players[0].toString() + "\n" + playerTwoName.getText() + "\n" + players[1].toString());
	}
	
	
	
	/*
	  --------------------------------------
	  -                MAIN                -
	  --------------------------------------
	*/
	public static void main(String[] args) {
		 new FirstPage().setVisible(true);
  	}
}
