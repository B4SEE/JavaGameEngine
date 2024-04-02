package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;

import java.io.FileWriter;
import java.io.IOException;
public class QuestNPC extends Entity {
    @JsonProperty("questCompleted")
    private boolean questCompleted = false;
    @JsonProperty("questItem")
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
    @JsonCreator
    public QuestNPC(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("questCompleted") boolean questCompleted, @JsonProperty("questItem") Item questItem) {
        super(name, texturePath);
        super.setAsDefaultNPC();
        super.setType(Constants.EntityType.QUEST_NPC);
        this.questCompleted = questCompleted;
        this.questItem = questItem;
        if (!questCompleted) {
            writeQuestItemToNecessaryToSpawnItems();
        }
    }
    @JsonIgnore
    public Item getQuestItem() {
        return questItem;
    }
    @JsonIgnore
    public void setQuestItem(Item questItem) {
        this.questItem = questItem;
    }
    @JsonIgnore
    public boolean isQuestCompleted() {
        return questCompleted;
    }
    @JsonIgnore
    public void setQuestCompleted(boolean questCompleted) {
        this.questCompleted = questCompleted;
    }
    @JsonIgnore
    public boolean checkIfPlayerHasQuestItem(Player player) {
        if (player.getPlayerInventory().getWithSameName(questItem) != null) {
            player.getPlayerInventory().removeItem(player.getPlayerInventory().getWithSameName(questItem));
            return true;
        }
        return false;
    }
    @JsonIgnore
    private void writeQuestItemToNecessaryToSpawnItems() {
        //write to necessary_to_spawn_item.json
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            int count = RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/").size();
            FileWriter file = new FileWriter("items/necessary_to_spawn_item_" + count + ".json");
            objectMapper.writeValue(file, questItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
