package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

//Stage for showing a informational dialog to the user
public class InfoDialog extends Stage {
    Text txtError;
    Button btnConfirm;
    Separator seperator;

    InfoDialog(String msg) {
        txtError = new Text(msg);
        btnConfirm = new Button("Ok");

        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);

        Scene scene = new Scene(new Group(new Text(25, 25, "Error!")));
        this.setScene(scene);
        this.setScene(new Scene(VBoxBuilder.create().
                children(txtError, btnConfirm).
                alignment(Pos.CENTER).padding(new Insets(5)).spacing(20).build()));

        //Listen for close button press
        btnConfirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Get the stage of the event passed to the handler and close it
                ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close(); //TODO: Ugly!
            }
        });
    }
}
