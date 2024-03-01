package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
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
            }
        }
    };

//    map example: 11AA_12BB_13CC-11AA_00AA_00BB
//first number tile type: 0 - floor (not Solid), 1 - wall (Solid), 2 - door (not Solid)
//second number tile height (only for walls) min 1, max 3
//third number tile letter id (for example: AA, BB, CC)
//'_' separates tiles, '-' separates rows
    String row1 = "12HW_12HW_13WW_12HW_11SW_11SW_12HW_11SW_11SW_11SW_00TF_00TF_11SW_11SW_11SW_11SW_11SW_11SW_11SW_11SW_00BB_00BB";
    String row2 = "13WW_12HW_13WW_12HW_11SW_11SW_12HW_11SW_11SW_11SW_00TF_00BB_11SW_11SW_11SW_11SW_11SW_11SW_11SW_11SW_00BB_00BB";
    String row3 = "12HW_12HW_12HW_12HW_11SW_11SW_12HW_11SW_00TF_11SW_00TF_00BB_13WW_11SW_11SW_11SW_11SW_11SW_11SW_11SW_11SW_00BB";
    String row4 = "12HW_12HW_13WW_12HW_11SW_11SW_12HW_13WW_00TF_00TF_00TF_00BB_11SW_11SW_11SW_11SW_11SW_11SW_11SW_11SW_11SW_00BB";
    String row5 = "12HW_12HW_13WW_12HW_11SW_11SW_12HW_11SW_00TF_11SW_00TF_00TF_11SW_11SW_11SW_11SW_11SW_13WW_11SW_11SW_00BB_00BB";
    String map2 = row1 + "-" + row2 + "-" + row3 + "-" + row4 + "-" + row5;
    Pane grid = new Pane();
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        loadMap(map2);
        drawIsometricGrid(mainStage);
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
        grid.setPrefSize(1000, 1000);
        grid.setStyle("-fx-background-color: #000000;");

        for (int i = 0; i < objectsToDraw.length; i++) {
            for (int j = 0; j < objectsToDraw[i].length; j++) {

                int x = TILE_WIDTH + j * TILE_WIDTH + deltaX * TILE_WIDTH;
                int y = i * TILE_HEIGHT + deltaY * TILE_HEIGHT;

                Image objectTexture = new Image("file:src/main/resources/" + objectsToDraw[i][j].getTexturePath());

                if (objectsToDraw[i][j].getHeight() > 0) {
                    if (j > 0) {
                        if (objectsToDraw[i][j - 1].getHeight() == 0 && !Objects.equals(objectsToDraw[i][j - 1].getTwoLetterId(), "BB")) {
                            Image gapTexture = new Image("file:src/main/resources/" + objectsToDraw[i][j - 1].getTexturePath());
                            placeIsometricTileWithTexture(gapTexture, (int) (x + 2 * TILE_WIDTH - gapTexture.getHeight()), (int) (y + TILE_HEIGHT - gapTexture.getHeight()));
                        }
                    }
                }

                placeIsometricTileWithTexture(objectTexture, (int) (x + 2 * TILE_WIDTH - objectTexture.getHeight()), (int) (y + TILE_HEIGHT - objectTexture.getHeight()));

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
    public void placeIsometricTileWithTexture(Image image, int cartX, int cartY) {
        double[] isoXY = cartesianToIsometric(cartX, cartY);
        ImageView img = new ImageView(image);
        img.setX(isoXY[0]);
        img.setY(isoXY[1]);
        grid.getChildren().add(img);
    }

    public static void main(String[] args) {
        launch();
    }
}