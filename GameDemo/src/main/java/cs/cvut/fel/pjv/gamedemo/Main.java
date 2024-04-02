package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Atmospheric.updateBackgroundMusic();

        // Load and start the game
        Game game = new Game();
        game.setStage(stage);
        game.loadGame();
        game.startGame();
    }

    public static void main(String[] args) {
        launch();
    }
}