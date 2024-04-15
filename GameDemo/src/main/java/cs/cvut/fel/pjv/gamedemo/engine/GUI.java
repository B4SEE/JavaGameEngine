package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class GUI {
    public static Scene pauseScreen;
    public static Button resumeButton;
    public static Button exitButton;
    public static Button saveButton;
    public static Button mainMenuButton;
    public static Scene confirmScene;
    public static Button confirmButton;
    public static Button cancelButton;
    public static Scene mainMenuScene;
    public static Button startNewGameButton;
    public static Button loadGameButton;
    public static Button mapLoadButton;

    /**
     * Initialize the death screen
     * @param deathMessage message to display on the death screen
     */
    public static void initDeathScreen(String deathMessage) {
        initConfirmScene();
        initDeathScreenLabels(deathMessage);
        initDeathScreenButtons();
    }

    /**
     * Initialize the pause screen
     */
    public static void initPauseScreen() {
        Pane grid = loadFXML("gui_layouts/pauseScreen.fxml");

        initPauseButtons(grid);
        initPauseLabels(grid);

        pauseScreen = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    /**
     * Load FXML file
     * @param path path to the FXML file
     * @return loaded FXML file
     */
    private static Pane loadFXML(String path) {
        Pane grid = new Pane();
        try {
            File fxmlFile = new File(path);
            URL fxmlUrl = fxmlFile.toURI().toURL();
            grid = FXMLLoader.load(fxmlUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grid;
    }

    /**
     * Initialize the confirm screen
     */
    public static void initConfirmScene() {
        Pane grid = loadFXML("gui_layouts/confirmScreen.fxml");

        initConfirmButtons(grid);
        initConfirmLabels(grid);

        confirmScene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    /**
     * Initialize the game saved screen (use only after initConfirmScene())
     */
    public static void initGameSavedScreen() {
        initGameSavedButtons();
        initGameSavedLabels();
    }

    /**
     * Initialize the game cannot be saved screen (use only after initConfirmScene())
     */
    public static void initCannotSaveGameScreen() {
        initCannotSaveButtons();
        initCannotSaveLabels();
    }

    /**
     * Initialize the pause screen buttons
     * @param grid grid to initialize the buttons
     */
    private static void initPauseButtons(Pane grid) {
        Group buttonsGroup = (Group) grid.lookup("#pauseScreenButtons");

        resumeButton = (Button) buttonsGroup.lookup("#resume");
        saveButton = (Button) buttonsGroup.lookup("#save");
        mainMenuButton = (Button) buttonsGroup.lookup("#menu");
        exitButton = (Button) buttonsGroup.lookup("#exit");

        resumeButton.setText("Resume");
        saveButton.setText("Save");
        mainMenuButton.setText("Main menu");
        exitButton.setText("Exit");
    }

    /**
     * Initialize the pause screen labels
     * @param grid grid to initialize the labels
     */
    private static void initPauseLabels(Pane grid) {
        Group labelsGroup = (Group) grid.lookup("#pauseScreenLabels");
        Label mainLabel = (Label) labelsGroup.lookup("#mainLabel");
        Label subLabel = (Label) labelsGroup.lookup("#subLabel");
        mainLabel.setText("Game paused");
        subLabel.setText("Press ESC to resume");
    }

    /**
     * Initialize the confirm screen buttons
     * @param grid grid to initialize the buttons
     */
    private static void initConfirmButtons(Pane grid) {
        Group buttonsGroup = (Group) grid.lookup("#confirmScreenButtons");

        confirmButton = (Button) buttonsGroup.lookup("#confirm");
        cancelButton = (Button) buttonsGroup.lookup("#cancel");
        confirmButton.setText("Confirm");
        cancelButton.setText("Cancel");
    }

    /**
     * Initialize the confirm screen labels
     * @param grid grid to initialize the labels
     */
    private static void initConfirmLabels(Pane grid) {
        Group labelsGroup = (Group) grid.lookup("#confirmScreenLabels");
        Label mainLabel = (Label) labelsGroup.lookup("#mainLabel");
        Label subLabel = (Label) labelsGroup.lookup("#subLabel");
        mainLabel.setText("Are you sure?");
        subLabel.setText("Press ENTER to confirm, ESC to cancel");
    }

    /**
     * Initialize the game saved screen buttons
     */
    private static void initGameSavedButtons() {
        confirmButton.setText("Resume");
        cancelButton.setText("Main menu");
    }

    /**
     * Initialize the game saved screen labels
     */
    private static void initGameSavedLabels() {
        Label mainLabel = (Label) confirmScene.lookup("#mainLabel");
        Label subLabel = (Label) confirmScene.lookup("#subLabel");
        mainLabel.setText("Game saved");
        subLabel.setText("Press ENTER to continue");
    }

    /**
     * Initialize the game cannot be saved screen buttons
     */
    private static void initCannotSaveButtons() {
        confirmButton.setText("Resume");
        cancelButton.setText("Main menu");
    }

    private static void initCannotSaveLabels() {
        Label mainLabel = (Label) confirmScene.lookup("#mainLabel");
        Label subLabel = (Label) confirmScene.lookup("#subLabel");
        mainLabel.setText("Game cannot be saved right now");
        subLabel.setText("Press ENTER to continue");
    }

    /**
     * Initialize the death screen labels
     * @param deathMessage message to display on the death screen
     */
    private static void initDeathScreenLabels(String deathMessage) {
        //use function only after initConfirmScene()
        Label mainLabel = (Label) confirmScene.lookup("#mainLabel");
        Label subLabel = (Label) confirmScene.lookup("#subLabel");
        mainLabel.setText("Game over");
        subLabel.setText(deathMessage);
    }

    /**
     * Initialize the death screen buttons
     */
    private static void initDeathScreenButtons() {
        confirmButton.setText("Load last save");
        cancelButton.setText("Main menu");
    }

    /**
     * Initialize the main menu
     */
    public static void initMainMenu() {
        Pane grid = loadFXML("gui_layouts/mainMenu.fxml");

        initMainMenuButtons(grid);
        initMainMenuLabels(grid);
        //add image

        Image image = new Image("textures/default/gui/mainMenuImage.png");
        ImageView imageView = new ImageView(image);
        imageView.setX(Constants.WINDOW_WIDTH - imageView.getImage().getWidth());
        imageView.setY(0);
        imageView.setFitWidth(imageView.getImage().getWidth());
        imageView.setFitHeight(imageView.getImage().getHeight());
        imageView.setPreserveRatio(true);//keep aspect ratio
        imageView.setSmooth(true);//better quality
        imageView.setCache(true);//cache to improve performance
        grid.getChildren().add(imageView);

        mainMenuScene = new Scene(grid, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    /**
     * Initialize the main menu labels
     * @param grid grid to initialize the labels
     */
    private static void initMainMenuLabels(Pane grid) {
        Group buttonsGroup = (Group) grid.lookup("#mainMenuButtons");

        startNewGameButton = (Button) buttonsGroup.lookup("#start");
        loadGameButton = (Button) buttonsGroup.lookup("#load");
        mapLoadButton = (Button) buttonsGroup.lookup("#map");
        exitButton = (Button) buttonsGroup.lookup("#exit");

        startNewGameButton.setText("Start new game");
        loadGameButton.setText("Load game");
        mapLoadButton.setText("Load map");
        exitButton.setText("Exit");
    }

    /**
     * Initialize the main menu buttons
     * @param grid grid to initialize the buttons
     */
    private static void initMainMenuButtons(Pane grid) {
        Label title = (Label) grid.lookup("#title");
        Group labelsGroup = (Group) grid.lookup("#mainMenuLabels");
        Label mainLabel = (Label) labelsGroup.lookup("#mainLabel");
        Label subLabel = (Label) labelsGroup.lookup("#subLabel");
        title.setText("Infinite Train Demo");
        mainLabel.setText("Main menu");
        subLabel.setText("Choose an option");
    }
}
