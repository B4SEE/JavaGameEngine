package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.IsometricEngine.Isometric;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Isometric isometric = new Isometric();
        isometric.initialiseStage(stage);
        isometric.setPlayer(new Player(0, "PLAYER_NAME", "player_front.png", 0, 0));
        isometric.setMap("maps/test_map_2.txt");
//        isometric.previewMap();
        isometric.start();
    }

    public static void main(String[] args) {
        launch();
    }
}