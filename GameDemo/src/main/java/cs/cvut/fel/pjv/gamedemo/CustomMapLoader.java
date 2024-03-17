package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import cs.cvut.fel.pjv.gamedemo.common_classes.Wagon;
import cs.cvut.fel.pjv.gamedemo.engine.GameLogic;
import cs.cvut.fel.pjv.gamedemo.engine.Isometric;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CustomMapLoader extends Application {

    private File file;
    private String mapPath;

    private int buttonWidth = 200;
    private Stage stage;
    private VBox grid = new VBox();
    private Label label = new Label("no files selected; select png or txt file to load a custom map");
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        showFileChooser();
    }

    private void showFileChooser() {
        try {
            grid.setAlignment(Pos.CENTER);
            //add padding
            grid.setSpacing(20);

            grid.setStyle("-fx-background-color: #ffffff;");

            label.setStyle("-fx-text-fill: #000000;");

            grid.getChildren().add(label);
            // set title for the stage
            stage.setTitle("Map Loader");
            // create a File chooser
            FileChooser file_chooser = new FileChooser();

            //set allowed file types to png and txt
            file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files", "*.png"));
            file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files", "*.txt"));

            Button fileChooserButton = fileChooserButton(file_chooser);
            Button loadButton = loadButton();
            Button exitButton = exitButton();
            // add buttons
            grid.getChildren().add(fileChooserButton);
            grid.getChildren().add(loadButton);
            grid.getChildren().add(exitButton);

            // create a scene
            Scene scene = new Scene(grid, 800, 500);

            // set the scene
            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Button loadButton() {

        Button previewButton = previewButton();

        Button loadButton = new Button("Load map");
        loadButton.setPrefWidth(buttonWidth);

        String successMsg = "Map loaded successfully; press exit button to exit, or select another map to load";
        String errorMsg = "invalid map file; select another map to load";
        String invalidFileTypeMsg = "invalid file type; select png or txt file to load a custom map";
        String noFilesSelectedMsg = "no files selected; select png or txt file to load a custom map";

        String successMsgStyle = "-fx-text-fill: #548c33;";
        String errorMsgStyle = "-fx-text-fill: #8c1313;";

        EventHandler<ActionEvent> loadEvent = e -> {
            if (file != null) {
                if (file.getName().endsWith(".png")) {
                    if (parseMapPNG(file)) {
                        label.setText(successMsg);
                        label.setStyle(successMsgStyle);
                        grid.getChildren().add(previewButton);
                    } else {
                        label.setText(errorMsg);
                        label.setStyle("-fx-text-fill: #8c1313;");
                    }
                } else if (file.getName().endsWith(".txt")) {
                    if (parseMapTXT(file)) {
                        label.setText(successMsg);
                        label.setStyle(successMsgStyle);
                        grid.getChildren().add(previewButton);
                    } else {
                        label.setText(errorMsg);
                        label.setStyle(errorMsgStyle);
                    }
                } else {
                    label.setText(invalidFileTypeMsg);
                    label.setStyle(errorMsgStyle);
                }
            } else {
                label.setText(noFilesSelectedMsg);
                label.setStyle(errorMsgStyle);
            }
        };

        loadButton.setOnAction(loadEvent);

        return loadButton;
    }
    private Button previewButton() {
        Button previewButton = new Button("Preview map");
        previewButton.setPrefWidth(buttonWidth);

        EventHandler<ActionEvent> previewEvent = e -> {
            if (mapPath != null) {
                Scene loaderScene = stage.getScene();
                Scene scene = previewMap(mapPath);
                if (scene != null) {
                    stage.setScene(null);
                    stage.setScene(scene);
                    scene.setOnKeyPressed(event1 -> {
                        if (event1.getCode().toString().equals("ESCAPE")) {
                            stage.setScene(loaderScene);
                        }
                    });
                }
            } else {
                label.setText("no map loaded; load a map to preview");
                label.setStyle("-fx-text-fill: #8c1313;");
            }
        };

        previewButton.setOnAction(previewEvent);

        return previewButton;
    }
    private Button fileChooserButton(FileChooser file_chooser) {
        Button button = new Button("Show open dialog");
        button.setPrefWidth(buttonWidth);
        EventHandler<ActionEvent> event =
                e -> {
                    this.file = null;
                    this.mapPath = null;
                    // get the file selected
                    File file = file_chooser.showOpenDialog(stage);

                    if (file != null) {
                        label.setText(file.getAbsolutePath() + "  selected");
                        label.setStyle("-fx-text-fill: #000000;");
                        setFile(file);
                    }
                };

        button.setOnAction(event);

        return button;
    }

    private Button exitButton() {
        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(buttonWidth);
        exitButton.setLayoutX(100);
        exitButton.setLayoutY(500);
        EventHandler<ActionEvent> exitEvent = e -> System.exit(0);

        exitButton.setOnAction(exitEvent);

        return exitButton;
    }
    public Scene previewMap(String mapPath) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPrefSize(1000, 800);
        grid.setStyle("-fx-background-color: #000000;");
        StringBuilder sb = new StringBuilder();
        try {
            java.util.Scanner scanner = new java.util.Scanner(new File(mapPath));
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
                sb.append(Constants.MAP_ROW_SEPARATOR);
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String map = sb.toString();
        String[] rows = map.split(Constants.MAP_ROW_SEPARATOR);

        for (int i = 0; i < rows.length; i++) {
            String[] subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
            for (int j = 0; j < subRows.length; j++) {
                if (subRows[j].length() == 4) {
                    String texturePath = Constants.OBJECT_IDS.get(subRows[j].substring(2, 4));
                    int height = Integer.parseInt(String.valueOf(subRows[j].charAt(1)));
                    if (texturePath == null) {
                        texturePath = Constants.INTERACTIVE_OBJECTS.get(subRows[j].substring(2, 4));
                    }
                    if (texturePath != null) {
                        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource(texturePath)).toExternalForm()));
                        imageView.setFitWidth(32);
                        imageView.setFitHeight(32);
                        if (height == 0) {
                            imageView.setRotate(45);
                        }
                        grid.add(imageView, j, i);
                    }
                }
            }
        }
        stage.setTitle("Map preview, press ESC to exit");

        Scene scene = new Scene(grid, 1000, 800);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ESCAPE")) {
                stage.setScene(stage.getScene());
            }
        });
        return scene;
    }
//format for png map:
    //check wiki page for more info
    //use colour coding for different objects
    //unique colours for each object
    //use the same colour for the same object
    //if colour is not defined, the object will be replaced with a blank space
    //blank space uses black colour, for better visibility in map preview (map preview is not implemented yet)
    private boolean parseMapPNG(File file) {
        //parse the map file
        return true;
    }
//format for txt map:
    //maximum number of unique two-letter codes is 26*26=676; map parsers might be modified to allow more codes, but now it's limited to 676
    //this means that the map can have only 676 unique objects/textures; this is more than enough for most maps
    //custom constants could be used in maps, but that function was removed; use png for better map readability
    private boolean parseMapTXT(File file) {
        //basic validation of the map file
        if (file == null) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        try {
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
                sb.append("\n");
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] lines = sb.toString().split("\n");

        System.out.println(Arrays.toString(lines));
        //check if the map is valid
        return checkAndLoadTXTmap(lines, file.getName());
    }

    private boolean checkAndLoadTXTmap(String[] lines, String filename) {
        //if map is only one line long, check if it uses separator for rows

        if (lines.length == 1) {
            if (lines[0].contains(Constants.MAP_ROW_SEPARATOR)) {
                lines = lines[0].split(Constants.MAP_ROW_SEPARATOR);
            }
        }

        //check if the map is valid

        //check if all map lines are the same length
        int lineLength = lines[0].length();
        for (String line : lines) {
            if (line.length() != lineLength) {
                return false;
            }
        }

        //check if all map codes have the same number of characters (4)
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String code : subRows) {
                if (code.length() != 4) {
                    System.out.println("first");
                    return false;
                }
            }
        }

        //check if all map codes are valid (are in Constant dictionaries)
        for (String row : lines) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String subRow : subRows) {
                if (!List.of(Constants.ALLOWED_CODES).contains(subRow.charAt(0))) {
                    System.out.println(subRow.charAt(0));
                    System.out.println("second");
                    return false;
                }
                if (!List.of(Constants.ALLOWED_HEIGHTS).contains(subRow.charAt(1))) {
                    if (subRow.charAt(0) != Constants.INTERACTIVE_OBJECT) {
                        System.out.println(subRow.charAt(1) + " " + subRow);
                        System.out.println("third");
                        return false;
                    }
                }
                if (!Character.isLetter(subRow.charAt(2))) {
                    System.out.println("fourth");
                    return false;
                }
                if (!Character.isLetter(subRow.charAt(3))) {
                    System.out.println("fifth");
                    return false;
                }
                if (!Constants.OBJECT_IDS.containsKey(subRow.substring(2, 4)) && !Constants.INTERACTIVE_OBJECTS.containsKey(subRow.substring(2, 4))) {
                    System.out.println("sixth");
                    return false;
                }
            }
        }

        //create map lines, and load the map
        StringBuilder sb = new StringBuilder();
        for (String row : lines) {
            sb.append(row);
            sb.append("\n");
        }
        String map = sb.toString();

        //load the map
        String path = "maps/custom_maps/" + filename;

        this.mapPath = path;

        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(path);
            writer.print(map);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    private void setFile(File file) {
        this.file = file;
    }
}
