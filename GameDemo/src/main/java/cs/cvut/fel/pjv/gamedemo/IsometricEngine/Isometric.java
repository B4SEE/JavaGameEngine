package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
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
                updateIsoGrid();
                showCoordinatesOnCursor();
                updatePlayerPosition(player, playerDeltaX, playerDeltaY);
//                showPlayerCoordinates();
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
    String row4 = "13WW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";
    String row5 = "13WW_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_00TF_11SW";

//    String row1 = "11SW_11SW_00TF_00TF_00TF";
//    String row2 = "11SW_00TF_00TF_00TF_00TF";
//    String row3 = "00TF_00TF_00TF_00TF_00TF";
//    String row4 = "00TF_00TF_00TF_00TF_00TF";
//    String row5 = "00TF_00TF_00TF_00TF_00TF";
    String map2 = row1 + "-" + row2 + "-" + row3 + "-" + row4 + "-" + row5;
    Pane grid = new Pane();

    Circle playerHitbox = getPlayerHitBox(player.getPositionX(), player.getPositionY(), 32, 0);
    @Override
    public void start(Stage stage) throws IOException {
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");
        mainStage = stage;
        loadMap(map2);
        drawIsometricGrid(mainStage);
        Scene scene = new Scene(grid, 1600, 500);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
        timer.start();
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
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
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
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
                }
            }
        });
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
            int gridPosition[] = getTileGridPosition((int) e.getX(), (int) e.getY());
            int playerGridPosition[] = getTileGridPosition(player.getPositionX(), player.getPositionY() + 64);

            mainStage.setTitle("X: " + (int) e.getX() + " Y: " + (int) e.getY() + " Grid X: " + gridPosition[0] + " Grid Y: " + gridPosition[1] + " Player X: " + playerGridPosition[0] + " Player Y: " + playerGridPosition[1]);
        });
    }

    public void showPlayerCoordinates() {
            mainStage.setTitle("Player X: " + player.getPositionX() + " Player Y: " + player.getPositionY());
    }
    public Circle getPlayerHitBox(int cartX, int cartY, int objectWidth, int deltaHeight) {
        Circle circle = new Circle();
        cartX += 32;
        cartY += 96;
        circle.setCenterX(cartX);
        circle.setCenterY(cartY);
        circle.setRadius(16);
        circle.setStyle("-fx-stroke: #563131; -fx-stroke-width: 2; -fx-fill: #ff00af;");
        return circle;
    }
    public Polygon getObjectHitBox(int cartX, int cartY, int objectWidth, int deltaHeight) {
        Polygon parallelogram = new Polygon();
        double[] isoXY1 = cartesianToIsometric(cartX, cartY);
        double[] isoXY2 = cartesianToIsometric(cartX + objectWidth, cartY);
        double[] isoXY3 = cartesianToIsometric(cartX + objectWidth, cartY + 32 + deltaHeight);
        double[] isoXY4 = cartesianToIsometric(cartX, cartY + 32 + deltaHeight);
//        System.out.println(Arrays.toString(isoXY1));
//        System.out.println(Arrays.toString(isoXY2));
//        System.out.println(Arrays.toString(isoXY3));
//        System.out.println(Arrays.toString(isoXY4));
        parallelogram.getPoints().addAll(new Double[]{
                isoXY1[0] + 32, isoXY1[1],
                isoXY2[0] + 32, isoXY2[1],
                isoXY3[0] + 32, isoXY3[1],
                isoXY4[0] + 32, isoXY4[1]
        });
        parallelogram.setStyle("-fx-stroke: #563131; -fx-stroke-width: 2; -fx-fill: #ff00af;");
//        grid.getChildren().add(parallelogram);
//        System.out.println(parallelogram.getBoundsInParent());
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
                if (object.isSolid()) {
                    System.out.println("check collision");
                    if (checkCollision(playerHitbox, object.getObjectHitbox())) {
                        System.out.println("collision detected");
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
                if (subRows[j].charAt(0) == '0' || subRows[j].charAt(0) == '2') {
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
    public void updateIsoGrid() {
        grid.getChildren().clear();
        drawIsometricGrid(mainStage);
    }

    public void moveGrid(int deltaX, int deltaY) {
        this.deltaX += deltaX;
        this.deltaY += deltaY;
    }
    public void drawIsometricGrid(Stage stage) {

        grid.getChildren().add(playerHitbox);

        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {

                int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                Rectangle tile = new Rectangle(TILE_WIDTH * 2, TILE_HEIGHT);
                tile.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 2; -fx-fill: #ff7200; -fx-opacity: 0.5;");
                double[] isoXY = cartesianToIsometric(x, y);
                tile.setX(isoXY[0]);
                tile.setY(isoXY[1]);
                int gridXY[] = getTileGridPosition((int) tile.getX(), (int) tile.getY());

                Image objectTexture = new Image("file:src/main/resources/" + objectsToDraw[i][j].getTexturePath());

                if (objectsToDraw[i][j].getHeight() > 0) {
                    if (j > 0) {
                        if (objectsToDraw[i][j - 1].getHeight() == 0 && !Objects.equals(objectsToDraw[i][j - 1].getTwoLetterId(), "BB")) {
                            Image gapTexture = new Image("file:src/main/resources/" + objectsToDraw[i][j - 1].getTexturePath());
                            placeIsometricTileWithTexture(gapTexture, (int) (x + 2 * TILE_WIDTH - gapTexture.getHeight()), (int) (y + TILE_HEIGHT - gapTexture.getHeight()));
                        }
                    }
                }

                objectsToDraw[i][j].setCartX(x);
                objectsToDraw[i][j].setCartY(y);

                placeIsometricTileWithTexture(objectTexture, (int) (x + 2 * TILE_WIDTH - objectTexture.getHeight()), (int) (y + TILE_HEIGHT - objectTexture.getHeight()));

                if (objectsToDraw[i][j].isSolid()) {
                    Polygon parallelogram = getObjectHitBox(x, y, 32, 0);
                    objectsToDraw[i][j].setObjectHitbox(parallelogram);
                    grid.getChildren().add(objectsToDraw[i][j].getObjectHitbox());
                }

                objectsToDraw[i][j].setGridPositionX((int) gridXY[0]);
                objectsToDraw[i][j].setGridPositionY((int) gridXY[1]);

                grid.getChildren().add(tile);

                placeSmallTile((int) tile.getX(), (int) tile.getY());
            }
        }
        drawPlayer();
    }

    public void drawPlayer() {
        Image playerTexture = new Image("file:src/main/resources/" + this.player.getTexturePath());
        ImageView player = new ImageView(playerTexture);
        player.setX(this.player.getPositionX());
        player.setY(this.player.getPositionY());
        grid.getChildren().add(player);
        placeSmallTile((int) player.getX(), (int) player.getY() + 64);
    }
    public int[] getTileGridPosition(int x, int y) {
        double[] isoXY = cartesianToIsometric(x, y);
        int isoX = (int) isoXY[0];
        int isoY = (int) isoXY[1];
        double tx = Math.ceil(((isoX / TILE_WIDTH) + (isoY / (TILE_HEIGHT / 2))) / 2);
        double ty = Math.ceil((isoY / (TILE_HEIGHT / 2))) - ((isoX / TILE_WIDTH) / 2);
        int gridX = (int) (Math.ceil(tx) - 1);
        int gridY = (int) (Math.ceil(ty) - 1);
        return new int[]{gridX, gridY};
    }

    public int[] getTileIsoPositionFromGrid(int gridX, int gridY) {
        int isoX = (gridX - gridY) * TILE_WIDTH;
        int isoY = (gridX + gridY) * (TILE_HEIGHT / 2);
        isoX += -TILE_WIDTH;
        isoY += 0;
        return new int[]{isoX, isoY};
    }
    public void placeSmallTile(int x, int y) {
        Rectangle rect = new Rectangle(10, 10);
        rect.setStyle("-fx-stroke: #0046ab; -fx-stroke-width: 2; -fx-fill: #00ffe8;");
        int[] cartXY = new int[]{x, y};

        cartXY[0] = cartXY[0] + 64 / 2 - 5;
        cartXY[1] = cartXY[1] + 32 / 2 - 5;
        rect.setX(cartXY[0]);
        rect.setY(cartXY[1]);
        grid.getChildren().add(rect);
    }
    public double[] isometricToCartesian(int isoX, int isoY) {
        double[] cartXY = new double[2];
        cartXY[0] = (double) (2 * isoY + isoX) / 2;
        cartXY[1] = (double) (2 * isoY - isoX) / 2;
        return cartXY;
    }

    public double[] cartesianToIsometric(int cartX, int cartY) {
        double[] isoXY = new double[2];
        isoXY[0] = (cartX - cartY);
        isoXY[1] = (double) (cartX + cartY) / 2;
        return isoXY;
    }
    public void placeIsometricTileWithTexture(Image image, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        ImageView img = new ImageView(image);
        img.setX(isoXY[0] - TILE_WIDTH);
        img.setY(isoXY[1] - TILE_HEIGHT / 2);
        grid.getChildren().add(img);
    }
}