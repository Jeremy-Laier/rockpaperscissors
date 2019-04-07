import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application{
	public Label instructL,portL, clientNumL;
	public Button startServerB, exitB;
	public TextField portTF, curClientNumTF;
	public BorderPane launchP,gameP;
	public HBox launchHB, launchBHB, currentClientNumHB;
	public VBox launchVB;
	public Scene launchS,gameS;
	public TextArea msgTA;
	
	public Server server;
	public static ArrayList<String> hands;
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage primaryStage) throws Exception {
		createLaunchScene(primaryStage);
		primaryStage.setTitle("RPSLS");
		primaryStage.setScene(launchS);
		primaryStage.setHeight(100);
		primaryStage.setWidth(200);
		primaryStage.show();
	}
	private void createGameScene() {
		hands = new ArrayList<String>();
		//make new scene
		//border pane
		//top is gonna be label of what port num
		//center is gonna be text area
		//bottom is gonna be exit 
		portL = new Label("Server is hosted on port: " + portTF.getText());
		msgTA = new TextArea();
		msgTA.setWrapText(true);
		msgTA.setEditable(false);
		clientNumL = new Label("Number of connected Clients: ");
		curClientNumTF = new TextField("0");
		curClientNumTF.setDisable(true);
		curClientNumTF.maxWidth(20);
		curClientNumTF.setAlignment(Pos.BASELINE_LEFT);
		currentClientNumHB = new HBox();
		currentClientNumHB.getChildren().addAll(clientNumL, curClientNumTF);
		
		gameP = new BorderPane();
		gameP.setRight(portL);
		gameP.setLeft(currentClientNumHB);
		gameP.setCenter(msgTA);
		gameP.setBottom(exitB);
		gameS = new Scene(gameP);
	}
	private void createLaunchScene(Stage primaryStage) {
		//2 buttons
		//go and exit
		//flow pane
		//one text field for port #
		
		instructL = new Label("Enter a port number to launch the server with!");
		instructL.setWrapText(true);
		instructL.setPrefWidth(250);
		portTF = new TextField();
		portTF.setPromptText("Enter the port here");
		
		startServerB = new Button("Start server");
		startServerB.setOnAction( e -> {
			server = new Server( portTF.getText());
			server.start();
			createGameScene();
			primaryStage.setScene(gameS);
			primaryStage.setTitle("RPSLS");
		});
		exitB = new Button("Exit");
		exitB.setOnAction( e -> {
			System.exit(0);
		});
		
		launchHB = new HBox();
		launchHB.getChildren().addAll(instructL, portTF);
		
		launchBHB = new HBox();
		launchBHB.getChildren().addAll(startServerB, exitB);
		
		launchVB = new VBox();
		launchVB.getChildren().addAll(launchHB, launchBHB);
		
		launchP = new BorderPane();
		launchP.setCenter(launchVB);
	
		launchS = new Scene(launchP);
	}
	@Override
	public void stop() throws Exception{
		server.close();
	}
	public class Server extends Thread{
		private int port;
		private ServerSocket myServer;
		private ArrayList<ConnectionClient> conClients;
		private int curClients;
		private int p1score;
		private int p2score;
		private boolean checked;
		
		public void run() {
			try {
				startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public Server(String port) {
			this.port = Integer.parseInt(port);
			this.conClients = new ArrayList<ConnectionClient>();
			this.curClients = 0;
			p1score  = 0;
			p2score  = 0;
			checked = false;
		}
		public void startServer() throws IOException {
			myServer = new ServerSocket(port);	
			while(true) {
				Socket con = myServer.accept();
				curClients++;
				curClientNumTF.setText( Integer.toString(curClients));
				ConnectionClient client = new ConnectionClient(con);
				conClients.add(client);
				client.start();
			}
		}
		public void close() {
			for( ConnectionClient i : conClients) {
				i.close();
			}
		}
		public class ConnectionClient extends Thread{
			private Scanner in;
			private PrintWriter out;
			private Socket connection;
			private String hand;
			public void run() {
				try {
					startClient();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			public ConnectionClient( Socket con) {
				this.connection = con;
				this.hand = "f";
			}
			public void startClient() throws IOException, InterruptedException {
				in = new Scanner(connection.getInputStream());
				out = new PrintWriter(connection.getOutputStream(), true);
				out.println(curClients);
				while( true ) {
					if( in.hasNext()) {
						String input = in.nextLine();
						msgTA.appendText("\n" + input);
						if( input.intern() == "p") {
							out.println(curClients);
							msgTA.appendText("\nThere are " + curClients + " players.");
						}
						else if( input.intern().equals("m")) {
							checked = false;
							Thread.sleep(15);
							String num = in.nextLine();
							if( num.intern().equals("one")) {
								Thread.sleep(15);
								String hand = in.nextLine();
								msgTA.appendText("\nClient " + num + " played " + hand);
								conClients.get(0).hand = hand;
							}
							else if ( num.intern().equals("two")){
								Thread.sleep(15);
								String hand = in.nextLine();
								msgTA.appendText("\nClient " + num + " played " + hand);
								conClients.get(1).hand = hand;
							}
						}
						else if( input.intern() == "g") {
							 Thread.sleep(15);
							 String num = in.nextLine();
							 if( num.intern() == "one") {
								 out.println(conClients.get(1).hand);
							 }
							 else if( num.intern() == "two") {
								 out.println(conClients.get(0).hand);
							 }
						}
						else if( input.intern()== "c") {
							out.println( checkWinner());
						}
						else if( input.intern()=="v") {
							p1score = 0;
							p2score = 0;
							checked = false;
							for(ConnectionClient i: conClients) {
								i.hand = "f";
							}
						}
						else if( input.intern().equals("s")) {
							Thread.sleep(15);
							String num = in.nextLine();
							if( num.intern().equals("one")) {
								msgTA.appendText("\nClient 1 has " + p1score + " points.");
								out.println(p1score);
							}
							else if( num.intern().equals("two")) {
								msgTA.appendText("\nClient 2 has " + p2score + " points.");
								out.println(p2score);
							}
						}
						else if( input.intern() == "q") {
							curClients--;
							close();
							break;
						}
					}
				}
			}
			public void incPlayer(boolean found, int player) {
				msgTA.appendText("\nSomething is working");
				if( !found) {
					if( player == 1) {
						p1score++;
					}
					else {
						p2score++;
					}
					checked = true;
				}
			}
			public synchronized String checkWinner() {
				boolean found = true;
				if(!checked)
					found = false;
				if( conClients.size() == 1)
					return "Not enough players...";
				String player1 = conClients.get(0).hand;
				String player2 = conClients.get(1).hand;
				msgTA.appendText("\n"+player1);
				msgTA.appendText("\n"+player2);
				
				if( player1 .equals( "paper")) {
					if( player2 .equals( "lizard") || player2 .equals( "scissors")) {
						incPlayer(found, 2);
						return "Player 2 has won";
					}
					else if( player2.equals("paper")) {
						return "Tie";
					}
					else {
						incPlayer(found, 1);
						return "Player 1 has won";
					}
				}
				else if( player1 .equals( "rock")) {
					if( player2 .equals( "paper") || player2 .equals( "spock")) {
						incPlayer(found, 2);
						return "Player 2 has won";
					}
					else if( player2.equals("rock")) {
						return "Tie";
					}
					else {
						incPlayer(found, 1);
						return "Player 1 has won";
					}
				}
				else if( player1 .equals( "scissors")){
					if( player2 .equals( "spock") || player2 .equals( "rock")) {
						incPlayer(found, 2);
						return "Player 2 has won";
					}
					else if( player2.equals("scissors")) {
						return "Tie";
					}
					else {
						incPlayer(found, 1);
						return "Player 1 has won";
					}
				}
				else if( player1 .equals( "lizard")) {
					if( player2 .equals( "rock") || player2 .equals("scissors")) {
						incPlayer(found, 2);
						return "Player 2 has won";
					}
					else if( player2.equals("lizard")) {
						return "Tie";
					}
					else {
						incPlayer(found, 1);
						return "Player 1 has won";
					}
				}
				else if( player1 .equals( "spock")) {
					if( player2 .equals( "lizard") || player2 .equals( "paper")) {
						incPlayer(found, 2);
						return "Player 2 has won";
					}
					else if( player2.equals("spock")) {
						return "Tie";
					}
					else {
						incPlayer(found, 1);
						return "Player 1 has won";
					}
				}
				return "Tie";
			}
			public void close() {
				in.close();
				out.close();
				curClients--;
				curClientNumTF.setText(Integer.toString(curClients));
			}
		}
	}
}
