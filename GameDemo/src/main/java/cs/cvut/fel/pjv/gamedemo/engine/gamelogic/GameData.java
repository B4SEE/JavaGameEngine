package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import cs.cvut.fel.pjv.gamedemo.common_classes.Train;
import cs.cvut.fel.pjv.gamedemo.common_classes.Wagon;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class GameData {
    private static final Logger logger = LogManager.getLogger(GameData.class);
    private Isometric isometric;
    private final Stage stage;
    private int kidnapStage = 0;
    private int kidnapStageCount = 3;
    private long time;
    private Player player;
    private Entity boss = null;
    private final List<Entity> leftWagonPursuers = new java.util.ArrayList<>();
    private final List<Entity> rightWagonPursuers = new java.util.ArrayList<>();
    private Entity conductor;
    private Entity grandmother;
    private Wagon wagon;
    private Train train;
    private long eventStartTime = 0;
    private int eventDuration;
    private String deathMessage = "";
    private String hint = "";

    public GameData(Stage stage) {
        this.stage = stage;
    }

    /**
     * Reset all game data.
     */
    public void resetAll() {
        logger.debug("Resetting all game data...");
        leftWagonPursuers.clear();
        rightWagonPursuers.clear();
        kidnapStage = 0;
        kidnapStageCount = 3;
        time = 0;
        player = null;
        boss = null;
        eventStartTime = 0;
        eventDuration = 0;
        deathMessage = "";
        hint = "";
        logger.info("All game data reset.");
    }

    //region Getters

    public Isometric getIsometric() {
        return isometric;
    }

    public Stage getStage() {
        return stage;
    }
    public int getKidnapStage() {
        return kidnapStage;
    }
    public int getKidnapStageCount() {
        return kidnapStageCount;
    }

    public long getTime() {
        return time;
    }

    public Player getPlayer() {
        return player;
    }
    public Entity getBoss() {
        return boss;
    }
    public List<Entity> getLeftWagonPursuers() {
        return leftWagonPursuers;
    }
    public List<Entity> getRightWagonPursuers() {
        return rightWagonPursuers;
    }

    public Entity getConductor() {
        return conductor;
    }
    public Entity getGrandmother() {
        return grandmother;
    }

    public Wagon getWagon() {
        return wagon;
    }

    public Train getTrain() {
        return train;
    }
    public long getEventStartTime() {
        return eventStartTime;
    }
    public int getEventDuration() {
        return eventDuration;
    }
    public String getDeathMessage() {
        return deathMessage;
    }
    public String getHint() {
        return hint;
    }

    //endregion

    //region Setters

    public void setIsometric(Isometric isometric) {
        this.isometric = isometric;
    }
    public void setKidnapStage(int kidnapStage) {
        this.kidnapStage = kidnapStage;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setBoss(Entity boss) {
        this.boss = boss;
    }

    public void setConductor(Entity conductor) {
        this.conductor = conductor;
    }
    public void setGrandmother(Entity grandmother) {
        this.grandmother = grandmother;
    }

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
    public void setEventStartTime(long eventStartTime) {
        this.eventStartTime = eventStartTime;
    }
    public void setEventDuration(int eventDuration) {
        this.eventDuration = eventDuration;
    }
    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }
    //endregion
}
