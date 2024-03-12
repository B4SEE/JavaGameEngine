package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.GameLogic;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Player player = new Player(0, "PLAYER_NAME", "player_front.png", 400, 0);
        player.setHitBoxSize(1);
        player.setAttackRangeSize(1);
        player.setHealth(Constants.PLAYER_MAX_HEALTH);
        Entity entity = new Entity(1, "ENTITY_NAME", "player_front.png", "ENTITY", 0, 0, 1, 100, 10, null);
        entity.setHeight(2);
        entity.setHitBoxSize(1);
        entity.setAttackRangeSize(1);
        entity.setWhenAttacked(0);
        entity.setCooldown(0);
        entity.setBehaviour("NEUTRAL");
        entity.setInitialBehaviour(entity.getBehaviour());
        Entity[] entities = {entity};
//        Inventory inventory = new Inventory(50);
//        Item item = new Item(0, "ITEM_NAME", "block_wall.png");
//        inventory.addItem(item);
//        inventory.openInventory(stage);
//        inventory.closeInventory(stage);
        GameLogic gameLogic = new GameLogic(stage);
        gameLogic.loadGame(player, entities, "maps/test_map_3.txt");
    }

    public static void main(String[] args) {
        launch();
    }
}