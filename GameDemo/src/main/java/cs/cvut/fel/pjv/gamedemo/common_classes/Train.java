package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cs.cvut.fel.pjv.gamedemo.Main;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Represents a train with wagons.
 * Stores an array of wagons.
 * Can add or remove wagons.
 * Needed for the game to store wagons, their connections and contents.
 */
public class Train {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Train.class);
    @JsonProperty
    private final Wagon[] wagonsArray;

    public Train() {
        wagonsArray = new Wagon[Constants.TRAIN_WAGONS];
    }
    @JsonCreator
    public Train(@JsonProperty("wagonsArray") Wagon[] wagonsArray) {
        this.wagonsArray = wagonsArray;
    }
    @JsonIgnore
    public void addWagon(Wagon wagon) {
        logger.info("Adding wagon " + wagon.getId() + " to the train...");
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == null) {
                wagonsArray[i] = wagon;
                logger.info("Wagon " + wagon.getId() + " added to the train");
                return;
            }
        }
        logger.info("No empty space found, removing the oldest wagon...");
        removeWagon(wagonsArray[findMinWagonId()]);
        addWagon(wagon);
    }
    @JsonIgnore
    public Wagon[] getWagonsArray() {
        return wagonsArray;
    }
    @JsonIgnore
    public void removeWagon(Wagon wagon) {
        logger.info("Removing wagon " + wagon.getId() + " from the train...");
        for (int i = 0; i < wagonsArray.length; i++) {
            Entity conductor = null;
            if (wagonsArray[i] == wagon) {
                if (Events.isConductorSpawned()) {
                    if (wagon.getConductor() != null) {
                        logger.info("Conductor found in wagon " + wagon.getId() + ", moving to the next wagon...");
                        conductor = wagon.getConductor();
                        getWagonById(findMinWagonId()).getEntities().add(conductor);
                        conductor.setCurrentWagon(getWagonById(findMinWagonId()));
                        logger.info("Conductor moved to wagon " + findMinWagonId());
                    }
                }
                wagonsArray[i] = null;
                logger.info("Wagon " + wagon.getId() + " removed from the train");
            }
        }
    }
    @JsonIgnore
    public int findMinWagonId() {
        int minId = wagonsArray[0].getId();
        for (int i = 1; i < wagonsArray.length; i++) {
            if (wagonsArray[i] != null && wagonsArray[i].getId() < minId) {
                minId = wagonsArray[i].getId();
            }
        }
        return minId;
    }
    @JsonIgnore
    public int findMaxWagonId() {
        int maxId = wagonsArray[0].getId();
        for (int i = 1; i < wagonsArray.length; i++) {
            if (wagonsArray[i] != null && wagonsArray[i].getId() > maxId) {
                maxId = wagonsArray[i].getId();
            }
        }
        return maxId;
    }
    public Wagon getWagonById(int id) {
        for (Wagon wagon : wagonsArray) {
            if (wagon != null && wagon.getId() == id) {
                return wagon;
            }
        }
        return null;
    }
    public int indexOf(Wagon wagon) {
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == wagon) {
                return i;
            }
        }
        return -1;
    }
}