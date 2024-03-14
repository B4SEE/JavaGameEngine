package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.GameLogic;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Create a new player
        Player player = new Player(0, "PLAYER_NAME", "player_front.png", 400, 0);
        player.setHitBoxSize(1);
        player.setAttackRangeSize(1);
        player.setCooldown(2);

        Inventory inventory = new Inventory(50);
        Item item = new Item(0, "ITEM_NAME", "block_wall.png");
        inventory.addItem(item);
        Scene scene = inventory.openInventory(true);
        stage.setScene(scene);
        stage.show();
//        inventory.closeInventory(stage);

        // Load and start the game
//        GameLogic gameLogic = new GameLogic(stage);
//        gameLogic.loadGame(player, new Wagon(0, "DEFAULT", "wall_3.png"));
//        gameLogic.start();
    }

    public static void main(String[] args) {
        launch();
    }
}