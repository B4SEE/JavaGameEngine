package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;

import java.io.FileWriter;
import java.io.IOException;

public class QuestNPC extends Entity {
    private boolean questCompleted = false;
    private Item questItem;

    public QuestNPC(String name, String texturePath) {
        super(name, texturePath);
        super.setAsDefaultNPC();
        super.setType(Constants.EntityType.QUEST_NPC);
        setDialoguePath(name + "_start.json");
        String nameWithoutSpaces = name.replaceAll("\\s", "");
        this.questItem = RandomHandler.getQuestItem(nameWithoutSpaces);
        writeQuestItemToNecessaryToSpawnItems();
    }
    public Item getQuestItem() {
        return questItem;
    }

    public void setQuestItem(Item questItem) {
        this.questItem = questItem;
    }
    public boolean isQuestCompleted() {
        return questCompleted;
    }

    public void setQuestCompleted(boolean questCompleted) {
        this.questCompleted = questCompleted;
    }

    public boolean checkIfPlayerHasQuestItem(Player player) {
        if (player.getPlayerInventory().containsWithSameName(questItem)) {
            player.getPlayerInventory().removeItem(questItem);
            return true;
        }
        return false;
    }
    private void writeQuestItemToNecessaryToSpawnItems() {
        //write to necessary_to_spawn_item.json
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/");
            int count = RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/").size();
            FileWriter file = new FileWriter("items/necessary_to_spawn_item_" + count + ".json");
            objectMapper.writeValue(file, questItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
