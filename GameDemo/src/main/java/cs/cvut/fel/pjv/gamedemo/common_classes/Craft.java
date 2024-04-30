package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Represents a craft of two items, responsible for crafting logic.
 */
public class Craft {

    //region Attributes
    private static final Logger logger = LogManager.getLogger(Craft.class);
    //endregion

    //region Constructors
    public Craft() {
    }
    //endregion

    //region Methods
    /**
     * Returns a new item if the combination of two items is in @../craft/craft.json; does not depend on the order of the items.
     * @param firstItem first item to craft
     * @param secondItem second item to craft
     * @return result item
     */
    public Item craft(Item firstItem, Item secondItem) {
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Trying to craft " + firstItem.getName() + " and " + secondItem.getName() + "...");
        try {
            // Read craft recipes from file
            logger.info("Reading craft recipes from file...");
            byte[] jsonData = Files.readAllBytes(Paths.get("craft/craft.json"));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode craftRecipes = rootNode.get("craft_recipes");

            // Normalize item names
            String firstItemName = firstItem.getName().toLowerCase().replace(" ", "_");
            String secondItemName = secondItem.getName().toLowerCase().replace(" ", "_");

            // Find result item name
            String resultItemName = "";
            for (JsonNode node : craftRecipes) {
                String item1 = node.get("item1").asText();
                String item2 = node.get("item2").asText();
                if ((item1.equals(firstItemName) && item2.equals(secondItemName)) ||
                        (item1.equals(secondItemName) && item2.equals(firstItemName))) {
                    resultItemName = node.get("result").asText();
                    logger.info("Recipe found, result item will be: " + resultItemName);
                    break;
                }
            }

            // Read crafted items data
            logger.info("Reading crafted items data...");
            jsonData = Files.readAllBytes(Paths.get("items/crafted_only_items.json"));
            rootNode = objectMapper.readTree(jsonData);
            JsonNode craftedItems = rootNode.get("crafted_only_items");

            // Find and parse result item info
            for (JsonNode craftedItem : craftedItems) {
                JsonNode itemInfo = craftedItem.get(resultItemName);
                if (itemInfo != null) {
                    return parseItemInfo(itemInfo);
                }
            }
        } catch (Exception e) {
            logger.error("Error while crafting items: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parses item info from JSON node.
     * @param itemInfo JSON node with item info
     * @return parsed item
     */
    private Item parseItemInfo(JsonNode itemInfo) {
        String name = itemInfo.findValue("name").asText();
        String texturePath = itemInfo.findValue("texturePath").asText();
        String itemType = itemInfo.findValue("itemType").asText().toUpperCase();

        // Map optional fields to their respective values to avoid null pointer exceptions
        Optional<Integer> damageOpt = Optional.ofNullable(itemInfo.findValue("damage")).map(JsonNode::asInt);
        Optional<Integer> attackSpeedOpt = Optional.ofNullable(itemInfo.findValue("attackSpeed")).map(JsonNode::asInt);
        Optional<Integer> shootingSpeedOpt = Optional.ofNullable(itemInfo.findValue("shootingSpeed")).map(JsonNode::asInt);
        Optional<Integer> nourishmentOpt = Optional.ofNullable(itemInfo.findValue("nourishment")).map(JsonNode::asInt);

        Constants.ItemType type = Constants.ItemType.valueOf(itemType);

        logger.info("Crafted item info parsed successfully");
        logger.info("Crafted item: " + name + ", type: " + type + ", texture path: " + texturePath);

        return switch (type) {
            case MELEE_WEAPON -> new MeleeWeapon(name, texturePath, damageOpt.orElse(0), attackSpeedOpt.orElse(0));
            case FIREARM -> new Firearm(name, texturePath, damageOpt.orElse(0), shootingSpeedOpt.orElse(0));
            case FOOD -> new Food(name, texturePath, nourishmentOpt.orElse(0));
            default -> new Item(name, texturePath, type);
        };
    }
    //endregion
}