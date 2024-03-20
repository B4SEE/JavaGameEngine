package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Class with all checking methods.
 */
public class Checker {
     /**
     * Check if map is valid.
     * @param map - map to check
     * @return - true if map is valid, false if not
     */
    public boolean checkMap(String map) {
        String[] lines = map.split("\n");
        //if map is only one line long, check if it uses separator for rows
        if (lines.length == 1) {
            if (lines[0].contains(Constants.MAP_ROW_SEPARATOR)) {
                lines = lines[0].split(Constants.MAP_ROW_SEPARATOR);
            }
        }
        if (!checkLinesLength(lines)) {
            return false;
        }
        if (!checkMapCodes(lines)) {
            return false;
        }
        return checkHowManyDoors(map);
    }
    private boolean checkLinesLength(String[] lines) {
        //check if all map lines are the same length
        int lineLength = lines[0].length();
        if (lineLength < 4) {
            return false;
        }
        for (String line : lines) {
            if (line.length() != lineLength) {
                return false;
            }
        }
        return true;
    }
    private boolean checkMapCodes(String[] lines) {
        //check if all map codes have the same number of characters (4)
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String code : subRows) {
                if (code.length() != 4) {
                    System.out.println("first");
                    return false;
                }
            }
        }
        return checkIfCodeIsInDictionary(lines);
    }
    private boolean checkIfCodeIsInDictionary(String[] lines) {
        //check if all map codes are valid (are in Constant dictionaries)
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String subRow : subRows) {
                if (!List.of(Constants.ALLOWED_CODES).contains(subRow.charAt(0))) {
                    System.out.println(subRow.charAt(0));
                    System.out.println("second");
                    return false;
                }
                if (!List.of(Constants.ALLOWED_HEIGHTS).contains(subRow.charAt(1))) {
                    if (subRow.charAt(0) != Constants.INTERACTIVE_OBJECT) {
                        System.out.println(subRow.charAt(1) + " " + subRow);
                        System.out.println("third");
                        return false;
                    }
                }
                if (!Character.isLetter(subRow.charAt(2))) {
                    System.out.println("fourth");
                    return false;
                }
                if (!Character.isLetter(subRow.charAt(3))) {
                    System.out.println("fifth");
                    return false;
                }
                if (!Constants.OBJECT_IDS.containsKey(subRow.substring(2, 4)) && !Constants.INTERACTIVE_OBJECTS.containsKey(subRow.substring(2, 4))) {
                    System.out.println("sixth");
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkHowManyDoors(String map) {
        String[] lines = map.split("\n");
        int doors = 0;
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String subRow : subRows) {
                if (subRow.charAt(0) == Constants.INTERACTIVE_OBJECT) {
                    if (subRow.startsWith(Constants.WAGON_DOOR, 2)) {
                        doors++;
                    }
                }
            }
        }
        return doors == 2;
    }
    /**
     * Check if the player can interact with the objects.
     * @return the object the player can interact with, null otherwise
     */
    public Object checkIfPlayerCanInteract(Player player, Object[] interactiveObjects) {
        if (interactiveObjects == null) {
            return null;
        }
        for (Object object : interactiveObjects) {
            if (checkCollision(player.getAttackRange(), object.getObjectHitbox())) {
//                updateLabel("Press E to interact", player.getPositionX() - 50, player.getPositionY() - 50);
                return object;
            }
        }
//        updateLabel("No interactive objects nearby", player.getPositionX() - 50, player.getPositionY() - 50);
        return null;
    }
    /**
     * Check if there is a collision between two shapes.
     * @param hitbox1 the first hitbox
     * @param hitbox2 the second hitbox
     * @return true if there is a collision, false otherwise
     */
    public boolean checkCollision(Shape hitbox1, Shape hitbox2) {
        Shape intersect = Shape.intersect(hitbox1, hitbox2);
        return !intersect.getBoundsInParent().isEmpty();
    }

    /**
     * Check if the x position of the object is within the player's range (constant).
     * @param objectIsoXY the object's isometric x and y position
     * @return true if the x position of the object is within the player's range, false otherwise
     */
    public boolean checkX(Entity entity, double[] objectIsoXY) {
        return (objectIsoXY[0] > entity.getPositionX() - Constants.TILE_WIDTH && objectIsoXY[0] < entity.getPositionX() + 3 * Constants.TILE_WIDTH);
    }
    /**
     * Check if the y position of the object is lower than the player's y position.
     * @param objectIsoXY the object's isometric x and y position
     * @param objectTexture the object's texture
     * @return true if the y position of the object is lower than the player's y position, false otherwise
     */
    public boolean checkY(Entity entity, double[] objectIsoXY, Image objectTexture) {
        return (entity.getPositionY() + (double) Constants.TILE_HEIGHT / 2 + entity.getHeight() * Constants.TILE_HEIGHT < (objectIsoXY[1] - Constants.TILE_HEIGHT + objectTexture.getHeight()));
    }
}
