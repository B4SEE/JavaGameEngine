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
}
