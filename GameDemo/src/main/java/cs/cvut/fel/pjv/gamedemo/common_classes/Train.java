package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a train with wagons.
 * Stores an array of wagons.
 * Can add or remove wagons.
 * Needed for the game to store wagons, their connections and contents.
 */
public class Train {
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
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == null) {
                System.out.println("Wagon " + wagon.getId() + " added to the train.");
                wagonsArray[i] = wagon;
                return;
            }
        }
        //if no empty space is found, remove the oldest wagon (with the lowest id)
        removeWagon(wagonsArray[findMinWagonId()]);
        addWagon(wagon);
    }
    @JsonIgnore
    public Wagon[] getWagonsArray() {
        return wagonsArray;
    }
    @JsonIgnore
    public void removeWagon(Wagon wagon) {//when wagon deleted, other wagon door that leads to it will have empty targetId (will behave as locked, new wagon will not generate)
                                            //it could be solved, but it is not necessary for the game (player will not be able to enter the wagon behind the conductor (removed wagon))
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == wagon) {
                wagonsArray[i] = null;
            }
        }
        //TODO
        //if there is conductor in the wagon, move him to the nearest wagon (next with the lowest id)
    }
    @JsonIgnore
    private int findMinWagonId() {
        int minId = wagonsArray[0].getId();
        for (int i = 1; i < wagonsArray.length; i++) {
            if (wagonsArray[i].getId() < minId) {
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
}