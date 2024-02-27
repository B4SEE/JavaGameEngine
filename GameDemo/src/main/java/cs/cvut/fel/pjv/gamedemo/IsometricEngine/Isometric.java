package cs.cvut.fel.pjv.gamedemo.IsometricEngine;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class Isometric extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        Label label = new Label("Hello, JavaFX!");
//        label.setStyle("-fx-text-fill: #ff0000; -fx-font-size: 40px;");
//        Pane mainPane = new FlowPane(label);
//        mainPane.setStyle("-fx-background-color: #15efef;");
//        Scene scene = new Scene(mainPane, 640, 480);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//        drawCircle(stage);
        drawGrid(stage);
    }

    public void drawCircle(Stage stage) throws IOException {
        Circle circ = new Circle(40, 40, 30);
        Group root = new Group(circ);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }

    public void drawGrid(Stage stage) throws IOException {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        int[][] gridMap = stringToGridMap("111111111111111-100000000000001-100000000000001-100000000000001-111111111111111");
        grid.setHgap(5);
        grid.setVgap(5);
        for (int i = 0; i < gridMap.length; i++) {
            for (int j = 0; j < gridMap[i].length; j++) {
                Rectangle rect = new Rectangle(50, 50);
                if (gridMap[i][j] == 1) {
                    rect.setStyle("-fx-fill: #692424");
                } else {
                    rect.setStyle("-fx-fill: #476946");
                }
                grid.add(rect, j, i);
            }
        }
        Scene scene = new Scene(grid, 400, 300);
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
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
        return gridMap;
    }

    public static void main(String[] args) {
        launch();
    }
}