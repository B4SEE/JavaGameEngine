package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.GameLogic;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Player player = createPlayer();
        // Load and start the game
        GameLogic gameLogic = new GameLogic(stage);
        gameLogic.loadGame(player, new Wagon(0, "DEFAULT", "wall_3.png"));
        gameLogic.start();
    }

    private Player createPlayer() {
        Player player = new Player(0, "PLAYER_NAME", "player_front.png", 400, 200);

        player.setHitBoxSize(1);
        player.setAttackRangeSize(1);
        player.setCooldown(2);

        Food item = new Food("orange", "orange.png", 15);
        MeleeWeapon item2 = new MeleeWeapon("knife", "knife.png", 20, 1);
        Item item3 = new Item("box", "chest_object_1.png");

        PlayerInventory playerInventory = new PlayerInventory();

        playerInventory.addItem(item);
        playerInventory.addItem(item2);
        playerInventory.addItem(item3);

        player.setPlayerInventory(playerInventory);
        player.getPlayerInventory().setMoney(60);
        return player;
    }

    public static void main(String[] args) {
        launch();
    }
}