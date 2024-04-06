package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.engine.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load and start the game
//        Platform.setImplicitExit(false);//do not close the game when the window is closed
        Atmospheric.resetAll();
        Atmospheric.updateBackgroundMusic();
        Game.setStage(stage);
        Game.start();
    }

    public static void main(String[] args) {
        launch();
    }
}