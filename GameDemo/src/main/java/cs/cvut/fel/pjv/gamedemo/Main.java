package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.IsometricEngine.Isometric;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
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
        Player player = new Player(0, "PLAYER_NAME", "player_front.png", 0, 0);
        player.setHitBoxSize(1);
        player.setAttackRangeSize(1);
        Entity entity = new Entity(1, "ENTITY_NAME", "player_front.png", "enemy", 0, 0, 1, 100, 10, null);
        entity.setHeight(2);
        entity.setHitBoxSize(1);
        entity.setAttackRangeSize(1);
        Entity[] entities = {entity};
        Isometric isometric = new Isometric();
        isometric.initialiseStage(stage);
        isometric.setPlayer(player);
        isometric.setEntities(entities);
        isometric.setMap("maps/test_map_3.txt");
//        isometric.previewMap();
        isometric.start();
    }

    public static void main(String[] args) {
        launch();
    }
}