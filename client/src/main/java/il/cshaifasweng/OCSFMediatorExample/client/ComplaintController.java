package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.msgObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ComplaintController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField email_txt;

    @FXML
    private TextArea content_txt;

    @FXML
    private Button send_btn;

    @FXML
    void sendComplaint(ActionEvent event) throws IOException {
    	System.out.println("starting the sending event handler");
    	Complaint comp=new Complaint();
    	comp.setEmail( email_txt.getText());
    	comp.setContent(content_txt.getText());
    	comp.setSendTime(LocalTime.now());
    	comp.setDate(LocalDate.now());
    	comp.setStatus("Not answered");
    	msgObject msg=new msgObject();
    	msg.setMsg("#addComplaint");
    	msg.setObject(comp);
    	System.out.println("sending to server a new complaint");
    	SimpleClient.getClient().sendToServer(msg);
    	email_txt.clear();
    	content_txt.clear();
    }

    @FXML
    void initialize() {
        assert email_txt != null : "fx:id=\"email_txt\" was not injected: check your FXML file 'Complaint.fxml'.";
        assert content_txt != null : "fx:id=\"content_txt\" was not injected: check your FXML file 'Complaint.fxml'.";
        assert send_btn != null : "fx:id=\"send_btn\" was not injected: check your FXML file 'Complaint.fxml'.";

    }
    @FXML
    void goCatalog(ActionEvent event) {
        msgObject msg = new msgObject("#getAllMovies");
        try {
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("message sent to server to get all movies");
    }

}
