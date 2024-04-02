package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.GameLogic;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Game {
    @JsonIgnore
    private Stage stage;
    @JsonIgnore//player will be saved in player.json
    private Player player;
    @JsonProperty("currentWagon")
    private Wagon currentWagon;
    @JsonProperty("train")
    private Train train;
    public Game() {
    }
    public Game(Player player, Train train) {
        this.player = player;
        this.currentWagon = player.getCurrentWagon();
        this.train = train;
    }
    @JsonIgnore
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @JsonIgnore
    public void setCurrentWagon(Wagon currentWagon) {
        this.currentWagon = currentWagon;
    }
    @JsonIgnore
    public Wagon getCurrentWagon() {
        return currentWagon;
    }
    @JsonIgnore
    public void saveGame() {
        //TODO: save Train class, Player class, Player inventory, currentWagon//save all to folder "save_date" (inside will be saved game.json, player.json and player_inventory.json)
        Date currentDate = new Date();
        String date = currentDate.toString().replaceAll("\\s", "_");
        //remove unnecessary characters
        date = date.replaceAll(":", "_");
        String path = "saves/save_" + date;
        //create folder
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        //save game
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/game.json");
            objectMapper.writeValue(file, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //save player
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/player.json");
            objectMapper.writeValue(file, player);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //save player inventory
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/player_inventory.json");
            objectMapper.writeValue(file, player.getPlayerInventory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @JsonIgnore
    public void loadGame() {
        //find last save
        List<File> files = RandomHandler.getListOfDirectoriesThatStartWith("save_", "saves/");
        System.out.println(files);
        if (files.isEmpty()) {
            return;
        }
        File lastSave = files.getFirst();
        for (File file : files) {
            if (file.lastModified() > lastSave.lastModified()) {
                lastSave = file;
            }
        }
        System.out.println("Last save: " + lastSave);
        //load game
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //disable fail on unknown properties
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //register subtypes
            objectMapper.registerSubtypes(Player.class);
            objectMapper.registerSubtypes(QuestNPC.class);
            objectMapper.registerSubtypes(Vendor.class);
            objectMapper.registerSubtypes(Door.class);
            objectMapper.registerSubtypes(Firearm.class);
            objectMapper.registerSubtypes(MeleeWeapon.class);
            objectMapper.registerSubtypes(Food.class);
            objectMapper.registerSubtypes(PlayerInventory.class);
            Game game = objectMapper.readValue(new File(lastSave.getPath() + "/game.json"), Game.class);
            this.currentWagon = game.currentWagon;
            this.train = game.train;
            this.player = objectMapper.readValue(new File(lastSave.getPath() + "/player.json"), Player.class);
            this.player.setPlayerInventory(objectMapper.readValue(new File(lastSave.getPath() + "/player_inventory.json"), PlayerInventory.class));
            this.player.setCurrentWagon(currentWagon);
            System.out.println("Game loaded successfully");
            System.out.println("Player: " + player);
            System.out.println("Current wagon: " + currentWagon);
            System.out.println("Train: " + train);
            //set current wagon for all entities (it is not saved in json)
            for (Wagon wagon : train.getWagonsArray()) {
                if (wagon != null) {
                    for (Entity entity : wagon.getEntities()) {
                        entity.setCurrentWagon(wagon);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @JsonIgnore
    public void startGame() {
        if (stage == null) {
            return;
        }
        removeUnnecessaryFiles();
        if (player == null || currentWagon == null || train == null) {//start new game
            System.out.println("Starting new game");
            prepareEvents();
            createPlayer();
            createWagon();
            createTrain();
            //TODO place player in the middle of the train
        }
        currentWagon.printAllObjects();
        GameLogic gameLogic = new GameLogic(stage, train);
        gameLogic.loadGame(player, currentWagon);
        gameLogic.start();
    }
    @JsonIgnore
    private void prepareEvents() {//only for new game
        for (String name : Constants.QUEST_NPC_NAMES) {
            Events.addQuestNPC(new QuestNPC(name, name + "_front.png"));
            System.out.println("Quest NPC added: " + name);
        }
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
    }
    @JsonIgnore
    private void createPlayer() {
        this.player = new Player(Constants.PLAYER_START_POS_X, Constants.PLAYER_START_POS_Y);
    }
    @JsonIgnore
    private void createWagon() {
        String randomWagonType = RandomHandler.getRandomWagonType();
        Wagon wagon = new Wagon(0, randomWagonType);
        wagon.generateWagon();
        this.currentWagon = wagon;
    }
    @JsonIgnore
    private void createTrain() {
        Train train = new Train();
        train.addWagon(currentWagon);
        this.train = train;
    }
    @JsonIgnore
    private void removeUnnecessaryFiles() {
        List<File> files = RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/");
        for (File file : files) {
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete the file");
            }
        }
    }
}