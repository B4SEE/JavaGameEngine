package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Isometric extends Application {
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private int deltaX = 10;
    private int deltaY = 0;
    Object[][] objectsToDraw;
    private Stage mainStage;
    private int playerDeltaX = 0;
    private int playerDeltaY = 0;
    Label label = new Label();
    public Player player = new Player(0, "PLAYER_NAME", "player_front.png", 0, 0);

    AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 10000000000000L;
        long lastTime = -1;
        @Override
        public void handle(long l) {
            if (lastTime < 0) {
                lastTime = l;
                return;
            } else if (l - lastTime < INTERVAL) {
                updateWalls();
                showCoordinatesOnCursor();
                updatePlayerPosition(player, playerDeltaX, playerDeltaY);
            }
        }
    };

////    map example: 11AA_12BB_13CC-11AA_00AA_00BB
////first number tile type: 0 - floor (not Solid), 1 - wall (Solid), 2 - door (not Solid)
////second number tile height (only for walls) min 1, max 3
////third number tile letter id (for example: AA, BB, CC)
////'_' separates tiles, '-' separates rows
    String row1 = "13WW_13WW_13WW_13WW_13WW_13WW_13WW_13WW_13WW_13WW_00TF_00TF_13WW_13WW_13WW_13WW_13WW_13WW_13WW_13WW_13WW_13WW";
    String row2 = "13WW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";
    String row3 = "13WW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";
    String row4 = "23SW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";
    String row5 = "13WW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";
    String row6 = "13WW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";
    String row7 = "11SW_11SW_00TF_11SW_11SW_11SW_11SW_00TF_00TF_13WW_12HW_13WW_00TF_00TF_00TF_11SW_11SW_11SW_11SW_11SW_11SW_11SW";
    String map2 = row1 + "-" + row2 + "-" + row3 + "-" + row4 + "-" + row5 + "-" + row6 + "-" + row7;
    Pane grid = new Pane();

    Circle playerHitbox = getPlayerHitBox(player.getPositionX(), player.getPositionY(), 64, player.getHeight());
    @Override
    public void start(Stage stage) throws IOException {
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");
        mainStage = stage;
        loadMap(map2);
//        drawIsometricGrid(mainStage);
        placeFloor();
        placePolygons();
        placeWalls();
        Scene scene = new Scene(grid, 1600, 500);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
        timer.start();
        scene.setOnKeyReleased(keyEvent -> {
                switch (keyEvent.getCode()) {
                    case W:
                        updatePlayerDeltaY(0);
                        break;
                    case S:
                        updatePlayerDeltaY(0);
                        break;
                    case A:
                        updatePlayerDeltaX(0);
                        break;
                    case D:
                        updatePlayerDeltaX(0);
                        break;
                }
        });
        scene.setOnKeyPressed(keyEvent -> {
                switch (keyEvent.getCode()) {
                    case W:
                        player.setTexturePath("player_back.png");
                        updatePlayerDeltaY(-Constants.PLAYER_BASIC_SPEED_Y / 2);
                        break;
                    case S:
                        player.setTexturePath("player_front.png");
                        updatePlayerDeltaY(Constants.PLAYER_BASIC_SPEED_Y / 2);
                        break;
                    case A:
                        player.setTexturePath("player_left.png");
                        updatePlayerDeltaX(-Constants.PLAYER_BASIC_SPEED_X / 2);
                        break;
                    case D:
                        player.setTexturePath("player_right.png");
                        updatePlayerDeltaX(Constants.PLAYER_BASIC_SPEED_X / 2);
                        break;
                    case UP:
                        moveGrid(0, -1);
                        updateAll();
                        break;
                    case DOWN:
                        moveGrid(0, 1);
                        updateAll();
                        break;
                    case LEFT:
                        moveGrid(-1, 0);
                        updateAll();
                        break;
                    case RIGHT:
                        moveGrid(1, 0);
                        updateAll();
                        break;
                }
        });
    }

     public void placeFloor() {
         for (int i = 0; i < objectsToDraw.length; i++) {
             for (int j = 0; j < objectsToDraw[i].length; j++) {
                 int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                 int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;
                 if (objectsToDraw[i][j].getHeight() == 0) {

                     Image objectTexture = new Image("file:src/main/resources/" + objectsToDraw[i][j].getTexturePath());

                     objectsToDraw[i][j].setCartX(x);
                     objectsToDraw[i][j].setCartY(y);

                     ImageView object = new ImageView(objectTexture);
                     objectsToDraw[i][j].setTexture(object);

                     placeIsometricTileWithTexture(object, (int) (x + 2 * TILE_WIDTH - objectTexture.getHeight()), (int) (y + TILE_HEIGHT - objectTexture.getHeight()));
                 }
             }
         }
     }
     private void updateLabel(String text) {
        grid.getChildren().remove(label);
            label.setText(text);
            label.setStyle("-fx-font-size: 20; -fx-text-fill: #ffffff;");
            grid.getChildren().add(label);
     }
    public void updatePlayerDeltaX(int deltaX) {
        this.playerDeltaX = deltaX;
    }

    public void updatePlayerDeltaY(int deltaY) {
        this.playerDeltaY = deltaY;
    }

    public static void main(String[] args) {
        launch();
    }
    public void showCoordinatesOnCursor() {
        mainStage.getScene().setOnMouseMoved(e -> {
            mainStage.setTitle("X: " + (int) e.getX() + " Y: " + (int) e.getY());
        });
    }
    public Circle getPlayerHitBox(int cartX, int cartY, int objectWidth, int height) {
        Circle circle = new Circle();
        cartX += objectWidth / 2;
        cartY += TILE_HEIGHT / 2 + height * TILE_HEIGHT;
        circle.setCenterX(cartX);
        circle.setCenterY(cartY);
        circle.setRadius(8);
        circle.setStyle("-fx-stroke: #563131; -fx-stroke-width: 2; -fx-fill: #ff00af;");
        return circle;
    }
    public Polygon getObjectHitBox(int cartX, int cartY, int objectWidth, int deltaHeight) {
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
        parallelogram.setStyle("-fx-stroke: #563131; -fx-stroke-width: 2; -fx-fill: #ff00af;");

        return parallelogram;
    }

    public boolean checkCollision(Circle hitbox1, Polygon hitbox2) {
        Shape intersect = Shape.intersect(hitbox1, hitbox2);
        return !intersect.getBoundsInParent().isEmpty();
    }
    private void updatePlayerPosition(Player player, int deltaX, int deltaY) {
        playerHitbox.translateXProperty().set(player.getPositionX() + deltaX * 1.5);
        playerHitbox.translateYProperty().set(player.getPositionY() + deltaY * 1.5);
        for (Object[] objects : objectsToDraw) {
            for (Object object : objects) {
                if (object.isSolid() && !Objects.equals(object.getTwoLetterId(), "DD")) {
                    if (checkCollision(playerHitbox, object.getObjectHitbox())) {
//                        System.out.println("Collision with " + object.getName() + " at " + object.getCartX() + " " + object.getCartY());
                        updateLabel("Collision with " + object.getName() + " at " + player.getPositionX() + " " + player.getPositionY());
                        return;
                    }
                }
            }
        }
        double mag = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        deltaX = (int) (deltaX / mag * Constants.PLAYER_BASIC_SPEED_X);
        deltaY = (int) (deltaY / mag * Constants.PLAYER_BASIC_SPEED_Y);
        player.setPositionX(player.getPositionX() + deltaX);
        player.setPositionY(player.getPositionY() + deltaY);
    }
    private boolean checkStringMapValidity(String map) {
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
        System.out.println("Map string is valid");
        return true;
    }
    public void loadMap(String map) {
        if (!checkStringMapValidity(map)) {
            System.out.println("Map string is not valid");
            return;
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
    }
    public void updateWalls() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].isSolid()) {
                    grid.getChildren().remove(objectsToDraw[i][j].getObjectHitbox());
                    grid.getChildren().remove(objectsToDraw[i][j].getTexture());
                }
            }
        }
        grid.getChildren().remove(playerHitbox);
        grid.getChildren().remove(player.getEntityView());
        placeWalls();
    }

    private void updateAll() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() == 0) {
                    grid.getChildren().remove(objectsToDraw[i][j].getTexture());
                }
            }
        }
        placeFloor();
        placePolygons();
        updateWalls();
    }

    public void moveGrid(int deltaX, int deltaY) {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].isSolid()) {
                    if (checkCollision(playerHitbox, objectsToDraw[i][j].getObjectHitbox())) {
                        this.deltaX -= deltaX;
                        this.deltaY -= deltaY;
                        return;
                    }
                }
            }
        }
        this.deltaX += deltaX;
        this.deltaY += deltaY;
    }

    public void placePolygons() {
        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() > 0) {
                    if (objectsToDraw[i][j].isSolid()) {
                        int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                        int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;
                        Polygon parallelogram = getObjectHitBox(x, y, 32, 0);
                        objectsToDraw[i][j].setObjectHitbox(parallelogram);
                        grid.getChildren().add(objectsToDraw[i][j].getObjectHitbox());
                    }
                }
            }
        }
    }
    private boolean checkPlayer(double[] objectIsoXY, Image objectTexture) {
        return (player.getPositionY() + (double) TILE_HEIGHT / 2 + player.getHeight() * TILE_HEIGHT < (objectIsoXY[1] - TILE_HEIGHT + objectTexture.getHeight())) && (objectIsoXY[0] > player.getPositionX() - TILE_WIDTH && objectIsoXY[0] < player.getPositionX() + 3 * TILE_WIDTH);
    }
    public void placeWalls() {
        boolean playerDrawn = false;

        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {
                if (objectsToDraw[i][j].getHeight() > 0) {
                    int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                    int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                    Image objectTexture = new Image("file:src/main/resources/" + objectsToDraw[i][j].getTexturePath());

                    objectsToDraw[i][j].setCartX((int) (x + 2 * TILE_WIDTH - objectTexture.getHeight()));
                    objectsToDraw[i][j].setCartY((int) (y + TILE_HEIGHT - objectTexture.getHeight()));

                    ImageView object = new ImageView(objectTexture);
                    objectsToDraw[i][j].setTexture(object);
                    double[] objectIsoXY = cartesianToIsometric(objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());

                    if (checkPlayer(objectIsoXY, objectTexture) && !playerDrawn) {
                        drawPlayer();
                        playerDrawn = true;
                    }

                    placeIsometricTileWithTexture(objectsToDraw[i][j].getTexture(), objectsToDraw[i][j].getCartX(), objectsToDraw[i][j].getCartY());

                }
            }
        }
        if (!playerDrawn) {
            drawPlayer();
        }
    }
    public void drawPlayer() {
        player.setEntityView(new ImageView("file:src/main/resources/" + this.player.getTexturePath()));
        grid.getChildren().add(playerHitbox);
        player.getEntityView().setX(this.player.getPositionX());
        player.getEntityView().setY(this.player.getPositionY());
        grid.getChildren().add(player.getEntityView());
    }
    public double[] cartesianToIsometric(int cartX, int cartY) {
        double[] isoXY = new double[2];
        isoXY[0] = (cartX - cartY);
        isoXY[1] = (double) (cartX + cartY) / 2;
        return isoXY;
    }
    public void placeIsometricTileWithTexture(ImageView img, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        img.setX(isoXY[0] - TILE_WIDTH);
        img.setY(isoXY[1] - (double) TILE_HEIGHT / 2);
        grid.getChildren().add(img);
    }
}