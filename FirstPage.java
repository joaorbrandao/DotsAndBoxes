/*

2015/2016


----- FIRST PAGE -----

Esta classe permite apresentar uma primeira janela para
serem inseridos os nomes dos jogadores e selecionado o
numero de pontos no tabuleiro.

*/



import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;


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

	private MyDialog md = new MyDialog(this);
	private FirstPage frame;
	public FirstPage(){
		super("Dots & Boxes"); //Escreve o nome do jogo na parte superior da tabela


		//Define o tamanho da janela assim como a cor de fundo
		setSize(500, 600);
		setResizable(false);
		setBackground(Color.WHITE);
		frame = this;
		createMenu();

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


	public void createMenu(){
		// Creates a menubar for a JFrame
		JMenuBar menuBar = new JMenuBar();

		// Add the menubar to the frame
		setJMenuBar(menuBar);

		// Define and add two drop down menu to the menubar
		JMenu fileMenu = new JMenu("File");
		JMenu scoresMenu = new JMenu("Scores");
		menuBar.add(fileMenu);
		menuBar.add(scoresMenu);

		// Create and add simple menu item to one of the drop down menu
		JMenuItem exitAction = new JMenuItem("Exit");
		JMenuItem lastTenAction = new JMenuItem("Last 10");

		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		scoresMenu.add(lastTenAction);

		exitAction.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				dispose();
			}
		});
		lastTenAction.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				//Show last 10 Scores
				try(FileReader fr = new FileReader("scores.log");
					BufferedReader br = new BufferedReader(fr)){
					String line = null;
					String auxLine[] = new String[10];
					int lineNumber = 0;
					int position = 0;
					while ((line = br.readLine()) != null) {
						lineNumber++;
						if(lineNumber % 2 == 0){
							auxLine[position] = line;
							position++;
						}
						if(lineNumber == 10)
							break;
						System.out.println(line);
					}
					md.setDialog(auxLine);
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		});
	}

	/*
	  --------------------------------------
	  -			  EVENTS				-
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
	  -				MAIN				-
	  --------------------------------------
	*/
	public static void main(String[] args) {
		 new FirstPage().setVisible(true);
  	}
}

class MyDialog extends JDialog{
	private static final long serialVersionUID = 1L;

	public MyDialog(JFrame frame) {
		super(frame, "Scores", false);

		setResizable(false);
		setLocationRelativeTo(frame);
		setVisible(false);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				dispose();
			}
		});
	}

	public void setDialog(String text[]){

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10,1));

		if(text[0] != null){
			for(int i = 0; i < text.length; i++){
				if(text[i] != null){
					JLabel labelText = new JLabel(text[i]);
					labelText.setHorizontalAlignment(JLabel.CENTER);
					labelText.setFont(new Font(labelText.getName(), Font.PLAIN, 12));
					panel.add(labelText);
				}
			}
		}else{
			JLabel labelText = new JLabel("No results to show! :)");
			labelText.setHorizontalAlignment(JLabel.CENTER);
			labelText.setFont(new Font(labelText.getName(), Font.PLAIN, 12));
			panel.add(labelText);
		}


		add(panel);
		pack();

		setVisible(true);
	}
}
