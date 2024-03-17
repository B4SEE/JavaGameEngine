package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Wagon {
    private int id;
    private String type;
    private String texturePath;
    private Door doorLeft;
    private Door doorRight;
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

    public void generateWagon() {
        // Generate the wagon
        String path = "maps/common/" + type + "_wagon.txt";
        String seed = load(path);
        parseMap(seed);
    }
    public void setWagonMap(String path) {
        String seed = load(path);
        parseMap(seed);
    }
    private String load(String path) {
        File file = new File(path);
        StringBuilder map = new StringBuilder();
        try {
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()) {
                map.append(scanner.nextLine());
                map.append(Constants.MAP_ROW_SEPARATOR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map.toString();
    }

    private void parseMap(String seed) {
        try {
            String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
            String[] subRows;
            //3 - random; next number: 1 - chest, 2 - trap, 3 - enemy; next two numbers: chance; Note: was tested and works
            for (int i = 0; i < rows.length; i++) {
                subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
                for (int j = 0; j < subRows.length; j++) {
//                    System.out.println("-" + subRows[j].charAt(0));
                    if (subRows[j].charAt(0) == Constants.RANDOM) {
//                        System.out.println(subRows[j]);
                        int chance = Integer.parseInt(subRows[j].substring(2, 4));
                        int generate = (int) (Math.random() * 100);
                        if (chance > generate) {
                            if (subRows[j].charAt(1) == '1') {
                                // Generate the chest
                                String chest = "21CO";
                                subRows[j] = chest;
                            }
                            if (subRows[j].charAt(1) == '2') {
                                // Generate the trap
                                String trap = "00TR";
                                subRows[j] = trap;
                            }
                            if (subRows[j].charAt(1) == '3') {
                                // Generate the enemy
                                String enemy = "00EN";
                                subRows[j] = enemy;
                            }
                        } else {
                            subRows[j] = "00TF";
                        }
                    }
                }
                rows[i] = String.join(Constants.MAP_COLUMN_SEPARATOR, subRows);
            }
            this.seed = String.join(Constants.MAP_ROW_SEPARATOR, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initWagon();
    }

    public void initWagon() {
        // Initialize the wagon

        if (!checkStringMapValidity(seed)) {
            System.out.println("Invalid seed");
            return;
        }

        String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
        String[] subRows = rows[0].split(Constants.MAP_COLUMN_SEPARATOR);

        objectsArray = new Object[rows.length][subRows.length];

        int interactiveObjectsCount = 0;
        int entitiesCount = 0;
        int trapsCount = 0;

        for (int i = 0; i < rows.length; i++) {
            subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
            for (int j = 0; j < subRows.length; j++) {

                String texture = Constants.OBJECT_IDS.get(subRows[j].substring(2, 4));

                if (subRows[j].charAt(0) == Constants.FLOOR) {
                    String letterID = subRows[j].substring(2, 4);
                    if (letterID.equals(Constants.ENEMY_SPAWN)) {
                        System.out.println(subRows[j]);
                        entitiesCount++;
                    }
                    if (letterID.equals(Constants.TRAP)) {
                        System.out.println(subRows[j]);
                        trapsCount++;
                    }

                    Object object = new Object(Character.getNumericValue(Character.getNumericValue(subRows[j].charAt(0))), Constants.OBJECT_NAMES.get(letterID), texture, letterID, 0, 0, 0, false);

                    objectsArray[i][j] = object;
                    continue;
                }
                if (subRows[j].charAt(0) == Constants.INTERACTIVE_OBJECT) {
                    interactiveObjectsCount++;
                    String letterID = subRows[j].substring(2, 4);
                    if (letterID.equals(Constants.CHEST_OBJECT)) {
                        texture = Constants.INTERACTIVE_OBJECTS.get(letterID);
                        Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID), texture, letterID, 0, 0, 0, false);
                        object.setObjectInventory(new Inventory(Character.getNumericValue(subRows[j].charAt(1))));
                        object.setHeight(1);
                        System.out.println(object.getHeight());
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
                        Door wagonDoor = new Door(Character.getNumericValue(subRows[j].charAt(0)), Constants.INTERACTIVE_OBJECTS_NAMES.get(letterID), texture, new int[]{-1, 0, 0}, false);
                        wagonDoor.setHeight(Character.getNumericValue(subRows[j].charAt(1)));
                        wagonDoor.setTwoLetterId(letterID);//without this, the door's twoLetterId is null
                        objectsArray[i][j] = wagonDoor;
                    }
                    continue;
                }
                String letterID = subRows[j].substring(2, 4);
                Object object = new Object(Character.getNumericValue(subRows[j].charAt(0)), Constants.OBJECT_NAMES.get(letterID), texture, letterID, Character.getNumericValue(subRows[j].charAt(1)), 0, 0, true);
                objectsArray[i][j] = object;
            }
        }

        System.out.println("io count: " + interactiveObjectsCount);
        System.out.println("en count: " + entitiesCount);
        System.out.println("traps count: " + trapsCount);

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
                        String[] names = Constants.WAGON_TYPE_ENTITIES.get(type);
                        String name = names[(int) (Math.random() * names.length)];

                        Entity enemy = new Entity(name, name + "_front.png");//works, but entities are all in the same position

                        enemy.setAsDefaultEnemy();
                        enemy.setPositionX(enemy.getHealth());
//                        enemy.setPositionY(enemy.getHealth());

                        System.out.println(enemy.getTexturePath());
                        System.out.println("enemy created");

                        entities.add(enemy);
                    }
                }
            }
        }
    }

    public boolean checkStringMapValidity(String map) {
        try {
            String[] rows = map.split(Constants.MAP_ROW_SEPARATOR);
            String[] subRows = rows[0].split(Constants.MAP_COLUMN_SEPARATOR);

            for (String subRow : subRows) {
                if (subRow.length() != subRows[0].length()) {
                    return false;
                }
                //check if not in Constants.ALLOWED_CODES
                if (!List.of(Constants.ALLOWED_CODES).contains(subRow.charAt(0))) {
                    return false;
                }
                if (!List.of(Constants.ALLOWED_HEIGHTS).contains(subRow.charAt(1))) {
                    return false;
                }
                if (!Character.isLetter(subRow.charAt(2))) {
                    return false;
                }
                if (!Character.isLetter(subRow.charAt(3))) {
                    return false;
                }
                if (!Constants.OBJECT_IDS.containsKey(subRow.substring(2, 4)) && !Constants.INTERACTIVE_OBJECTS.containsKey(subRow.substring(2, 4))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    private String setRandomTexture(String path) {
////        path = "src/main/resources/" + path;
////
////        getResourceAsStream(path);
//
////        String[] textures = new File(path).list();
////        System.out.println(Arrays.toString(textures));
////        if (textures != null) {
////            return textures[(int) (Math.random() * textures.length)];
////        }
////        return null;
////    }
}