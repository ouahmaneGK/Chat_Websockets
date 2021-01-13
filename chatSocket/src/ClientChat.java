import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientChat extends Application {
	
	PrintWriter pw;

	public static void main(String[] args) {
		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//primaryStage.show();
		primaryStage.setTitle("Client Chat");
		BorderPane borderPane = new BorderPane();
		
		  Label labelHost = new Label("Host:");
		  TextField textFieldHost = new TextField("localhost");
		  Label labelPort = new Label("Port:");
		  TextField textFielPort= new TextField("8000");
		  Button buttonConnecter = new Button("Connecter");
		  
		  HBox hBox = new HBox(); hBox.setSpacing(10); hBox.setPadding(new Insets(10));
		  hBox.setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
		  hBox.getChildren().addAll(labelHost, textFieldHost, labelPort, textFielPort, buttonConnecter);
		  borderPane.setTop(hBox);
		  
		  VBox vBox = new VBox(); vBox.setSpacing(10); vBox.setPadding(new Insets(10));
		  
		  ObservableList<String> listModel = FXCollections.observableArrayList();
		  
		  ListView<String> listView =new ListView<String>(listModel);
		  vBox.getChildren().add(listView);
		  borderPane.setCenter(vBox);
		  
		  Label labelMessage = new Label("Message:");
		  TextField textFieldMessage = new TextField();
		  textFieldMessage.setPrefSize(300, 30);
		  Button buttonEnvoyer = new Button("Envoyer");
		  
		  HBox hBox2= new HBox(); hBox2.setSpacing(10); hBox2.setPadding(new Insets(10));
		  hBox2.getChildren().addAll(labelMessage, textFieldMessage, buttonEnvoyer );
		 borderPane.setBottom(hBox2);
		  
		  Scene scene = new Scene(borderPane, 600, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonConnecter.setOnAction(   (evt) ->{
			String host= textFieldHost.getText();
			int port = Integer.parseInt(textFielPort.getText());
			    try {
					Socket socket = new Socket(host, port);
					InputStream is = socket.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader bufferedReader = new BufferedReader(isr);
					
					pw = new PrintWriter(socket.getOutputStream(), true);
					new Thread(  ()->{
					     while(true) { // le tHread a l'ecoute des reponces! pour ne rater aucun message
								     try {
									
										String response = bufferedReader.readLine(); // qd ya une reponse l'ajouter a la liste model
										// ici probleme, car on utilise un autre tread que celui de javaFx, solution:
											Platform.runLater(()->{
												listModel.add(response);
											});
										} catch (Exception e) {	
											e.printStackTrace();
									 }								
						 }
						
					}).start();
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
		});
		
		buttonEnvoyer.setOnAction((evt)->{
			String message = textFieldMessage.getText();
			pw.println(message);
		});
	}

}
