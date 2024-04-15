package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.engine.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.Game;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Atmospheric.resetAll();
        Game.setStage(stage);
        Game.start();
    }

    public static void main(String[] args) {
        launch();
    }
}