package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Isometric {
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private int deltaX = 10;
    private int deltaY = 0;

    //    map example: 11AA_12BB_13CC-21AA_00AA_00BB
//first number tile type: 0 - floor (not Solid), 1 - wall (Solid), 2 - interactive: chest object (not solid), lockable door (solid/not solid), wagon door (solid)
//type 3 is invalid and used only to generate wagon, wagon seed is valid map and only contains 0, 1, 2;
//second number tile height min 1, max 3; floor height is always 0; responsible for drawing order (if player should be drawn in front or behind the object)
//third number tile letter id (for example: AA, BB, CC), represents texture; Dictionaries are in Constants class
//'_' separates tiles, '-' separates rows

    //question: can I move some of the methods to other classes? yes

    //question: can I interpreter Isometric class as a graphic (game) engine? or is it too specific? yes
    //it became even more specific after adding relation with Wagon class, maps cannot be generated and placed without initialised Wagon instance;

    //Note: need to implement A* algorithm for entity movement
    private String map = "";
    private Object[][] objectsToDraw;
    private Object[] interactiveObjects;
    private Shape walls;
    private Player player;
    private List<Entity> entities;
    private List<Entity> drawnEntities;
    private int playerDeltaX = 0;
    private int playerDeltaY = 0;
    protected Stage mainStage;
    private Pane grid = new Pane();
    private Label hint = new Label();
    private Label healthLabel = new Label();
    private long time;
    private Scene isoScene;
    public Isometric() {
    }
    public Isometric(Stage stage) {
        initialiseStage(stage);
    }

    /**
     * Updates the time.
     * @param time the current time (in seconds)
     * <br>
     * <br>
     * Note: the time is updated automatically by the AnimationTimer every INTERVAL nanoseconds;
     * the time is used for entity cooldowns and player death handling;
     * Isometric class does not have its own timer
     */
    public void updateTime(long time) {
        this.time = time;
    }

    /**
     * Sets stage title to player health.
     * <br>
     * <br>
     * Note: not the final implementation, function will be used to draw health bar for all entities and player
     */
    public void showHealth() {
        mainStage.setTitle("player health: " + player.getHealth());
    }
    /**
     * Updates the entities.
     */
    public void updateEntities() {
        if (entities == null) {
            return;
        }
        for (Entity entity : entities) {
            if (entity != null && entity.isAlive()) {
                if (Objects.equals(entity.getBehaviour(), Constants.NEUTRAL)) {
                    return;
                }
                moveEntities();
                entity.tryAttack(entity, List.of(player), time);
            }
        }
    }

    /**
     * Checks if the player can interact with the objects.
     * @return true if the player has interactive objects in his attack range, false otherwise
     */
    public Object checkIfPlayerCanInteract() {
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
     * Checks if the entities and player are alive.
     * Removes the entities from the grid if they are not alive.
     * @return true if the player is not alive, false otherwise
     * <br>
     * <br>
     * Used for player death handling.
     */
    public boolean checkEntities() {
        if (!player.isAlive()) {
            grid.getChildren().remove(hint);

            return true;
        }
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity != null) {
                    if (!entity.isAlive()) {
                        grid.getChildren().remove(entity.getEntityView());
                        grid.getChildren().remove(entity.getHitbox());
                        grid.getChildren().remove(entity.getAttackRange());

                        grid.getChildren().remove(healthLabel);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Initializes and starts the game.
     * <br>
     * <br>
     * If the main stage, player, or map is not initialized properly,
     * the program exits with an error message.
     * <br>
     * <br>
     * Sets up the game grid, loads the map, and sets the scene.
     * Sets key event handlers for player movement.
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
        if (!checkStringMapValidity(map)) {
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
     * Updates player's texture.
     * @param texturePath the texture path
     */
    public void updatePlayerTexture(String texturePath) {
        player.setTexturePath(texturePath);
    }
    /**
     * Resets the game:
     * resets the player and entities, clears the grid, and restarts the game.
     */
    public void reset() {
        resetPlayer();
        resetEntities();
        grid.getChildren().clear();
        start();
    }
    /**
     * Sets the key event handlers to null. Needed for death screen.
     */
    public void setHandleToNull() {
        isoScene.onKeyReleasedProperty().set(null);
        isoScene.onKeyPressedProperty().set(null);
    }

    /**
     * Clears the grid and all the game data.
     */
    public void clearAll() {
        grid.getChildren().clear();
        objectsToDraw = null;
        map = "";
        walls = null;
        player = null;
        entities = null;
        drawnEntities = null;
        playerDeltaX = 0;
        playerDeltaY = 0;
        hint = new Label();
        healthLabel = new Label();
        time = 0;
    }
    /**
     * Initialises the main stage.
     * @param stage the main stage
     */
    public void initialiseStage(Stage stage) {
        mainStage = stage;
        Scene scene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainStage.setScene(scene);
    }

    /**
     * initialises the wagon.
     * @param wagon the wagon
     */
    public void initialiseWagon(Wagon wagon) {
        if (wagon == null) {
            wagon = new Wagon("DEFAULT");
            wagon.generateWagon();
        }
        setMap(wagon.getSeed());
        setObjectsToDraw(wagon.getObjectsArray());
        setInteractiveObjects(wagon.getInteractiveObjects());
        setEntities(wagon.getEntities());
    }
    public void setMap(String map) {
        this.map = map;
    }
    public void setObjectsToDraw(Object[][] objectsToDraw) {
        this.objectsToDraw = objectsToDraw;
    }
    public void setInteractiveObjects(Object[] interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }
    /**
     * Sets the player.
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
        player.setHitbox(getEntityHitBox(player.getPositionX(), player.getPositionY(), 64, player.getHeight(), player.getHitBoxSize()));
        player.setAttackRange(getEntityAttackRange(player.getPositionX(), player.getPositionY(), 64, player.getHeight(), 1));
        player.setStartPositionX(player.getPositionX());
        player.setStartPositionY(player.getPositionY());
        updatePlayerDeltaX(0);
        updatePlayerDeltaY(0);
    }
    /**
     * Resets the player.
     */
    public void resetPlayer() {
        grid.getChildren().remove(player.getEntityView());
        grid.getChildren().remove(player.getHitbox());
        grid.getChildren().remove(player.getAttackRange());

        player.setHealth(Constants.PLAYER_MAX_HEALTH);
        player.setPositionX(player.getStartPositionX());
        player.setPositionY(player.getStartPositionY());
        player.setHitbox(getEntityHitBox(player.getPositionX(), player.getPositionY(), 64, player.getHeight(), player.getHitBoxSize()));
        player.setAttackRange(getEntityAttackRange(player.getPositionX(), player.getPositionY(), 64, player.getHeight(), 1));
    }
    /**
     * Gets the player.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Sets the entities.
     * @param entities the entities
     */
    public void setEntities(List<Entity> entities) {
        this.entities = entities;
        drawnEntities = new ArrayList<>();
        for (Entity entity : this.entities) {
            entity.setHitbox(getEntityHitBox(entity.getPositionX(), entity.getPositionY(), 64, entity.getHeight(), entity.getHitBoxSize()));
            entity.setAttackRange(getEntityAttackRange(entity.getPositionX(), entity.getPositionY(), 64, entity.getHeight(), 1));
            entity.setStartPositionX(entity.getPositionX());
            entity.setStartPositionY(entity.getPositionY());
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    /**
     * Resets the entities.
     */
    public void resetEntities() {
        for (Entity entity : entities) {
            grid.getChildren().remove(entity.getEntityView());
            grid.getChildren().remove(entity.getHitbox());
            grid.getChildren().remove(entity.getAttackRange());
            drawnEntities = new ArrayList<>();

            entity.setHealth(entity.getMaxHealth());
            entity.setPositionX(entity.getStartPositionX());
            entity.setPositionY(entity.getStartPositionY());
            entity.setHitbox(getEntityHitBox(entity.getPositionX(), entity.getPositionY(), 64, entity.getHeight(), entity.getHitBoxSize()));
            entity.setAttackRange(getEntityAttackRange(entity.getPositionX(), entity.getPositionY(), 64, entity.getHeight(), 1));
            entity.setBehaviour(entity.getInitialBehaviour());
        }
    }
    /**
     * Gets the entities.
     * @return the entities
     */
    public List<Entity> getEntities() {
        return entities;
    }
    /**
     * Updates the label with the specified text.
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
    private void updateHealthLabel(String text, int x, int y) {
        grid.getChildren().remove(healthLabel);
        healthLabel.setText(text);
        healthLabel.setLayoutX(x);
        healthLabel.setLayoutY(y);
        healthLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(healthLabel);
    }
    /**
     * Updates the player's delta x.
     * @param deltaX the player's delta x
     */
    public void updatePlayerDeltaX(int deltaX) {
        this.playerDeltaX = deltaX;
    }
    /**
     * Updates the player's delta y.
     * @param deltaY the player's delta y
     */
    public void updatePlayerDeltaY(int deltaY) {
        this.playerDeltaY = deltaY;
    }
    /**
     * Updates the player's position.
     * If the player's hitbox collides with the walls, the player's position is not updated.
     */
    public void updatePlayerPosition() {
        updateEntityPosition(player, playerDeltaX, playerDeltaY, Constants.PLAYER_BASIC_SPEED_X, Constants.PLAYER_BASIC_SPEED_Y);
    }
    /**
     * Updates the position of the entity.
     * @param entity the entity
     * @param deltaX the delta x
     * @param deltaY the delta y
     * @param speedX the x-axis speed
     * @param speedY the y-axis speed
     * <br>
     * <br>
     * if the entity's hitbox collides with the walls, the entity's position is not updated
     */
    public void updateEntityPosition(Entity entity, int deltaX, int deltaY, int speedX, int speedY) {

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

        deltaX = (int) (deltaX / mag * speedX / 2);
        deltaY = (int) (deltaY / mag * speedY / 2);
        entity.setPositionX(entity.getPositionX() + deltaX);
        entity.setPositionY(entity.getPositionY() + deltaY);
    }

    /**
     * Checks if the entity can move to the specified position.
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
    private boolean tryToMove(Entity entity, int deltaX, int deltaY) {
        entity.getHitbox().translateXProperty().set(entity.getPositionX() - entity.getStartPositionX() + deltaX);
        entity.getHitbox().translateYProperty().set(entity.getPositionY() - entity.getStartPositionY() + deltaY);
        entity.getAttackRange().translateXProperty().set(entity.getPositionX() - entity.getStartPositionX() + deltaX);
        entity.getAttackRange().translateYProperty().set(entity.getPositionY() - entity.getStartPositionY() + deltaY);

        return !checkCollision(entity.getHitbox(), walls);
    }
    /**
     * Moves the entities towards the player.
     */
    public void moveEntities() {
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity != null && entity.isAlive()) {

                    int x = player.getPositionX() - entity.getPositionX();
                    int y = player.getPositionY() - entity.getPositionY();

                    int deltaX = x > 0 ? 1 : -1;
                    int deltaY = y > 0 ? 1 : -1;
                    //A* algorithm needed

                    updateEntityPosition(entity, deltaX, deltaY, entity.getSpeedX(), entity.getSpeedX());
                }
            }
        }
    }

    /**
     * Checks if the player can attack the entities.
     * If the entities are in attack range, the player attacks the entities.
     * If the entity is neutral, the entity's behaviour is set to enemy.
     */
    public void playerAttack() {
        List<Entity> entitiesInRange = player.inAttackRange(entities);
        if (entitiesInRange != null) {
            for (Entity entity : entitiesInRange) {
                if (entity != null) {
                    if (entity.isAlive()) {
                        player.attack(entity);
                    }
                }
            }
        }
    }
    /**
     * Updates all the objects in the game.
     */
    public void updateAll() {
        grid.getChildren().clear();
        placeFloor();
        placePolygons();
        mergeObjectHitboxes();
        placeWalls();
    }
    /**
     * Moves the grid by the specified delta x and delta y.
     * If there is a collision with the player hitbox and the walls, the delta x and delta y are adjusted.
     * @param deltaX the delta x
     * @param deltaY the delta y
     */
    public void moveGrid(int deltaX, int deltaY) {
        if (checkCollision(player.getHitbox(), walls)) {
            this.deltaX -= deltaX;
            this.deltaY -= deltaY;
            return;
        }
        this.deltaX += deltaX;
        this.deltaY += deltaY;
        updateAll();
    }
    /**
     * Updates the walls.
     */
    public void updateWalls() {
        grid.getChildren().remove(player.getEntityView());
        grid.getChildren().remove(player.getHitbox());
        grid.getChildren().remove(player.getAttackRange());
        if (entities != null) {
            drawnEntities = new ArrayList<>();
            for (Entity entity : entities) {
                if (entity != null) {
                    grid.getChildren().remove(entity.getEntityView());
                    grid.getChildren().remove(entity.getHitbox());
                    grid.getChildren().remove(entity.getAttackRange());
                }
            }
        }
        boolean playerDrawn = false;
        int count = 0;
        for (Object[] objects : objectsToDraw) {
            for (Object object : objects) {
                if (object.getHeight() > 0) {
                    Image objectTexture = new Image(object.getTexturePath());
                    double[] objectIsoXY = cartesianToIsometric(object.getCartX(), object.getCartY());
                    grid.getChildren().remove(object.getTexture());
                    if (checkX(player, objectIsoXY) && checkY(player, objectIsoXY, objectTexture) && !playerDrawn) {
                        drawEntity(player);
                        playerDrawn = true;
                    }
                    if (entities != null) {
                        for (Entity entity : entities) {
                            if (entity != null && entity.isAlive() && !drawnEntities.contains(entity)) {
                                if (checkX(entity, objectIsoXY) && checkY(entity, objectIsoXY, objectTexture)) {
                                    drawEntity(entity);
                                    drawnEntities.add(entity);
                                }
                            }
                        }
                    }
                    placeIsometricTileWithTexture(object.getTexture(), object.getCartX(), object.getCartY());
                }
            }
        }
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
     * Checks if the map is valid.
     * @param map the map as a string
     * @return true if the map is valid, false otherwise
     */
    private boolean checkStringMapValidity(String map) {
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
    /**
     * Draws the map and the player.
     */
    private void placeMap() {
        placeFloor();
        placePolygons();
        mergeObjectHitboxes();
        placeWalls();
    }
    /**
     * Places the floor.
     */
    private void placeFloor() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {

                int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                if (objectsToDraw[i][j].getHeight() == 0) {
                    System.out.println(objectsToDraw[i][j].getTexturePath());
                    Image objectTexture = new Image(objectsToDraw[i][j].getTexturePath());

                    objectsToDraw[i][j].setCartX(x);
                    objectsToDraw[i][j].setCartY(y);

                    ImageView object = new ImageView(objectTexture);
                    objectsToDraw[i][j].setTexture(object);

                    placeIsometricTileWithTexture(object, (int) (x + 2 * TILE_WIDTH - objectTexture.getHeight()), (int) (y + TILE_HEIGHT - objectTexture.getHeight()));
                }
            }
        }
    }
    /**
     * Places the walls.
     */
    private void placeWalls() {
        boolean playerDrawn = false;
        int count = 0;

        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() > 0) {
                    int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                    int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                    //get texture

                    Image objectTexture = new Image(objectsToDraw[i][j].getTexturePath());

                    objectsToDraw[i][j].setCartX((int) (x + 2 * TILE_WIDTH - objectTexture.getHeight()));
                    objectsToDraw[i][j].setCartY((int) (y + TILE_HEIGHT - objectTexture.getHeight()));

                    ImageView object = new ImageView(objectTexture);
                    objectsToDraw[i][j].setTexture(object);
                    double[] objectIsoXY = cartesianToIsometric(objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());

                    if (checkX(player, objectIsoXY) && checkY(player, objectIsoXY, objectTexture) && !playerDrawn) {
                        drawEntity(player);
                        playerDrawn = true;
                    }
                    if (entities != null) {
                        for (Entity entity : entities) {
                            if (entity != null && entity.isAlive() && !drawnEntities.contains(entity)) {
                                if (checkX(entity, objectIsoXY) && checkY(entity, objectIsoXY, objectTexture)) {
                                    drawEntity(entity);
                                    drawnEntities.add(entity);
                                }
                            }
                        }
                    }

                    placeIsometricTileWithTexture(objectsToDraw[i][j].getTexture(), objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());

                }
            }
        }
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
     * Draws the entity.
     */
    private void drawEntity(Entity entity) {
        System.out.println(entity.getTexturePath());
        entity.setEntityView(new ImageView(entity.getTexturePath()));

        grid.getChildren().add(entity.getHitbox());
        grid.getChildren().add(entity.getAttackRange());
        entity.getEntityView().setX(entity.getPositionX());
        entity.getEntityView().setY(entity.getPositionY());
        grid.getChildren().add(entity.getEntityView());
    }

    public Scene previewWagon(Wagon wagon) {
        if (wagon == null) {
            return null;
        }
        setMap(wagon.getSeed());
        setObjectsToDraw(wagon.getObjectsArray());
        setInteractiveObjects(wagon.getInteractiveObjects());
        setEntities(wagon.getEntities());
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");
        placeMap();
        isoScene = mainStage.getScene();
        mainStage.setResizable(false);
        mainStage.show();
        return isoScene;
    }
    /**
     * Places the polygons for object hitboxes.
     */
    private void placePolygons() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() > 0 || objectsToDraw[i][j].isSolid()) {
                    int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                    int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;
                    Polygon parallelogram = getObjectHitBox(x, y, TILE_WIDTH, 0);
                    objectsToDraw[i][j].setObjectHitbox(parallelogram);
                }
            }
        }
    }
    /**
     * Merges the object hitboxes to create one solid shape.
     */
    private void mergeObjectHitboxes() {
        Shape shape = Shape.union(objectsToDraw[0][0].getObjectHitbox(), objectsToDraw[0][1].getObjectHitbox());
        grid.getChildren().remove(objectsToDraw[0][0].getObjectHitbox());
        grid.getChildren().remove(objectsToDraw[0][1].getObjectHitbox());
        for (Object[] objects : objectsToDraw) {
            for (Object object : objects) {
                if (object.isSolid()) {
                    shape = Shape.union(shape, object.getObjectHitbox());
                }
            }
        }
        shape.setStyle("-fx-fill: #ff00af; -fx-opacity: 0;");
        walls = shape;
        grid.getChildren().add(shape);
    }
    /**
     * Gets the entity hitbox.
     * @param cartX the entity's x position
     * @param cartY the entity's y position
     * @param objectWidth the entity's width
     * @param height the entity's height
     * @return the entity hitbox
     */
    private Circle getEntityHitBox(int cartX, int cartY, int objectWidth, int height, int hitBoxSize) {
        Circle circle = new Circle();
        cartX += objectWidth / 2;
        cartY += TILE_HEIGHT / 2 + height * TILE_HEIGHT;
        circle.setCenterX(cartX);
        circle.setCenterY(cartY);
        circle.setRadius((double) (hitBoxSize * TILE_WIDTH) / 4);
        circle.setStyle("-fx-stroke: #563131; -fx-stroke-width: 2; -fx-fill: #ff00af;");
        return circle;
    }
    /**
     * Gets the entity attack range.
     * @param cartX the entity's x position
     * @param cartY the entity's y position
     * @param objectWidth the entity's width
     * @param height the entity's height
     * @param attackRange the entity's attack range
     * @return the entity attack range
     */
    private Circle getEntityAttackRange(int cartX, int cartY, int objectWidth, int height, int attackRange) {
        Circle circle = new Circle();
        cartX += objectWidth / 2;
        cartY += TILE_HEIGHT / 2 + height * TILE_HEIGHT;
        circle.setCenterX(cartX);
        circle.setCenterY(cartY);
        circle.setRadius(attackRange * TILE_WIDTH);
        circle.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 2; -fx-fill: #ff0000; -fx-opacity: 0.5;");
        return circle;
    }
    /**
     * Gets the object hitbox.
     * @param cartX the object's x position
     * @param cartY the object's y position
     * @param objectWidth the object's width
     * @param deltaHeight the object's height
     * @return the object hitbox
     */
    private Polygon getObjectHitBox(int cartX, int cartY, int objectWidth, int deltaHeight) {
        Polygon parallelogram = new Polygon();
        double[] isoXY1 = cartesianToIsometric(cartX, cartY);
        double[] isoXY2 = cartesianToIsometric(cartX + objectWidth, cartY);
        double[] isoXY3 = cartesianToIsometric(cartX + objectWidth, cartY + 32 + deltaHeight);
        double[] isoXY4 = cartesianToIsometric(cartX, cartY + TILE_WIDTH + deltaHeight);

        parallelogram.getPoints().addAll(new Double[]{
                isoXY1[0] + TILE_WIDTH, isoXY1[1],
                isoXY2[0] + TILE_WIDTH, isoXY2[1],
                isoXY3[0] + TILE_WIDTH, isoXY3[1],
                isoXY4[0] + TILE_WIDTH, isoXY4[1]
        });
        parallelogram.setStyle("-fx-opacity: 0");

        return parallelogram;
    }
    /**
     * Checks if there is a collision between two shapes.
     * @param hitbox1 the first hitbox
     * @param hitbox2 the second hitbox
     * @return true if there is a collision, false otherwise
     */
    private boolean checkCollision(Shape hitbox1, Shape hitbox2) {
        Shape intersect = Shape.intersect(hitbox1, hitbox2);
        return !intersect.getBoundsInParent().isEmpty();
    }
    /**
     * Checks if the x position of the object is within the player's range (constant).
     * @param objectIsoXY the object's isometric x and y position
     * @return true if the x position of the object is within the player's range, false otherwise
     */
    private boolean checkX(Entity entity, double[] objectIsoXY) {
        return (objectIsoXY[0] > entity.getPositionX() - TILE_WIDTH && objectIsoXY[0] < entity.getPositionX() + 3 * TILE_WIDTH);
    }
    /**
     * Checks if the y position of the object is lower than the player's y position.
     * @param objectIsoXY the object's isometric x and y position
     * @param objectTexture the object's texture
     * @return true if the y position of the object is lower than the player's y position, false otherwise
     */
    private boolean checkY(Entity entity, double[] objectIsoXY, Image objectTexture) {
        return (entity.getPositionY() + (double) TILE_HEIGHT / 2 + entity.getHeight() * TILE_HEIGHT < (objectIsoXY[1] - TILE_HEIGHT + objectTexture.getHeight()));
    }
    /**
     * Converts the cartesian x and y position to isometric x and y position.
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
     * Places the isometric tile with the specified texture.
     * @param img the image view
     * @param cartX the cartesian x position
     * @param cartY the cartesian y position
     */
    private void placeIsometricTileWithTexture(ImageView img, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        img.setX(isoXY[0] - TILE_WIDTH);
        img.setY(isoXY[1] - (double) TILE_HEIGHT / 2);
        grid.getChildren().add(img);
    }

    public Pane getIsoGrid() {
        return grid;
    }

}