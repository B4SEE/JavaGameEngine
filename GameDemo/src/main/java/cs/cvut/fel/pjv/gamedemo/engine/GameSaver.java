package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GameSaver {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(GameSaver.class);
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
    @JsonIgnore
    public GameSaver(Player player, Train train, int currentWagonIndex) {
        this.player = player;
        this.train = train;
        this.currentWagonIndex = currentWagonIndex;
        setCurrentWagon(train.getWagonsArray()[currentWagonIndex]);
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
    public Player getPlayer() {
        return player;
    }
    @JsonIgnore
    public Train getTrain() {
        return train;
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
        logger.info("Saving game...");
        Date currentDate = new Date();
        String date = currentDate.toString().replaceAll("\\s", "_");
        //remove unnecessary characters
        date = date.replaceAll(":", "_");
        String path = "saves/save_" + date;
        //create folder
        File folder = new File(path);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                logger.info("Folder " + path + " created");
            } else {
                logger.error("Failed to create folder " + path);
            }
        }
        //save game
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/game.json");
            objectMapper.writeValue(file, this);
            logger.info("Game saved");
        } catch (IOException e) {
            logger.error("Failed to save game");
        }

        //save player
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/player.json");
            objectMapper.writeValue(file, player);
            logger.info("Player saved");
        } catch (IOException e) {
            logger.error("Failed to save player");
        }
        //save player inventory
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/player_inventory.json");
            objectMapper.writeValue(file, player.getPlayerInventory());
            logger.info("Player inventory saved");
        } catch (IOException e) {
            logger.error("Failed to save player inventory");
        }
        //save Events: availableQuestNPCs, currentEvent, canSpawnLockedDoor, timeLoopCounter, nextEvent
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/events.json");
            EventsData eventsData = getEventsData();
            objectMapper.writeValue(file, eventsData);
            logger.info("Events saved");
        } catch (IOException e) {
            logger.error("Failed to save events");
        }
    }

    private static EventsData getEventsData() {
        EventsData eventsData = new EventsData();
        eventsData.setAvailableQuestNPCs(Events.getAvailableQuestNPCs());
        eventsData.setCurrentEvent(Events.getCurrentEvent());
        eventsData.setCanSpawnLockedDoor(Events.canSpawnLockedDoor());
        eventsData.setTimeLoopCounter(Events.getTimeLoopCounter());
        eventsData.setNextEvent(Events.getNextEvent());
        eventsData.setPlayerKidnapped(Events.isPlayerKidnapped());
        eventsData.setPlayerKilledGuard(Events.isPlayerKilledGuard());
        eventsData.setCanSpawnKey(Events.canSpawnKey());
        eventsData.setShouldCallGuard(Events.shouldCallGuard());
        return eventsData;
    }

    @JsonIgnore
    public void loadGame() {
        logger.info("Loading game...");
        //find last save
        List<File> files = RandomHandler.getListOfDirectoriesThatStartWith("save_", "saves/");
        //return if there are no saves
        if (files.isEmpty()) return;

        File lastSave = files.stream()
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        //load game
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //disable fail on unknown properties
            objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //register subtypes
            objectMapper.registerSubtypes(
                    Player.class,
                    QuestNPC.class,
                    Vendor.class,
                    Door.class,
                    Firearm.class,
                    MeleeWeapon.class,
                    Food.class,
                    PlayerInventory.class
            );

            GameSaver game = objectMapper.readValue(new File(lastSave.getPath() + "/game.json"), GameSaver.class);
            logger.info("Game loaded");
            train = game.train;
            logger.info("Train loaded");
            //set current wagon for all entities (it is not saved in json)
            for (Wagon wagon : train.getWagonsArray()) {
                if (wagon != null) wagon.updateEntities();
            }
            logger.info("Wagons updated");

            currentWagon = train.getWagonsArray()[game.currentWagonIndex];
            logger.info("Current wagon loaded");

            player = objectMapper.readValue(new File(lastSave.getPath() + "/player.json"), Player.class);
            logger.info("Player loaded");
            player.setPlayerInventory(objectMapper.readValue(new File(lastSave.getPath() + "/player_inventory.json"), PlayerInventory.class));
            logger.info("Player inventory loaded");
            player.setCurrentWagon(currentWagon);

            //load events
            EventsData eventsData = objectMapper.readValue(new File(lastSave.getPath() + "/events.json"), EventsData.class);
            Events.setAvailableQuestNPCs(eventsData.getAvailableQuestNPCs());
            Events.setCurrentEvent(eventsData.getCurrentEvent());
            Events.setCanSpawnLockedDoor(eventsData.isCanSpawnLockedDoor());
            Events.setTimeLoopCounter(eventsData.getTimeLoopCounter());
            Events.setNextEvent(eventsData.getNextEvent());
            Events.setPlayerKidnapped(eventsData.isPlayerKidnapped());
            Events.setPlayerKilledGuard(eventsData.isPlayerKilledGuard());
            Events.setCanSpawnKey(eventsData.isCanSpawnKey());
            Events.setShouldCallGuard(eventsData.isShouldCallGuard());
            logger.info("Events loaded");
        } catch (IOException e) {
            logger.error("Failed to load game");
        }
    }
    @JsonIgnore
    public void startGame() {
        if (stage == null) {
            return;
        }
        removeUnnecessaryFiles();
        if (player == null || currentWagon == null || train == null) {//start new game
            logger.info("Starting new game...");
            prepareEvents();
            createPlayer();
            createWagon();
            createTrain();
        }
        GameLogic gameLogic = new GameLogic(stage, train);
        gameLogic.loadGame(player, currentWagon);
        gameLogic.start();
    }
    @JsonIgnore
    public void prepareEvents() {//only for new game
        logger.info("Preparing events");
        for (String name : Constants.QUEST_NPC_NAMES) {
            logger.info("Adding quest NPC " + name + " to availableQuestNPCs...");
            Events.addQuestNPC(new QuestNPC(name, "textures/default/entities/npcs/quest/" + name + "_front.png"));
            logger.info("Quest NPC " + name + " added to availableQuestNPCs");
        }
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
        Events.setCanSpawnKey(false);
        logger.info("Events prepared");
    }
    @JsonIgnore
    public void createPlayer() {
        logger.info("Creating new player");
        this.player = new Player(Constants.PLAYER_START_POS_X, Constants.PLAYER_START_POS_Y);
        player.getPlayerInventory().setAmmo(50);
        player.getPlayerInventory().setMoney(100);
        player.getPlayerInventory().addItem(new Item("ticket", "orange.png", Constants.ItemType.VALID_TICKET));
        logger.info("New player created");
    }
    @JsonIgnore
    public void createWagon() {
        logger.info("Creating new wagon");
        String randomWagonType = RandomHandler.getRandomWagonType();
        Wagon wagon = new Wagon(0, randomWagonType);
        wagon.generateWagon();
        wagon.getDoorRight().lock();
        this.currentWagon = wagon;
        logger.info("New wagon created");
    }
    @JsonIgnore
    public void createTrain() {
        logger.info("Creating new train");
        Train train = new Train();
        train.addWagon(currentWagon);
        this.train = train;
        logger.info("New train created");
    }
    @JsonIgnore
    private void removeUnnecessaryFiles() {
        List<File> files = RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/");
        for (File file : files) {
            if (file.delete()) {
                logger.info("File " + file.getName() + " deleted");
            } else {
                logger.error("Failed to delete file " + file.getName());
            }
        }
    }
    @JsonIgnore
    public void resetGame() {
        this.player = null;
        this.currentWagon = null;
        this.train = null;
        logger.info("Game reset");
    }
}