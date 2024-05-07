package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.gamelogic.GameLogicMain;
import cs.cvut.fel.pjv.gamedemo.engine.gamelogic.GameManagement;
import cs.cvut.fel.pjv.gamedemo.engine.utils.RandomHandler;
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
    @JsonIgnore// Player will be saved in player.json
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
        //check if saves folder exists
        File savesFolder = new File("saves");
        if (!savesFolder.exists()) {
            if (savesFolder.mkdir()) {
                logger.debug("Folder saves created");
            } else {
                logger.error("Failed to create folder saves");
            }
        }
        String path = "saves/save_" + date;
        //create folder
        File folder = new File(path);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                logger.debug("Folder " + path + " created");
            } else {
                logger.error("Failed to create folder " + path);
            }
        }
        //save game
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/game.json");
            objectMapper.writeValue(file, this);
            logger.debug("Game saved");
        } catch (IOException e) {
            logger.error("Failed to save game");
        }

        //save player
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/player.json");
            objectMapper.writeValue(file, player);
            logger.debug("Player saved");
        } catch (IOException e) {
            logger.error("Failed to save player");
        }
        //save player inventory
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/player_inventory.json");
            objectMapper.writeValue(file, player.getPlayerInventory());
            logger.debug("Player inventory saved");
        } catch (IOException e) {
            logger.error("Failed to save player inventory");
        }
        //save Events: availableQuestNPCs, currentEvent, canSpawnLockedDoor, timeLoopCounter, nextEvent
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FileWriter file = new FileWriter(path + "/events.json");
            EventsData eventsData = getEventsData();
            objectMapper.writeValue(file, eventsData);
            logger.debug("Events saved");
        } catch (IOException e) {
            logger.error("Failed to save events");
        }
        logger.info("Game saved");
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
        eventsData.setCanSpawnTicket(Events.canSpawnTicket());
        eventsData.setEra(Events.getEra());
        return eventsData;
    }

    @JsonIgnore
    public void loadGame() {
        logger.info("Loading game...");
        List<File> files = RandomHandler.getListOfDirectoriesThatStartWith("save_", "saves/");
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
            logger.debug("Game loaded");
            train = game.train;
            logger.debug("Train loaded");
            //set current wagon for all entities (it is not saved in json)
            for (Wagon wagon : train.getWagonsArray()) {
                if (wagon != null) wagon.updateEntities();
            }
            logger.debug("Wagons updated");

            currentWagon = train.getWagonsArray()[game.currentWagonIndex];
            logger.debug("Current wagon loaded");

            player = objectMapper.readValue(new File(lastSave.getPath() + "/player.json"), Player.class);
            logger.debug("Player loaded");
            player.setPlayerInventory(objectMapper.readValue(new File(lastSave.getPath() + "/player_inventory.json"), PlayerInventory.class));
            logger.debug("Player inventory loaded");
            player.setCurrentWagon(currentWagon);
            logger.debug("Player current wagon set");

            loadEvents(objectMapper.readValue(new File(lastSave.getPath() + "/events.json"), EventsData.class));
            logger.info("Game loaded");
        } catch (IOException e) {
            logger.error("Failed to load game");
        }
    }
    @JsonIgnore
    private void loadEvents(EventsData eventsData) {
        logger.debug("Loading events...");
        Events.setAvailableQuestNPCs(eventsData.getAvailableQuestNPCs());
        Events.setCurrentEvent(eventsData.getCurrentEvent());
        Events.setCanSpawnLockedDoor(eventsData.isCanSpawnLockedDoor());
        Events.setTimeLoopCounter(eventsData.getTimeLoopCounter());
        Events.setNextEvent(eventsData.getNextEvent());
        Events.setPlayerKidnapped(eventsData.isPlayerKidnapped());
        Events.setPlayerKilledGuard(eventsData.isPlayerKilledGuard());
        Events.setCanSpawnKey(eventsData.isCanSpawnKey());
        Events.setShouldCallGuard(eventsData.isShouldCallGuard());
        Events.setCanSpawnTicket(eventsData.isCanSpawnTicket());
        Events.setEra(eventsData.getEra());
        logger.debug("Events loaded");
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
        GameLogicMain gameLogic = new GameLogicMain(stage, train);
        GameManagement.loadGame(player, currentWagon);
        GameManagement.start();
    }
    @JsonIgnore
    public void prepareEvents() {//only for new game
        logger.debug("Preparing events...");
        Events.resetAll();
        logger.debug("Resetting all events...");
        for (String name : Constants.QUEST_NPC_NAMES) {
            logger.debug("Adding quest NPC " + name + " to availableQuestNPCs...");
            Events.addQuestNPC(new QuestNPC(name, "textures/default/entities/npcs/quest/" + name + "_front.png"));
            logger.debug("Quest NPC " + name + " added to availableQuestNPCs");
        }
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
        Events.setCanSpawnKey(false);
        logger.info("Events prepared");
    }
    @JsonIgnore
    public void createPlayer() {
        logger.debug("Creating new player...");
        this.player = new Player(Constants.PLAYER_START_POS_X, Constants.PLAYER_START_POS_Y);
        logger.info("New player created");
    }
    @JsonIgnore
    public void createWagon() {
        logger.debug("Creating new wagon...");
        String randomWagonType = RandomHandler.getRandomWagonType();
        Wagon wagon = new Wagon(0, randomWagonType);
        wagon.generateWagon();
        wagon.getDoorRight().lock();
        this.currentWagon = wagon;
        logger.info("New wagon created");
    }
    @JsonIgnore
    public void createTrain() {
        logger.debug("Creating new train...");
        Train train = new Train();
        train.addWagon(currentWagon);
        this.train = train;
        logger.info("New train created");
    }
    @JsonIgnore
    public void removeUnnecessaryFiles() {
        List<File> files = RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/");
        for (File file : files) {
            if (file.delete()) {
                logger.debug("File " + file.getName() + " deleted");
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
        logger.info("Saved game reset");
    }
}