import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientGUI extends Application {

        public Button connectB, exitB, lizB, rockB, papB, scisB, spockB, checkOpp,getWinner;
        public TextField ipTF, nameTF, scoreTF,cpuScore, playerScore,playerPlayed,cpuPlayed;
        public Label cpuL, playerL,winnerL, cpuLC,playerLC;
        public Scene gameScene,menuScene;

        public Client connect;
        private ArrayList<Button> gameFlowBMap;
        public static void main(String[] args) {
                launch(args);
        }
        public void start(Stage primaryStage) throws Exception {
                gameFlowBMap = new ArrayList<Button>();
                connectB = new Button("Connect to server");

                connectB.setOnAction(e -> {
                        String ip = ipTF.getText();
                        String port = nameTF.getText();
                        connect = new Client(ip, port);
                        connect.start();
                        try {
                                connect.join();
                                testPlayerCount(primaryStage);
                        } catch (InterruptedException e1) {
                                e1.printStackTrace();
                        }
                        primaryStage.setTitle("RPSLS");
                        primaryStage.setWidth(1400);			
                        primaryStage.setHeight(400);			
                });
                exitB = new Button("Exit");
                exitB.setOnAction(e -> {
                        System.exit(0);
                });
                ipTF = new TextField();
                ipTF.setPromptText("Enter ip address here");
                ipTF.setMaxWidth(240);
                ipTF.setMaxHeight(20);

                nameTF = new TextField();
                nameTF.setPromptText("Enter the port number here");
                nameTF.setMaxWidth(240);
                nameTF.setMaxHeight(20);

                BorderPane mainBorder = new BorderPane();
                HBox buttonMenu = new HBox();
                VBox connectMenu = new VBox();

                buttonMenu.setSpacing(10);
                buttonMenu.setPadding(new Insets(15, 12, 15, 12));

                connectMenu.setSpacing(10);
                connectMenu.setPadding(new Insets(15, 12, 15, 12));

                buttonMenu.getChildren().addAll(connectB, exitB);
                connectMenu.getChildren().addAll(ipTF, nameTF);

                mainBorder.setBottom(buttonMenu);
                mainBorder.setCenter(connectMenu);

                menuScene = new Scene(mainBorder);
                //new scene now

                //This is our game scene
                BorderPane gameBP = new BorderPane();

                setupButtons();
                setupTextField();
                HBox playB = new HBox();
                playB.getChildren().addAll( rockB, papB, scisB, lizB, spockB);
                playB.autosize();
                playB.setAlignment(Pos.CENTER);

                HBox scoreBR = new HBox();
                scoreBR.getChildren().addAll(  cpuScore,cpuL);
                scoreBR.setAlignment(Pos.BASELINE_RIGHT);

                HBox scoreBL = new HBox();
                scoreBL.getChildren().addAll( playerScore, playerL);
                scoreBL.setAlignment(Pos.BASELINE_RIGHT);

                VBox score = new VBox();
                score.getChildren().addAll(scoreBL, scoreBR);
                VBox gameUpdates = new VBox();

                HBox winHB = new HBox();

                winnerL = new Label("Winner of last round: ");
                winHB.getChildren().addAll(winnerL);
                winHB.setAlignment(Pos.CENTER);

                HBox playedHB = new HBox();
                cpuLC = new Label("Opponent");
                cpuLC.setAlignment(Pos.BASELINE_LEFT);
                playerLC = new Label("You");
                playerLC.setAlignment(Pos.BASELINE_RIGHT);
                playerPlayed = new TextField("Nothing played yet");
                playerPlayed.setMinWidth(300);
                cpuPlayed = new TextField("Nothing played yet");
                cpuPlayed.setMinWidth(300);
                playedHB.getChildren().addAll(playerLC,playerPlayed,cpuPlayed,cpuLC);
                playedHB.setAlignment(Pos.CENTER);

                gameUpdates.getChildren().addAll(winHB,playedHB,checkOpp,getWinner);
                gameUpdates.setAlignment(Pos.CENTER);
                gameUpdates.autosize();

                gameBP.setTop(scoreTF);
                gameBP.setCenter(playB);
                gameBP.setLeft(score);
                gameBP.setBottom(gameUpdates);
                gameScene = new Scene(gameBP);
                //main stage setting here
                primaryStage.setTitle("RPSLS");
                primaryStage.setScene(menuScene);

                primaryStage.setWidth(250);
                primaryStage.show();	
        }
        public void testPlayerCount(Stage primaryStage) throws InterruptedException {
                int players = connect.getPlayers();
                connect.join();
                if( players < 2) {
                        Button b = new Button("Check for new players");
                        b.setOnAction(f ->{
                                try {
                                        int playe = connect.getPlayers();
                                        connect.join();
                                        if( playe >=2)
                                                primaryStage.setScene(gameScene);
                                        else {
                                                b.setText("Please try again, 1 player currently");
                                        }
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                }
                        });
                        Label noPL = new Label("You are the only player!\nPlease wait!");
                        FlowPane p = new FlowPane();
                        p.getChildren().addAll(noPL,b);
                        Scene s = new Scene(p);
                        primaryStage.setScene(s);
                }
                else
                        primaryStage.setScene(gameScene);
        }
        public String getData() {
                return getData();
        }
        public void setupTextField() {
                scoreTF = new TextField("Choose your item!");
                scoreTF.setDisable(true);
                scoreTF.setPrefWidth(160);
                scoreTF.setAlignment(Pos.CENTER);

                cpuScore = new TextField("Opponent: ");
                cpuScore.setDisable(true);
                cpuScore.setPrefWidth(80);
                cpuScore.setAlignment(Pos.CENTER_RIGHT);
                playerScore = new TextField("You: ");
                playerScore.setDisable(true);
                playerScore.setPrefWidth(80);
                playerScore.setAlignment(Pos.CENTER_RIGHT);

                cpuL = new Label("0");
                cpuL.setPrefWidth(80);
                playerL = new Label("0");
                playerL.setPrefWidth(80);
        }
        @SuppressWarnings("deprecation")
        public  void setupButtons() throws MalformedURLException {
                File lizF = new File("/home/jeremy/school/cs342/JLaier2Proj3Client/src/images/lizard.png");
                Image liz = new Image( lizF.toURL().toString());
                lizB = new Button("Lizard", new ImageView(liz));
                lizB.setMinWidth(200);
                lizB.setMaxWidth(200);
                lizB.setMinHeight(100);
                lizB.setMaxHeight(100);
                lizB.setOnAction( e -> {
                        try {
                                connect.sendMove("lizard");
                        } catch (InterruptedException e1) {
                                e1.printStackTrace();
                        }
                        playerPlayed.setText("Lizard");
                });

                File rocF = new File("/home/jeremy/school/cs342/JLaier2Proj3Client/src/images/rock.png");
                Image roc = new Image( rocF.toURL().toString());
                rockB = new Button("Rock",new ImageView(roc));
                rockB.setMinWidth(200);
                rockB.setMaxWidth(200);
                rockB.setMinHeight(100);
                rockB.setMaxHeight(100);

                rockB.setOnAction( e -> {
                        try {
                                connect.sendMove("rock");
                        } catch (InterruptedException e1) {
                                e1.printStackTrace();
                        }
                        playerPlayed.setText("Rock");
                });
                File papF = new File("/home/jeremy/school/cs342/JLaier2Proj3Client/src/images/paper.png");
                Image pap = new Image( papF.toURL().toString());
                papB = new Button("Paper",new ImageView(pap));
                papB.setMinWidth(200);
                papB.setMaxWidth(200);
                papB.setMinHeight(100);
                papB.setMaxHeight(100);

                papB.setOnAction( e -> {
                        try {
                                connect.sendMove("paper");
                        } catch (InterruptedException e1) {
                                e1.printStackTrace();
                        }
                        playerPlayed.setText("Paper");
                });
                File sciF = new File("/home/jeremy/school/cs342/JLaier2Proj3Client/src/images/scissors.png");
                Image sci = new Image( sciF.toURL().toString());
                scisB = new Button("Scissors",new ImageView(sci));
                scisB.setMinWidth(200);
                scisB.setMaxWidth(200);
                scisB.setMinHeight(100);
                scisB.setMaxHeight(100);

                scisB.setOnAction( e -> {
                        try {
                                connect.sendMove("scissors");
                        } catch (InterruptedException e1) {
                                e1.printStackTrace();
                        }
                        playerPlayed.setText("Scissors");
                });
                File spoF = new File("/home/jeremy/school/cs342/JLaier2Proj3Client/src/images/spock.png");
                Image spo = new Image( spoF.toURL().toString());
                spockB = new Button("Spock",new ImageView(spo));
                spockB.setMinWidth(200);
                spockB.setMaxWidth(200);
                spockB.setMinHeight(100);
                spockB.setMaxHeight(100);

                spockB.setOnAction( e -> {
                        try {
                                connect.sendMove("spock");
                        } catch (InterruptedException e1) {
                                e1.printStackTrace();
                        }
                        playerPlayed.setText("Spock");
                });		

                checkOpp = new Button("Reset and play again!");
                checkOpp.setMinHeight(25);
                checkOpp.setMinWidth(50);
                checkOpp.setOnAction( e -> {
                        connect.clear();
                        enableButtons();
                        resetScore();
                        winnerL.setText("Play a hand!");
                });

                getWinner = new Button("Get round winner!");
                getWinner.setOnAction(e -> {
                        winnerL.setText( connect.getWinner() );
                });
                gameFlowBMap.add(rockB);
                gameFlowBMap.add(lizB);
                gameFlowBMap.add(spockB);
                gameFlowBMap.add(papB);
                gameFlowBMap.add(scisB);
                gameFlowBMap.add(getWinner);
        }
        public void resetScore() {
                playerL.setText( "0"); 
                cpuL.setText("0");
        }
        public void enableButtons() {
                for( Button b: gameFlowBMap) {
                        b.setDisable(false);
                }
        }
        public void disableButtons() {
                for( Button b: gameFlowBMap) {
                        b.setDisable(true);
                }
        }
        public void showConnectScreeen(Stage primaryStage, String addr) {
                //this was taken off of stack over flow, mostly to make the gui look nice
                //https://stackoverflow.com/questions/22166610/how-to-create-a-popup-windows-in-javafx
                Stage success = new Stage();
                success.initModality(Modality.APPLICATION_MODAL);
                success.initOwner(primaryStage);

                HBox suc = new HBox();
                suc.getChildren().add( new Label("You have connected to: " + addr));

                Scene sucScene = new Scene( suc);
                success.setScene(sucScene);
                success.sizeToScene();
                success.show();
        }
        @Override
        public void stop() throws Exception{
                connect.close();
        }
        class Client extends Thread{
                private  String ipAdd,port;
                private Socket socketClient;
                private Scanner in,scanner;
                private PrintWriter out;
                private int ID;
                private int curPlayers;
                Client(String ip,String p){
                        ipAdd = ip;
                        port = p;
                }
                public void run() {
                        try {
                                clientCode(ipAdd,port);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                public void clientCode(String ipAdress, String port) throws IOException{	                
                        socketClient= new Socket(ipAdress,Integer.parseInt(port));
                        System.out.println("Client: "+"Connection Established");
                        System.out.println(socketClient.getRemoteSocketAddress());
                        scanner = new Scanner(System.in);
                        in = new Scanner(socketClient.getInputStream());
                        out = new PrintWriter(socketClient.getOutputStream(), true);

                        this.curPlayers = Integer.parseInt(in.nextLine());	   
                        this.ID = curPlayers;
                }
                public String getOpp() throws InterruptedException {
                        out.println("g");
                        connect.join();
                        if( ID == 1)
                                out.println("one");
                        else
                                out.println("two");
                        connect.join();
                        String op = in.nextLine();
                        if( op.equals("f"))
                                return "Opponent has not played, try again soon!";
                        else
                                return "Opponent has played! Click the winner button!";
                }
                public void clear() {
                        out.println("v");
                }
                public int getPlayers() throws InterruptedException {
                        out.println("p");
                        connect.join();
                        return Integer.parseInt(in.nextLine());
                }
                public void sendMove(String move) throws InterruptedException {
                        out.println("m");
                        connect.join();
                        if( ID == 1)
                                out.println("one");
                        else
                                out.println("two");
                        connect.join();
                        out.println(move);
                        connect.join();
                }
                public int getScore(int player) {
                        out.println("s");

                        if( player == 0)
                                out.println("one");
                        else
                                out.println("two");
                        try {
                                Thread.sleep(15);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                        return Integer.parseInt(in.nextLine());
                }
                public String getWinner() {
                        out.println("c");
                        String winner =in.nextLine();

                        if( ID == 1 ) {	        			
                                int score1 = connect.getScore(0);
                                int score2 = connect.getScore(1);

                                if( score1 == 3) {
                                        disableButtons();
                                        winner = ("You won!!!!");
                                }
                                if( score2 == 3) {
                                        disableButtons();
                                        winner = ("Your opponent won!!!!");
                                }
                                playerL.setText( Integer.toString(connect.getScore(0)) ); 
                                cpuL.setText( Integer.toString(connect.getScore( 1) ) ); 
                        }
                        if( ID == 2 ) {
                                int score1 = connect.getScore(1);
                                int score2 = connect.getScore(0);

                                if( score1 == 3) {
                                        disableButtons();
                                        winner = ("You won!!!!");
                                }
                                if( score2 == 3) {
                                        disableButtons();
                                        winner = ("Your opponent won!!!!");
                                }
                                playerL.setText( Integer.toString(connect.getScore(1)) ); 
                                cpuL.setText( Integer.toString(connect.getScore( 0) ) ); 
                        }

                        return winner;
                }
                public SocketAddress getAddress() {
                        return socketClient.getRemoteSocketAddress();
                }
                public void close() {
                        try {
                                socketClient.close();
                                in.close();
                                out.close();
                                scanner.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
}
