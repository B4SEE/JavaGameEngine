package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.utils.MapLoader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class for loading custom maps.
 */
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
            grid.getChildren().add(removeButton());

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
                    if (parseMapPng(file)) {
                        label.setText(successMsg);
                        label.setStyle(successMsgStyle);
                        grid.getChildren().add(previewButton);
                    } else {
                        label.setText(errorMsg);
                        label.setStyle("-fx-text-fill: #8c1313;");
                    }
                } else if (file.getName().endsWith(".txt")) {
                    if (parseMapTxt(file)) {
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

    private Button removeButton() {
        Button removeButton = new Button("Remove all invalid maps");
        removeButton.setPrefWidth(buttonWidth);
        removeButton.setLayoutX(100);
        removeButton.setLayoutY(500);
        //set style: grey button with white text, on hover: red button with white text
        removeButton.setStyle("-fx-background-color: #808080; -fx-text-fill: #ffffff;");
        removeButton.setOnMouseEntered(e -> removeButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #ffffff;"));
        removeButton.setOnMouseExited(e -> removeButton.setStyle("-fx-background-color: #808080; -fx-text-fill: #ffffff;"));
        final int[] count = {0};
        EventHandler<ActionEvent> removeEvent = e -> {
            MapLoader mapLoader = new MapLoader();
            File folder = new File("maps/common");
            File[] listOfFiles = folder.listFiles();
            List<File> filesToMove;
            filesToMove = getInvalidMaps(count, mapLoader, listOfFiles);
            folder = new File("maps/custom");
            listOfFiles = folder.listFiles();
            filesToMove.addAll(getInvalidMaps(count, mapLoader, listOfFiles));
            for (File f : filesToMove) {
                try {
                    //create a copy of the file and move it to the invalid maps folder
                    if (!new File("maps/invalid").exists()) {
                        Files.createDirectory(new File("maps/invalid").toPath());
                    }
                    if (!new File("maps/invalid/" + f.getName()).exists()) {
                        Files.copy(f.toPath(), new File("maps/invalid/" + f.getName()).toPath());
                    } else {
                        //delete the copy if it already exists
                        Files.deleteIfExists(new File("maps/invalid/" + f.getName()).toPath());
                        Files.copy(f.toPath(), new File("maps/invalid/" + f.getName()).toPath());
                    }
                    //delete the original file
                    Files.deleteIfExists(new File(f.getPath()).toPath());
                    System.out.println("Invalid map removed: " + f.getPath());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            if (count[0] == 0) {
                label.setText("No invalid maps found");
                label.setStyle("-fx-text-fill: #548c33;");
            } else {
                if (count[0] == 1) {
                    label.setText("1 invalid map removed");
                } else {
                    label.setText(count[0] + " invalid maps removed");
                }
                label.setStyle("-fx-text-fill: #d07e00;");
            }
            count[0] = 0;
        };

        removeButton.setOnAction(removeEvent);

        return removeButton;
    }

    private List<File> getInvalidMaps(int[] count, MapLoader mapLoader, File[] listOfFiles) {
        List<File> filesToMove = new java.util.ArrayList<>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile()) {
                    String unparsedSeed = mapLoader.load(f.getPath());
                    String parsedSeed = mapLoader.parseMap(unparsedSeed);
                    if (!Checker.checkMap(parsedSeed)) {
                        filesToMove.add(f);
                        count[0]++;
                    }
                }
            }
        }
        return filesToMove;
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
    private boolean parseMapPng(File file) {
        //parse the map file
        return true;
    }
//format for txt map:
    //maximum number of unique two-letter codes is 26*26=676; map parsers might be modified to allow more codes, but now it's limited to 676
    //this means that the map can have only 676 unique objects/textures; this is more than enough for most maps
    //custom constants could be used in maps, but that function was removed; use png for better map readability
    private boolean parseMapTxt(File file) {
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
        //first line is wagon type
        String[] lines = sb.toString().split("\n");
        String wagonType = lines[0];
        //name file based on wagon type and number of files with the same wagon type (to avoid overwriting)
        String filename = getString(wagonType);
        //check if wagon type is in String[] WAGON_TYPES
        if (!Arrays.asList(Constants.WAGON_TYPES).contains(wagonType)) {
            return false;
        } else {
            //remove wagon type from the map
            sb.delete(0, lines[0].length() + 1);
        }
        //check if the map is valid
        return loadTxtMap(sb.toString(), filename);
    }

    private static String getString(String wagonType) {
        int filesCount = 0;
        File folder = new File("maps/custom");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile()) {
                    if (f.getName().startsWith(wagonType)) {
                        filesCount++;
                    }
                }
            }
        }
        String filename = wagonType + "_" + filesCount + "_wagon.txt";
        return filename;
    }

    private boolean loadTxtMap(String map, String filename) {
        //load random objects
        map = MapLoader.parseMap(map);
        //check if the map is valid
        if (!Checker.checkMap(map)) {
            return false;
        }
        //load the map
        String path = "maps/custom/" + filename;

        this.mapPath = path;

        try {
            //create a new file
            File file = new File(path);
            //write the map to the file
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(map);
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
