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
        removeUnnecessaryFiles();
        Player player = createPlayer();
        // Load and start the game
        GameLogic gameLogic = new GameLogic(stage);
        Wagon wagon = new Wagon(0, "DEFAULT", "wall_3.png");
        Vendor vendor = new Vendor("vendor_test", "zombie_front.png", 10);
        vendor.setPositionX(300);
        vendor.setPositionY(240);
        vendor.setNegativeThreshold(1);
        Food item = new Food("orange", "orange.png", 15);
        item.setValue(10);
        MeleeWeapon item2 = new MeleeWeapon("knife", "knife.png", 20, 1);
        item2.setValue(50);
//        Item item3 = new Item("box", "chest_object_1.png");
        Firearm item4 = new Firearm("gun", "chest_object_1.png", 10, 1);
        item4.setValue(100);
        vendor.getVendorInventory().addItem(item);
        vendor.getVendorInventory().addItem(item2);
        vendor.getVendorInventory().addItem(item4);

        wagon.generateWagon();
//        wagon.addEntity(vendor);
        gameLogic.loadGame(player, wagon);
        gameLogic.start();
    }

    private Player createPlayer() {
        Player player = new Player("PLAYER_NAME", "player_front.png", 350, 200);

        player.setHitBoxSize(1);
        player.setAttackRangeSize(1);
        player.setCooldown(2);

        PlayerInventory playerInventory = new PlayerInventory();
        RandomHandler randomHandler = new RandomHandler();
        playerInventory.addItem(randomHandler.getRandomDefaultItem());
        playerInventory.addItem(randomHandler.getRandomFoodItem());
        playerInventory.addItem(randomHandler.getRandomMeleeItem());
        playerInventory.addItem(randomHandler.getRandomFirearmItem());

        player.setPlayerInventory(playerInventory);
        player.getPlayerInventory().setMoney(160);
        player.getPlayerInventory().setAmmo(50);

        for (String name : Constants.QUEST_NPC_NAMES) {
            Events.addQuestNPC(new QuestNPC(name, name + "_front.png"));
        }
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);

        return player;
    }
    private void removeUnnecessaryFiles() {
        RandomHandler randomHandler = new RandomHandler();
        List<File> files = randomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/");
        for (File file : files) {
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete the file");
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}