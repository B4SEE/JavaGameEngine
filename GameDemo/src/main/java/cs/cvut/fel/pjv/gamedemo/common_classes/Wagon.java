package cs.cvut.fel.pjv.gamedemo.common_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import cs.cvut.fel.pjv.gamedemo.engine.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.MapLoader;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing the wagon, holds all the objects, interactive objects and entities that are in the wagon at the moment.
 */
public class Wagon {
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
        System.out.println(objectsArray.length);
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
    public void printAllObjects() {
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                System.out.print(object.getName() + " ");
            }
            System.out.println();
        }
    }
    @JsonIgnore
    public String getSeed() {
        return seed;
    }

    /**
     * Generate the wagon
     */
    @JsonIgnore
    public void generateWagon() {
        // Generate the wagon
        MapLoader mapLoader = new MapLoader();
        String path = RandomHandler.getRandomWagonTypeLayout(type);
        String unparsedSeed = mapLoader.load(path);
        seed = mapLoader.parseMap(unparsedSeed);
        initWagon();
    }
    @JsonIgnore
    public void generateNextWagon(Wagon wagon, boolean leftWagon) {//wagon - current wagon; leftWagon - if next wagon will be on the left side of the current wagon
        generateWagon();

        System.out.println(wagon.getDoorLeftTarget().getIsoX() + " " + wagon.getDoorLeftTarget().getIsoY());
        System.out.println(wagon.getDoorRightTarget().getIsoX() + " " + wagon.getDoorRightTarget().getIsoY());

        if (!leftWagon) {
            this.doorLeft.setTargetId(wagon.getId());//set the targetId(wagon) of the door
            this.doorLeft.setTeleport(wagon.getDoorRightTarget());//set the teleport object of the door (where player will be teleported in next wagon)
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
    }

    /**
     * Initialize the wagon; set the objects, interactive objects and entities.
     */
    @JsonIgnore
    public void initWagon() {
        // Initialize the wagon

        if (!Checker.checkMap(seed)) {
            System.out.println("Invalid seed");
            return;
        }

        String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
        String[] subRows = rows[0].split(Constants.MAP_COLUMN_SEPARATOR);

        objectsArray = new Object[rows.length][subRows.length];

        for (int i = 0; i < rows.length; i++) {
            subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
            for (int j = 0; j < subRows.length; j++) {

                String texture = Constants.OBJECT_IDS.get(subRows[j].substring(2, 4));
                String letterID = subRows[j].substring(2, 4);

                if (subRows[j].charAt(0) == Constants.FLOOR) {
                    Object object = new Object(Character.getNumericValue(Character.getNumericValue(subRows[j].charAt(0))), Constants.OBJECT_NAMES.get(letterID), texture, letterID, 0, 0, 0, false);

                    objectsArray[i][j] = object;

                    continue;
                }
                if (subRows[j].charAt(0) == Constants.INTERACTIVE_OBJECT) {
                    if (letterID.equals(Constants.CHEST_OBJECT)) {
//                        texture = setRandomTexture(Constants.INTERACTIVE_OBJECTS.get(letterID));
                        texture = Constants.INTERACTIVE_OBJECTS.get(letterID);
                        Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID), texture, letterID, 0, 0, 0, false);
                        object.setObjectInventory(new Inventory(Character.getNumericValue(subRows[j].charAt(1))));
                        RandomHandler.fillInventoryWithRandomItems(object.getObjectInventory());
                        object.setHeight(1);
                        objectsArray[i][j] = object;
                    }
                    if (letterID.equals(Constants.LOCKABLE_DOOR)) {
                        texture = Constants.INTERACTIVE_OBJECTS.get(letterID);
                        Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID), texture, letterID, 0, 0, 0, true);
                        object.setHeight(2);
                        objectsArray[i][j] = object;
                    }
                    if (letterID.equals(Constants.WAGON_DOOR)) {
                        texture = Constants.INTERACTIVE_OBJECTS.get(letterID);
                        Door wagonDoor = new Door(Character.getNumericValue(subRows[j].charAt(0)), Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID), texture, Constants.NULL_WAGON_ID, null, false);
                        wagonDoor.setHeight(Character.getNumericValue(subRows[j].charAt(1)));
                        wagonDoor.setTwoLetterId(letterID);//without this, the door's twoLetterId is null

                        if (Events.canSpawnLockedDoor()) {//locked door can be spawned only if the player has at least one key (if the key was spawned in the chest or in vendor's inventory)
                            int chance = (int) (Math.random() * 100);
                            System.out.println("can spawn locked door");
                            if (chance < Constants.LOCKED_DOOR_SPAWN_CHANCE) {
                                wagonDoor.lock();
                                Events.setCanSpawnLockedDoor(false);
                                System.out.println("Locked wagon door spawned");
                            }
                        }

                        objectsArray[i][j] = wagonDoor;
                    }
                    continue;
                }
                Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.OBJECT_NAMES.get(letterID), texture, letterID, Character.getNumericValue(subRows[j].charAt(1)), 0, 0, true);
                objectsArray[i][j] = object;
            }
        }
        countInteractiveObjects();

        //set door and doors' targets (tile near the door)
        initWagonDoors();

        //initialise interactive objects
        initInteractiveObjects();

        //initialise entities
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                String letterID = object.getTwoLetterId();
                if (object.getHeight() == 0) {
                    if (letterID.equals(Constants.ENEMY_SPAWN)) {
                        //pick random name from dictionary
                        String[] names = Constants.WAGON_TYPE_ENEMIES.get(type);
                        String name = names[(int) (Math.random() * names.length)];
                        Entity enemy = new Entity(name, name + "_front.png");//works, but entities are all in the same position
                        enemy.setCurrentWagon(this);
                        enemy.setAsDefaultEnemy();
                        enemy.setPositionX(300);
                        enemy.setPositionY(240);
                        entities.add(enemy);
                    }
                    if (letterID.equals(Constants.NPC_SPAWN)) {
                        //pick random name from dictionary
                        String[] names = Constants.WAGON_TYPE_NPC.get(type);
                        String name = names[(int) (Math.random() * names.length)];
                        Entity npc = new Entity(name, name + "_front.png");
                        npc.setCurrentWagon(this);
                        npc.setAsDefaultNPC();
                        npc.setPositionX(500);
                        npc.setPositionY(240);
                        entities.add(npc);
                    }
                    if (letterID.equals(Constants.VENDOR_SPAWN)) {
                        String[] names = Constants.WAGON_TYPE_NPC.get(type);
                        String name = names[(int) (Math.random() * names.length)];
                        System.out.println("vendor_" + name);
                        Vendor vendor = new Vendor("vendor_" + name, "zombie" + "_front.png");
                        vendor.setCurrentWagon(this);
                        vendor.setPositionX(500);
                        vendor.setPositionY(240);
                        entities.add(vendor);
                    }
                    if (letterID.equals(Constants.QUEST_SPAWN)) {
                        List<QuestNPC> availableQuestNPCs = Events.getAvailableQuestNPCs();
                        System.out.println("***" + availableQuestNPCs);
                        if (availableQuestNPCs != null && !availableQuestNPCs.isEmpty()) {
                            QuestNPC questNPC = Events.getAvailableQuestNPCs().get((int) (Math.random() * availableQuestNPCs.size()));
                            Events.removeQuestNPC(questNPC);
                            questNPC.setCurrentWagon(this);
                            questNPC.setPositionX(500);
                            questNPC.setPositionY(240);
                            entities.add(questNPC);
                        }
                    }
                }
            }
        }
    }
    private void countInteractiveObjects() {
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getId() == 2) {
                    interactiveObjectsCount++;
                }
            }
        }
    }
    private void initWagonDoors() {
        for (Object[] objects : objectsArray) {
            for (int j = 0; j < objects.length; j++) {
                if (Objects.equals(objects[j].getTwoLetterId(), Constants.WAGON_DOOR)) {
                    if (j == 0) {
                        doorLeft = (Door) objects[j];
                        doorLeftTarget = objects[j + 1];
                    } else if (j == objects.length - 1) {
                        doorRight = (Door) objects[j];
                        doorRightTarget = objects[j - 2];
                    }
                }
            }
        }
    }
    private void initInteractiveObjects() {
        interactiveObjects = new Object[interactiveObjectsCount];
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getId() == 2) {
                    interactiveObjects[interactiveObjectsCount - 1] = object;
                    interactiveObjectsCount--;
                }
            }
        }
    }
    @JsonIgnore
    public int[][] getMapForPathFinder(boolean doorIsWalkable) {
        int[][] map = new int[objectsArray.length][objectsArray[0].length];
        for (int i = 0; i < objectsArray.length; i++) {
            for (int j = 0; j < objectsArray[i].length; j++) {
                if (objectsArray[i][j] == null || !objectsArray[i][j].isSolid()) {
                    map[i][j] = 1;
                } else {
                    if ((doorIsWalkable && objectsArray[i][j].getTwoLetterId().equals(Constants.WAGON_DOOR)) || (doorIsWalkable && objectsArray[i][j].getTwoLetterId().equals(Constants.LOCKABLE_DOOR))) {
                        map[i][j] = 1;
                    } else {
                        map[i][j] = 0;
                    }
                }
            }
        }
        return map;
    }
    @JsonIgnore
    public void addEntity(Entity entity) {
        entities.add(entity);
        System.out.println("Entity added to wagon " + id);
        System.out.println(java.util.Arrays.toString(entities.toArray()));
    }
    @JsonIgnore
    public void removeTrap() {//removes only one trap per call
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getTwoLetterId().equals(Constants.TRAP)) {
                    object.setTwoLetterId("TF");
                    return;
                }
            }
        }

    }
}