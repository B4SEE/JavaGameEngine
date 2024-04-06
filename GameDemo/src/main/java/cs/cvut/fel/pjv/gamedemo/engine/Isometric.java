package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
//consider bitmap

/**
 * Class for isometric graphics logic.
 */
public class Isometric {
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
    private final Pane grid = new Pane();
    private Label hint = new Label();
    private Line aimLine = new Line();
    private Scene isoScene;
    public Isometric() {
    }
    /**
     * Remove dead entities from the grid.
     */
    public void removeDeadEntities() {
        if (!player.isAlive()) {
            grid.getChildren().remove(hint);
        }
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity != null) {
                    if (!entity.isAlive()) {
                        grid.getChildren().remove(entity.getEntityView());
                        grid.getChildren().remove(entity.getHitbox());
                        grid.getChildren().remove(entity.getTrackPoint());
                        grid.getChildren().remove(entity.getAttackRange());
                    }
                }
            }
        }
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
        if (mainStage == null) {
            System.out.println("Stage is not initialised");
            System.out.println("Exiting the program");
            System.exit(1);
        }
        if (player == null) {
            System.out.println("Player is not initialised");
            System.out.println("Exiting the program");
            System.exit(1);
        }
        if (!Checker.checkMap(map)) {
            System.out.println("Map string is not valid");
            System.out.println("Exiting the program");
            System.exit(1);
        }
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");
        placeMap();
        isoScene = mainStage.getScene();
        mainStage.setResizable(false);
        mainStage.show();
    }

    /**
     * Update player's texture.
     * @param texturePath the texture path
     */
    public void updatePlayerTexture(String texturePath) {
        player.setTexturePath(texturePath);
    }
    /**
     * Reset the game:
     * reset the player and entities, clear the grid, and restart the game.
     */
    public void reset() {
        resetPlayer();
        resetEntities();
        grid.getChildren().clear();
        start();
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
    }
    /**
     * Initialise the main stage.
     * @param stage the main stage
     */
    public void initialiseStage(Stage stage) {
        mainStage = stage;
        Scene scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainStage.setScene(scene);
    }

    /**
     * Initialise the wagon.
     * @param wagon the wagon
     */
    public void initialiseWagon(Wagon wagon) {
        if (wagon == null) {
            return;
        }
        setMap(wagon.getSeed());
        setObjectsToDraw(wagon.getObjectsArray());
        setEntities(wagon.getEntities());
    }
    public void setMap(String map) {
        this.map = map;
    }
    public void setObjectsToDraw(Object[][] objectsToDraw) {
        this.objectsToDraw = objectsToDraw;
    }
    /**
     * Set the player.
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
        setEntityBoxes(this.player);
        this.player.setStartPositionX(player.getPositionX());
        this.player.setStartPositionY(player.getPositionY());
        updatePlayerDeltaX(0);
        updatePlayerDeltaY(0);
    }
    /**
     * Reset the player.
     */
    public void resetPlayer() {
        grid.getChildren().remove(player.getEntityView());
        grid.getChildren().remove(player.getHitbox());
        grid.getChildren().remove(player.getTrackPoint());
        grid.getChildren().remove(player.getAttackRange());

        player.setHealth(Constants.PLAYER_MAX_HEALTH);
        player.setHunger(Constants.PLAYER_MAX_HUNGER);
        player.setPositionX(player.getStartPositionX());
        player.setPositionY(player.getStartPositionY());
        setEntityBoxes(player);
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
        this.entities = entities;
        drawnEntities = new ArrayList<>();
        for (Entity entity : this.entities) {
            setEntityBoxes(entity);
            entity.setStartPositionX(entity.getPositionX());
            entity.setStartPositionY(entity.getPositionY());
        }
    }
    public void setEntityBoxes(Entity entity) {
        entity.setHitbox(getEntityHitBox((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight(), entity.getHitBoxSize()));
        entity.setTrackPoint(getEntityTrackPoint((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight()));
        entity.setAttackRange(getEntityAttackRange((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight(), 1));
    }
    /**
     * Reset the entities.
     */
    public void resetEntities() {
        for (Entity entity : entities) {
            grid.getChildren().remove(entity.getEntityView());
            grid.getChildren().remove(entity.getHitbox());
            grid.getChildren().remove(entity.getTrackPoint());
            grid.getChildren().remove(entity.getAttackRange());
            drawnEntities = new ArrayList<>();

            entity.setHealth(entity.getMaxHealth());
            entity.setPositionX(entity.getStartPositionX());
            entity.setPositionY(entity.getStartPositionY());
            entity.setHitbox(getEntityHitBox((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight(), entity.getHitBoxSize()));
            entity.setTrackPoint(getEntityTrackPoint((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight()));
            entity.setAttackRange(getEntityAttackRange((int) entity.getPositionX(), (int) entity.getPositionY(), entity.getHeight(), entity.getAttackRangeSize()));
            entity.setBehaviour(entity.getInitialBehaviour());
        }
    }
    /**
     * Get the entities.
     * @return the entities
     */
    public List<Entity> getEntities() {
        return entities;
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

        //slipX and slipY are used to set how fast the entity will slip when trying to move diagonally
        int slipX = Constants.SLIP_X;
        int slipY = Constants.SLIP_Y;

        //logic for slipping when entity collides with walls
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
        //check collision with walls
        return !Checker.checkCollision(entity.getHitbox(), walls);
    }
    /**
     * Update all the objects in the game.
     */
    public void updateAll() {
        grid.getChildren().clear();
        placeFloor();
        placePolygons();
        mergeObjectHitboxes();
        placeWalls();
    }
    /**
     * Move the grid by the specified delta x and delta y.
     * If there is a collision with the player hitbox and the walls, the delta x and delta y are adjusted.
     * @param deltaX the delta x
     * @param deltaY the delta y
     */
    public void moveGrid(int deltaX, int deltaY) {
        if (Checker.checkCollision(player.getHitbox(), walls)) {
            this.deltaX -= deltaX;
            this.deltaY -= deltaY;
            return;
        }
        this.deltaX += deltaX;
        this.deltaY += deltaY;
        updateAll();
    }
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
                    double[] objectIsoXY = cartesianToIsometric(object.getCartX(), object.getCartY());
                    grid.getChildren().remove(object.getTexture());
                    playerDrawn = isEntitiesDrawn(playerDrawn, objectTexture, objectIsoXY);
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
        placeFloor();
        placePolygons();
        mergeObjectHitboxes();
        placeWalls();
    }
    /**
     * Place/draw the floor.
     */
    private void placeFloor() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {

                int x = Constants.TILE_WIDTH + j * Constants.TILE_WIDTH + deltaX * Constants.TILE_WIDTH;
                int y = i * Constants.TILE_HEIGHT + deltaY * Constants.TILE_HEIGHT;

                if (objectsToDraw[i][j].getHeight() == 0) {
                    Image objectTexture = new Image(objectsToDraw[i][j].getTexturePath());

                    objectsToDraw[i][j].setCartX((int) (x + 2 * Constants.TILE_WIDTH - objectTexture.getHeight()));
                    objectsToDraw[i][j].setCartY((int) (y + Constants.TILE_HEIGHT - objectTexture.getHeight()));
                    double[] objectIsoXY = cartesianToIsometric(objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());
                    objectsToDraw[i][j].setIsoX(objectIsoXY[0]);
                    objectsToDraw[i][j].setIsoY(objectIsoXY[1]);

                    ImageView object = new ImageView(objectTexture);
                    objectsToDraw[i][j].setTexture(object);

                    placeIsometricTileWithTexture(object, objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());
                }
            }
        }
    }
    /**
     * Place/draw the walls.
     */
    private void placeWalls() {
        boolean playerDrawn = false;

        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() > 0) {
                    int x = Constants.TILE_WIDTH + j * Constants.TILE_WIDTH + deltaX * Constants.TILE_WIDTH;
                    int y = i * Constants.TILE_HEIGHT + deltaY * Constants.TILE_HEIGHT;

                    //get texture

                    Image objectTexture = new Image(objectsToDraw[i][j].getTexturePath());

                    objectsToDraw[i][j].setCartX((int) (x + 2 * Constants.TILE_WIDTH - objectTexture.getHeight()));
                    objectsToDraw[i][j].setCartY((int) (y + Constants.TILE_HEIGHT - objectTexture.getHeight()));

                    ImageView object = new ImageView(objectTexture);
                    objectsToDraw[i][j].setTexture(object);
                    double[] objectIsoXY = cartesianToIsometric(objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());
                    objectsToDraw[i][j].setIsoX(objectIsoXY[0]);
                    objectsToDraw[i][j].setIsoY(objectIsoXY[1]);
//                    System.out.println("Drawing object at " + objectsToDraw[i][j].getIsoX() + " " + objectsToDraw[i][j].getIsoY());

                    playerDrawn = isEntitiesDrawn(playerDrawn, objectTexture, objectIsoXY);

                    placeIsometricTileWithTexture(objectsToDraw[i][j].getTexture(), objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());

                }
            }
        }
        drawEntitiesAbove(playerDrawn);
    }

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
        //add health bar
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
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                int x = Constants.TILE_WIDTH + j * Constants.TILE_WIDTH + deltaX * Constants.TILE_WIDTH;
                int y = i * Constants.TILE_HEIGHT + deltaY * Constants.TILE_HEIGHT;
                Polygon parallelogram = getObjectHitBox(x, y);
                objectsToDraw[i][j].setObjectHitbox(parallelogram);
//                if (objectsToDraw[i][j].getId() == 2) {
//                    System.out.println(objectsToDraw[i][j].getObjectHitbox());
//                }
            }
        }
    }
    /**
     * Merge the object hitboxes to create one solid shape.
     */
    private void mergeObjectHitboxes() {
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
//        circle.setStyle("-fx-opacity: 0");
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
//        circle.setStyle("-fx-stroke: #48ff00; -fx-stroke-width: 2; -fx-fill: #83ff00;");
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
        double[] isoXY1 = cartesianToIsometric(cartX, cartY);
        double[] isoXY2 = cartesianToIsometric(cartX + Constants.TILE_WIDTH, cartY);
        double[] isoXY3 = cartesianToIsometric(cartX + Constants.TILE_WIDTH, cartY + Constants.TILE_HEIGHT);
        double[] isoXY4 = cartesianToIsometric(cartX, cartY + Constants.TILE_WIDTH);

        parallelogram.getPoints().addAll(isoXY1[0] + Constants.TILE_WIDTH, isoXY1[1],
                isoXY2[0] + Constants.TILE_WIDTH, isoXY2[1],
                isoXY3[0] + Constants.TILE_WIDTH, isoXY3[1],
                isoXY4[0] + Constants.TILE_WIDTH, isoXY4[1]);
        parallelogram.setStyle("-fx-opacity: 0");

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
            bulletHitbox = new Line(player.getPositionX() + 32, player.getPositionY() + 64, collisionPoint[0], collisionPoint[1]);
            bulletHitbox.setStyle("-fx-stroke: #0095ff; -fx-stroke-width: 1; -fx-opacity: 0.5;");
        }
        aimLine = bulletHitbox;
    }
    public void resetAimLine() {
        aimLine = new Line();
    }

    /**
     * Get the walls/obstacles with height 2 or taller.
     * @return Shape of the walls with height 2 or taller
     */
    public Shape getTwoAndTallerWalls() {
        return twoAndTallerWalls;
    }
    public Shape getWalls() {
        return walls;
    }
    public void setWalls(Shape walls) {
        this.walls = walls;
    }
}