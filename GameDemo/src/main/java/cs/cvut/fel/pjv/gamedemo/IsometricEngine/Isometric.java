package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class Isometric extends Application {
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private int deltaX = 10;
    private int deltaY = 0;
    private Stage mainStage;

    AnimationTimer timer = new AnimationTimer() {
        final long INTERVAL = 100000000000L;
        long lastTime = -1;
        @Override
        public void handle(long l) {
            if (lastTime < 0) {
                lastTime = l;
                return;
            } else if (l - lastTime < INTERVAL) {
                updateIsoGrid();
            }
        }
    };

    Image block_wall = new Image("file:src/main/resources/block_wall.png");
    Image tile_path = new Image("file:src/main/resources/tile_path.png");
    Image tile_floor = new Image("file:src/main/resources/tile_floor.png");
    Image block_wagon_wall = new Image("file:src/main/resources/block_wagon_wall.png");
    Image wall_3 = new Image("file:src/main/resources/wall_3.png");
    String map = "557511511133111111113-570000000005770000000-500000001010071000000-100000000000000000000-100050501000000000000-100100000000000000000";
//    String map = "10000000010000000000";
    int[][] gridMap = stringToGridMap(map);
    Pane grid = new Pane();
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        drawIsometricGrid(mainStage);
//        drawGrid(tilesToDraw);
        Scene scene = new Scene(grid, 1600, 500);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
        timer.start();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case W:
                        moveGrid(0, -1);
                        break;
                    case S:
                        moveGrid(0, 1);
                        break;
                    case A:
                        moveGrid(-1, 0);
                        break;
                    case D:
                        moveGrid(1, 0);
                        break;
                }
            }
        });
    }
    public void updateIsoGrid() {
        grid.getChildren().clear();
        drawIsometricGrid(mainStage);
    }

    public void moveGrid(int deltaX, int deltaY) {
        this.deltaX += deltaX;
        this.deltaY += deltaY;
//        System.out.println("deltaX: " + this.deltaX + ", deltaY: " + this.deltaY);
    }
    public void drawGrid(Tile[] tiles) {
        for (Tile tile : tiles) {
            Rectangle rect = new Rectangle(TILE_WIDTH, TILE_HEIGHT);
            if (tile.getId() == 1) {
                rect.setStyle("-fx-fill: #692424;");
            } else if (tile.getId() == 2) {
                rect.setStyle("-fx-fill: #d07e00;");
            } else {
                rect.setStyle("-fx-fill: #476946;");
            }
            placeIsometricTile(rect, tile.getCartX(), tile.getCartY());
        }
    }

    public void drawIsometricGrid(Stage stage) {
        String BACKGROUND_COLOR = "#000000";
        String TILE_WALL_COLOR = "#692424";
        String TILE_FLOOR_COLOR = "#476946";
        String TILE_PATH_COLOR = "#d07e00";

        grid.setPrefSize(800, 800);
        grid.setStyle("-fx-background-color: #000000;");

        for (int i = 0; i < gridMap.length; i++) {
            for (int j = 0; j < gridMap[i].length; j++) {

                int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                if (gridMap[i][j] == 0) {
                    Tile tile = new Tile(0, "TF", x, y, tile_floor);
                    placeIsometricTileWithTexture(tile_floor, x, y);
                } else if (gridMap[i][j] == 1) {
                    if (j > 0) {
                        if (gridMap[i][j - 1] == 0 || gridMap[i][j - 1] == 9) {
                            Tile tile = new Tile(0, "TF", x, y, tile_floor);
                            placeIsometricTileWithTexture(tile_floor, x, y);
                        }
                    }
                    Tile tile = new Tile(1, "BW", x, (int) (y + TILE_HEIGHT - block_wall.getHeight()), block_wall);
                    placeIsometricTileWithTexture(block_wall, x, (int) (y + TILE_HEIGHT - block_wall.getHeight()));
                } else if (gridMap[i][j] == 5) {
                    if (j > 0) {
                        if (gridMap[i][j - 1] == 0 || gridMap[i][j - 1] == 9) {
                            Tile tile = new Tile(0, "TF", x, y, tile_floor);
                            placeIsometricTileWithTexture(tile_floor, x, y);
                        }
                    }
                    Tile tile = new Tile(5, "BC", (int) (x + TILE_WIDTH - block_wagon_wall.getWidth()), (int) (y + TILE_HEIGHT - block_wagon_wall.getHeight()), block_wagon_wall);
                    placeIsometricTileWithTexture(block_wagon_wall, (int) (x + 2 * TILE_WIDTH - block_wagon_wall.getHeight()), (int) (y + TILE_HEIGHT - block_wagon_wall.getHeight()));
                    System.out.println(TILE_WIDTH + " " + block_wagon_wall.getHeight());
                } else if (gridMap[i][j] == 7) {
                    if (j > 0) {
                        if (gridMap[i][j - 1] == 0 || gridMap[i][j - 1] == 9) {
                            Tile tile = new Tile(0, "TF", x, y, tile_floor);
                            placeIsometricTileWithTexture(tile_floor, x, y);
                        }
                    }
                    Tile tile = new Tile(7, "W3", (int) (x - wall_3.getWidth()), (int) (y + TILE_HEIGHT - wall_3.getHeight()), wall_3);
                    placeIsometricTileWithTexture(wall_3, (int) (x + 2 * TILE_WIDTH - wall_3.getHeight()), (int) (y + TILE_HEIGHT - wall_3.getHeight()));
                    System.out.println(TILE_WIDTH + " " + wall_3.getHeight());
                }
            }
        }
    }
    public double[] isometricToCartesian(int isoX, int isoY) {
        double[] cartXY = new double[2];
        cartXY[0] = (double) (2 * isoY + isoX) / 2;
        cartXY[1] = (double) (2 * isoY - isoX) / 2;
        return cartXY;
    }

    public double[] cartesianToIsometric(int cartX, int cartY) {
        double[] isoXY = new double[2];
        isoXY[0] = cartX - cartY;
        isoXY[1] = (cartX + cartY) / 2;
        return isoXY;
    }

    public void placeIsometricTile(Rectangle rect, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        rect.setX(isoXY[0]);
        rect.setY(isoXY[1]);
        grid.getChildren().add(rect);
    }

    public void placeIsometricTileWithTexture(Image image, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        ImageView img = new ImageView(image);
        img.setX(isoXY[0]);
        img.setY(isoXY[1]);
        grid.getChildren().add(img);
    }

    public int[][] stringToGridMap(String gridMapString) {
        //string example: "111111111111111-100000000000001-100000000000001-100000000000001-111111111111111"
        String[] rows = gridMapString.split("-");
        int[][] gridMap = new int[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                gridMap[i][j] = Integer.parseInt(rows[i].substring(j, j + 1));
            }
        }
        for (int i = 0; i < gridMap.length; i++) {
            for (int j = 0; j < gridMap[i].length; j++) {
                if (j > 0) {
                    if (gridMap[i][j] == 0 && gridMap[i][j - 1] > 0 && gridMap[i][j - 1] != 3 && gridMap[i][j - 1] != 9) {
                        gridMap[i][j] = 9;
                    }
                }
            }
        }
        System.out.println(Arrays.deepToString(gridMap));
        return gridMap;
    }

    public static void main(String[] args) {
        launch();
    }
}