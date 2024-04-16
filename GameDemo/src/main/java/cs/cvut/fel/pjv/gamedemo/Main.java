package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.engine.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.Game;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import java.io.IOException;


public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);
    @Override
    public void start(Stage stage) throws IOException {
        // enable/disable logging
        boolean loggingEnabled = true;

        if (!loggingEnabled) {
            Logger.getRootLogger().removeAllAppenders();
            Logger.getRootLogger().addAppender(new NullAppender());
        }

        logger.info("---------------------------------\n\n\n");
        logger.info("Starting application...");
        Atmospheric.resetAll();
        Game.setStage(stage);
        Game.start();
    }

    public static void main(String[] args) {
        launch();
    }
}