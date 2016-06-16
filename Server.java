import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import org.apache.commons.lang.SerializationUtils;

public class Server{

	//SERVER
	private static int PORT = 5000;

	protected final int DIM = 2;
	protected ObjectOutputStream  oos[] = new ObjectOutputStream[DIM];
	protected ObjectInputStream  in[] = new ObjectInputStream[DIM];
	protected ServerSocket SocketEscuta;
	protected Socket [] sockets = new Socket[DIM];
	protected Protocolo [] games = new Protocolo[DIM];
	public Player players[];
	Thread one[];
	int n = -1;
	private Main controller;
	private boolean gameOver;


	public Server (int listenPort) throws IOException{
		SocketEscuta = new ServerSocket(listenPort, DIM);
		players = new Player[DIM];
		one = new Thread[DIM];
		controller = new Main(false);
		gameOver = false;
		System.out.println("Running at: " + listenPort);
	}
	/*public Server(){

	}*/



	private synchronized void setController(Main main){
		controller = main;
	}
	private synchronized Main getController(){
		return controller;
	}

	private synchronized void setGameOver(boolean end){
		gameOver = end;
	}
	private synchronized boolean getGameOver(){
		return gameOver;
	}

	public void waitForClients(){
		try {
			while ( n < DIM ){
				sockets[n + 1] = SocketEscuta.accept();
				n++;

				//System.out.println(n);

				oos[n] = new ObjectOutputStream(sockets[n].getOutputStream());
				in[n] = new ObjectInputStream(sockets[n].getInputStream());
				players[n] = new Player();
				Main main[] = new Main[DIM];
				main[n] = new Main(false);
				players[n] = main[n].players[n];

				one[n]=new Thread(){
					public void run(){

						int eachClient = n;
						boolean flag = true;

						//----- MSG ID -----start
						try{
							oos[eachClient].write(eachClient);
							oos[eachClient].flush();
							System.out.println("Success connecting to client " + eachClient);
						}catch (IOException er){
							System.err.println(er.getMessage());
							System.exit(1);
						}

						do{
							try{
								games[eachClient] = new Protocolo();
								games[eachClient] = Protocolo.recebe(in[eachClient]);
							}catch(Exception er){
								er.printStackTrace();
							}

							players[eachClient].name = (String) games[eachClient].arg1;

							while(n == 0){
								try{
									sleep(500);
								} catch(Exception er){
									er.printStackTrace();
								}
							}

							try{
								games[eachClient].arg1 = players;
								//oos[eachClient].reset();
								games[eachClient].envia(oos[eachClient]);
								//System.out.println("P1 - " + players[0].name + " | P2 - " + players[1].name);
								if(players[1].name != null){
									flag = false;
								}
							} catch (Exception er) {
								er.printStackTrace();
							}
						}while(flag);


						System.out.println(eachClient + ": Break bitches! :D");
						main[eachClient].players = players;
						setController(main[eachClient]);
						System.out.println("P1 - " + controller.players[0].name + " | P2 - " + controller.players[1].name);
						//----- MSG ID -----end



						//----- MSG PLAY -----start
						while(!gameOver){
							try{
								games[eachClient] = new Protocolo();
								games[eachClient] = Protocolo.recebe(in[eachClient]);//games[eachClient].recebe(in[eachClient]);	//Recebe coordenadas X e Y onde jogador clica
							}catch(Exception e){
								e.printStackTrace();
							}

							if(games[eachClient].state.equals("TURN_FALSE")){
								games[eachClient] = new Protocolo();
								games[eachClient].state = "OK";

								games[eachClient].arg1 =  getController();

								try{
									//oos[eachClient].reset();
									games[eachClient].envia(oos[eachClient]);

								}catch(Exception e){
									e.printStackTrace();
								}
							}

							if(games[eachClient].state.equals("TURN_TRUE")){
								main[eachClient].clickX = (int) games[eachClient].arg1;
								main[eachClient].clickY = (int) games[eachClient].arg2;
								main[eachClient].mouseX = (int) games[eachClient].arg3;
								main[eachClient].mouseY = (int) games[eachClient].arg4;


								Main auxCtrl = new Main(true);
								auxCtrl = getController();
								auxCtrl.clickX = main[eachClient].clickX;
								auxCtrl.clickY = main[eachClient].clickY;
								auxCtrl.mouseX = main[eachClient].mouseX;
								auxCtrl.mouseY = main[eachClient].mouseY;
								setController(auxCtrl);

								main[eachClient] = setLine(auxCtrl, eachClient);



								if(main[eachClient] == null){
									//System.out.println("NULL");
									games[eachClient] = new Protocolo();
									games[eachClient].state = "NOT_OK";
								}else{
									//System.out.println("NOT NULL");
									games[eachClient] = new Protocolo();
									games[eachClient].state = "OK";

									games[eachClient].arg1 =  main[eachClient];

									setController(main[eachClient]);


									//System.out.println("X: " + main[eachClient].clickX + " | Y: " + main[eachClient].clickY);
								}
								try{
									//oos[eachClient].reset();
									games[eachClient].envia(oos[eachClient]);	//Envia alteracoes

								}catch(Exception e){
									e.printStackTrace();
								}
								main[eachClient] = getController();

								//System.out.println("Game Over? " + main[eachClient].gameOver);
							}
						}
						//----- MSG PLAY -----end


						//----- MSG END -----start
						games[eachClient] = new Protocolo();
						games[eachClient].arg1 = main[eachClient];
						games[eachClient].state = "END";

						try{
							//oos[eachClient].reset();
							games[eachClient].envia(oos[eachClient]);
						}catch(Exception e){
							e.printStackTrace();
						}
						//----- MSG END -----end
					}
				};
				//System.out.println("Sai da thread!");

				one[n].start();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}







	/*
	  --------------------------------------
	  -         APLICATION LOGIC           -
	  --------------------------------------
	*/
	//Metodo para ativar uma Line que foi clicada
	public synchronized Main setLine(Main localCtrl, int clt){
		Lines line = getLine(localCtrl.clickX, localCtrl.clickY);

		if(line == null){
			return null;
		}

		if(line.connected == false){
			boolean newBox = false;
			boolean boxStatusBeforeClick[] =  getBoxesStatus();

			if(localCtrl.activePlayer == localCtrl.PLAYER_ONE){
				line.color = localCtrl.PLAYER_ONE_COLOR;
				line.connected = true;

				localCtrl = updateBoxes(line, localCtrl);

				setController(localCtrl);

				boolean boxStatusAfterClick[] = getBoxesStatus();

				for(int i = 0; i < localCtrl.boxes.length; i++){
					if(boxStatusAfterClick[i] != boxStatusBeforeClick[i]){
						newBox = true;
						localCtrl.boxes[i].player = localCtrl.PLAYER_ONE;
						localCtrl.boxes[i].color = localCtrl.PLAYER_ONE_COLOR_BOX;
						localCtrl.boxes[i].closed = true;
						localCtrl.playerOneScore++;

						//System.out.println("\nLAST BOX:\n"+boxes[i].toString());
					}
				}
			}else{
				line.color = localCtrl.PLAYER_TWO_COLOR;
				line.connected = true;

				localCtrl = updateBoxes(line, localCtrl);

				setController(localCtrl);

				boolean boxStatusAfterClick[] = getBoxesStatus();

				for(int i = 0; i < localCtrl.boxes.length; i++){
					if(boxStatusAfterClick[i] != boxStatusBeforeClick[i]){
						newBox = true;
						localCtrl.boxes[i].player = localCtrl.PLAYER_TWO;
						localCtrl.boxes[i].color = localCtrl.PLAYER_TWO_COLOR_BOX;
						localCtrl.boxes[i].closed = true;
						localCtrl.playerTwoScore++;

						//System.out.println("\nLAST BOX:\n"+boxes[i].toString());
					}
				}
			}

			if(newBox == false){
				if(localCtrl.activePlayer == localCtrl.PLAYER_ONE){
					localCtrl.activePlayer = localCtrl.PLAYER_TWO;
					localCtrl.players[0].active = false;
					localCtrl.players[1].active = true;
					System.out.println("Change turn!");
				}else{
					localCtrl.activePlayer = localCtrl.PLAYER_ONE;
					localCtrl.players[0].active = true;
					localCtrl.players[1].active = false;
				}
			}
		}

		System.out.println(localCtrl.players[0].toString() + "\n" + localCtrl.players[1].toString());
		localCtrl.repaint();
		checkGameOver(localCtrl);
		localCtrl.gameOver = getGameOver();
		return localCtrl;
	}

	//Metodo para obter Line de acordo as coordenadas do rato
	private Lines getLine(int x, int y){
		for(int i = 0; i < getController().horizontalLines.length; i++){
			if(getController().horizontalLines[i].shape.contains(x, y)){
				return getController().horizontalLines[i];
			}
		}
		for(int i = 0; i < getController().verticalLines.length; i++){
			if(getController().verticalLines[i].shape.contains(x, y)){
				return getController().verticalLines[i];
			}
		}

		return null;
	}

	//Metodo para obter o estado de todas as Boxes
	private boolean[] getBoxesStatus(){
		boolean status [] = new boolean[getController().boxes.length];

		for(int i = 0; i < getController().boxes.length; i++){
			status[i] = getController().boxes[i].checkState();
		}

		return status;
	}

	//Metodo para atualizar campos das Boxes
	private Main updateBoxes(Lines line, Main localCtrl){
		for(int i = 0; i < localCtrl.boxes.length; i++){
			if(line.type == 0){
				//Procura apenas nas linhas horizontais
				for(int j = 0; j < localCtrl.boxes[i].horizontalLines.length; j++){
					if(line.order == localCtrl.boxes[i].horizontalLines[j].order){
						localCtrl.boxes[i].horizontalLines[j] = line;
					}
				}
			}else{
				//Procura apenas nas linhas verticais
				for(int j = 0; j < localCtrl.boxes[i].verticalLines.length; j++){
					if(line.order == localCtrl.boxes[i].verticalLines[j].order){
						localCtrl.boxes[i].verticalLines[j] = line;
					}
				}
			}
		}
		return localCtrl;
	}

	//Metodo para verificar se o jogo terminou
	private void checkGameOver(Main localCtrl){
		boolean status[] = getBoxesStatus();
		boolean gameOver = true;

		for(int i = 0; i < status.length; i++){
			if(status[i] == false){
				gameOver = false;
				break;
			}
		}

		setGameOver(gameOver);
	}



	public static void main(String [] args){
		try {
			Server servidor = new Server(PORT);
			servidor.waitForClients();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
