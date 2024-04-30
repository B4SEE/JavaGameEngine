package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Train;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Class with the main game logic, handles the game state, player input and game events.
 */
public class GameLogicMain {
    private static final Logger logger = LogManager.getLogger(GameLogicMain.class);

    private final GameData gameData;
    public GameLogicMain(Stage stage, Train train) {
        this.gameData = new GameData(stage);
        gameData.setIsometric(new Isometric());
        gameData.setTrain(train);
        setUpLogic();
    }
    private void setUpLogic() {
        logger.debug("Setting up game logic...");

        List<String> classNames = Arrays.asList(
                "GameManagement",
                "GameLogicVisuals",
                "GameLogicHandlers",
                "GameLogicWindows",
                "UpdateLogic",
                "WagonLogic",
                "EntitiesLogic",
                "EntitiesMovementLogic",
                "TrapLogic"
        );

        // Set up game logic classes (use reflection to call setGameData method on each class)
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName("cs.cvut.fel.pjv.gamedemo.engine.gamelogic." + className);
                Method setGameData = clazz.getMethod("setGameData", GameData.class);
                setGameData.invoke(null, gameData);
            } catch (Exception e) {
                logger.error("Error setting up game logic: " + className, e);
            }
        }

        logger.info("Game logic set up");
    }
}