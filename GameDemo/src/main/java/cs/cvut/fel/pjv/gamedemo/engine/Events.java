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
    public static List<QuestNPC> getAvailableQuestNPCs() {
        return availableQuestNPCs;
    }
    public static void addQuestNPC(QuestNPC questNPC) {
        availableQuestNPCs.add(questNPC);
    }
    public static void removeQuestNPC(QuestNPC questNPC) {
        availableQuestNPCs.remove(questNPC);
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
}
