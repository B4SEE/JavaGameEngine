package cs.cvut.fel.pjv.gamedemo;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CustomMapLoader extends Application {

    private File file;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        showFileChooser(stage);
    }

    private void showFileChooser(Stage stage) {
        try {
            // set title for the stage
            stage.setTitle("FileChooser");

            // create a File chooser
            FileChooser fil_chooser = new FileChooser();

            //set allowed file types to png and txt
            fil_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files", "*.png"));
            fil_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files", "*.txt"));

            Label label = new Label("no files selected; select png or txt file to load a custom map");

            Button button = new Button("Show open dialog");

            EventHandler<ActionEvent> event =
                    e -> {

                        // get the file selected
                        File file = fil_chooser.showOpenDialog(stage);

                        if (file != null) {
                            label.setText(file.getAbsolutePath() + "  selected");
                            label.setStyle("-fx-text-fill: #000000;");
                            setFile(file);
                        }
                    };

            button.setOnAction(event);

            Button loadButton = new Button("Load map");

            EventHandler<ActionEvent> loadEvent = e -> {
                if (file != null) {
                    if (file.getName().endsWith(".png")) {
                        if (parseMapPNG(file)) {
                            label.setText("Map loaded successfully; press exit button to exit, or select another map to load");
                            label.setStyle("-fx-text-fill: #548c33;");
                        } else {
                            label.setText("invalid map file; select another map to load");
                            label.setStyle("-fx-text-fill: #8c1313;");
                        }
                    } else if (file.getName().endsWith(".txt")) {
                        if (parseMapTXT(file)) {
                            label.setText("Map loaded successfully; press exit button to exit, or select another map to load");
                            label.setStyle("-fx-text-fill: #548c33;");
                        } else {
                            label.setText("invalid map file; select another map to load");
                            label.setStyle("-fx-text-fill: #8c1313;");
                        }
                    } else {
                        label.setText("invalid file type; select png or txt file to load a custom map");
                        label.setStyle("-fx-text-fill: #8c1313;");
                    }
                } else {
                    label.setText("no files selected; select png or txt file to load a custom map");
                    label.setStyle("-fx-text-fill: #8c1313;");
                }
            };

            loadButton.setOnAction(loadEvent);

            Button exitButton = new Button("Exit");

            EventHandler<ActionEvent> exitEvent = e -> System.exit(0);

            exitButton.setOnAction(exitEvent);

            VBox vbox = new VBox(30, label, button, loadButton, exitButton);

            // set Alignment
            vbox.setAlignment(Pos.CENTER);

            // create a scene
            Scene scene = new Scene(vbox, 800, 500);

            // set the scene
            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    //first 11 lines are for custom constants:
    //line 1: row separator
    //line 2: column separator
    //line 3: character for floor
    //line 4: character for wall
    //line 5: character for interactive object
    //line 6: character for random
    //line 7: two-letter code for wagon door
    //line 8: two-letter code for lockable door
    //line 9: two-letter code for chest object
    //line 10: two-letter code for enemy spawn
    //line 11: two-letter code for trap
    //next lines are for map
    //!custom constants MUST be unique (unique character for each constant)
    //!!custom constants line template:
    //MAP_ROW_SEPARATOR:-
    //MAP_COLUMN_SEPARATOR:_
    //FLOOR:0
    //WALL:1
    //INTERACTIVE_OBJECT:2
    //RANDOM:3
    //WAGON_DOOR:WD
    //LOCKABLE_DOOR:LD
    //CHEST_OBJECT:CO
    //ENEMY_SPAWN:EN
    //TRAP:TR
    //!!!codes for other objects (non-interactive) CANNOT be custom
    //!!!custom constants MUST be unique (unique character for each constant) and CANNOT be codes for other objects (check Constants.java dictionaries)
    //maximum number of unique two-letter codes is 26*26=676; map parsers might be modified to allow more codes, but now it's limited to 676
    //this means that the map can have only 676 unique objects/textures;
    //if standard constants (-, _, 0, 1, 2, 3, WD, LD, CO, EN, TR) are used, write 0 at the first line, next lines will be read as map;
    private boolean parseMapTXT(File file) {
        //basic validation of the map file
        //check if the first line is 0 or starts with Constants.MAP_ROW_SEPARATOR + ":", if not, return false
        //if the first line is constants, check if all constants are unique and have correct names, if not, return false
        //second line should start with Constants.MAP_COLUMN_SEPARATOR + ":", if not, return false
        //third line should start with Constants.FLOOR + ":", if not, return false
        //fourth line should start with Constants.WALL + ":", if not, return false
        //fifth line should start with Constants.INTERACTIVE_OBJECT + ":", if not, return false
        //sixth line should start with Constants.RANDOM + ":", if not, return false
        //seventh line should start with Constants.WAGON_DOOR + ":", if not, return false
        //eighth line should start with Constants.LOCKABLE_DOOR + ":", if not, return false
        //ninth line should start with Constants.CHEST_OBJECT + ":", if not, return false
        //tenth line should start with Constants.ENEMY_SPAWN + ":", if not, return false
        //eleventh line should start with Constants.TRAP + ":", if not, return false
        //get all constants and check if they are unique, if not, return false
        //if constants are valid, check if the map is valid, if not, return false
        //if line starts with 0, check if the map is valid, if not, return false
        //check if all map lines are the same length, if not, return false
        //check if all map codes have the same number of characters (4), if not, return false
        //check if all map codes are valid, if not, return false
        //if all checks pass, return true

        //check first line

        if (file == null) {
            return false;
        }

        System.out.println(file.getName());

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

        String firstLine = lines[0];

        if (firstLine.startsWith("0")) {
            System.out.println("0");
            //get map
            String[] map = new String[lines.length - 1];
            for (int i = 1; i < lines.length; i++) {
                map[i - 1] = lines[i];
            }
            //check if the map is valid
            String[] defaultConstants = {Constants.MAP_ROW_SEPARATOR, Constants.MAP_COLUMN_SEPARATOR, String.valueOf(Constants.FLOOR), String.valueOf(Constants.WALL), String.valueOf(Constants.INTERACTIVE_OBJECT), String.valueOf(Constants.RANDOM), Constants.WAGON_DOOR, Constants.LOCKABLE_DOOR, Constants.CHEST_OBJECT, Constants.ENEMY_SPAWN, Constants.TRAP};
            return checkAndLoadTXTmap(map, defaultConstants, file.getName());
        } else if (firstLine.startsWith("Constants.MAP_ROW_SEPARATOR:")) {
            System.out.println("constants");
            String[] constants = new String[11];
            //check if all constants start with correct names
            String[] constantsOrder = {"Constants.MAP_ROW_SEPARATOR", "Constants.MAP_COLUMN_SEPARATOR", "Constants.FLOOR", "Constants.WALL", "Constants.INTERACTIVE_OBJECT", "Constants.RANDOM", "Constants.WAGON_DOOR", "Constants.LOCKABLE_DOOR", "Constants.CHEST_OBJECT", "Constants.ENEMY_SPAWN", "Constants.TRAP"};
            for (int i = 0; i < 11; i++) {
                if (!lines[i].startsWith(constantsOrder[i] + ":")) {
                    System.out.println("first");
                    return false;
                }
            }
            //get all constants
            for (int i = 0; i < 11; i++) {
                //: index
                int index = lines[i].indexOf(":");
                if (index == -1) {
                    System.out.println("second");
                    return false;
                }
                if (lines[i].startsWith(constantsOrder[6] + ":")) {
                    constants[i] = lines[i].substring(index + 1, index + 3);
                    System.out.println(constants[i]);
                } else if (lines[i].startsWith(constantsOrder[7] + ":")) {
                    constants[i] = lines[i].substring(index + 1, index + 3);
                    System.out.println(constants[i]);
                } else if (lines[i].startsWith(constantsOrder[8] + ":")) {
                    constants[i] = lines[i].substring(index + 1, index + 3);
                } else if (lines[i].startsWith(constantsOrder[9] + ":")) {
                    constants[i] = lines[i].substring(index + 1, index + 3);
                } else if (lines[i].startsWith(constantsOrder[10] + ":")) {
                constants[i] = String.valueOf(lines[i].charAt(index + 1));
                } else {
                    constants[i] = String.valueOf(lines[i].charAt(index + 1));
                }
            }
            //check if all constants are unique
            for (int i = 0; i < constants.length; i++) {
                for (int j = 0; j < constants.length; j++) {
                    if (i != j) {
                        if (Objects.equals(constants[i], constants[j])) {
                            System.out.println(constants[i] + " " + constants[j]);
                            System.out.println("third");
                            return false;
                        }
                    }
                }
            }
            //get map
            String[] map = new String[lines.length - 11];
            for (int i = 11; i < lines.length; i++) {
                map[i - 11] = lines[i];
            }
            //check if the map is valid
            return checkAndLoadTXTmap(map, constants, file.getName());
        } else {
            System.out.println("v_false");
            return false;
        }
    }

    private boolean checkAndLoadTXTmap(String[] lines, String[] constants, String filename) {
        //check if the map is valid

        //check if all map lines are the same length
        int lineLength = lines[0].length();
        for (String line : lines) {
            if (line.length() != lineLength) {
                return false;
            }
        }
        //create one String from all lines, with row separator
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
            sb.append(constants[0]);
        }
        String map = sb.toString();

        //replace all row separators with standard row separator
        map = map.replaceAll(Pattern.quote(constants[0]), Constants.MAP_ROW_SEPARATOR);

        //replace all column separators with standard column separator
        map = map.replaceAll(Pattern.quote(constants[1]), Constants.MAP_COLUMN_SEPARATOR);

        //check if all map codes have the same number of characters (4)
        String[] rows = map.split(Constants.MAP_ROW_SEPARATOR);
        for (String row : rows) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String code : subRows) {
                if (code.length() != 4) {
                    System.out.println("first");
                    return false;
                }
            }
        }

        Character[] customCodes = {constants[2].charAt(0), constants[3].charAt(0), constants[4].charAt(0), constants[5].charAt(0)};
        Character[] defaultCodes = {Constants.FLOOR, Constants.WALL, Constants.INTERACTIVE_OBJECT, Constants.RANDOM};
        String[] customTwoLetterCodes = {constants[6], constants[7], constants[8], constants[9], constants[10]};
        String[] defaultTwoLetterCodes = {Constants.WAGON_DOOR, Constants.LOCKABLE_DOOR, Constants.CHEST_OBJECT, Constants.ENEMY_SPAWN, Constants.TRAP};

        //replace custom codes (ind: 0, 2 & 3) in map with standard codes; ignore second character (height, ind: 1)
        for (int i = 0; i < rows.length; i++) {
            String[] subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
            for (int j = 0; j < subRows.length; j++) {
                String objectTypeCode = String.valueOf(subRows[j].charAt(0));
                String heightCode = String.valueOf(subRows[j].charAt(1));
                String twoLetterCode = subRows[j].substring(2, 4);

                for (int k = 0; k < customCodes.length; k++) {
                    objectTypeCode = objectTypeCode.replaceAll(String.valueOf(customCodes[k]), String.valueOf(defaultCodes[k]));
                }

                for (int k = 0; k < customTwoLetterCodes.length; k++) {
                    twoLetterCode = twoLetterCode.replaceAll(customTwoLetterCodes[k], defaultTwoLetterCodes[k]);
                }

                subRows[j] = objectTypeCode + heightCode + twoLetterCode;
            }
            rows[i] = String.join(Constants.MAP_COLUMN_SEPARATOR, subRows);
        }

        System.out.println(rows[4]);

        //check if all map codes are valid (are in Constant dictionaries)
        for (String row : rows) {
            String[] subRows = row.split(Constants.MAP_COLUMN_SEPARATOR);
            for (String subRow : subRows) {
                if (!List.of(Constants.ALLOWED_CODES).contains(subRow.charAt(0))) {
                    System.out.println(subRow.charAt(0));
                    System.out.println("second");
                    return false;
                }
                if (!List.of(Constants.ALLOWED_HEIGHTS).contains(subRow.charAt(1))) {
                    System.out.println("third");
                    return false;
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
        sb = new StringBuilder();
        for (String subRow : rows) {
            sb.append(subRow);
            sb.append("\n");
        }
        map = sb.toString();

        //load the map

        String path = "maps/custom_maps/" + filename;

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
