package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.QuestNPC;

import java.util.List;

public class EventsData {
    @JsonProperty("availableQuestNPCs")
    private List<QuestNPC> availableQuestNPCs;
    @JsonProperty("currentEvent")
    private Constants.Event currentEvent;
    @JsonProperty("canSpawnLockedDoor")
    private boolean canSpawnLockedDoor;
    @JsonProperty("timeLoopCounter")
    private int timeLoopCounter;
    @JsonProperty("nextEvent")
    private Constants.Event nextEvent;
    @JsonCreator
    public EventsData(@JsonProperty("availableQuestNPCs") List<QuestNPC> availableQuestNPCs, @JsonProperty("currentEvent") Constants.Event currentEvent, @JsonProperty("canSpawnLockedDoor") boolean canSpawnLockedDoor, @JsonProperty("timeLoopCounter") int timeLoopCounter, @JsonProperty("nextEvent") Constants.Event nextEvent) {
        this.availableQuestNPCs = availableQuestNPCs;
        this.currentEvent = currentEvent;
        this.canSpawnLockedDoor = canSpawnLockedDoor;
        this.timeLoopCounter = timeLoopCounter;
        this.nextEvent = nextEvent;
    }
    @JsonIgnore
    public List<QuestNPC> getAvailableQuestNPCs() {
        return availableQuestNPCs;
    }
    @JsonIgnore
    public Constants.Event getCurrentEvent() {
        return currentEvent;
    }
    @JsonIgnore
    public boolean isCanSpawnLockedDoor() {
        return canSpawnLockedDoor;
    }
    @JsonIgnore
    public int getTimeLoopCounter() {
        return timeLoopCounter;
    }
    @JsonIgnore
    public Constants.Event getNextEvent() {
        return nextEvent;
    }
}
