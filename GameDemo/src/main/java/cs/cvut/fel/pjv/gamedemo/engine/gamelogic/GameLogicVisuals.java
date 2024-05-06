package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Door;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;
import cs.cvut.fel.pjv.gamedemo.engine.*;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.utils.GUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class GameLogicVisuals {
    private static GameData gameData;

    public static void setGameData(GameData gameData) {
        GameLogicVisuals.gameData = gameData;
    }

    //region PauseScreen and MainMenu methods
    /**
     * Show the pause screen.
     */
    public static void showPauseScreen() {
        GameManagement.pauseGame();

        Atmospheric.playPauseScreenMusic();

        Scene isoScene = gameData.getStage().getScene();

        GUI.initPauseScreen();
        GUI.initConfirmScene();

        EventHandler<? super KeyEvent> onEscResume = keyEvent -> {
            if (keyEvent.getCode().toString().equals("ESCAPE")) {
                Atmospheric.stopPauseScreenMusic();
                gameData.getStage().setScene(isoScene);
                GameManagement.resumeGame();
            }
        };

        EventHandler<? super KeyEvent> onEscPause = keyEvent -> {
            if (keyEvent.getCode().toString().equals("ESCAPE")) {
                GUI.initConfirmScene();
                gameData.getStage().setScene(GUI.pauseScreen);
            }
        };

        EventHandler<? super KeyEvent> onEnterResume = keyEvent -> {
            if (keyEvent.getCode().toString().equals("ENTER")) {
                Atmospheric.stopPauseScreenMusic();
                gameData.getStage().setScene(isoScene);
                GameManagement.resumeGame();
            }
        };

        EventHandler<ActionEvent> cancel = actionEvent -> {
            GUI.initConfirmScene();
            gameData.getStage().setScene(GUI.pauseScreen);
        };

        GUI.resumeButton.setOnAction(actionEvent -> {
            Atmospheric.stopPauseScreenMusic();
            gameData.getStage().setScene(isoScene);
            GameManagement.resumeGame();
        });

        GUI.mainMenuButton.setOnAction(actionEvent -> {
            GUI.confirmButton.setOnAction(actionEvent1 -> {
                gameData.getStage().setScene(null);
                mainMenu();
            });
            GUI.cancelButton.setOnAction(cancel);
            GUI.confirmScene.setOnKeyPressed(keyEvent -> {
                onEscPause.handle(keyEvent);
                if (keyEvent.getCode().toString().equals("ENTER")) {
                    gameData.getStage().setScene(null);
                    mainMenu();
                }
            });
            gameData.getStage().setScene(GUI.confirmScene);
        });

        GUI.exitButton.setOnAction(actionEvent -> {
            GUI.confirmButton.setOnAction(actionEvent1 -> {
                gameData.getStage().close();
                System.exit(0);
            });
            GUI.cancelButton.setOnAction(cancel);
            GUI.confirmScene.setOnKeyPressed(keyEvent -> {
                onEscPause.handle(keyEvent);
                if (keyEvent.getCode().toString().equals("ENTER")) {
                    gameData.getStage().close();
                    System.exit(0);
                }
            });
            gameData.getStage().setScene(GUI.confirmScene);
        });

        GUI.saveButton.setOnAction(actionEvent -> {
            Events.setCanSaveGame(Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT && gameData.getLeftWagonPursuers().isEmpty() && gameData.getRightWagonPursuers().isEmpty());
            if (Events.canSaveGame()) {
                GameManagement.saveGame();
                GUI.initGameSavedScreen();
            } else {
                GUI.initCannotSaveGameScreen();
            }
            GUI.confirmButton.setOnAction(actionEvent1 -> {
                Atmospheric.stopPauseScreenMusic();
                gameData.getStage().setScene(isoScene);
                GameManagement.resumeGame();
            });
            GUI.cancelButton.setOnAction(actionEvent1 -> {
                gameData.getStage().setScene(null);
                mainMenu();
            });
            gameData.getStage().setScene(GUI.confirmScene);
            GUI.confirmScene.setOnKeyPressed(keyEvent -> {
                onEscPause.handle(keyEvent);
                onEnterResume.handle(keyEvent);
            });
        });

        GUI.pauseScreen.onKeyPressedProperty().set(onEscResume);
        gameData.getStage().setScene(GUI.pauseScreen);
    }

    /**
     * Show the main menu.
     */
    public static void mainMenu() {
        gameData.getIsometric().resetAll();
        Scene mainMenuScene = Game.showMainMenuScene();
        gameData.getStage().setScene(mainMenuScene);
    }
    //endregion

    //region Visual methods
    /**
     * Play the player's footsteps sound and show the player's footsteps animation.
     */
    public static void playPlayerFootsteps() {
        Player player = gameData.getPlayer();
        // Do not start a new thread if the previous one is still running
        if (player.getSoundThread() != null) {
            return;
        }

        Thread footstepLoop = new Thread(() -> {
            String prev = "_1";
            while (true) {
                String texturePath = player.getTexturePath();
                // Change texture path based on player movement
                if (!texturePath.contains("_1") && !texturePath.contains("_2")) {//if the texture is normal
                    player.setTexturePath(texturePath.replace(".png", prev + ".png"));
                    prev = (prev.equals("_1")) ? "_2" : "_1";
                } else {
                    player.setTexturePath(texturePath.contains("_1") ? texturePath.replace("_1", "_2") : texturePath.replace("_2", "_1"));
                }
                //TODO play sound
//                    Atmospheric.playSound("resources/sounds/footstep_sounds/single_footstep_sound.wav");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        player.setSoundThread(footstepLoop);
        player.getSoundThread().start();
    }

    /**
     * Stop the player's footsteps sound and show the player's normal texture.
     */
    public static void stopPlayerFootsteps() {
        Player player = gameData.getPlayer();
        if (player.getSoundThread() != null) player.getSoundThread().interrupt();
        player.setTexturePath(player.getTexturePath().replaceAll("_[12]", ""));
        player.setSoundThread(null);
    }

    /**
     * Show the hint for the player to interact with the object.
     */
    public static void showHint() {
        Object interactiveObject = Checker.checkIfCanInteract(gameData.getPlayer(), gameData.getWagon().getInteractiveObjects());
        if (interactiveObject instanceof Door door && door.isLocked()) {
            gameData.setHint((gameData.getPlayer().getHandItem() != null && gameData.getPlayer().getHandItem().getType() == Constants.ItemType.KEY) ? "Press E to unlock" : "Locked");
        } else if (interactiveObject != null) {
            gameData.setHint("Press E to open");
        } else if (Checker.checkIfPlayerCanSpeak(gameData.getPlayer(), gameData.getWagon().getEntities()) != null) {
            gameData.setHint("Press E to speak");
        } else {
            gameData.setHint("");
        }
        gameData.getIsometric().updateHint(gameData.getHint(), (int) gameData.getPlayer().getPositionX(), (int) gameData.getPlayer().getPositionY() - 25);
    }
    //endregion
}
