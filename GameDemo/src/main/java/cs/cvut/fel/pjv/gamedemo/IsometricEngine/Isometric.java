package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Isometric {
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private int deltaX = 10;
    private int deltaY = 0;

    ////    map example: 11AA_12BB_13CC-11AA_00AA_00BB
////first number tile type: 0 - floor (not Solid), 1 - wall (Solid), 2 - door (Solid)
////second number tile height (only for walls) min 1, max 3
////third number tile letter id (for example: AA, BB, CC)
////'_' separates tiles, '-' separates rows
    private String map = "";
    private Object[][] objectsToDraw;
    private Shape walls;
    private Player player;
    private Entity[] entities;
    private Entity[] drawnEntities;
    private int playerDeltaX = 0;
    private int playerDeltaY = 0;
    private Stage mainStage;
    private Pane grid = new Pane();
    private Label label = new Label();
    public Isometric() {
    }
    public Isometric(Stage stage) {
        initialiseStage(stage);
    }

    private AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 100000000000L;
        long lastTime = -1;

        /**
         * Updates the game state every INTERVAL nanoseconds.
         * Updates the walls and player position.
         * If the last time is not set, sets it to the current time.
         * If the time difference between the last time and the current time is less than INTERVAL,
         * updates the walls, shows the cursor coordinates, and updates the player position.
         * @param l current time in nanoseconds
         */
        @Override
        public void handle(long l) {
            if (lastTime < 0) {
                lastTime = l;
                return;
            } else if (l - lastTime < INTERVAL) {
                updateWalls();
                showCoordinatesOnCursor();
                updateEntityPosition(player, playerDeltaX, playerDeltaY, 0, 0);
                moveEntities();
            }
        }
    };

    /**
     * Initializes and starts the game.
     * If the main stage, player, or map is not initialized properly,
     * the program exits with an error message.
     * Sets up the game grid, loads the map, and sets the scene.
     * Registers key event handlers for player movement.
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
        if (!loadMap(map)) {
            System.out.println("Map string is not valid");
            System.out.println("Exiting the program");
            System.exit(1);
        }
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");
        placeMap();
        Scene scene = new Scene(grid, 1600, 800);
        mainStage.setTitle("My JavaFX Application");
        mainStage.setScene(scene);
        mainStage.show();
        timer.start();
        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W, S:
                    updatePlayerDeltaY(0);
                    break;
                case A, D:
                    updatePlayerDeltaX(0);
                    break;
            }
        });
        //player movement is laggy, don't know why, need to fix
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    player.setTexturePath("player_back.png");
                    updatePlayerDeltaY(-Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case S:
                    player.setTexturePath("player_front.png");
                    updatePlayerDeltaY(Constants.PLAYER_BASIC_SPEED_Y);
                    break;
                case A:
                    player.setTexturePath("player_left.png");
                    updatePlayerDeltaX(-Constants.PLAYER_BASIC_SPEED_X);
                    break;
                case D:
                    player.setTexturePath("player_right.png");
                    updatePlayerDeltaX(Constants.PLAYER_BASIC_SPEED_X);
                    break;
            }
            updateWalls();
        });
    }
    /**
     * Initializes the main stage.
     * @param stage the main stage
     */
    public void initialiseStage(Stage stage) {
        mainStage = stage;
    }
    /**
     * Sets the player.
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
        player.setHitbox(getEntityHitBox(player.getPositionX(), player.getPositionY(), 64, player.getHeight(), player.getHitBoxSize()));
        player.setAttackRange(getEntityAttackRange(player.getPositionX(), player.getPositionY(), 64, player.getHeight(), 1));
        updatePlayerDeltaX(0);
        updatePlayerDeltaY(0);
    }
    /**
     * Sets the entities.
     * @param entities the entities
     */
    public void setEntities(Entity[] entities) {
        this.entities = entities;
        drawnEntities = new Entity[this.entities.length];
        for (Entity entity : this.entities) {
            entity.setHitbox(getEntityHitBox(entity.getPositionX(), entity.getPositionY(), 64, entity.getHeight(), entity.getHitBoxSize()));
            entity.setAttackRange(getEntityAttackRange(entity.getPositionX(), entity.getPositionY(), 64, entity.getHeight(), 1));
        }
    }
    /**
     * Gets the player.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Sets the map.
     * @param filePath the file path of the map
     */
    public void setMap(String filePath) {
        this.map = loadMapFromFile(filePath);
    }
    /**
     * Displays a preview of the map using the specified map data.
     * If the map data is invalid, an error message is printed and the function returns.
     * Otherwise, the map is loaded and displayed in a GridPane with objects represented by images.
     * The size of each grid cell is determined by TILE_WIDTH and TILE_HEIGHT constants.
     */
    public void previewMap() {
        if (!checkStringMapValidity(map)) {
            System.out.println("Map is not valid");
            return;
        }
        loadMap(map);
        GridPane grid = new GridPane();
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                int x = TILE_WIDTH + j * TILE_WIDTH;
                int y = i * TILE_HEIGHT;
                Image objectTexture = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource(objectsToDraw[i][j].getTexturePath())).toExternalForm());
                ImageView object = new ImageView(objectTexture);
                object.setFitWidth(TILE_WIDTH);
                object.setFitHeight(TILE_HEIGHT);
                grid.add(object, j, i);
            }
        }
        grid.setPrefSize(1000, 1000);
        mainStage.setTitle("Map preview");
        grid.setStyle("-fx-background-color: #000000;");
        mainStage.setScene(new Scene(grid, 1600, 800));
        mainStage.show();
    }
    /**
     * Updates the label with the specified text.
     * @param text the text to display
     */
    public void updateLabel(String text) {
        grid.getChildren().remove(label);
        label.setText(text);
        label.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
        grid.getChildren().add(label);
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
     * Shows the coordinates of the cursor on the main stage.
     */
    public void showCoordinatesOnCursor() {
        mainStage.getScene().setOnMouseMoved(e -> {
            mainStage.setTitle("X: " + (int) e.getX() + " Y: " + (int) e.getY());
        });
    }
    /**
     * Updates the position of the entity.
     * @param entity the entity
     * @param deltaX the delta x
     * @param deltaY the delta y
     * @param deltaSpeedX the delta speed x
     * @param deltaSpeedY the delta speed y
     * if the entity's hitbox collides with the walls, the entity's position is not updated
     * @return the updated entity position
     */
    public void updateEntityPosition(Entity entity, int deltaX, int deltaY, int deltaSpeedX, int deltaSpeedY) {
        System.out.println(entity.getHitbox().getTranslateX());
        entity.getHitbox().translateXProperty().set(entity.getPositionX() + deltaX);
        entity.getHitbox().translateYProperty().set(entity.getPositionY() + deltaY);
        entity.getAttackRange().translateXProperty().set(entity.getPositionX() + deltaX);
        entity.getAttackRange().translateYProperty().set(entity.getPositionY() + deltaY);
        if (checkCollision(entity.getHitbox(), walls)) {
            System.out.println("Collision with walls" + " at " + entity.getPositionX() + " " + entity.getPositionY());
            return;
        }
        double mag = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        int speedX = Constants.ENTITY_BASIC_SPEED_X + deltaSpeedX;
        int speedY = Constants.ENTITY_BASIC_SPEED_Y + deltaSpeedY;
        if (entity instanceof Player) {
            speedX = Constants.PLAYER_BASIC_SPEED_X;
            speedY = Constants.PLAYER_BASIC_SPEED_Y;
        }
        deltaX = (int) (deltaX / mag * speedX / 2);
        deltaY = (int) (deltaY / mag * speedY / 2);
        entity.setPositionX(entity.getPositionX() + deltaX);
        entity.setPositionY(entity.getPositionY() + deltaY);
    }
    /**
     * Moves the entities towards the player.
     */
    public void moveEntities() {
        //find deltaX and deltaY for each entity to move towards the player
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity != null && entity.isAlive()) {
//                    if (entity.inAttackRange(new Entity[]{player})[0] != null) {
//                        entity.attack(player);
//                    } else {
//                        int deltaX = player.getPositionX() - entity.getPositionX();
//                        int deltaY = player.getPositionY() - entity.getPositionY();
//                        updateEntityPosition(entity, deltaX, deltaY);
//                    }
                    int x = player.getPositionX() - entity.getPositionX();
                    int y = player.getPositionY() - entity.getPositionY();

                    int deltaX = x > 0 ? 1 : -1;
                    int deltaY = y > 0 ? 1 : -1;

                    updateEntityPosition(entity, deltaX, deltaY,0,0);
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
    private void updateWalls() {
        grid.getChildren().remove(player.getEntityView());
        grid.getChildren().remove(player.getHitbox());
        grid.getChildren().remove(player.getAttackRange());
        if (entities != null) {
            drawnEntities = new Entity[entities.length];
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
                if (object.isSolid()) {
                    Image objectTexture = new Image(object.getTexturePath());
                    double[] objectIsoXY = cartesianToIsometric(object.getCartX(), object.getCartY());
                    grid.getChildren().remove(object.getTexture());
                    if (checkX(player, objectIsoXY) && checkY(player, objectIsoXY, objectTexture) && !playerDrawn) {
                        drawEntity(player);
                        playerDrawn = true;
                    }
                    if (entities != null) {
                        for (Entity entity : entities) {
                            if (entity != null && entity.isAlive() && !Arrays.asList(drawnEntities).contains(entity)) {
                                if (checkX(entity, objectIsoXY) && checkY(entity, objectIsoXY, objectTexture)) {
                                    drawEntity(entity);
                                    drawnEntities[count] = entity;
                                    count++;
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
                if (entity != null && entity.isAlive() && !Arrays.asList(drawnEntities).contains(entity)) {
                    drawEntity(entity);
                    drawnEntities[count] = entity;
                    count++;
                }
            }
        }
    }
    /**
     * Loads the map from the specified file path.
     * @param path the file path of the map
     * @return the map as a string
     */
    private String loadMapFromFile(String path) {
        File file = new File(path);
        StringBuilder map = new StringBuilder();
        try {
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()) {
                map.append(scanner.nextLine());
                map.append("-");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map.toString();
    }
    /**
     * Loads the map from the specified string.
     * @param map the map as a string
     * @return true if the map was loaded successfully, false otherwise
     */
    private boolean loadMap(String map) {
        if (!checkStringMapValidity(map)) {
            return false;
        }
        String[] rows = map.split("-");
        String[] subRows = rows[0].split("_");
        objectsToDraw = new Object[rows.length][subRows.length];
        for (int i = 0; i < rows.length; i++) {
            subRows = rows[i].split("_");
            for (int j = 0; j < subRows.length; j++) {
                if (subRows[j].charAt(0) == '0') {
                    String letterID = subRows[j].substring(2, 4);
                    Object object = new Object(subRows[j].charAt(0), Constants.OBJECT_NAMES.get(letterID), Constants.OBJECT_IDS.get(letterID), letterID, 0, 0, 0, false);

                    objectsToDraw[i][j] = object;
                    continue;
                }
                String letterID = subRows[j].substring(2, 4);
                Object object = new Object(subRows[j].charAt(0), Constants.OBJECT_NAMES.get(letterID), Constants.OBJECT_IDS.get(letterID), letterID, subRows[j].charAt(1), 0, 0, true);
                objectsToDraw[i][j] = object;
            }
        }
        return true;
    }
    /**
     * Checks if the map is valid.
     * @param map the map as a string
     * @return true if the map is valid, false otherwise
     */
    private boolean checkStringMapValidity(String map) {
        try {
            String[] rows = map.split("-");
            String[] subRows = rows[0].split("_");

            for (String subRow : subRows) {
                if (subRow.length() != subRows[0].length()) {
                    return false;
                }
                if (subRow.charAt(0) != '0' && subRow.charAt(0) != '1' && subRow.charAt(0) != '2') {
                    return false;
                }
                if (subRow.charAt(1) != '0' && subRow.charAt(1) != '1' && subRow.charAt(1) != '2' && subRow.charAt(1) != '3') {
                    return false;
                }
                if (!Character.isLetter(subRow.charAt(2))) {
                    return false;
                }
                if (!Character.isLetter(subRow.charAt(3))) {
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
                            if (entity != null && entity.isAlive() && !Arrays.asList(drawnEntities).contains(entity)) {
                                if (checkX(entity, objectIsoXY) && checkY(entity, objectIsoXY, objectTexture)) {
                                    drawEntity(entity);
                                    drawnEntities[count] = entity;
                                    count++;
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
                if (entity != null && entity.isAlive() && !Arrays.asList(drawnEntities).contains(entity)) {
                    drawEntity(entity);
                    drawnEntities[count] = entity;
                    count++;
                }
            }
        }
    }
    /**
     * Draws the entity.
     */
    private void drawEntity(Entity entity) {
        entity.setEntityView(new ImageView(entity.getTexturePath()));
        grid.getChildren().add(entity.getHitbox());
        grid.getChildren().add(entity.getAttackRange());
        entity.getEntityView().setX(entity.getPositionX());
        entity.getEntityView().setY(entity.getPositionY());
        grid.getChildren().add(entity.getEntityView());
    }
    /**
     * Places the polygons for object hitboxes.
     */
    private void placePolygons() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() > 0) {
                    if (objectsToDraw[i][j].isSolid()) {
                        int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                        int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;
                        Polygon parallelogram = getObjectHitBox(x, y, TILE_WIDTH, 0);
                        objectsToDraw[i][j].setObjectHitbox(parallelogram);
                    }
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
}