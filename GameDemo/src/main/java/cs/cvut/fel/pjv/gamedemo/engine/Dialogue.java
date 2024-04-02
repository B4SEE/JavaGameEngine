package cs.cvut.fel.pjv.gamedemo.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Dialogue {//answer types: 1 - negative, 2 - fight, 3 - trade, 4 - check
    private String action;
    private String dialoguePath;
    private JsonNode dialoguesNode;
    private JsonNode currentDialogueNode;
    private JsonNode previousDialogueNode;
    private JsonNode nextDialogueNode;
    private JsonNode optionsNode;
    private JsonNode currentOptionNode;
    private Scene scene;
    private Pane grid = new Pane();
    private Entity entity;

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Dialogue(String dialoguePath) {
        this.dialoguePath = dialoguePath;
    }
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    public Scene openDialogue() {
        loadDialogues();
        scene = new Scene(grid);
        setSelectHandlers();
        updateDialogue();
        return scene;
    }
    public void closeDialogue() {
        action = null;
        dialoguesNode = null;
        currentDialogueNode = null;
        nextDialogueNode = null;
        optionsNode = null;
        currentOptionNode = null;
        scene = null;
        grid = null;
    }
    private void updateDialogue() {
        grid.getChildren().clear();
        nextDialogueNode = null;
        drawDialogue();
    }
    private void drawDialogue() {
        drawBackground();
        if (entity != null) {
            drawEntityPortrait();
        }
        drawMainText();
        drawOptions();
    }
    private void drawBackground() {
        grid.setPrefSize(1600, 800);
        grid.setStyle("-fx-background-color: black");
    }
    private void drawEntityPortrait() {
        Image image = new Image(entity.getTexturePath());
        ImageView imageView = new ImageView(image);
        imageView.setX(100);
        imageView.setY(100);
        grid.getChildren().add(imageView);
    }
    private void drawMainText() {
        if (currentDialogueNode == null) {
            return;
        }
        Text text = new Text(currentDialogueNode.get("text").asText());
        text.setStyle("-fx-font-size: 20; -fx-fill: white");
        text.setWrappingWidth(1000);
        text.setX(300);
        text.setY(100);
        if (currentOptionNode != null) {
            Text text2 = new Text("You: " + currentOptionNode.get("text").asText());
            text2.setStyle("-fx-font-size: 20; -fx-fill: white");
            text2.setWrappingWidth(1000);
            text2.setX(300);
            text2.setY(100);
            //player will see his answer and npc's response
            text.setY(100 + text2.getLayoutBounds().getHeight() + 50);
            grid.getChildren().add(text2);
            System.out.println("option text: " + currentOptionNode.get("text").asText());
        }
        grid.getChildren().add(text);
    }
    private void drawOptions() {
        for (int i = 0; i < optionsNode.size(); i++) {
            Text text = new Text((i+1) + " | " + optionsNode.get(i).get("text").asText());
            text.setStyle("-fx-font-size: 20; -fx-fill: white");
            text.setWrappingWidth(1300);
            text.setX(300);
            text.setY(300 + i * 50);
            grid.getChildren().add(text);
        }
    }

    private void setSelectHandlers() {
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    currentDialogueNode = previousDialogueNode;
                    currentOptionNode = null;
                    if (currentDialogueNode == null) {
                        return;
                    }
                    optionsNode = currentDialogueNode.get("options");
                    updateDialogue();
                    break;
                case DIGIT1:
                    selectOption(0);
                    break;
                case DIGIT2:
                    selectOption(1);
                    break;
                case DIGIT3:
                    selectOption(2);
                    break;
                case DIGIT4:
                    selectOption(3);
                    break;
                case DIGIT5:
                    selectOption(4);
                    break;
                case DIGIT6:
                    selectOption(5);
                    break;
                case DIGIT7:
                    selectOption(6);
                    break;
                case DIGIT8:
                    selectOption(7);
                    break;
                case DIGIT9:
                    selectOption(8);
                    break;
                case DIGIT0:
                    selectOption(9);
                    break;
            }
            if (nextDialogueNode != null) {
                System.out.println("next dialogue");
                previousDialogueNode = currentDialogueNode;
                currentDialogueNode = nextDialogueNode;
                optionsNode = currentDialogueNode.get("options");
                updateDialogue();
            }
            System.out.println("no next dialogue");
        });
    }

    private void loadDialogues() {
        //check path
        if (dialoguePath == null) {
            return;
        }
        if (dialoguePath.equals("")) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("dialogues/" + dialoguePath));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            dialoguesNode = rootNode.get("dialogues");
            currentDialogueNode = dialoguesNode.get(0);
            optionsNode = currentDialogueNode.get("options");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void selectOption(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= optionsNode.size()) {
            return;
        }
        System.out.println("selected option " + optionIndex);
        currentOptionNode = optionsNode.get(optionIndex);
        nextDialogueNode = currentOptionNode.get("nextDialogue");//null if no next dialogue
        if (nextDialogueNode != null) {
            //get dialogue by id
            for (JsonNode dialogueNode : dialoguesNode) {
                if (dialogueNode.get("id").asText().equals(nextDialogueNode.asText())) {
                    nextDialogueNode = dialogueNode;
                    break;
                }
            }
        }
        if (currentOptionNode.has("action")) {
            action = currentOptionNode.get("action").asText();
        }
    }
}
