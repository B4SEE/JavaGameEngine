package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import cs.cvut.fel.pjv.gamedemo.engine.*;
import javafx.scene.shape.Shape;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Class representing the wagon, holds all the objects, interactive objects and entities that are in the wagon at the moment.
 */
public class Wagon {
    @JsonIgnore
    private static final Logger logger = LogManager.getLogger(Wagon.class);
    @JsonProperty("id")
    private int id;
    @JsonProperty("type")
    private String type;
    @JsonIgnore
    private Door doorLeft;
    @JsonIgnore
    private Door doorRight;
    @JsonIgnore
    private Object doorLeftTarget;
    @JsonIgnore
    private Object doorRightTarget;
    @JsonProperty("entities")
    private List<Entity> entities = new ArrayList<>();
    @JsonProperty("objectsArray")
    private Object[][] objectsArray;
    @JsonIgnore
    private int interactiveObjectsCount = 0;
    @JsonIgnore
    private Object[] interactiveObjects;
    @JsonProperty("seed")
    private String seed;
    @JsonIgnore
    private Shape obstacles;
    @JsonCreator
    public Wagon(@JsonProperty("id") int id, @JsonProperty("type") String type) {
        this.id = id;
        this.type = type;
    }
    @JsonIgnore
    public int getId() {
        return id;
    }
    @JsonIgnore
    public void setId(int id) {
        this.id = id;
    }
    @JsonIgnore
    public String getType() {
        return type;
    }
    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }
    @JsonIgnore
    public Door getDoorLeft() {
        return doorLeft;
    }
    @JsonIgnore
    public Door getDoorRight() {
        return doorRight;
    }
    @JsonIgnore
    public Object getDoorLeftTarget() {
        return doorLeftTarget;
    }
    @JsonIgnore
    public Object getDoorRightTarget() {
        return doorRightTarget;
    }
    @JsonSetter("entities")
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
    @JsonIgnore
    public List<Entity> getEntities() {
        return entities;
    }
    @JsonSetter("objectsArray")
    public void setObjectsArray(Object[][] objectsArray) {
        this.objectsArray = objectsArray;
        countInteractiveObjects();
        initWagonDoors();
        initInteractiveObjects();
    }
    @JsonIgnore
    public Object[][] getObjectsArray() {
        return objectsArray;
    }
    @JsonIgnore
    public int getInteractiveObjectsCount() {
        return interactiveObjectsCount;
    }
    @JsonIgnore
    public Object[] getInteractiveObjects() {
        return interactiveObjects;
    }
    @JsonSetter("seed")
    public void setSeed(String seed) {
        this.seed = seed;
    }
    @JsonIgnore
    public String getSeed() {
        return seed;
    }
    @JsonIgnore
    public Shape getObstacles() {
        return obstacles;
    }
    @JsonIgnore
    public void setObstacles(Shape obstacles) {
        this.obstacles = obstacles;
    }

    /**
     * Generate the wagon
     */
    @JsonIgnore
    public void generateWagon() {
        logger.info("Generating new wagon: ID " + id + ", type " + type + "...");
        String path = RandomHandler.getRandomWagonTypeLayout(type);
        String unparsedSeed = MapLoader.load(path);
        seed = MapLoader.parseMap(unparsedSeed);
        initWagon();
    }
    @JsonIgnore
    public void generateNextWagon(Wagon wagon, boolean leftWagon) {//wagon - current wagon; leftWagon - if next wagon will be on the left side of the current wagon
        generateWagon();
        logger.info("Linking wagons...");
        if (!leftWagon) {
            this.doorLeft.setTargetId(wagon.getId());//set the targetId(wagon) of the door
            this.doorLeft.setTeleport(wagon.getDoorRightTarget());//set the teleport object of the door (where player will be teleported in next wagon)
            wagon.getDoorRight().setTargetId(this.id);
            wagon.getDoorRight().setTeleport(this.doorLeftTarget);
        } else {
            this.doorRight.setTargetId(wagon.getId());
            this.doorRight.setTeleport(wagon.getDoorLeftTarget());
            wagon.getDoorLeft().setTargetId(this.id);
            wagon.getDoorLeft().setTeleport(this.doorRightTarget);
        }
        logger.info("Wagons with ID " + id + " and wagon with ID " + wagon.getId() + " linked successfully");
    }

    /**
     * Initialize the wagon; set the objects, interactive objects and entities.
     */
    @JsonIgnore
    public void initWagon() {
        logger.info("Initializing wagon: ID " + id + ", type " + type + "...");
        if (!Checker.checkMap(seed)) {
            logger.error("Map seed is invalid, trying to generate new wagon...");
            generateWagon();
            return;
        }

        String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
        objectsArray = new Object[rows.length][];

        logger.info("Creating objects...");
        for (int i = 0; i < rows.length; i++) {
            String[] subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
            objectsArray[i] = new Object[subRows.length];

            for (int j = 0; j < subRows.length; j++) {
                char firstChar = subRows[j].charAt(0);
                char secondChar = subRows[j].charAt(1);
                String letterID = subRows[j].substring(2, 4);
                String name = Constants.OBJECT_NAMES.get(letterID);
                String texture = Constants.OBJECT_IDS.get(letterID);

                if (firstChar == Constants.FLOOR) {
                    Object object = new Object(Character.getNumericValue(firstChar), name, texture, letterID, 0, 0, 0, false);
                    objectsArray[i][j] = object;
                    continue;
                }
                if (firstChar == Constants.INTERACTIVE_OBJECT) {
                    String interactiveObjectName = Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID);
                    String interactiveObjectTexture = Constants.INTERACTIVE_OBJECTS.get(letterID);

                    logger.info("Interactive object: " + interactiveObjectName + " at index " + i + ", " + j + "...");

                    if (letterID.equals(Constants.CHEST_OBJECT)) {
//                        texture = setRandomTexture(Constants.INTERACTIVE_OBJECTS.get(letterID));
                        Object object = new Object(Character.getNumericValue(firstChar), interactiveObjectName, interactiveObjectTexture, letterID, 1, 0, 0, false);
                        object.setObjectInventory(new Inventory(Character.getNumericValue(secondChar)));
                        RandomHandler.fillInventoryWithRandomItems(object.getObjectInventory());
                        objectsArray[i][j] = object;
                    }
                    if (letterID.equals(Constants.LOCKABLE_DOOR)) {
                        Object object = new Object(Character.getNumericValue(firstChar), interactiveObjectName, interactiveObjectTexture, letterID, Character.getNumericValue(secondChar), 0, 0, true);
                        objectsArray[i][j] = object;
                    }
                    if (letterID.equals(Constants.WAGON_DOOR)) {
                        Door wagonDoor = new Door(Character.getNumericValue(firstChar), interactiveObjectName, interactiveObjectTexture, Constants.NULL_WAGON_ID, null, false);
                        wagonDoor.setHeight(Character.getNumericValue(secondChar));
                        wagonDoor.setTwoLetterId(letterID);

                        if (Events.canSpawnLockedDoor() && (int) (Math.random() * 100) < Constants.LOCKED_DOOR_SPAWN_CHANCE) {
                            wagonDoor.lock();
                            Events.setCanSpawnLockedDoor(false);
                            logger.info("Locked door spawned");
                        }

                        objectsArray[i][j] = wagonDoor;
                    }
                    continue;
                }

                Object object = new Object(Character.getNumericValue(firstChar), name, texture, letterID, Character.getNumericValue(secondChar), 0, 0, true);
                objectsArray[i][j] = object;
            }
        }
        logger.info("Objects created successfully");

        countInteractiveObjects();
        initWagonDoors();
        initInteractiveObjects();
        initEntities();

        logger.info("Wagon initialized successfully");
    }

    private void initEntities() {
        logger.info("Setting entities...");
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getHeight() != 0) continue;
                String letterID = object.getTwoLetterId();

                switch (letterID) {
                    case Constants.ENEMY_SPAWN -> {
                        logger.info("Enemy spawn found");
                        Method[] enemyCreators = Constants.WAGON_TYPE_ENEMIES.get(type);
                        Method enemyCreator = enemyCreators[(int) (Math.random() * enemyCreators.length)];
                        try {
                            Entity enemy = (Entity) enemyCreator.invoke(EntitiesCreator.class);
                            enemy.setCurrentWagon(this);
                            entities.add(enemy);
                            logger.info("Enemy " + enemy.getName() + " spawned");
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            logger.error("Error while creating enemy: " + e.getMessage());
                        }
                    }
                    case Constants.NPC_SPAWN -> {
                        logger.info("NPC spawn found");
                        Entity npc = EntitiesCreator.createNPC(type);
                        entities.add(npc);
                        logger.info("NPC " + npc.getName() + " spawned");
                    }
                    case Constants.VENDOR_SPAWN -> {
                        logger.info("Vendor spawn found");
                        String[] names = Constants.WAGON_TYPE_NPC.get(type);
                        Vendor vendor = new Vendor("vendor_" + names[(int) (Math.random() * names.length)], "zombie" + "_front.png");
                        vendor.setCurrentWagon(this);
                        entities.add(vendor);
                        logger.info("Vendor " + vendor.getName() + " spawned");
                    }
                    case Constants.QUEST_SPAWN -> {
                        logger.info("Quest spawn found");
                        List<QuestNPC> availableQuestNPCs = Events.getAvailableQuestNPCs();
                        if (availableQuestNPCs != null && !availableQuestNPCs.isEmpty()) {
                            logger.info("Available quest NPCs found");
                            QuestNPC questNPC = availableQuestNPCs.remove((int) (Math.random() * availableQuestNPCs.size()));
                            questNPC.writeQuestItemToNecessaryToSpawnItems();
                            questNPC.setCurrentWagon(this);
                            entities.add(questNPC);
                            logger.info("Quest NPC " + questNPC.getName() + " spawned");
                        }
                    }
                }
            }
        }
        logger.info("Entities set successfully");
    }

    @JsonIgnore
    private void countInteractiveObjects() {
        interactiveObjectsCount = (int) Arrays.stream(objectsArray)
                .flatMap(Arrays::stream)
                .filter(object -> object.getId() == 2)
                .count();
    }
    @JsonIgnore
    private void initWagonDoors() {
        logger.info("Setting wagon doors...");
        for (Object[] objects : objectsArray) {
            //check only first and last object in the row
            if (Objects.equals(objects[0].getTwoLetterId(), Constants.WAGON_DOOR)) {
                logger.info("Left door found...");
                doorLeft = (Door) objects[0];
                doorLeftTarget = objects[1];
            }
            if (Objects.equals(objects[objects.length - 1].getTwoLetterId(), Constants.WAGON_DOOR)) {
                logger.info("Right door found...");
                doorRight = (Door) objects[objects.length - 1];
                doorRightTarget = objects[objects.length - 3];
            }
        }
        logger.info("Wagon doors set successfully");
    }
    @JsonIgnore
    private void initInteractiveObjects() {
        logger.info("Setting interactive objects...");
        interactiveObjects = new Object[interactiveObjectsCount];
        int index = 0;
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getId() == 2) {
                    interactiveObjects[index++] = object;
                }
            }
        }
        logger.info("Interactive objects set successfully");
    }
    @JsonIgnore
    public int[][] getMapForPathFinder(boolean doorIsWalkable) {
        int[][] map = new int[objectsArray.length][objectsArray[0].length];
        for (int i = 0; i < objectsArray.length; i++) {
            for (int j = 0; j < objectsArray[i].length; j++) {
                Object currentObject = objectsArray[i][j];
                // use ternary operator to check if the object is walkable or not (1 - walkable, 0 - not walkable)
                map[i][j] = (currentObject == null
                        || !currentObject.isSolid())
                        || (doorIsWalkable && currentObject.getTwoLetterId().equals(Constants.WAGON_DOOR))
                        || (doorIsWalkable && currentObject.getTwoLetterId().equals(Constants.LOCKABLE_DOOR)) ? 1 : 0;
            }
        }
        return map;
    }
    @JsonIgnore
    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    @JsonIgnore
    public void removeTrap() {//removes only one trap per call
        logger.info("Removing trap from the wagon ID" + id + "...");
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getTwoLetterId().equals(Constants.TRAP)) {
                    logger.info("Trap found at " + object.getIsoX() + ", " + object.getIsoY() + ", removing...");
                    object.setTwoLetterId("TF");
                    return;
                }
            }
        }
    }
    @JsonIgnore
    public Object getTrap() {
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getTwoLetterId().equals(Constants.TRAP)) {
                    return object;
                }
            }
        }
        return null;
    }
    @JsonIgnore
    public void updateEntities() {
        for (Entity entity : entities) {
            entity.setCurrentWagon(this);
        }
    }

    public Entity getConductor() {
        for (Entity entity : entities) {
            if (entity.getType().equals(Constants.EntityType.CONDUCTOR) && Objects.equals(entity.getName(), Constants.CONDUCTOR)) {
                return entity;
            }
        }
        return null;
    }
    public Entity getGrandmother() {
        for (Entity entity : entities) {
            if (entity.getType().equals(Constants.EntityType.CONDUCTOR) && Objects.equals(entity.getName(), Constants.GRANDMOTHER)) {
                return entity;
            }
        }
        return null;
    }
}