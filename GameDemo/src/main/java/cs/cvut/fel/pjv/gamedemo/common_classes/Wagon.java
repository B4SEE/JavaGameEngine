package cs.cvut.fel.pjv.gamedemo.common_classes;

import cs.cvut.fel.pjv.gamedemo.engine.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.MapLoader;
import cs.cvut.fel.pjv.gamedemo.engine.RandomHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class representing the wagon, holds all the objects, interactive objects and entities that are in the wagon at the moment.
 */
public class Wagon {
    private int id;
    private String type;
    private String texturePath;
    private Door doorLeft;
    private Door doorRight;
    private Object doorLeftTarget;
    private Object doorRightTarget;
    private List<Entity> entities = new ArrayList<>();
    private Object[][] objectsArray;
    private Object[] interactiveObjects;
    private String seed;


    public Wagon(String type) {
        this.type = type;
    }

    public Wagon(int id, String type, String texturePath) {
        this.id = id;
        this.type = type;
        this.texturePath = texturePath;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public Wagon(int id, String type, String texturePath, Door doorLeft, Door doorRight, List<Entity> entities, Object[][] objectsArray) {
        this.id = id;
        this.type = type;
        this.texturePath = texturePath;
        this.doorLeft = doorLeft;
        this.doorRight = doorRight;
        this.entities = entities;
        this.objectsArray = objectsArray;
    }
    public void setDoorLeft(Door doorLeft) {
        this.doorLeft = doorLeft;
    }
    public void setDoorRight(Door doorRight) {
        this.doorRight = doorRight;
    }

    public Door getDoorLeft() {
        return doorLeft;
    }

    public Door getDoorRight() {
        return doorRight;
    }

    public Object getDoorLeftTarget() {
        return doorLeftTarget;
    }

    public Object getDoorRightTarget() {
        return doorRightTarget;
    }
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
    public List<Entity> getEntities() {
        return entities;
    }
    public void setObjectsArray(Object[][] objectsArray) {
        this.objectsArray = objectsArray;
    }

    public Object[][] getObjectsArray() {
        return objectsArray;
    }

    public void setInteractiveObjects(Object[] interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }

    public Object[] getInteractiveObjects() {
        return interactiveObjects;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getSeed() {
        return seed;
    }

    /**
     * Generate the wagon
     */
    public void generateWagon() {
        // Generate the wagon
        RandomHandler randomHandler = new RandomHandler();
        MapLoader mapLoader = new MapLoader();
        String path = randomHandler.getRandomWagonTypeLayout(type);
        String unparsedSeed = mapLoader.load(path);
        seed = mapLoader.parseMap(unparsedSeed);
        initWagon();
    }
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
    public void initWagon() {
        // Initialize the wagon

        Checker checker = new Checker();

        if (!checker.checkMap(seed)) {
            System.out.println("Invalid seed");
            return;
        }

        String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
        String[] subRows = rows[0].split(Constants.MAP_COLUMN_SEPARATOR);

        objectsArray = new Object[rows.length][subRows.length];

        int interactiveObjectsCount = 0;

        for (int i = 0; i < rows.length; i++) {
            subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
            for (int j = 0; j < subRows.length; j++) {

                String texture = Constants.OBJECT_IDS.get(subRows[j].substring(2, 4));
                String letterID = subRows[j].substring(2, 4);

                if (subRows[j].charAt(0) == Constants.FLOOR) {

                    if (letterID.equals(Constants.TRAP)) {
//                        System.out.println(subRows[j]);
                    }

                    Object object = new Object(Character.getNumericValue(Character.getNumericValue(subRows[j].charAt(0))), Constants.OBJECT_NAMES.get(letterID), texture, letterID, 0, 0, 0, false);

                    objectsArray[i][j] = object;

                    continue;
                }
                if (subRows[j].charAt(0) == Constants.INTERACTIVE_OBJECT) {
                    interactiveObjectsCount++;
                    if (letterID.equals(Constants.CHEST_OBJECT)) {
//                        texture = setRandomTexture(Constants.INTERACTIVE_OBJECTS.get(letterID));
                        texture = Constants.INTERACTIVE_OBJECTS.get(letterID);
                        Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID), texture, letterID, 0, 0, 0, false);
                        object.setObjectInventory(new Inventory(Character.getNumericValue(subRows[j].charAt(1))));
                        for (int k = 0; k < object.getObjectInventory().inventorySize; k++) {
                            //generate random chance for each item
                            int chance = (int) (Math.random() * 100);
                            int generate = (int) (Math.random() * 100);
                            if (chance > generate) {
                                RandomHandler randomHandler = new RandomHandler();
                                //get random number for item type: 1 - default, 2 - melee, 3 - firearm, 4 - food
                                int itemType = (int) (Math.random() * 4 + 1);
                                switch (itemType) {
                                    case 1:
                                        object.getObjectInventory().addItem(randomHandler.getRandomDefaultItem());
                                        break;
                                    case 2:
                                        object.getObjectInventory().addItem(randomHandler.getRandomMeleeItem());
                                        break;
                                    case 3:
                                        object.getObjectInventory().addItem(randomHandler.getRandomFirearmItem());
                                        break;
                                    case 4:
                                        object.getObjectInventory().addItem(randomHandler.getRandomFoodItem());
                                        break;
                                }
                            } else {
                                object.getObjectInventory().addItem(null);
                            }
                        }
//                        object.getObjectInventory().fillWithRandomItems(Constants.LOOT_TABLE_STANDARD, chance);//fill the chest with random items, with random chance
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

                        objectsArray[i][j] = wagonDoor;
                    }
                    continue;
                }
                Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.OBJECT_NAMES.get(letterID), texture, letterID, Character.getNumericValue(subRows[j].charAt(1)), 0, 0, true);
                objectsArray[i][j] = object;
            }
        }
        //set door targets (tile near the door)
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

        //initialise interactive objects
        interactiveObjects = new Object[interactiveObjectsCount];
        for (Object[] objects : objectsArray) {
            for (Object object : objects) {
                if (object.getId() == 2) {
                    interactiveObjects[interactiveObjectsCount - 1] = object;
                    interactiveObjectsCount--;
                }
            }
        }
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

                        int randomInventorySize = (int) (Math.random() * 20 + 1);

                        int randomIntelligence = (int) (Math.random() * 2);

                        int randomNegativeThreshold = (int) (Math.random() * 10 + 1);

                        Vendor vendor = new Vendor("vendor" + name, "vendor" + name + "_front.png", randomInventorySize);
                        vendor.setCurrentWagon(this);
                        vendor.setIntelligence(randomIntelligence);
                        vendor.setNegativeThreshold(randomNegativeThreshold);

                        entities.add(vendor);
                    }
                }
            }
        }
    }
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
    public void addEntity(Entity entity) {
        entities.add(entity);
        System.out.println("Entity added to wagon " + id);
        System.out.println(java.util.Arrays.toString(entities.toArray()));
    }

//    private String setRandomTexture(String path) {
//        File resourceDir = new File("default/");
//        File[] files = resourceDir.listFiles();
//        List<String> textures = new ArrayList<>();
//        for (File file : files) {
//            if (file.getName().contains(path)) {
//                textures.add(file.getName());
//            }
//        }
//        return textures.get((int) (Math.random() * textures.size()));
//    }
}