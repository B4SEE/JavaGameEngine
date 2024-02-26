package cs.cvut.fel.pjv.gamedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Label label = new Label("Hello, JavaFX!");
        label.setStyle("-fx-text-fill: #ff0000; -fx-font-size: 40px;");
        Pane mainPane = new FlowPane(label);
        mainPane.setStyle("-fx-background-color: #15efef;");
        Scene scene = new Scene(mainPane, 640, 480);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}