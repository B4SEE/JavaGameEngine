package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
//consider bitmap

/**
 * Class for isometric graphics logic.
 */
public class Isometric {
    private static final Logger logger = LogManager.getLogger(Isometric.class);
    private int deltaX = 10;
    private int deltaY = 0;
    private String map = "";
    private Object[][] objectsToDraw;
    private Shape walls;
    private Shape twoAndTallerWalls;
    private Player player;
    private List<Entity> entities;
    private List<Entity> drawnEntities;
    private int playerDeltaX = 0;
    private int playerDeltaY = 0;
    protected Stage mainStage;
    private Pane grid = new Pane();
    private Label hint = new Label();
    private Line aimLine = new Line();
    private Scene isoScene;
    private ImageView mainHandSlotImage = new ImageView();
    private Rectangle mainHandSlot = new Rectangle();
    public Isometric() {
    }

    /**
     * Initialize and start the game.
     * <br>
     * <br>
     * If the main stage, player, or map is not initialized properly,
     * the program exits with an error message.
     * <br>
     * <br>
     * Set up the game grid, load the map, and set the scene.
     * Set key event handlers for player movement.
     * <br>
     * <br>
     * Note: Player movement may experience lag, requires further investigation.
     */
    public void start() {
        logger.info("Starting isometric graphics...");
        if (mainStage == null) {
            logger.error("Main stage is not initialised");
            logger.error("Exiting...");
            System.exit(1);
        }
        if (player == null) {
            logger.error("Player is not initialised");
            logger.error("Exiting...");
            System.exit(1);
        }
        if (!Checker.checkMap(map)) {
            logger.error("Map is not valid");
            logger.error("Exiting...");
            System.exit(1);
        }
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");
        placeMap();
        isoScene = mainStage.getScene();
        mainStage.setResizable(false);
        mainStage.show();
        logger.info("Isometric graphics started");
    }

    /**
     * Update player's texture.
     * @param texturePath the texture path
     */
    public void updatePlayerTexture(String texturePath) {
        player.setTexturePath(texturePath);
    }

    /**
     * Set the key event handlers to null. Needed for death screen.
     */
    public void setHandleToNull() {
        isoScene.onKeyReleasedProperty().set(null);
        isoScene.onKeyPressedProperty().set(null);
    }

    /**
     * Clear the grid and all the game data.
     */
    public void clearAll() {
        logger.info("Clearing all isometric graphics data...");
        grid.getChildren().clear();
        objectsToDraw = null;
        map = "";
        walls = null;
        twoAndTallerWalls = null;
        player = null;
        entities = null;
        drawnEntities = null;
        playerDeltaX = 0;
        playerDeltaY = 0;
        hint = new Label();
        logger.info("Isometric graphics data cleared");
    }

    /**
     * Initialise the main stage.
     * @param stage the main stage
     */
    public void initialiseStage(Stage stage) {
        logger.debug("Initialising main stage...");
        grid = new Pane();
        mainStage = stage;
        Scene scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainStage.setScene(scene);
        logger.debug("Main stage initialised");
    }

    /**
     * Initialise the wagon.
     * @param wagon the wagon
     */
    public void initialiseWagon(Wagon wagon) {
        logger.debug("Initialising wagon...");
        if (wagon == null) {
            logger.error("Wagon is not initialised");
            return;
        }
        if (wagon.getSeed() == null) {
            logger.error("Wagon seed is not initialised");
            return;
        }
        setMap(wagon.getSeed());
        setObjectsToDraw(wagon.getObjectsArray());
        setEntities(wagon.getEntities());
        logger.debug("Wagon initialised");
    }

    /**
     * Set the map.
     * @param map the map
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * Set the objects to draw.
     * @param objectsToDraw the objects to draw
     */
    public void setObjectsToDraw(Object[][] objectsToDraw) {
        this.objectsToDraw = objectsToDraw;
    }

    /**
     * Set the player.
     * @param player the player
     */
    public void setPlayer(Player player) {
        logger.debug("Setting player...");
        this.player = player;
        setEntityBoxes(this.player);
        this.player.setStartPositionX(player.getPositionX());
        this.player.setStartPositionY(player.getPositionY());
        updatePlayerDeltaX(0);
        updatePlayerDeltaY(0);
        logger.debug("Player set");
    }

    /**
     * Get the player.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the entities.
     * @param entities the entities
     */
    public void setEntities(List<Entity> entities) {
        logger.debug("Setting entities...");
        this.entities = entities;
        drawnEntities = new ArrayList<>();
        for (Entity entity : this.entities) {
            setEntityBoxes(entity);
            entity.setStartPositionX(entity.getPositionX());
            entity.setStartPositionY(entity.getPositionY());
        }
        logger.debug("Entities set");
    }

    /**
     * Set the entity hitboxes, track points, and attack ranges.
     * @param entity the entity
     */
    public void setEntityBoxes(Entity entity) {
        entity.setHitbox(getEntityHitBox((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight(), entity.getHitBoxSize()));
        entity.setTrackPoint(getEntityTrackPoint((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight()));
        entity.setAttackRange(getEntityAttackRange((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight(), entity.getAttackRangeSize()));
    }

    /**
     * Update the label with the specified text.
     * @param text the text to display
     */
    public void updateHint(String text, int x, int y) {
        grid.getChildren().remove(hint);
        hint.setText(text);
        hint.setLayoutX(x);
        hint.setLayoutY(y);
        hint.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(hint);
    }

    /**
     * Update the player's delta x.
     * @param deltaX the player's delta x
     */
    public void updatePlayerDeltaX(int deltaX) {
        this.playerDeltaX = deltaX;
    }

    /**
     * Update the player's delta y.
     * @param deltaY the player's delta y
     */
    public void updatePlayerDeltaY(int deltaY) {
        this.playerDeltaY = deltaY;
    }

    /**
     * Update the player's position.
     * If the player's hitbox collides with the walls, the player's position is not updated.
     */
    public void updatePlayerPosition() {
        updateEntityPosition(player, playerDeltaX, playerDeltaY, Constants.PLAYER_BASIC_SPEED_X, Constants.PLAYER_BASIC_SPEED_Y);
    }

    /**
     * Update the position of the entity.
     * @param entity the entity
     * @param deltaX the delta x
     * @param deltaY the delta y
     * @param speedX the x-axis speed
     * @param speedY the y-axis speed
     * <br>
     * <br>
     * if the entity's hitbox collides with the walls, the entity's position is not updated
     */
    public void updateEntityPosition(Entity entity, double deltaX, double deltaY, int speedX, int speedY) {

        int slipX = Constants.SLIP_X;
        int slipY = Constants.SLIP_Y;

        if (!tryToMove(entity, deltaX, deltaY)) {
            if (deltaX > 0) {
                if (tryToMove(entity, 0, -slipY)) {
                    deltaY = -slipY;
                } else if (tryToMove(entity, 0, slipY)) {
                    deltaY = slipY;
                } else {
                    return;
                }
            } else if (deltaX < 0) {
                if (tryToMove(entity, 0, slipY)) {
                    deltaY = slipY;
                } else if (tryToMove(entity, 0, -slipY)) {
                    deltaY = -slipY;
                } else {
                    return;
                }
            } else if (deltaY > 0) {
                if (tryToMove(entity, slipX, 0)) {
                    deltaX = slipX;
                    deltaY = 0;
                } else if (tryToMove(entity, -slipX, 0)) {
                    deltaX = -slipX;
                    deltaY = 0;
                } else {
                    return;
                }
            } else if (deltaY < 0) {
                if (tryToMove(entity, -slipX, 0)) {
                    deltaX = -slipX;
                    deltaY = 0;
                } else if (tryToMove(entity, slipX, 0)) {
                    deltaX = slipX;
                    deltaY = 0;
                } else {
                    return;
                }
            }
        }

        //normalise the vector for diagonal movement
        double mag = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        deltaX = (int) (deltaX / mag * speedX / 2); //otherwise the entity moves too fast
        deltaY = (int) (deltaY / mag * speedY / 2);
        entity.setPositionX(entity.getPositionX() + deltaX);
        entity.setPositionY(entity.getPositionY() + deltaY);
    }

    /**
     * Move the entity to the specified position.
     * @param entity the entity
     * @param deltaX the delta x
     * @param deltaY the delta y
     * @return true if the entity can move to the specified position, false otherwise
     * <br>
     * <br>
     * Note: the entity's hitbox is translated to the specified position and checked for collision with the walls
     * If the entity's hitbox collides with the walls, the entity's position is not updated
     * Used for player and entity movement and slipping when colliding with walls
     */
    private boolean tryToMove(Entity entity, double deltaX, double deltaY) {
        entity.getHitbox().translateXProperty().set(entity.getPositionX() - entity.getStartPositionX() + deltaX);
        entity.getHitbox().translateYProperty().set(entity.getPositionY() - entity.getStartPositionY() + deltaY);

        entity.getTrackPoint().translateXProperty().set(entity.getPositionX() - entity.getStartPositionX() + deltaX);
        entity.getTrackPoint().translateYProperty().set(entity.getPositionY() - entity.getStartPositionY() + deltaY);

        entity.getAttackRange().translateXProperty().set(entity.getPositionX() - entity.getStartPositionX() + deltaX);
        entity.getAttackRange().translateYProperty().set(entity.getPositionY() - entity.getStartPositionY() + deltaY);

        return !Checker.checkCollision(entity.getHitbox(), walls);
    }

    /**
     * Update all the objects in the game.
     */
    public void updateAll() {
        logger.debug("Updating all objects...");
        grid.getChildren().clear();
        placeFloor();
        placePolygons();
        mergeObjectHitboxes();
        placeWalls();
        logger.debug("All objects updated");
    }

//    /**
//     * Move the grid by the specified delta x and delta y.
//     * If there is a collision with the player hitbox and the walls, the delta x and delta y are adjusted.
//     * @param deltaX the delta x
//     * @param deltaY the delta y
//     */
//    public void moveGrid(int deltaX, int deltaY) {
//        if (Checker.checkCollision(player.getHitbox(), walls)) {
//            this.deltaX -= deltaX;
//            this.deltaY -= deltaY;
//            return;
//        }
//        this.deltaX += deltaX;
//        this.deltaY += deltaY;
//        updateAll();
//    }

    /**
     * Update the walls.
     */
    public void updateWalls() {
        grid.getChildren().remove(player.getEntityView());
        grid.getChildren().remove(player.getHitbox());
        grid.getChildren().remove(player.getTrackPoint());
        grid.getChildren().remove(player.getAttackRange());
        grid.getChildren().removeIf(node -> node instanceof Rectangle);
        grid.getChildren().removeIf(node -> node instanceof Line);
        if (entities != null) {
            drawnEntities = new ArrayList<>();
            for (Entity entity : entities) {
                if (entity != null) {
                    grid.getChildren().remove(entity.getEntityView());
                    grid.getChildren().remove(entity.getHitbox());
                    grid.getChildren().remove(entity.getTrackPoint());
                    grid.getChildren().remove(entity.getAttackRange());
                }
            }
        }
        boolean playerDrawn = false;
        for (Object[] objects : objectsToDraw) {
            for (Object object : objects) {
                if (object.getHeight() > 0) {
                    Image objectTexture = new Image(object.getTexturePath());
                    grid.getChildren().remove(object.getTexture());
                    playerDrawn = isEntitiesDrawn(playerDrawn, objectTexture, new double[]{object.getIsoX(), object.getIsoY()});
                    placeIsometricTileWithTexture(object.getTexture(), object.getCartX(), object.getCartY());
                }
            }
        }
        drawEntitiesAbove(playerDrawn);
    }

    /**
     * Draw the map and the player.
     */
    private void placeMap() {
        logger.debug("Drawing map...");
        placePolygons();
        placeFloor();
        mergeObjectHitboxes();
        placeWalls();
        logger.debug("Map drawn");
    }

    /**
     * Place/draw the floor.
     */
    private void placeFloor() {
        logger.debug("Drawing floor...");
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {

                int x = Constants.TILE_WIDTH + j * Constants.TILE_WIDTH + deltaX * Constants.TILE_WIDTH;
                int y = i * Constants.TILE_HEIGHT + deltaY * Constants.TILE_HEIGHT;

                if (objectsToDraw[i][j].getHeight() == 0) {
                    initObject(objectsToDraw[i][j], x, y);

                    placeIsometricTileWithTexture(objectsToDraw[i][j].getTexture(), objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());
                }
            }
        }
        logger.debug("Floor drawn");
    }

    /**
     * Initialise the object.
     * @param object the object
     * @param x the x position
     * @param y the y position
     */
    private void initObject(Object object, int x, int y) {
        Image objectTexture = new Image(object.getTexturePath());

        object.setCartX((int) (x + 2 * Constants.TILE_WIDTH - objectTexture.getHeight()));
        object.setCartY((int) (y + Constants.TILE_HEIGHT - objectTexture.getHeight()));
        double[] objectIsoXY = cartesianToIsometric(object.getCartX(), object.getCartY());
        object.setIsoX(objectIsoXY[0]);
        object.setIsoY(objectIsoXY[1]);

        ImageView objectView = new ImageView(objectTexture);
        object.setTexture(objectView);
    }

    /**
     * Place/draw the walls.
     */
    private void placeWalls() {
        logger.debug("Drawing walls...");
        boolean playerDrawn = false;

        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {

                int x = Constants.TILE_WIDTH + j * Constants.TILE_WIDTH + deltaX * Constants.TILE_WIDTH;
                int y = i * Constants.TILE_HEIGHT + deltaY * Constants.TILE_HEIGHT;

                if (objectsToDraw[i][j].getHeight() > 0) {

                    initObject(objectsToDraw[i][j], x, y);

                    playerDrawn = isEntitiesDrawn(playerDrawn, objectsToDraw[i][j].getTexture().getImage(), new double[]{objectsToDraw[i][j].getIsoX(), objectsToDraw[i][j].getIsoY()});

                    placeIsometricTileWithTexture(objectsToDraw[i][j].getTexture(), objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());
                }
            }
        }
        drawEntitiesAbove(playerDrawn);
        logger.debug("Walls drawn");
    }

    /**
     * Draw the entities.
     * @param playerDrawn true if the player is drawn, false otherwise
     */
    private void drawEntitiesAbove(boolean playerDrawn) {
        if (!playerDrawn) {
            drawEntity(player);
        }
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity != null && entity.isAlive() && !drawnEntities.contains(entity)) {
                    drawEntity(entity);
                    drawnEntities.add(entity);
                }
            }
        }
    }

    /**
     * Check if the entities are drawn.
     * @param playerDrawn true if the player is drawn, false otherwise
     * @param objectTexture the object texture
     * @param objectIsoXY the object isometric x and y position
     * @return true if the entities are drawn, false otherwise
     */
    private boolean isEntitiesDrawn(boolean playerDrawn, Image objectTexture, double[] objectIsoXY) {
        if (Checker.checkX(player, objectIsoXY) && Checker.checkY(player, objectIsoXY, objectTexture) && !playerDrawn) {
            drawEntity(player);
            playerDrawn = true;
        }
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity != null && entity.isAlive() && !drawnEntities.contains(entity)) {
                    if (Checker.checkX(entity, objectIsoXY) && Checker.checkY(entity, objectIsoXY, objectTexture)) {
                        drawEntity(entity);
                        drawnEntities.add(entity);
                    }
                }
            }
        }
        return playerDrawn;
    }

    /**
     * Draw the entity.
     */
    private void drawEntity(Entity entity) {
        entity.setEntityView(new ImageView(entity.getTexturePath()));
        grid.getChildren().add(entity.getHitbox());
        grid.getChildren().add(entity.getAttackRange());
        entity.getEntityView().setX(entity.getPositionX());
        entity.getEntityView().setY(entity.getPositionY());
        grid.getChildren().add(entity.getEntityView());
        drawBars(entity);
    }

    /**
     * Draw the bars for the entity.
     * @param entity the entity
     */
    private void drawBars(Entity entity) {

        int maxHealth = entity.getMaxHealth();
        int health = entity.getHealth();

        int barWidth = Constants.TILE_WIDTH * 2;
        int barHeight = 10;
        int barGap = 20;
        //add background for health bar
        Rectangle healthBarBackground = new Rectangle(entity.getPositionX(), entity.getPositionY() - barGap, barWidth, barHeight);
        healthBarBackground.setStyle("-fx-fill: #313131;");
        //add health bar
        Rectangle healthBar = new Rectangle(entity.getPositionX(), entity.getPositionY() - barGap, barWidth, barHeight);
        healthBar.setStyle("-fx-fill: #af0303;");
        healthBar.setWidth((double) (barWidth * health) / maxHealth);
        //if player, move health bar up and add hunger bar
        if (entity == player) {
            healthBarBackground.setLayoutY(healthBarBackground.getLayoutY() - barGap);
            healthBar.setLayoutY(healthBar.getLayoutY() - barGap);
            healthBar.setStyle("-fx-fill: #217e21;");
            //add background for hunger bar
            Rectangle hungerBarBackground = new Rectangle(entity.getPositionX(), entity.getPositionY() - barGap, barWidth, barHeight);
            hungerBarBackground.setStyle("-fx-fill: #313131;");
            //add hunger bar
            Rectangle hungerBar = new Rectangle(entity.getPositionX(), entity.getPositionY() - barGap, barWidth, barHeight);
            hungerBar.setStyle("-fx-fill: #d07e00;");
            hungerBar.setWidth((double) (barWidth * player.getHunger()) / Constants.PLAYER_MAX_HUNGER);
            grid.getChildren().add(hungerBarBackground);
            grid.getChildren().add(hungerBar);
            grid.getChildren().add(aimLine);
        }
        //add bars to grid
        grid.getChildren().add(healthBarBackground);
        grid.getChildren().add(healthBar);
    }

    /**
     * Place the polygons for object hitboxes.
     */
    private void placePolygons() {
        logger.debug("Setting object hitboxes...");
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                int x = Constants.TILE_WIDTH + j * Constants.TILE_WIDTH + deltaX * Constants.TILE_WIDTH;
                int y = i * Constants.TILE_HEIGHT + deltaY * Constants.TILE_HEIGHT;
                Polygon parallelogram = getObjectHitBox(x, y);
                objectsToDraw[i][j].setObjectHitbox(parallelogram);
            }
        }
        logger.debug("Object hitboxes set");
    }

    /**
     * Merge the object hitboxes to create one solid shape.
     */
    private void mergeObjectHitboxes() {
        logger.debug("Merging object hitboxes...");
        walls = Shape.union(objectsToDraw[0][0].getObjectHitbox(), objectsToDraw[0][1].getObjectHitbox());
        grid.getChildren().remove(objectsToDraw[0][0].getObjectHitbox());
        grid.getChildren().remove(objectsToDraw[0][1].getObjectHitbox());
        for (Object[] objects : objectsToDraw) {
            for (Object object : objects) {
                if (object.isSolid()) {
                    walls = Shape.union(walls, object.getObjectHitbox());
                }
            }
        }
        walls.setStyle("-fx-opacity: 0;");
        //get walls that are two or taller
        twoAndTallerWalls = Shape.union(objectsToDraw[0][0].getObjectHitbox(), objectsToDraw[0][1].getObjectHitbox());
        for (Object[] objects : objectsToDraw) {
            for (Object object : objects) {
                if (object.getHeight() >= 2 && object.isSolid()) {
                    object.getObjectHitbox().scaleXProperty().set(1);
                    object.getObjectHitbox().scaleYProperty().set(1);
//                    object.getObjectHitbox().setTranslateY(-Constants.TILE_HEIGHT * object.getHeight());
                    twoAndTallerWalls = Shape.union(twoAndTallerWalls, object.getObjectHitbox());
                    object.getObjectHitbox().scaleXProperty().set(0.8);
                    object.getObjectHitbox().scaleYProperty().set(0.8);
                }
            }
        }
        twoAndTallerWalls.setStyle("-fx-opacity: 0;");
        grid.getChildren().add(walls);
        grid.getChildren().add(twoAndTallerWalls);
        logger.debug("Object hitboxes merged");
    }

    /**
     * Get the entity hitbox. The hitbox is used for fighting, if entity's hitbox collides with enemy's attack range, the entity is damaged.
     *
     * @param cartX  the entity's x position
     * @param cartY  the entity's y position
     * @param height the entity's height
     * @return the entity hitbox
     */
    private Circle getEntityHitBox(int cartX, int cartY, int height, int hitBoxSize) {
        Circle circle = new Circle();
        int circleCenterX = cartX + Constants.TILE_WIDTH;
        int circleCenterY = cartY + Constants.TILE_HEIGHT / 2 + height * Constants.TILE_HEIGHT;
        circle.setCenterX(circleCenterX);
        circle.setCenterY(circleCenterY);
        circle.setRadius((double) (hitBoxSize * Constants.TILE_WIDTH) / 4 + 2);
        circle.setStyle("-fx-stroke: #693131; -fx-stroke-width: 2; -fx-fill: #693131; -fx-opacity: 1");
        return circle;
    }

    /**
     * Get the entity track point. The track point is used for entity movement, pathfinding, and collision detection.
     *
     * @param cartX  the entity's x position
     * @param cartY  the entity's y position
     * @param height the entity's height
     * @return the entity track point
     */
    private Circle getEntityTrackPoint(int cartX, int cartY, int height) {
        Circle circle = new Circle();
        int circleCenterX = cartX + Constants.TILE_WIDTH;
        int circleCenterY = cartY + Constants.TILE_HEIGHT / 2 + height * Constants.TILE_HEIGHT;
        circle.setCenterX(circleCenterX);
        circle.setCenterY(circleCenterY);
        circle.setRadius(4);
        return circle;
    }

    /**
     * Get the entity attack range. The attack range is used for entity fighting, if entity's attack range collides with enemy's hitbox, the enemy is damaged.
     * @param cartX the entity's x position
     * @param cartY the entity's y position
     * @param height the entity's height
     * @param attackRange the entity's attack range
     * @return the entity attack range
     */
    private Circle getEntityAttackRange(int cartX, int cartY, int height, int attackRange) {
        Circle circle = new Circle();
        int circleCenterX = cartX + Constants.TILE_WIDTH;
        int circleCenterY = cartY + Constants.TILE_HEIGHT / 2 + height * Constants.TILE_HEIGHT;
        circle.setCenterX(circleCenterX);
        circle.setCenterY(circleCenterY);
        circle.setRadius(attackRange * Constants.TILE_WIDTH);
        circle.setStyle("-fx-stroke: #814141; -fx-stroke-width: 2; -fx-fill: #814141; -fx-opacity: 0.3;");
        return circle;
    }

    /**
     * Get the object hitbox.
     *
     * @param cartX the object's x position
     * @param cartY the object's y position
     * @return the object hitbox
     */
    private Polygon getObjectHitBox(int cartX, int cartY) {
        Polygon parallelogram = new Polygon();
        double[][] isoXY = {
                cartesianToIsometric(cartX, cartY),
                cartesianToIsometric(cartX + Constants.TILE_WIDTH, cartY),
                cartesianToIsometric(cartX + Constants.TILE_WIDTH, cartY + Constants.TILE_HEIGHT),
                cartesianToIsometric(cartX, cartY + Constants.TILE_WIDTH)
        };

        for (double[] doubles : isoXY) {
            parallelogram.getPoints().addAll(doubles[0] + Constants.TILE_WIDTH, doubles[1]);
        }

        parallelogram.setStyle("-fx-opacity: 0;");
        parallelogram.setScaleX(0.8);
        parallelogram.setScaleY(0.8);

        return parallelogram;
    }

    /**
     * Convert the cartesian x and y position to isometric x and y position.
     * @param cartX the cartesian x position
     * @param cartY the cartesian y position
     * @return the isometric x and y position
     */
    private double[] cartesianToIsometric(int cartX, int cartY) {
        double[] isoXY = new double[2];
        isoXY[0] = (cartX - cartY);
        isoXY[1] = (double) (cartX + cartY) / 2;
        return isoXY;
    }

    /**
     * Place the isometric tile with the specified texture.
     * @param img the image view
     * @param cartX the cartesian x position
     * @param cartY the cartesian y position
     */
    private void placeIsometricTileWithTexture(ImageView img, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        img.setX(isoXY[0] - Constants.TILE_WIDTH);
        img.setY(isoXY[1] - (double) Constants.TILE_HEIGHT / 2);
        grid.getChildren().add(img);
    }

    /**
     * Draw the player's firearm aim. The aim is a line from the player to the mouse position.
     * If the aim collides with the walls, the aim line changes colour to indicate the collision.
     * @param mouseX the mouse x position
     * @param mouseY the mouse y position
     */
    public void drawPlayerFirearmAim(double mouseX, double mouseY) {
        Line bulletHitbox = new Line(player.getPositionX() + 32, player.getPositionY() + 64, mouseX, mouseY);
        bulletHitbox.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 1; -fx-opacity: 0.5;");

        //cut line to player aim radius (100)
        double mag = Math.sqrt(Math.pow(mouseX - player.getPositionX() - 32, 2) + Math.pow(mouseY - player.getPositionY() - 64, 2));
        double ratio = 100 / mag;
        bulletHitbox.setEndX(player.getPositionX() + 32 + (mouseX - player.getPositionX() - 32) * ratio);
        bulletHitbox.setEndY(player.getPositionY() + 64 + (mouseY - player.getPositionY() - 64) * ratio);

        //if collision with walls, set end of line to the point of collision
        if (Checker.checkCollision(bulletHitbox, twoAndTallerWalls)) {
            int[] collisionPoint = Checker.getCollisionPoint(bulletHitbox, twoAndTallerWalls);
            if (collisionPoint == null) return;
            bulletHitbox = new Line(player.getPositionX() + 32, player.getPositionY() + 64, collisionPoint[0], collisionPoint[1]);
            bulletHitbox.setStyle("-fx-stroke: #0095ff; -fx-stroke-width: 1; -fx-opacity: 0.5;");
        }
        aimLine = bulletHitbox;
    }

    /**
     * Reset the aim line.
     */
    public void resetAimLine() {
        aimLine = new Line();
    }

    /**
     * Show the boss attack target. The target is a circle that expands and then disappears.
     * @param x the x position
     * @param y the y position
     * @param radius the radius
     * @param seconds the number of seconds the target is displayed
     */
    public void showBossAttackTarget(double x, double y, int radius, int seconds) {
        Platform.runLater(() -> {//runlater to avoid concurrent modification exception
            Circle circle = new Circle();
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setRadius(radius * Constants.TILE_WIDTH);
            circle.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 2; -fx-fill: #ff0000; -fx-opacity: 0.3;");
            Circle circleFill = new Circle();
            circleFill.setCenterX(x);
            circleFill.setCenterY(y);
            circleFill.setRadius(0);
            circleFill.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 2; -fx-fill: #ff0000; -fx-opacity: 0.7;");
            grid.getChildren().add(circle);
            grid.getChildren().add(circleFill);
            Thread attackTarget = new Thread(() -> {
                for (int i = 0; i < seconds * 10; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.error("Boss attack circle animation interrupted");
                    }
                    Platform.runLater(() -> {//runlater to avoid concurrent modification exception
                        circleFill.setRadius(circleFill.getRadius() + (double) (radius * Constants.TILE_WIDTH) / (seconds * 10));
                    });
                }
                Platform.runLater(() -> {//runlater to avoid concurrent modification exception
                    grid.getChildren().remove(circle);
                    grid.getChildren().remove(circleFill);
                });
            });
            attackTarget.start();
        });
    }

    /**
     * Show the kidnapping progress.
     * @param stages the number of stages
     * @param currentStage the current stage
     */
    public void showKidnappingProgress(int stages, int currentStage) {
        Rectangle progressBarBackground = new Rectangle(200, 20);
        progressBarBackground.setStyle("-fx-fill: #313131;");
        progressBarBackground.setWidth(200);
        progressBarBackground.setLayoutX(isoScene.getWidth() / 2 - 100);
        progressBarBackground.setLayoutY(50);

        Rectangle progressBar = new Rectangle((double) (200 * currentStage) / stages, 20);
        progressBar.setStyle("-fx-fill: #04adad;");
        progressBar.setLayoutX(isoScene.getWidth() / 2 - 100);
        progressBar.setLayoutY(50);

        grid.getChildren().addAll(progressBarBackground, progressBar);
    }

    /**
     * Get the walls/obstacles with height 2 or taller.
     * @return Shape of the walls with height 2 or taller
     */
    public Shape getTwoAndTallerWalls() {
        return twoAndTallerWalls;
    }

    /**
     * Get the walls/obstacles.
     * @return Shape of the walls
     */
    public Shape getWalls() {
        return walls;
    }

    /**
     * Set the walls/obstacles.
     * @param walls the walls
     */
    public void setWalls(Shape walls) {
        this.walls = walls;
    }

    /**
     * Get the player's delta x.
     * @return the player's delta x
     */

    public int getPlayerDeltaX() {
        return playerDeltaX;
    }

    /**
     * Get the player's delta y.
     * @return the player's delta y
     */
    public int getPlayerDeltaY() {
        return playerDeltaY;
    }

    public void drawPlayerMainHandSlot(Item mainHandItem) {
        //clear the grid
        grid.getChildren().remove(mainHandSlot);
        grid.getChildren().remove(mainHandSlotImage);

        //draw the slot
        mainHandSlot = new Rectangle(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        mainHandSlot.setArcHeight(10);
        mainHandSlot.setArcWidth(10);
        mainHandSlot.setStyle("-fx-fill: #a20808; -fx-stroke: #ffffff; -fx-stroke-width: 10");
        //place the slot in the bottom left corner
        int x = Constants.SLOT_SIZE;
        int y = Constants.WINDOW_HEIGHT - 2 * Constants.SLOT_SIZE;
        mainHandSlot.setX(x);
        mainHandSlot.setY(y);
        grid.getChildren().add(mainHandSlot);
        //draw item in the slot
        if (mainHandItem != null) {
            mainHandSlotImage = new ImageView(new Image(mainHandItem.getTexturePath()));
            mainHandSlotImage.setFitWidth(Constants.SLOT_SIZE);
            mainHandSlotImage.setFitHeight(Constants.SLOT_SIZE);
            mainHandSlotImage.setX(x);
            mainHandSlotImage.setY(y);
            grid.getChildren().add(mainHandSlotImage);
        }
    }
}