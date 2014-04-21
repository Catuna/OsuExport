package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
            Scene scene = new Scene(root, 1240, 800);

            stage.setTitle("Osu! Export");
            stage.setScene(scene);
            stage.show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
