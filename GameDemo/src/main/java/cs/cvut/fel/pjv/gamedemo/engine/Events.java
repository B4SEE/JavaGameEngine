package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.QuestNPC;

import java.util.List;

/**
 * Stores information about the occurred events and other global variables (!Constants).
 */
public class Events {
    private static List<QuestNPC> availableQuestNPCs = new java.util.ArrayList<>();
    private static Constants.Event currentEvent;
    private static boolean canSpawnLockedDoor = false;
    private static int timeLoopCounter = 0;
    private static Constants.Event nextEvent;
    private static boolean canSaveGame = true;
    private static boolean isBossFight = false;
    private static boolean conductorSpawned = false;
    private static boolean grandmotherSpawned = false;
    private static boolean playerKidnapped = false;
    private static boolean guardCalled = false;
    private static boolean playerKilledGuard = false;
    private static boolean canSpawnKey = true;
    private static boolean shouldCallGuard = false;
    private static boolean canSpawnTicket = false;
    private static String era = Constants.DEFAULT_ERA;
    public Events(){
    }
    public static List<QuestNPC> getAvailableQuestNPCs() {
        return availableQuestNPCs;
    }
    public static void addQuestNPC(QuestNPC questNPC) {
        availableQuestNPCs.add(questNPC);
    }
    public static Constants.Event getCurrentEvent() {
        return currentEvent;
    }
    public static void setCurrentEvent(Constants.Event currentEvent) {
        Events.currentEvent = currentEvent;
    }
    public static boolean canSpawnLockedDoor() {
        return canSpawnLockedDoor;
    }
    public static void setCanSpawnLockedDoor(boolean canSpawnLockedDoor) {
        Events.canSpawnLockedDoor = canSpawnLockedDoor;
    }
    public static int getTimeLoopCounter() {
        return timeLoopCounter;
    }
    public static void decrementTimeLoopCounter() {
        timeLoopCounter--;
    }
    public static void setTimeLoopCounter(int timeLoopCounter) {
        Events.timeLoopCounter = timeLoopCounter;
    }
    public static Constants.Event getNextEvent() {
        return nextEvent;
    }
    public static void setNextEvent(Constants.Event nextEvent) {
        Events.nextEvent = nextEvent;
    }
    public static void setAvailableQuestNPCs(List<QuestNPC> availableQuestNPCs) {
        Events.availableQuestNPCs = availableQuestNPCs;
    }
    public static boolean canSaveGame() {
        return canSaveGame;
    }
    public static void setCanSaveGame(boolean canSaveGame) {
        Events.canSaveGame = canSaveGame;
    }

    public static boolean isBossFight() {
        return isBossFight;
    }

    public static void setBossFight(boolean bossFight) {
        isBossFight = bossFight;
    }

    public static boolean isConductorSpawned() {
        return conductorSpawned;
    }

    public static void setConductorSpawned(boolean conductorSpawned) {
        Events.conductorSpawned = conductorSpawned;
    }

    public static boolean isGrandmotherSpawned() {
        return grandmotherSpawned;
    }

    public static void setGrandmotherSpawned(boolean grandmotherSpawned) {
        Events.grandmotherSpawned = grandmotherSpawned;
    }

    public static boolean isPlayerKidnapped() {
        return playerKidnapped;
    }

    public static void setPlayerKidnapped(boolean playerKidnapped) {
        Events.playerKidnapped = playerKidnapped;
    }

    public static boolean isGuardCalled() {
        return guardCalled;
    }

    public static void setGuardCalled(boolean guardCalled) {
        Events.guardCalled = guardCalled;
    }

    public static boolean isPlayerKilledGuard() {
        return playerKilledGuard;
    }

    public static void setPlayerKilledGuard(boolean playerKilledGuard) {
        Events.playerKilledGuard = playerKilledGuard;
    }

    public static boolean canSpawnKey() {
        return canSpawnKey;
    }

    public static void setCanSpawnKey(boolean canSpawnKey) {
        Events.canSpawnKey = canSpawnKey;
    }

    public static boolean shouldCallGuard() {
        return shouldCallGuard;
    }

    public static void setShouldCallGuard(boolean shouldCallGuard) {
        Events.shouldCallGuard = shouldCallGuard;
    }
    public static boolean canSpawnTicket() {
        return canSpawnTicket;
    }
    public static void setCanSpawnTicket(boolean canSpawnTicket) {
        Events.canSpawnTicket = canSpawnTicket;
    }
    public static String getEra() {
        return era;
    }
    public static void setEra(String era) {
        Events.era = era;
    }
    public static void resetAll() {
        availableQuestNPCs = new java.util.ArrayList<>();
        currentEvent = null;
        canSpawnLockedDoor = false;
        timeLoopCounter = 0;
        nextEvent = null;
        canSaveGame = true;
        isBossFight = false;
        conductorSpawned = false;
        grandmotherSpawned = false;
        playerKidnapped = false;
        guardCalled = false;
        playerKilledGuard = false;
        canSpawnKey = true;
        shouldCallGuard = false;
        canSpawnTicket = false;
        era = Constants.DEFAULT_ERA;
    }
}
