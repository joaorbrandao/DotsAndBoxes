/*



----- MAIN -----

Aqui e apresentada toda a logica da apresentacao assim
como toda a logica da aplicacao.

*/

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
//import org.apache.commons.lang.SerializationUtils;



public class Main extends JFrame implements MouseListener, MouseMotionListener, Serializable{
	private static final long serialVersionUID = 1L;

	//WINDOW
	public final int WINDOW_WIDTH = 500;	//Define a largura da janela
	public final int WINDOW_HEIGHT = 600;	//Define a altura da janela
	private Dimension dim;					//Dimensoes da janela
	private int windowCenterX;				//Define o centro da janela em X
	private int windowCenterY;				//Define o centro da janela em Y

	//BOARD
	private int boardSide;					//Define o tamanho de cada lado do "tabuleiro" ficticio para posicionar as formas
	private int boardSpace;					//Define o espacamento para desenhar um novo ponto Largura do Ponto + Largura da Linha

	//DOTS
	public Master dots[];					//Vetor para guardar todos os Dots
	public final int DOT_NUMBER = 5;		//Define o numero de pontos do jogo
	public final int DOT_SPACE =  20;		//Define o espaco entre cada ponto
	public final int DOT_RADIUS = 6;		//Define o raio de cada ponto

	//BOXES
	public Boxes boxes[];					//Vetor para guardar todas as Boxes

	//LINES
	public int LINE_WIDTH;					//Define o comprimento das linhas
	public Lines horizontalLines[];		//Vetor para guardar todas as Lines horizontais
	public Lines verticalLines[];			//Vetor para guardar todas as Lines verticais

	//PLAYERS
	public Player players[];

	public final Color PLAYER_ONE_COLOR = new Color(255, 171, 0);		//Define a cor das Lines do jogador 1
	public final Color PLAYER_ONE_COLOR_BOX = new Color(255, 205, 102);	//Define a cor das Boxes do jogador 1
	public final Color PLAYER_TWO_COLOR = new Color(0, 162, 255);		//Define a cor das Lines do jogador 2
	public final Color PLAYER_TWO_COLOR_BOX = new Color(130, 209, 255);	//Defina a cor das Boxes do jogador 2

	public final int NUMBER_OF_PLAYERS = 2;

	public int activePlayer;											//Para ir alterando qual e o jogador que este a jogar
	public final int PLAYER_ONE = 1;									//Define um numero para o jogador 1
	public final int PLAYER_TWO = 2;									//Define um numero para o jogador 2
	public int playerOneScore;											//Contador das Boxes fechadas pelo PLAYER_ONE
	public int playerTwoScore;											//Contador das Boxes fechadas pelo PLAYER_TWO

	public boolean gameOver;		//To check if game ended

	//MOUSE
	public int mouseX;
	public int mouseY;
	public int clickX;
	public int clickY;

	//CONNECTIONS
	private final String IP_ADDRESS = "localhost";
	private final int PORT = 5000;
	static Thread one = new Thread();
	private int clientId = 0;
	static Socket client;
	static ObjectInputStream oosIn;
	static ObjectOutputStream oosOut;

	public FirstPage firstPage;
	public Main(String p){
		super("Dots & Boxes"); //Escreve o nome do jogo na parte superior da tabela


		loadPlayers();
		players[0].name = p;
		players[1].name = "Waiting for an opponent...";

		//Cria a janela de jogo
		createWindow();

		//INICIAR DEFINICOES E TODAS AS VARIAVEIS
		addMouseListener(this);
		addMouseMotionListener(this);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});

		newGame();
		connectToServer();

		//Mostra a janela
		setVisible(true);
	}
	public Main(boolean clean){
		if(!clean){
			//System.out.println("new Main NOT clean!");
			loadPlayers();
			createWindow();
			addMouseListener(this);
			addMouseMotionListener(this);
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent we) {
					dispose();
				}
			});
			newGame();
			setVisible(false);
		}else{
			//System.out.println("new Main clean!");
		}
	}


	//Metodo para criar a janela de jogo
	private void createWindow(){
		//Define o tamanho da janela assim como a cor de fundo
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setBackground(Color.WHITE);
	}

	//Metodo para iniciar os valores
	private void initializeFields(){
		dim = getSize();
		windowCenterX = dim.width / 2;				//Obtem o centro em X
		windowCenterY = (dim.height - 100) / 2;		//Obtem o centro em Y, mas deixando parte inferior da janela para informacoes do jogo

		//Define o tabuleiro de jogo
		boardSide = DOT_RADIUS * DOT_NUMBER + (DOT_NUMBER - 1) * DOT_SPACE;
		boardSpace = DOT_RADIUS + DOT_SPACE;

		LINE_WIDTH = DOT_SPACE - 2;					//Tira um pixel a cada extremidade da linha

		activePlayer = PLAYER_ONE;
		playerOneScore = 0;
		playerTwoScore = 0;

		gameOver = false;
	}

	//Metodo para comecar novo jogo
	private void newGame(){
		initializeFields();
		loadDots();
		loadLines();
		loadBoxes();
	}



	/*
	  --------------------------------------
	  -                DOTS                -
	  --------------------------------------
	*/
	private void loadDots(){
		dots = new Master[DOT_NUMBER * DOT_NUMBER];

		/*System.out.println("\n--------------------------");
		System.out.println("-          DOTS          -");
		System.out.println("--------------------------");*/
		for(int rows = 0; rows < DOT_NUMBER; rows++){
			for(int cols = 0; cols < DOT_NUMBER; cols++){
				Master dot = new Master();
				dot.locationX = windowCenterX - boardSide/2 + cols * boardSpace;
				dot.locationY = windowCenterY - boardSide/2 + rows * boardSpace;
				dot.width = DOT_RADIUS;
				dot.height = DOT_RADIUS;
				dot.color = Color.BLACK;
				dot.createShape();
				int i = rows * DOT_NUMBER + cols;

				dots[i] = dot;
				//System.out.println(dots[i].toString());
			}
		}
	}
	private void paintDots(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		for(int i = 0; i < dots.length; i++){
			g.setColor(dots[i].color);
			g2.draw(dots[i].shape);
			g2.fill(dots[i].shape);
		}
	}

	/*
	  --------------------------------------
	  -               LINES                -
	  --------------------------------------
	*/
	private void loadLines(){
		//Como nos dedos do homem! 5 dedos ha 4 espacos entre eles!
		//Assim, em DOT_NUMBER pontos ha DOT_NUMBER-1 espacos
		horizontalLines = new Lines[(DOT_NUMBER-1)*DOT_NUMBER];
		verticalLines = new Lines[(DOT_NUMBER-1)*DOT_NUMBER];

		/*System.out.println("\n--------------------------------------");
		System.out.println("-          HORIZONTAL LINES          -");
		System.out.println("--------------------------------------");*/
		//Carrega as linhas horizontais
		for(int rows = 0; rows < DOT_NUMBER; rows++){
			for(int cols = 0; cols < (DOT_NUMBER-1); cols++){
				Lines line = new Lines();

				line.locationX = windowCenterX - (boardSide/2) + (cols * boardSpace) + DOT_RADIUS + 1;
				line.locationY = windowCenterY - boardSide/2 + rows * boardSpace;
				line.width = LINE_WIDTH;
				line.height = DOT_RADIUS;
				line.color = Color.WHITE;
				line.type = 0;
				line.createShape();
				int i = rows * (DOT_NUMBER-1) + cols;
				line.order = i;
				horizontalLines[i] = line;

				//System.out.println(horizontalLines[i].toString());
			}
		}

		/*System.out.println("\n------------------------------------");
		System.out.println("-          VERTICAL LINES          -");
		System.out.println("------------------------------------");*/
		//Carrega as linhas verticais
		for(int rows = 0; rows < DOT_NUMBER-1; rows++){
			for(int cols = 0; cols < DOT_NUMBER; cols++){
				Lines line = new Lines();
				line.locationX = windowCenterX - boardSide/2 + cols * boardSpace;
				line.locationY = windowCenterY - boardSide/2 + rows * boardSpace + DOT_RADIUS + 1;
				line.width = DOT_RADIUS;
				line.height = LINE_WIDTH;
				line.color = Color.WHITE;
				line.type = 1;
				line.createShape();
				int i = rows * (DOT_NUMBER) + cols;
				line.order = i;
				verticalLines[i] = line;

				//System.out.println(verticalLines[i].toString());
			}
		}
	}
	private void paintLines(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		for(int i = 0; i < horizontalLines.length; i++){
			if(horizontalLines[i].connected == false){
				if(horizontalLines[i].shape.contains(mouseX, mouseY)){
					if(players[0].active){
						horizontalLines[i].color = PLAYER_ONE_COLOR;
					}else{
						horizontalLines[i].color = PLAYER_TWO_COLOR;
					}
				}else{
					horizontalLines[i].color = Color.WHITE;
				}
			}
			g.setColor(horizontalLines[i].color);
			g2.draw(horizontalLines[i].shape);
			g2.fill(horizontalLines[i].shape);
		}
		for(int i = 0; i < verticalLines.length; i++){
			if(verticalLines[i].connected == false){
				if(verticalLines[i].shape.contains(mouseX, mouseY)){
					if(players[0].active){
						verticalLines[i].color = PLAYER_ONE_COLOR;
					}else{
						verticalLines[i].color = PLAYER_TWO_COLOR;
					}
				}else{
					verticalLines[i].color = Color.WHITE;
				}
			}
			g.setColor(verticalLines[i].color);
			g2.draw(verticalLines[i].shape);
			g2.fill(verticalLines[i].shape);
		}
	}

	/*
	  --------------------------------------
	  -                BOXES               -
	  --------------------------------------
	*/
	private void loadBoxes(){
		boxes = new Boxes[(DOT_NUMBER-1) * (DOT_NUMBER-1)];

		/*System.out.println("\n---------------------------");
		System.out.println("-          BOXES          -");
		System.out.println("---------------------------");*/
		for(int rows = 0; rows < DOT_NUMBER-1; rows++){
			for(int cols = 0; cols < DOT_NUMBER-1; cols++){
				Boxes box = new Boxes();
				box.locationX = windowCenterX - boardSide/2 + cols * boardSpace + DOT_RADIUS + 1;
				box.locationY = windowCenterY - boardSide/2 + rows * boardSpace + DOT_RADIUS + 1;
				box.width = LINE_WIDTH;
				box.height = LINE_WIDTH;
				box.color = Color.WHITE;
				box.createShape();

				int i = rows * (DOT_NUMBER-1) + cols;
				int j = rows * (DOT_NUMBER) + cols;
				box.order = i;
				//Atribuicao das linhas adjacentes a cada caixa
				box.horizontalLines[0] = horizontalLines[i];
				box.horizontalLines[1] = horizontalLines[i + (DOT_NUMBER - 1)];
				box.verticalLines[0] = verticalLines[j];
				box.verticalLines[1] = verticalLines[j + 1];
				boxes[i] = box;

				//System.out.println(boxes[i].toString());
			}
		}
	}
	private void paintBoxes(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		for(int i = 0; i < boxes.length; i++){
			g.setColor(boxes[i].color);
			g2.draw(boxes[i].shape);
			g2.fill(boxes[i].shape);
		}
	}




	/*
	  --------------------------------------
	  -              PLAYERS               -
	  --------------------------------------
	*/
	private void loadPlayers(){
		players = new Player[NUMBER_OF_PLAYERS];
		/*System.out.println("\n--------------------------");
		System.out.println("-        PLAYERS         -");
		System.out.println("--------------------------");*/

		for(int i = 0; i < NUMBER_OF_PLAYERS; i++){
			Player player = new Player();
			player.number = i + 1;

			if(player.number == 1){
				player.active = true;
				player.color = PLAYER_ONE_COLOR;
				player.color_box = PLAYER_ONE_COLOR_BOX;
			}
			if(player.number == 2){
				player.active = false;
				player.color = PLAYER_TWO_COLOR;
				player.color_box = PLAYER_TWO_COLOR_BOX;
			}

			players[i] = player;
			//System.out.println(players[i].toString());
		}
	}

	public void endGame(){
		if(playerOneScore < playerTwoScore){
			JOptionPane.showMessageDialog(this, players[1].name + " Won!", "Game Over", JOptionPane.PLAIN_MESSAGE);
		}
		if(playerOneScore == playerTwoScore){
			JOptionPane.showMessageDialog(this, "It's a draw!", "Game Over", JOptionPane.PLAIN_MESSAGE);
		}
		if(playerOneScore > playerTwoScore){
			JOptionPane.showMessageDialog(this, players[0].name + " Won!", "Game Over", JOptionPane.PLAIN_MESSAGE);
		}

		setVisible(false);
		dispose();
		new FirstPage().setVisible(true);
	}


	//Metodo para desenhar a informacao do jogo
	private void drawInfoBox(Graphics g){
		Graphics2D g2 = (Graphics2D) g;

		g2.draw(new Rectangle2D.Double(20, 460, 460, 100));

		String status = null;
		if(players[0].active){//activePlayer == 1){
			status = "Current Player: " + players[0].name;
		}else{
			status = "Current Player: " + players[1].name;
		}
		String status2 = players[0].name + ": " + playerOneScore;
		String status3 = players[1].name + ": " + playerTwoScore;

		g.setColor(Color.BLACK);
		g.drawString(status, 30, 480);

		g.setColor(PLAYER_ONE_COLOR);
		g.drawString(status2, 30, 500);

		g.setColor(PLAYER_TWO_COLOR);
		g.drawString(status3, 30, 520);
	}




	/*
	  --------------------------------------
	  -               PAINT                -
	  --------------------------------------
	*/
	public void paint(Graphics g) {
		Image bufferImage = createImage(WINDOW_WIDTH, WINDOW_HEIGHT);
		Graphics bufferGraphics = bufferImage.getGraphics();

		drawInfoBox(bufferGraphics);
		paintDots(bufferGraphics);
		paintLines(bufferGraphics);
		paintBoxes(bufferGraphics);

		g.drawImage(bufferImage, 0, 0, null);
	}




	/*
	  --------------------------------------
	  -           MOUSE EVENTS             -
	  --------------------------------------
	*/
	public void mouseMoved(MouseEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		repaint();
	}
	public void mouseDragged(MouseEvent event) {
		mouseMoved(event);
	}

	public void mouseClicked(MouseEvent event) {
		if(players[clientId].active){
			System.out.println("It's your turn!");
			clickX = event.getX();
			clickY = event.getY();
			Protocolo a = new Protocolo();
			a.arg1 = (Object) clickX;
			a.arg2 = (Object) clickY;
			a.arg3 = (Object) mouseX;
			a.arg4 = (Object) mouseY;
			a.state = "TURN_TRUE";
			try{
				//oosOut.reset();
				a.envia(oosOut);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.out.println("It's NOT your turn!");
		}
	}
	public void mouseEntered(MouseEvent event) {
	}
	public void mouseExited(MouseEvent event) {
	}
	public void mousePressed(MouseEvent event){
	}
	public void mouseReleased(MouseEvent event){
	}




	/*
	  --------------------------------------
	  -           CONNECTIONS              -
	  --------------------------------------
	*/
	public void connectToServer(){
		try{
			client = new Socket(IP_ADDRESS, PORT);
			oosOut = new ObjectOutputStream(client.getOutputStream());
			oosIn = new ObjectInputStream(client.getInputStream());
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}

		try{
			clientId = oosIn.read();
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Success connecting to server..." + clientId);

		one = new Thread(){
			public void run() {
				Protocolo info = new Protocolo();
				try{
					//----- MSG ID -----start
					info.arg1 = /*(String)*/ players[0].name;
					System.out.println(players[0].name);

					//oosOut.reset();
					info.envia(oosOut);
					//info = info.recebe(oosIn);
					info = Protocolo.recebe(oosIn);
					players = (Player []) info.arg1;
					System.out.println("All players loaded!\n" + players[0].toString() + "\n" + players[1].toString());

					repaint();
					//----- MSG ID -----end

					//----- MSG PLAY -----start
					while(!gameOver){
						if(!players[clientId].active){
							System.out.println("It's NOT your turn!");
							try{
								info = new Protocolo();
								info.state = "TURN_FALSE";
								//oosOut.reset();
								info.envia(oosOut);
							}catch(Exception e){
								e.printStackTrace();
							}
							sleep(1000);
						}else{
							System.out.println("It's your turn!");
						}

						try{
							info = new Protocolo();
							info = Protocolo.recebe(oosIn);
						}catch(Exception e){
							e.printStackTrace();
						}
						if(info.state.equals("OK")){
							Main controller = new Main(true);
							controller = (Main) info.arg1;

							dots = controller.dots;
							players = controller.players;
							horizontalLines = controller.horizontalLines;
							verticalLines = controller.verticalLines;
							boxes = controller.boxes;
							gameOver = controller.gameOver;
							playerOneScore = controller.playerOneScore;
							playerTwoScore = controller.playerTwoScore;

							//System.out.println("CTRL\n" + controller.players[0].toString() + controller.players[1].toString());
							System.out.println("PLAYERS\n" + players[0].toString() + players[1].toString());
							System.out.println("-----PLAYER " + clientId + ": " + players[clientId].active);
							//System.out.println("Game Over? " + gameOver);

						}
						if(info.state.equals("NOT_OK")){
							System.out.println("Something is going wrong!");
						}
						repaint();
					}
					//----- MSG PLAY -----end



					//----- MSG END -----start
					info = new Protocolo();
					info = Protocolo.recebe(oosIn);
					Main controller = new Main(true);
					controller = (Main) info.arg1;

					dots = controller.dots;
					players = controller.players;
					horizontalLines = controller.horizontalLines;
					verticalLines = controller.verticalLines;
					boxes = controller.boxes;
					gameOver = controller.gameOver;
					playerOneScore = controller.playerOneScore;
					playerTwoScore = controller.playerTwoScore;

					System.out.println("CTRL\n" + controller.players[0].toString() + controller.players[1].toString());
					System.out.println("PLAYERS\n" + players[0].toString() + players[1].toString());
					System.out.println("Game Over? " + gameOver);

					repaint();

					endGame();
					//----- MSG END -----end

				}catch(Exception er){
					System.err.println(er.getMessage());
				}
			}
		};

		if(!one.isAlive())
		one.start();
	}
}
