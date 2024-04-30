package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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
    @JsonProperty("playerKidnapped")
    private boolean playerKidnapped = false;
    @JsonProperty("playerKilledGuard")
    private boolean playerKilledGuard = false;
    @JsonProperty("canSpawnKey")
    private boolean canSpawnKey = true;
    @JsonProperty("shouldCallGuard")
    private boolean shouldCallGuard = false;
    @JsonProperty("canSpawnTicket")
    private boolean canSpawnTicket = false;
    public EventsData() {
    }
    @JsonIgnore
    public List<QuestNPC> getAvailableQuestNPCs() {
        return availableQuestNPCs;
    }
    @JsonSetter("availableQuestNPCs")
    public void setAvailableQuestNPCs(List<QuestNPC> availableQuestNPCs) {
        this.availableQuestNPCs = availableQuestNPCs;
    }
    @JsonIgnore
    public Constants.Event getCurrentEvent() {
        return currentEvent;
    }
    @JsonSetter("currentEvent")
    public void setCurrentEvent(Constants.Event currentEvent) {
        this.currentEvent = currentEvent;
    }
    @JsonIgnore
    public boolean isCanSpawnLockedDoor() {
        return canSpawnLockedDoor;
    }
    @JsonSetter("canSpawnLockedDoor")
    public void setCanSpawnLockedDoor(boolean canSpawnLockedDoor) {
        this.canSpawnLockedDoor = canSpawnLockedDoor;
    }
    @JsonIgnore
    public int getTimeLoopCounter() {
        return timeLoopCounter;
    }
    @JsonSetter("timeLoopCounter")
    public void setTimeLoopCounter(int timeLoopCounter) {
        this.timeLoopCounter = timeLoopCounter;
    }
    @JsonIgnore
    public Constants.Event getNextEvent() {
        return nextEvent;
    }
    @JsonSetter("nextEvent")
    public void setNextEvent(Constants.Event nextEvent) {
        this.nextEvent = nextEvent;
    }
    @JsonIgnore
    public boolean isPlayerKidnapped() {
        return playerKidnapped;
    }
    @JsonSetter("playerKidnapped")
    public void setPlayerKidnapped(boolean playerKidnapped) {
        this.playerKidnapped = playerKidnapped;
    }
    @JsonIgnore
    public boolean isPlayerKilledGuard() {
        return playerKilledGuard;
    }
    @JsonSetter("playerKilledGuard")
    public void setPlayerKilledGuard(boolean playerKilledGuard) {
        this.playerKilledGuard = playerKilledGuard;
    }
    @JsonIgnore
    public boolean isCanSpawnKey() {
        return canSpawnKey;
    }
    @JsonSetter("canSpawnKey")
    public void setCanSpawnKey(boolean canSpawnKey) {
        this.canSpawnKey = canSpawnKey;
    }
    @JsonIgnore
    public boolean isShouldCallGuard() {
        return shouldCallGuard;
    }
    @JsonSetter("shouldCallGuard")
    public void setShouldCallGuard(boolean shouldCallGuard) {
        this.shouldCallGuard = shouldCallGuard;
    }
    @JsonIgnore
    public boolean isCanSpawnTicket() {
        return canSpawnTicket;
    }
    @JsonSetter("canSpawnTicket")
    public void setCanSpawnTicket(boolean canSpawnTicket) {
        this.canSpawnTicket = canSpawnTicket;
    }
}
