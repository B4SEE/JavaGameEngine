package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class GameSaver {
    @JsonIgnore
    private Stage stage;
    @JsonIgnore//player will be saved in player.json
    private Player player;
    @JsonIgnore
    private Wagon currentWagon;
    @JsonProperty("train")
    private Train train;
    @JsonProperty("currentWagonIndex")
    private int currentWagonIndex;
    public GameSaver() {
    }
    public GameSaver(Player player, Train train) {
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
    @JsonSetter("currentWagonIndex")
    public void setCurrentWagonIndex(int currentWagonIndex) {
        this.currentWagonIndex = currentWagonIndex;
    }
    @JsonSetter("train")
    public void setTrain(Train train) {
        this.train = train;
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
        //save Events: availableQuestNPCs, currentEvent, canSpawnLockedDoor, timeLoopCounter, nextEvent
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/events.json");
            //Events class is static class, so there is need to save fields separately
            EventsData eventsData = new EventsData(Events.getAvailableQuestNPCs(), Events.getCurrentEvent(), Events.canSpawnLockedDoor(), Events.getTimeLoopCounter(), Events.getNextEvent());
            objectMapper.writeValue(file, eventsData);
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
            //
            GameSaver game = objectMapper.readValue(new File(lastSave.getPath() + "/game.json"), GameSaver.class);
            train = game.train;
            //set current wagon for all entities (it is not saved in json)
            for (Wagon wagon : train.getWagonsArray()) {
                if (wagon != null) {
                    wagon.updateEntities();
                }
            }
            currentWagon = train.getWagonsArray()[game.currentWagonIndex];
            player = objectMapper.readValue(new File(lastSave.getPath() + "/player.json"), Player.class);
            player.setPlayerInventory(objectMapper.readValue(new File(lastSave.getPath() + "/player_inventory.json"), PlayerInventory.class));
            player.setCurrentWagon(currentWagon);
            //load events
            EventsData eventsData = objectMapper.readValue(new File(lastSave.getPath() + "/events.json"), EventsData.class);
            Events.setAvailableQuestNPCs(eventsData.getAvailableQuestNPCs());
            Events.setCurrentEvent(eventsData.getCurrentEvent());
            Events.setCanSpawnLockedDoor(eventsData.isCanSpawnLockedDoor());
            Events.setTimeLoopCounter(eventsData.getTimeLoopCounter());
            Events.setNextEvent(eventsData.getNextEvent());
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
        player.getPlayerInventory().setAmmo(50);
    }
    @JsonIgnore
    private void createWagon() {
        String randomWagonType = RandomHandler.getRandomWagonType();
        Wagon wagon = new Wagon(0, randomWagonType);
        wagon.generateWagon();
        this.currentWagon = wagon;
        //place conductor in the wagon
        Entity conductor = new Entity("Conductor", "zombie_front.png");
        EntitiesCreator.setAsDefaultNPC(conductor);
        conductor.setType(Constants.EntityType.CONDUCTOR);
        conductor.setCurrentWagon(currentWagon);
        conductor.setPositionX(currentWagon.getDoorRightTarget().getIsoX());
        conductor.setPositionY(currentWagon.getDoorRightTarget().getIsoY());
        conductor.setSpeedX(8);
        conductor.setSpeedY(8);
        currentWagon.addEntity(conductor);
        System.out.println("Conductor added to the wagon");
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
    @JsonIgnore
    public void resetGame() {
        this.player = null;
        this.currentWagon = null;
        this.train = null;
    }
}