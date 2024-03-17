package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.GameLogic;
import javafx.application.Application;
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

        Inventory inventory = new Inventory(11);
        Item item = new Item("block", "block_wall.png");
        Item item2 = new Item("seat", "seat_1.png");
        Item item3 = new Item("box", "chest_object_1.png");
        Item item4 = new Item("box", "chest_object_1.png");
        Item item5 = new Item("box", "chest_object_1.png");
        Item item6 = new Item("box", "chest_object_1.png");
        Item item7 = new Item("box", "chest_object_1.png");
        Item item8 = new Item("box", "chest_object_1.png");
        Item item9 = new Item("block", "block_wall.png");
        Item item10 = new Item("seat", "seat_1.png");
        Item item11 = new Item("box", "chest_object_1.png");
        Item item12 = new Item("box", "chest_object_1.png");
        Item item13 = new Item("block", "block_wall.png");
        Item item14 = new Item("seat", "seat_1.png");
        Item item15 = new Item("box", "chest_object_1.png");
        Item item16 = new Item("block", "block_wall.png");
        Item item17 = new Item("seat", "seat_1.png");
        Item item18 = new Item("box", "chest_object_1.png");
        Item item19 = new Item("box", "chest_object_1.png");
        Item item20 = new Item("box", "chest_object_1.png");

        PlayerInventory playerInventory = new PlayerInventory();

        playerInventory.addItem(item);
        playerInventory.addItem(item2);
        playerInventory.addItem(item3);
        playerInventory.addItem(item4);
        playerInventory.addItem(item5);
        playerInventory.addItem(item6);
        playerInventory.addItem(item7);
        playerInventory.addItem(item8);
        playerInventory.addItem(item9);
        playerInventory.addItem(item10);
        playerInventory.addItem(item11);
        playerInventory.addItem(item12);
        playerInventory.addItem(item13);
        playerInventory.addItem(item14);
        playerInventory.addItem(item15);
        playerInventory.addItem(item16);
        playerInventory.addItem(item17);
        playerInventory.addItem(item18);
        playerInventory.addItem(item19);
        playerInventory.addItem(item20);
        player.setPlayerInventory(playerInventory);
        player.getPlayerInventory().setMoney(60);

        // Load and start the game
        GameLogic gameLogic = new GameLogic(stage);
        gameLogic.loadGame(player, new Wagon(0, "DEFAULT", "wall_3.png"));
        gameLogic.start();
    }

    public static void main(String[] args) {
        launch();
    }
}