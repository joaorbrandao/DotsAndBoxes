/*
JoaoBrandao
2015/2016


----- MAIN -----

This class representes the main game frame. Here the
players interact with the GUI to communicate with the
server.

*/

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.sound.sampled.*;



public class Main extends JFrame implements MouseListener, MouseMotionListener, Serializable{
	private static final long serialVersionUID = 1L;

	//WINDOW
	public final int WINDOW_WIDTH = 500;	//Define frame width
	public final int WINDOW_HEIGHT = 600;	//Define frame height
	private Dimension dim;					//Frame dimensions
	private int windowCenterX;				//Define frame center X
	private int windowCenterY;				//Define frame center Y

	//BOARD
	private int boardSide;					//Define the size of the board to place the shapes
	private int boardSpace;					//Define ths size of the space after which other dots should be draw

	//DOTS
	public Master dots[];					//Dots array
	public final int DOT_NUMBER = 2;		//Define number of dots
	public final int DOT_SPACE =  20;		//Define space between dots
	public final int DOT_RADIUS = 6;		//Define dot radius

	//BOXES
	public Boxes boxes[];					//Boxes array

	//LINES
	public int LINE_WIDTH;					//Define line width
	public Lines horizontalLines[];			//Horizontal Lines array
	public Lines verticalLines[];			//Vertical Lines array

	//PLAYERS
	public Player players[];

	public final Color PLAYER_ONE_COLOR = new Color(255, 171, 0);		//Define Player 1 Line color
	public final Color PLAYER_ONE_COLOR_BOX = new Color(255, 205, 102);	//Define Player 1 Boxes color
	public final Color PLAYER_TWO_COLOR = new Color(0, 162, 255);		//Define Player 2 Line color
	public final Color PLAYER_TWO_COLOR_BOX = new Color(130, 209, 255);	//Define Player 2 Boxes color

	public final int NUMBER_OF_PLAYERS = 2;		//Set the maximum players

	public int activePlayer;											//To change turns
	public final int PLAYER_ONE = 1;									//Define player 1 number
	public final int PLAYER_TWO = 2;									//Define player 2 number
	public int playerOneScore;											//To keep player 1 score up to date
	public int playerTwoScore;											//To keep player 2 score up to date

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

	//sound
	private final String SOUND_FILE_PATH = "./sound/win.wav";
	public Date date = new Date();
	private String SCORES_FILE_PATH = "scoresClt.log";

	public FirstPage firstPage;
	public Main(String p){
		super("Dots & Boxes");


		loadPlayers();
		players[0].name = p;
		players[1].name = "Waiting for an opponent...";

		//Create game frame
		createWindow();

		addMouseListener(this);
		addMouseMotionListener(this);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});

		newGame();
		connectToServer();

		setVisible(true);
	}
	public Main(boolean clean){
		if(!clean){
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


	//Method to create game frame
	private void createWindow(){
		//Define frame size and color
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setBackground(Color.WHITE);
	}

	//Method to initialize fields
	private void initializeFields(){
		dim = getSize();
		windowCenterX = dim.width / 2;				//get X center coordinates
		windowCenterY = (dim.height - 100) / 2;		//get Y center coordinates livind space to game info

		//Define board game
		boardSide = DOT_RADIUS * DOT_NUMBER + (DOT_NUMBER - 1) * DOT_SPACE;
		boardSpace = DOT_RADIUS + DOT_SPACE;

		LINE_WIDTH = DOT_SPACE - 2;					//take a pixel for each side of the line

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
		horizontalLines = new Lines[(DOT_NUMBER-1)*DOT_NUMBER];
		verticalLines = new Lines[(DOT_NUMBER-1)*DOT_NUMBER];

		/*System.out.println("\n--------------------------------------");
		System.out.println("-          HORIZONTAL LINES          -");
		System.out.println("--------------------------------------");*/
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
				//Set adjacent lines of each box
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
			if(clientId == 1){
				//Play victory music
				AudioInputStream music;
				try{
					URL link = this.getClass().getClassLoader().getResource(SOUND_FILE_PATH);
					music = AudioSystem.getAudioInputStream(link);
					Clip song = AudioSystem.getClip();
					song.open(music);
					song.start();
				}catch(UnsupportedAudioFileException er){
					er.printStackTrace();
				}catch(IOException er){
					er.printStackTrace();
				}catch(LineUnavailableException er){
					er.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(this, players[1].name + " Won!", "Game Over", JOptionPane.PLAIN_MESSAGE);
		}
		if(playerOneScore == playerTwoScore){
			JOptionPane.showMessageDialog(this, "It's a draw!", "Game Over", JOptionPane.PLAIN_MESSAGE);
		}
		if(playerOneScore > playerTwoScore){
			if(clientId == 0){
				//Play victory music
				AudioInputStream music;
				try{
					URL link = this.getClass().getClassLoader().getResource(SOUND_FILE_PATH);
					music = AudioSystem.getAudioInputStream(link);
					Clip song = AudioSystem.getClip();
					song.open(music);
					song.start();
				}catch(UnsupportedAudioFileException er){
					er.printStackTrace();
				}catch(IOException er){
					er.printStackTrace();
				}catch(LineUnavailableException er){
					er.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(this, players[0].name + " Won!", "Game Over", JOptionPane.PLAIN_MESSAGE);
		}

		//----- LOG SCORE -----start
		String s = "[" + date.toString() + "]\n" +  players[0].name + " " + playerOneScore + "-" + playerTwoScore + " " + players[1].name;

		try(FileWriter fw = new FileWriter(SCORES_FILE_PATH, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw))
		{
			out.println(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//----- LOG SCORE -----end

		setVisible(false);
		dispose();
		new FirstPage().setVisible(true);
	}


	//Method to draw game info
	private void drawInfoBox(Graphics g){
		Graphics2D g2 = (Graphics2D) g;

		g2.draw(new Rectangle2D.Double(20, 460, 460, 100));

		String status = null;
		if(players[0].active){
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
			Protocol a = new Protocol();
			a.arg1 = (Object) clickX;
			a.arg2 = (Object) clickY;
			a.arg3 = (Object) mouseX;
			a.arg4 = (Object) mouseY;
			a.state = "TURN_TRUE";
			try{
				//oosOut.reset();
				a.send(oosOut);
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
				Protocol info = new Protocol();
				try{
					//----- MSG ID -----start
					info.arg1 =  players[0].name;
					System.out.println(players[0].name);

					info.send(oosOut);
					info = Protocol.receive(oosIn);
					players = (Player []) info.arg1;
					System.out.println("All players loaded!\n" + players[0].toString() + "\n" + players[1].toString());

					repaint();
					//----- MSG ID -----end

					//----- MSG PLAY -----start
					while(!gameOver){
						if(!players[clientId].active){
							System.out.println("It's NOT your turn!");
							try{
								info = new Protocol();
								info.state = "TURN_FALSE";
								info.send(oosOut);
							}catch(Exception e){
								e.printStackTrace();
							}
							sleep(1000);
						}else{
							System.out.println("It's your turn!");
						}

						try{
							info = new Protocol();
							info = Protocol.receive(oosIn);
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

							System.out.println("PLAYERS\n" + players[0].toString() + players[1].toString());
							System.out.println("-----PLAYER " + clientId + ": " + players[clientId].active);

						}
						if(info.state.equals("NOT_OK")){
							System.out.println("Something is going wrong!");
						}
						repaint();
					}
					//----- MSG PLAY -----end



					//----- MSG END -----start
					info = new Protocol();
					info = Protocol.receive(oosIn);
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
