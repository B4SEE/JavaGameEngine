package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.engine.EntitiesCreator;
import cs.cvut.fel.pjv.gamedemo.engine.utils.RandomHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
public class QuestNPC extends Entity {

    //region Attributes
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(QuestNPC.class);
    @JsonProperty("questCompleted")
    private boolean questCompleted = false;
    @JsonProperty("questItem")
    private Item questItem;
    //endregion

    //region Constructors
    public QuestNPC(String name, String texturePath) {
        super(name, texturePath);
        EntitiesCreator.setAsDefaultNPC(this);
        super.setType(Constants.EntityType.QUEST_NPC);
        setDialoguePath(name + "_start.json");
        String nameWithoutSpaces = name.replaceAll("\\s", "");
        this.questItem = RandomHandler.getQuestItem(nameWithoutSpaces);
    }
    @JsonCreator
    public QuestNPC(@JsonProperty("name") String name, @JsonProperty("texturePath") String texturePath, @JsonProperty("questCompleted") boolean questCompleted, @JsonProperty("questItem") Item questItem) {
        super(name, texturePath);
        EntitiesCreator.setAsDefaultNPC(this);
        super.setType(Constants.EntityType.QUEST_NPC);
        this.questCompleted = questCompleted;
        this.questItem = questItem;
    }
    //endregion

    //region Methods
    @JsonIgnore
    public boolean checkIfPlayerHasQuestItem(Player player) {
        if (player.getPlayerInventory().getWithSameName(questItem) != null) {
            player.getPlayerInventory().removeItem(player.getPlayerInventory().getWithSameName(questItem));
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void writeQuestItemToNecessaryToSpawnItems() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            int count = RandomHandler.getListOfFilesThatStartWith("necessary_to_spawn_item", "items/").size();
            FileWriter file = new FileWriter("items/necessary_to_spawn_item_" + count + ".json");
            objectMapper.writeValue(file, questItem);
        } catch (IOException e) {
            logger.error("Error while writing to necessary_to_spawn_item.json: " + e.getMessage());
        }
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public boolean isQuestCompleted() {
        return questCompleted;
    }
    //endregion

    //region Setters
    @JsonIgnore
    public void setQuestCompleted(boolean questCompleted) {
        this.questCompleted = questCompleted;
    }
    //endregion

    //endregion
}
