package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Objects;

/**
 * Class with all checking methods.
 */
public class Checker {

     /**
     * Check if map is valid.
     * @param map - map to check
     * @return - true if map is valid, false if not
     */
    public static boolean checkMap(String map) {
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
        return checkHowManyDoors(lines);
    }

    /**
     * Check if lines are the same length.
     * @param lines - lines to check
     * @return - true if lines are the same length, false otherwise
     */
    private static boolean checkLinesLength(String[] lines) {
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
        System.out.println("lines checked");
        return true;
    }

    /**
     * Check if map codes are valid.
     * @param lines - lines to check
     * @return - true if map codes are valid, false otherwise
     */
    private static boolean checkMapCodes(String[] lines) {
        //check if all map codes have the same number of characters (4)
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String code : subRows) {
                if (code.length() != 4) {
                    System.out.println(code);
                    System.out.println("first");
                    return false;
                }
            }
        }
        return checkIfCodeIsInDictionary(lines);
    }

    /**
     * Check if the code is in the dictionary.
     * @param lines - lines to check
     * @return - true if the code is in the dictionary, false otherwise
     */
    private static boolean checkIfCodeIsInDictionary(String[] lines) {
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

    /**
     * Check how many wagon doors are in the map.
     * @param lines - lines to check
     * @return - true if there are two doors, false otherwise
     */
    private static boolean checkHowManyDoors(String[] lines) {
        int doors = 0;
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String subRow : subRows) {
                if (subRow.charAt(0) == Constants.INTERACTIVE_OBJECT) {
                    if (subRow.substring(2, 4).equals(Constants.WAGON_DOOR)) {
                        doors++;
                    }
                }
            }
        }
        System.out.println("doors: " + doors);
        System.out.println(lines.length);
        return doors == 2;
    }

    /**
     * Check if the wagon has a trap.
     * @param objects - objects in the wagon
     * @return - true if the wagon has a trap, false otherwise
     */
    public static boolean checkIfWagonHasTrap(Object[][] objects) {
        if (objects == null) {
            return false;
        }
        //check if the wagon has a trap
        for (Object[] objectArray : objects) {
            for (Object object : objectArray) {
                if (object != null) {
                    if (object.getTwoLetterId().equals(Constants.TRAP)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the player can interact with the objects.
     * @return the object the player can interact with, null otherwise
     */
    public static Object checkIfCanInteract(Entity entity, Object[] interactiveObjects) {
        if (interactiveObjects == null) {
            return null;
        }
        for (Object object : interactiveObjects) {
            if (checkCollision(entity.getAttackRange(), object.getObjectHitbox())) {
                return object;
            }
        }
        return null;
    }

    /**
     * Check if entity remembers the target.
     * @param entity the entity
     * @param target the target
     * @param obstacles obstacles
     * @param time the time
     * @return true if the entity remembers the target, false otherwise
     * <br>
     * <br>
     * Note: the entity remembers the target if it can see it
     */
    public static boolean checkIfEntityRemember(Entity entity, Entity target, Shape obstacles, long time) {
        if (!checkIfEntityCanSee(entity, target, obstacles)) {
            if ((time - entity.getWhenStartedPursuing() != 0 && (time - entity.getWhenStartedPursuing()) % 13 == 0) || entity.getWhenStartedPursuing() == 0) {
                entity.setWhenStartedPursuing(0);
                return false;
            }
        } else {
            entity.setWhenStartedPursuing(time);
            return true;
        }
        return true;
    }

    /**
     * Check if entity can see the target.
     * @param entity the entity
     * @param target the target
     * @param obstacles obstacles
     * @return true if the entity can see the target, false otherwise
     */
    public static boolean checkIfEntityCanSee(Entity entity, Entity target, Shape obstacles) {
        Line sightLine = new Line(entity.getPositionX() + 32, entity.getPositionY() + 80, target.getPositionX() + 32, target.getPositionY() + 80);
        sightLine.setStrokeWidth(7);
        return !checkCollision(sightLine, obstacles);
    }

    /**
     * Check if entity stuck.
     * @param entity the entity
     * @return true if the entity is stuck, false otherwise
     */
    public static boolean checkIfEntityStuck(Entity entity) {
        Point2D currentPosition = new Point2D(entity.getPositionX(), entity.getPositionY());
        if (entity.getPreviousPositions().contains(currentPosition)) {
            entity.setCounter(entity.getCounter() + 1);
        } else {
            entity.getPreviousPositions().add(currentPosition);
        }
        if (entity.getPreviousPositions().size() > Constants.MAX_PREV_POS_LIST_SIZE) {
            entity.getPreviousPositions().removeFirst();
        }
        return entity.getCounter() > Constants.MAX_COUNTER;
    }

    /**
     * Check if the player is near the conductor.
     * @param conductor the conductor
     * @param player the player
     * @return true if the player is near the conductor, false otherwise
     * <br>
     * <br>
     * Note: the conductor can "feel" the player along its y-axis
     */
    public static boolean checkIfConductorNearPlayer(Entity conductor, Player player) {
        Line conductorLine = new Line(conductor.getPositionX(), 0, conductor.getPositionX(), 1000);
        Shape playerHitbox = player.getHitbox();
        Shape conductorHitbox = conductor.getHitbox();
        return checkCollision(conductorLine, playerHitbox) || checkCollision(conductorLine, conductorHitbox);
    }

    /**
     * Check if the player can speak with the entities.
     * @param player the player
     * @param entities the entities
     * @return the entity the player can speak with, null otherwise
     */
    public static Entity checkIfPlayerCanSpeak(Player player, List<Entity> entities) {
        if (entities == null) {
            return null;
        }
        for (Entity entity : entities) {
            if (entity != null) {
                if (entity.isAlive()) {
                    if (entity.getDialoguePath() != null && checkCollision(player.getAttackRange(), entity.getHitbox()) && Objects.equals(entity.getBehaviour(), Constants.Behaviour.NEUTRAL)) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check if player can shoot.
     * @param player the player
     * @param aimX the x coordinate of the aim
     * @param aimY the y coordinate of the aim
     * @param target the target
     * @param obstacles obstacles
     * @param time the time
     * @return true if the player can shoot, false otherwise
     */
    public static boolean checkIfPlayerCanShoot(Player player, int aimX, int aimY, Entity target, Shape obstacles, long time) {
        Line aimLine = new Line(player.getPositionX() + 32, player.getPositionY() + 64, aimX, aimY);
        aimLine.setStrokeWidth(7);
        Circle shootHitbox = (Circle) target.getHitbox();
        shootHitbox.setStroke(Color.RED);
        shootHitbox.setStrokeWidth(7);
        shootHitbox.setRadius(12);
        if (!checkCollision(aimLine, obstacles)) {
            if (player.getCooldown() == 0) {
                player.setCanAttack(true);
            }
            if (player.getCanAttack()) {
                player.setCanAttack(false);
                player.setWhenAttacked(time);
                return checkCollision(aimLine, shootHitbox);
            }
            if (!player.getCanAttack() && (time - player.getWhenAttacked() != 0) && (time - player.getWhenAttacked()) % player.getCooldown() == 0) {
                player.setCanAttack(true);
                player.setWhenAttacked(0);
            }
        }
        return false;
    }

    /**
     * Check if player has a valid ticket.
     * @param itemsArray the items array
     * @return true if the player has a valid ticket, false otherwise
     */
    public static boolean checkIfPlayerHasTicket(Item[] itemsArray) {
        for (Item item : itemsArray) {
            if (item != null) {
                if (item.getType() == Constants.ItemType.VALID_TICKET) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the player has a key in the main hand.
     * @param player - player to check
     * @return - true if the player has a key in the main hand, false otherwise
     */
    public static boolean checkIfPlayerHasKeyInMainHand(Player player) {
        if (player.getPlayerInventory().getMainHandItem() != null) {
            return player.getPlayerInventory().getMainHandItem().getType().equals(Constants.ItemType.KEY);
        }
        return false;
    }

    /**
     * Check if there is a collision between two shapes.
     * @param hitbox1 the first hitbox
     * @param hitbox2 the second hitbox
     * @return true if there is a collision, false otherwise
     */
    public static boolean checkCollision(Shape hitbox1, Shape hitbox2) {
        Shape shape1 = Shape.union(hitbox1, hitbox1);
        Shape shape2 = Shape.union(hitbox2, hitbox2);
        Shape intersect = Shape.intersect(shape1, shape2);
        return !intersect.getBoundsInParent().isEmpty();
    }

    /**
     * Get the collision point between two shapes.
     * @param hitbox1 the first hitbox
     * @param hitbox2 the second hitbox
     * @return the collision point
     */
    public static int[] getCollisionPoint(Shape hitbox1, Shape hitbox2) {
        Shape intersect = Shape.intersect(hitbox1, hitbox2);
        if (!intersect.getBoundsInParent().isEmpty()) {
            return new int[]{(int) intersect.getBoundsInParent().getMinX(), (int) intersect.getBoundsInParent().getMinY()};
        }
        return null;
    }

    /**
     * Check if the x position of the object is within the player's range (constant).
     * @param objectIsoXY the object's isometric x and y position
     * @return true if the x position of the object is within the player's range, false otherwise
     */
    public static boolean checkX(Entity entity, double[] objectIsoXY) {
        return (objectIsoXY[0] > entity.getPositionX() - Constants.TILE_WIDTH && objectIsoXY[0] < entity.getPositionX() + 3 * Constants.TILE_WIDTH);
    }

    /**
     * Check if the y position of the object is lower than the player's y position.
     * @param objectIsoXY the object's isometric x and y position
     * @param objectTexture the object's texture
     * @return true if the y position of the object is lower than the player's y position, false otherwise
     */
    public static boolean checkY(Entity entity, double[] objectIsoXY, Image objectTexture) {
        return (entity.getPositionY() + (double) Constants.TILE_HEIGHT / 2 + entity.getHeight() * Constants.TILE_HEIGHT < (objectIsoXY[1] - Constants.TILE_HEIGHT + objectTexture.getHeight()));
    }
}
