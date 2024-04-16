package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class RandomHandler {
    private static final Logger logger = LogManager.getLogger(RandomHandler.class);
    public  RandomHandler() {
    }
    public static String getRandomDialogueThatStartsWith(String start) {
        File folder = new File("dialogues/");
        File[] listOfFiles = folder.listFiles();
        List<File> listOfFilesThatStartWith = new java.util.ArrayList<>();
        String randomDialogue;
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    //check if file name starts with start
                    if (file.getName().startsWith(start)) {
                        listOfFilesThatStartWith.add(file);
                    }
                }
            }
        }
        randomDialogue = listOfFilesThatStartWith.get((int) (Math.random() * listOfFilesThatStartWith.size())).getName();
        return randomDialogue;
    }
    public static Item getRandomDefaultItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        Item item = null;
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("items/default_items.json"));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode items = rootNode.get("default_items");
            JsonNode randomItem = items.get((int) (Math.random() * items.size()));
            item = new Item(randomItem.get("name").asText(), randomItem.get("texturePath").asText(), randomItem.get("value").asInt());
            logger.info("Default item generated");
        } catch (Exception e) {
            logger.error("Error while reading default items: " + e);
        }
        return item;
    }

    public static Food getRandomFoodItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        Food food = null;
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("items/food_items.json"));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode items = rootNode.get("food_items");
            JsonNode randomItem = items.get((int) (Math.random() * items.size()));
            food = new Food(randomItem.get("name").asText(), randomItem.get("texturePath").asText(), randomItem.get("nourishment").asInt());
            food.setValue(randomItem.get("value").asInt());
            logger.info("Food item generated");
        } catch (Exception e) {
            logger.error("Error while reading food items: " + e);
        }
        return food;
    }
    public static MeleeWeapon getRandomMeleeItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        MeleeWeapon melee = null;
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("items/melee_items.json"));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode items = rootNode.get("melee_items");
            JsonNode randomItem = items.get((int) (Math.random() * items.size()));
            melee = new MeleeWeapon(randomItem.get("name").asText(), randomItem.get("texturePath").asText(), randomItem.get("damage").asInt(), randomItem.get("attackSpeed").asInt());
            melee.setValue(randomItem.get("value").asInt());
            logger.info("Melee item generated");
        } catch (Exception e) {
            logger.error("Error while reading melee items: " + e);
        }
        return melee;
    }

    public static Firearm getRandomFirearmItem() {
        ObjectMapper objectMapper = new ObjectMapper();
        Firearm firearm = null;
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("items/firearm_items.json"));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode items = rootNode.get("firearm_items");
            JsonNode randomItem = items.get((int) (Math.random() * items.size()));
            firearm = new Firearm(randomItem.get("name").asText(), randomItem.get("texturePath").asText(), randomItem.get("damage").asInt(), randomItem.get("shootingSpeed").asInt());
            firearm.setValue(randomItem.get("value").asInt());
            logger.info("Firearm generated");
        } catch (Exception e) {
            logger.error("Error while reading firearm items: " + e);
        }
        return firearm;
    }
    public static Item getQuestItem(String name) {
        ObjectMapper objectMapper = new ObjectMapper();
        Item questItem = null;
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("items/" + name + ".json"));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode item = rootNode.get("quest_item_data");
            questItem = new Item(item.get("name").asText(), item.get("texturePath").asText(), item.get("value").asInt());
            logger.info("Quest item generated");
        } catch (Exception e) {
            logger.error("Error while reading quest item: " + e);
        }
        return questItem;
    }
    public static String getRandomWagonTypeLayout(String wagonType) {
        //list all files from wagons directory
        List<File> listOfFilesThatStartWith = getListOfFilesThatStartWith(wagonType, "maps/common");
        String randomWagon;
        //check custom maps folder
        listOfFilesThatStartWith.addAll(getListOfFilesThatStartWith(wagonType, "maps/custom"));
        //get path
        randomWagon = listOfFilesThatStartWith.get((int) (Math.random() * listOfFilesThatStartWith.size())).getPath();
        return randomWagon;
    }
    public static List<File> getListOfFilesThatStartWith(String start, String path) {
        //list all files from path directory
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<File> listOfFilesThatStartWith = new java.util.ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    //check if file name starts with start
                    if (file.getName().startsWith(start)) {
                        listOfFilesThatStartWith.add(file);
                    }
                }
            }
        }
        return listOfFilesThatStartWith;
    }
    public static List<File> getListOfDirectoriesThatStartWith(String start, String path) {
        //list all files from path directory
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<File> listOfFilesThatStartWith = new java.util.ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isDirectory()) {
                    //check if file name starts with start
                    if (file.getName().startsWith(start)) {
                        listOfFilesThatStartWith.add(file);
                    }
                }
            }
        }
        return listOfFilesThatStartWith;
    }
    public static List<File> getAllFilesFromDirectory(String path) {
        //list all files from path directory
        File folder = new File(path);
        File[] files = folder.listFiles();
        List<File> listOfFiles = new java.util.ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    listOfFiles.add(file);
                }
            }
        }
        return listOfFiles;
    }
    public static Item getRandomNecessaryToSpawnItem() {
        for (File file : getListOfFilesThatStartWith("necessary_to_spawn_item", "items/")) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                byte[] jsonData = Files.readAllBytes(Paths.get(file.getPath()));
                JsonNode rootNode = objectMapper.readTree(jsonData);
                Item item = new Item(rootNode.get("name").asText(), rootNode.get("texturePath").asText(), rootNode.get("value").asInt());
                //delete the file
                if (file.delete()) {
                    logger.info("Duplicate necessary to spawn item deleted");
                } else {
                    logger.error("Failed to delete the file");
                }
                logger.info("Necessary to spawn item generated");
                return item;
            } catch (Exception e) {
                logger.error("Error while reading necessary to spawn item: " + e);
            }
        }
        return null;
    }
    public static String getRandomWagonType() {
        return Constants.WAGON_TYPES[(int) (Math.random() * Constants.WAGON_TYPES.length)];
    }
    public static Item getRandomKey() {
        if (Events.canSpawnKey()) {
            int value = Math.max(Constants.MIN_KEY_VALUE, (int) (Math.random() * Constants.MAX_KEY_VALUE));
            List<File> names = getListOfFilesThatStartWith("key", "textures/default/items/misc");
            String name = names.get((int) (Math.random() * names.size())).getName();
            Item key = new Item("Key", "textures/default/items/misc/" + name, Constants.ItemType.KEY);
            key.setValue(value);
            logger.info("Key generated");
            return key;
        }
        return null;
    }
    public static File getRandomMusicFile(String path) {
        File folder = new File("game_resources/sounds/" + path);
        List<File> listOfFiles = new java.util.ArrayList<>();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                listOfFiles.add(file);
            }
        }
        return listOfFiles.get((int) (Math.random() * listOfFiles.size()));
    }
    public static void fillInventoryWithRandomItems(Inventory inventory) {
        logger.info("Filling inventory with random items...");
        for (int k = 0; k < inventory.inventorySize; k++) {
            //generate random chance for each item
            int chance = (int) (Math.random() * 100);
            int generate = (int) (Math.random() * 100);
            if (chance > generate) {
                //get random number for item type: 1 - default, 2 - melee, 3 - firearm, 4 - food, 5 - necessary to spawn item, 6 - key
                int itemType = (int) (Math.random() * 5 + 1);
                switch (itemType) {
                    case 1:
                        inventory.addItem(getRandomDefaultItem());
                        break;
                    case 2:
                        inventory.addItem(getRandomMeleeItem());
                        break;
                    case 3:
                        inventory.addItem(getRandomFirearmItem());
                        break;
                    case 4:
                        inventory.addItem(getRandomFoodItem());
                        break;
                    case 5:
                        inventory.addItem(getRandomNecessaryToSpawnItem());
                        break;
                    case 6:
                        inventory.addItem(getRandomKey());
                        Events.setCanSpawnLockedDoor(true);
                        break;
                }
            } else {
                inventory.addItem(null);
            }
        }
        logger.info("Inventory filled with random items");
    }
}
