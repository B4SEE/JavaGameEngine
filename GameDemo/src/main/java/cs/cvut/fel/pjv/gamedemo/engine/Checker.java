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
    public static boolean checkIfPlayerHasKeyInMainHand(Player player) {
        if (player.getPlayerInventory().getMainHandItem() != null) {
            return player.getPlayerInventory().getMainHandItem().getType().equals(Constants.ItemType.KEY);
        }
        return false;
    }
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
    public static boolean checkIfEntityRemember(Entity entity, Entity target, Shape obstacle, long time) {
        if (!checkIfEntityCanSee(entity, target, obstacle)) {//if the entity cannot see the target, check if it remembers the target
            if ((time - entity.getWhenStartedPursuing() != 0 && (time - entity.getWhenStartedPursuing()) % 13 == 0) || entity.getWhenStartedPursuing() == 0) {
                entity.setWhenStartedPursuing(0);
                return false;
            }
        }
        //entity can see the target
        entity.setWhenStartedPursuing(time);
        return true;
    }
    public static boolean checkIfEntityCanSee(Entity entity, Entity target, Shape obstacle) {
        Line sightLine = new Line(entity.getPositionX() + 32, entity.getPositionY() + 80, target.getPositionX() + 32, target.getPositionY() + 80);
        sightLine.setStrokeWidth(7);
        return !checkCollision(sightLine, obstacle);
    }
    public static boolean checkIfPlayerCanShoot(Player player, int aimX, int aimY, Entity target, Shape obstacles, long time) {
        Line aimLine = new Line(player.getPositionX() + 32, player.getPositionY() + 64, aimX, aimY);
        aimLine.setStrokeWidth(7);
        Circle shootHitbox = (Circle) target.getHitbox();
        shootHitbox.setStroke(Color.RED);
        shootHitbox.setStrokeWidth(7);
        shootHitbox.setRadius(12);
        System.out.println(shootHitbox.getRadius());
        System.out.println(((Circle) target.getHitbox()).getRadius());
        System.out.println("aim line: " + aimLine.getStartX() + " " + aimLine.getStartY() + " " + aimLine.getEndX() + " " + aimLine.getEndY());
        if (!checkCollision(aimLine, obstacles)) {
            if (player.getCooldown() == 0) {
                player.setCanAttack(true);
                System.out.println("can attack");
            }
            if (player.getCanAttack()) {
                player.setCanAttack(false);
                player.setWhenAttacked(time);
                System.out.println("attacked");
                return checkCollision(aimLine, shootHitbox);
            }
            if (!player.getCanAttack() && (time - player.getWhenAttacked() != 0) && (time - player.getWhenAttacked()) % player.getCooldown() == 0) {
                System.out.println("can attack again");
                player.setCanAttack(true);
                player.setWhenAttacked(0);
            }
        }
        return false;
    }
    public static boolean checkIfEntityStuck(Entity entity) {
        Point2D currentPosition = new Point2D(entity.getPositionX(), entity.getPositionY());
        //check if the entity has already visited the current position
        if (entity.getPreviousPositions().contains(currentPosition)) {
            //increase the counter
            entity.setCounter(entity.getCounter() + 1);
//                        System.out.println("Entity revisited position: " + currentPosition);
        } else {
            //add the current position to the list of previous positions
            entity.getPreviousPositions().add(currentPosition);
        }
        //remove the oldest position (so the list does not grow indefinitely)
        if (entity.getPreviousPositions().size() > Constants.MAX_PREV_POS_LIST_SIZE) {
            entity.getPreviousPositions().removeFirst(); // Remove the oldest position
        }
        //check if entity is stuck
        return entity.getCounter() > Constants.MAX_COUNTER;
    }
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

    public static boolean checkIfConductorNearPlayer(Entity conductor, Player player) {
        //create shape/line along conductor's y-axis and check if it intersects with player's hitbox
        Line conductorLine = new Line(conductor.getPositionX(), 0, conductor.getPositionX(), 1000);
        Shape playerHitbox = player.getHitbox();
        Shape conductorHitbox = conductor.getHitbox();
        //check if player is near the conductor
        if (checkCollision(conductorLine, playerHitbox) || checkCollision(conductorLine, conductorHitbox)) {
            System.out.println("Conductor can feel player's presence");
            return true;
        }
        return false;
    }
}
