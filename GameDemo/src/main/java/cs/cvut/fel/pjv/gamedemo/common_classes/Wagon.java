package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import cs.cvut.fel.pjv.gamedemo.engine.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.utils.MapLoader;
import cs.cvut.fel.pjv.gamedemo.engine.utils.RandomHandler;
import javafx.scene.shape.Shape;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing the wagon, holds all the objects, interactive objects and entities that are in the wagon at the moment.
 */
public class Wagon {

    //region Attributes
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
    //endregion

    //region Constructors
    @JsonCreator
    public Wagon(@JsonProperty("id") int id, @JsonProperty("type") String type) {
        this.id = id;
        this.type = type;
    }
    //endregion

    //region Methods
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
        logger.info("Wagon generated successfully");
    }

    /**
     * Generate the next wagon
     * @param wagon the original/previous wagon
     * @param leftWagon whether the wagon is on the left side of the original wagon
     */
    @JsonIgnore
    public void generateNextWagon(Wagon wagon, boolean leftWagon) {
        generateWagon();
        logger.info("Linking wagons...");
        if (!leftWagon) {
            this.doorLeft.setTargetId(wagon.getId());
            this.doorLeft.setTeleport(wagon.getDoorRightTarget());
            System.out.println("Next wagon door target: " + wagon.getDoorRightTarget());
            System.out.println(doorLeft.getTeleport().getIsoX() + " " + doorLeft.getTeleport().getIsoY());
            wagon.getDoorRight().setTargetId(this.id);
            wagon.getDoorRight().setTeleport(this.doorLeftTarget);
            System.out.println("This wagon door target: " + this.doorLeftTarget);
            System.out.println(wagon.getDoorRight().getTeleport().getIsoX() + " " + wagon.getDoorRight().getTeleport().getIsoY());
            System.out.println("Wagon " + this.id + " is connected to wagon " + wagon.getId() + " on the right side.");
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
    private void initWagon() {
        logger.debug("Initializing wagon: ID " + id + ", type " + type + "...");
        if (!Checker.checkMap(seed)) {
            logger.error("Map seed is invalid, trying to generate new wagon...");
            type = RandomHandler.getRandomWagonType();
            logger.info("New wagon type: " + type);
            generateWagon();
            return;
        }

        String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
        objectsArray = new Object[rows.length][];

        logger.debug("Creating objects...");
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

                    logger.debug("Interactive object: " + interactiveObjectName + " at index " + i + ", " + j + "...");

                    if (letterID.equals(Constants.CHEST_OBJECT)) {
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
        logger.debug("Objects created successfully");

        countInteractiveObjects();
        initWagonDoors();
        initInteractiveObjects();
        initEntities();

        logger.debug("Wagon initialized successfully");
    }

    /**
     * Initialize the entities in the wagon.
     */
    @JsonIgnore
    private void initEntities() {
        logger.debug("Setting entities...");
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getHeight() != 0) continue;
                String letterID = object.getTwoLetterId();

                switch (letterID) {
                    case Constants.ENEMY_SPAWN -> {
                        logger.debug("Enemy spawn found");
                        Entity enemy = EntitiesCreator.createRandomEnemy(type);
                        if (enemy == null) return;
                        enemy.setCurrentWagon(this);
                        entities.add(enemy);
                        logger.debug("Enemy " + enemy.getName() + " spawned");
                    }
                    case Constants.NPC_SPAWN -> {
                        logger.debug("NPC spawn found");
                        Entity npc = EntitiesCreator.createNPC(type);
                        entities.add(npc);
                        logger.debug("NPC " + npc.getName() + " spawned");
                    }
                    case Constants.VENDOR_SPAWN -> {
                        logger.debug("Vendor spawn found");
                        String[] names = Constants.WAGON_TYPE_NPC.get(type);
                        Vendor vendor = new Vendor("vendor_" + names[(int) (Math.random() * names.length)], "zombie" + "_front.png");
                        vendor.setCurrentWagon(this);
                        entities.add(vendor);
                        logger.debug("Vendor " + vendor.getName() + " spawned");
                    }
                    case Constants.QUEST_SPAWN -> {
                        logger.debug("Quest spawn found");
                        List<QuestNPC> availableQuestNPCs = Events.getAvailableQuestNPCs();
                        if (availableQuestNPCs != null && !availableQuestNPCs.isEmpty()) {
                            logger.debug("Available quest NPCs found");
                            QuestNPC questNPC = availableQuestNPCs.remove((int) (Math.random() * availableQuestNPCs.size()));
                            questNPC.writeQuestItemToNecessaryToSpawnItems();
                            questNPC.setCurrentWagon(this);
                            entities.add(questNPC);
                            logger.debug("Quest NPC " + questNPC.getName() + " spawned");
                        }
                    }
                }
            }
        }
        logger.debug("Entities set successfully");
    }

    /**
     * Count the interactive objects in the wagon.
     */
    @JsonIgnore
    private void countInteractiveObjects() {
        interactiveObjectsCount = (int) Arrays.stream(objectsArray)
                .flatMap(Arrays::stream)
                .filter(object -> object.getId() == 2)
                .count();
    }

    /**
     * Initialize the wagon doors.
     */
    @JsonIgnore
    private void initWagonDoors() {
        logger.debug("Setting wagon doors...");
        for (Object[] objects : objectsArray) {
            //check only first and last object in the row
            if (Objects.equals(objects[0].getTwoLetterId(), Constants.WAGON_DOOR)) {
                logger.debug("Left door found...");
                doorLeft = (Door) objects[0];
                doorLeftTarget = objects[1];
            }
            if (Objects.equals(objects[objects.length - 1].getTwoLetterId(), Constants.WAGON_DOOR)) {
                logger.debug("Right door found...");
                doorRight = (Door) objects[objects.length - 1];
                doorRightTarget = objects[objects.length - 3];
            }
        }
        logger.info("Wagon doors set successfully");
    }

    /**
     * Initialize the interactive objects.
     */
    @JsonIgnore
    private void initInteractiveObjects() {
        logger.debug("Setting interactive objects...");
        interactiveObjects = new Object[interactiveObjectsCount];
        int index = 0;
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getId() == Character.getNumericValue(Constants.INTERACTIVE_OBJECT)) {
                    interactiveObjects[index++] = object;
                }
            }
        }
        logger.debug("Interactive objects set successfully");
    }
    @JsonIgnore
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Remove the trap from the wagon.
     * Note: removes only one trap per call.
     */
    @JsonIgnore
    public void removeTrap() {
        logger.info("Removing trap from the wagon ID" + id + "...");
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getTwoLetterId().equals(Constants.TRAP)) {
                    logger.debug("Trap found at " + object.getIsoX() + ", " + object.getIsoY() + ", removing...");
                    object.setTwoLetterId("TF");
                    logger.info("Trap removed successfully");
                    return;
                }
            }
        }
    }

    /**
     * Update the entities in the wagon.
     */
    @JsonIgnore
    public void updateEntities() {
        for (Entity entity : entities) {
            entity.setCurrentWagon(this);
        }
    }
    //endregion

    //region Getters & Setters

    //region Getters
    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonIgnore
    public String getType() {
        return type;
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

    @JsonIgnore
    public List<Entity> getEntities() {
        return entities;
    }

    @JsonIgnore
    public Object[][] getObjectsArray() {
        return objectsArray;
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
    /**
     * Get the map for the pathfinder.
     * @param doorIsWalkable whether the door is walkable
     * @return the map for the pathfinder
     */
    @JsonIgnore
    public int[][] getMapForPathFinder(boolean doorIsWalkable) {
        int[][] map = new int[objectsArray.length][objectsArray[0].length];
        for (int i = 0; i < objectsArray.length; i++) {
            for (int j = 0; j < objectsArray[i].length; j++) {
                Object currentObject = objectsArray[i][j];
                map[i][j] = (currentObject == null
                        || !currentObject.isSolid())
                        || (doorIsWalkable && currentObject.getTwoLetterId().equals(Constants.WAGON_DOOR))
                        || (doorIsWalkable && currentObject.getTwoLetterId().equals(Constants.LOCKABLE_DOOR)) ? 1 : 0;
            }
        }
        return map;
    }
    /**
     * Get the trap from the wagon.
     * @return the trap object
     */
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
    /**
     * Get the conductor entity.
     * @return the conductor entity
     */
    @JsonIgnore
    public Entity getConductor() {
        for (Entity entity : entities) {
            if (entity.getType().equals(Constants.EntityType.CONDUCTOR) && Objects.equals(entity.getName(), Constants.CONDUCTOR)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Get the grandmother entity.
     * @return the grandmother entity
     */
    @JsonIgnore
    public Entity getGrandmother() {
        for (Entity entity : entities) {
            if (entity.getType().equals(Constants.EntityType.CONDUCTOR) && Objects.equals(entity.getName(), Constants.GRANDMOTHER)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Get all the floor objects (free space) in the wagon.
     * @return all the floor objects in the wagon
     */
    @JsonIgnore
    public Object[] getAllFloorObjects() {
        List<Object> floorObjects = new ArrayList<>();
        int count = 0;
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getHeight() == 0) {
                    floorObjects.add(object);
                    count++;
                }
            }
        }
        return floorObjects.toArray(new Object[count]);
    }
    //endregion

    //region Setters
    @JsonIgnore
    public void setId(int id) {
        this.id = id;
    }
    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }
    @JsonSetter("entities")
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    /**
     * Set the objects array and initialize the wagon doors and interactive objects.
     * @param objectsArray the objects array
     */
    @JsonSetter("objectsArray")
    public void setObjectsArray(Object[][] objectsArray) {
        this.objectsArray = objectsArray;
        countInteractiveObjects();
        initWagonDoors();
        initInteractiveObjects();
    }
    @JsonIgnore
    public void setObstacles(Shape obstacles) {
        this.obstacles = obstacles;
    }
    //endregion

    //endregion
}