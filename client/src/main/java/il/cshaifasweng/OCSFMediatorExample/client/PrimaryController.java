package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.msgObject;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class PrimaryController {
	@FXML // fx:id="grid"
	private Button grid; // Value injected by FXMLLoader
	
	@FXML // fx:id="pricespage"
    private Button pricespage; // Value injected by FXMLLoader

	@FXML
	void open_grid(ActionEvent event) throws IOException {
		msgObject msg=new msgObject("#getAllMovies");
		SimpleClient.getClient().sendToServer(msg);
		System.out.println("message sent to server to get all movies");
		//App.setRoot("GridCatalog");
	}

	@FXML
	void sendWarning(ActionEvent event)  throws IOException {
		try {
			//SimpleClient.getClient().sendToServer("#warning");
			msgObject msg=new msgObject("#getAllMovies");
			SimpleClient.getClient().sendToServer(msg);
			System.out.println("message sent to server to get all movies");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@FXML
    void openpricess(ActionEvent event) {
		try {
			msgObject msg=new msgObject("#getAllPriceRequests");
			SimpleClient.getClient().sendToServer(msg);
			System.out.println("message sent to server to get all Requests");
			//App.setRoot("contentmanagerPrices");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}