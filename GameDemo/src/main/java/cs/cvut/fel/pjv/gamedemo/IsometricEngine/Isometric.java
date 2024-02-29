package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class Isometric extends Application {
    int TILE_WIDTH = 32;
    int TILE_HEIGHT = 32;
    int gap = 0;
    int deltaX = 10;
    int deltaY = 0;
    Image block_wall = new Image("file:src/main/resources/block_wall.png");
    Image tile_path = new Image("file:src/main/resources/tile_path.png");
    Image tile_floor = new Image("file:src/main/resources/tile_floor.png");
    String map = "11111111111111111111-10000000000000000000-10000000000000000000-12222222222222222222-10000000000000000000-10000000000000000000-1";
    int[][] gridMap = stringToGridMap(map);
    Pane grid = new Pane();
    @Override
    public void start(Stage stage) throws IOException {
        addGap(map);
        drawIsometricGrid(stage);
//        drawGrid(stage);
    }

    public void moveGrid(int deltaX, int deltaY) {
        this.deltaX += deltaX;
        this.deltaY += deltaY;
    }
    public void addGap(String gridMapString) {
        String[] rows = gridMapString.split("-");
        //paste gap zeros after each '-'
        System.out.println(Arrays.toString(rows));
        for (int i = 0; i < rows.length; i++) {
            String newRow = "6".repeat(gap) + rows[i];
//            System.out.println(newRow);
            rows[i] = newRow;
//            System.out.println(rows[i]);
        }
        gridMap = stringToGridMap(String.join("-", rows));
        System.out.println(String.join("-", rows));
    }
    public void drawGrid(Stage stage) throws IOException {
        GridPane grid = new GridPane();

        grid.setGridLinesVisible(true);
        grid.setHgap(5);
        grid.setVgap(5);
        addGap(map);
        for (int i = 0; i < gridMap.length; i++) {
            for (int j = 0; j < gridMap[i].length; j++) {
                Rectangle rect = new Rectangle(TILE_WIDTH, TILE_HEIGHT);
                if (gridMap[i][j] == 1) {
                    rect.setStyle("-fx-fill: #692424");
                } else if (gridMap[i][j] == 2) {
                    rect.setStyle("-fx-fill: #d07e00");
                } else if (gridMap[i][j] == 6) {
                    rect.setStyle("-fx-fill: #0ea896");
                } else if (gridMap[i][j] == 3) { continue; }
                else {
                    rect.setStyle("-fx-fill: #476946");
                }
                grid.add(rect, i, j);
            }
        }
        Scene scene = new Scene(grid, 1600, 500);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
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

                int x = gap * TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                Rectangle rect = new Rectangle(TILE_WIDTH, TILE_HEIGHT);
                if (gridMap[i][j] == 1) {
                    rect.setStyle("-fx-fill: " + TILE_WALL_COLOR + ";");
                    if (j < gridMap.length - 1) {
                        if (gridMap[i][j+1] != 1) {
                            gap = 1;
                        }
                    }
                    placeIsometricTileWithTexture(block_wall, x, (int) (y + TILE_HEIGHT - block_wall.getHeight()));
                } else if (gridMap[i][j] == 2) {
                    rect.setStyle("-fx-fill: " + TILE_PATH_COLOR + ";");
                    gap = 0;
                    placeIsometricTileWithTexture(tile_path, x, y);
                } else if (gridMap[i][j] == 3) { continue; }
                else {
                    rect.setStyle("-fx-fill: " + TILE_FLOOR_COLOR + ";");
                    gap = 0;
                    placeIsometricTileWithTexture(tile_floor, x, y);
                }
//                placeIsometricTile(rect, x, y);
            }
        }

        Scene scene = new Scene(grid, 1600, 500);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
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

    public double[] getIsoTileCoords(int cartX, int cartY, int tileWidth, int tileHeight) {
        double[] cartXY = new double[2];
        cartXY[0] = Math.floor((double) cartX / tileWidth);
        cartXY[1] = Math.floor((double) cartY / tileHeight);
        return cartXY;
    }

    public int[][] stringToGridMap(String gridMapString) {
        //string example: "111111111111111-100000000000001-100000000000001-100000000000001-111111111111111"
        String[] rows = gridMapString.split("-");
        int[][] gridMap = new int[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                if (rows[i].isEmpty()) {
                    gridMap[i][j] = 3;
                }
                gridMap[i][j] = Integer.parseInt(rows[i].substring(j, j + 1));
            }
        }
        return gridMap;
    }

    public static void main(String[] args) {
        launch();
    }
}