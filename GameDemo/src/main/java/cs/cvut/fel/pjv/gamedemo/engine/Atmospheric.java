package cs.cvut.fel.pjv.gamedemo.engine;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class Atmospheric {
    private static MediaPlayer backgroundMusic;
    private static MediaView viewer = new MediaView();
    private static final double normalVolume = 0.5;
    public static void updateBackgroundMusic() {
        int chance = (int) (Math.random() * 100);
        //check if music is playing
        if (backgroundMusic != null) {
            return;//do not play music if it is already playing
        }
        if (chance < 100) {
            System.out.println("Atmospheric music is playing.");
            //get random atmospheric music
            File musicFile = RandomHandler.getRandomMusicFile("background/");
            //play music
            playMusic(musicFile);
        }
    }
    public static void playMusic(File musicFile) {
        Media sound = new Media(musicFile.toURI().toString());
        backgroundMusic = new MediaPlayer(sound);
        backgroundMusic.setVolume(normalVolume);
        backgroundMusic.play();
    }
    public static void fadeOutMusic(double minVolume) {
        //fade out music with parallel thread (to avoid pausing the game)
        new Thread(() -> {
            double volume = getVolume();
            while (volume > minVolume) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                volume -= 0.005;
                setVolume(volume);
            }
            System.out.println("Music faded out.");
        }).start();
    }
    private static double getVolume() {
        if (backgroundMusic != null) {
            return (float) backgroundMusic.getVolume();
        } else {
            return 0.00;
        }
    }
    private static void setVolume(double volume) {
        if (volume < 0.00 || volume > 1.00)
            return;
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
        }
    }
    public static void resetVolume() {
        if (backgroundMusic != null) {
            final double[] volume = {getVolume()};
            if (normalVolume < getVolume()) {
                new Thread(() -> {
                    while (volume[0] > normalVolume) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        volume[0] -= 0.01;
                        setVolume(volume[0]);
                    }
                    System.out.println("Music faded out.");
                }).start();
            } else {
                new Thread(() -> {
                    while (volume[0] < normalVolume) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        volume[0] += 0.01;
                        setVolume(volume[0]);
                    }
                    System.out.println("Music faded in.");
                }).start();
            }
        }
    }
    public static void resetAll() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic = null;
        }
    }
    public static Scene playCutScene(File videoFile) {
        Pane pane = new Pane();
        pane.setPrefSize(1600, 800);
        Media media = new Media(videoFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        MediaView mediaView = new MediaView(mediaPlayer);
        pane.getChildren().add(mediaView);
        return new Scene(pane);
    }
    public static void stopCutScene() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic = null;
        }
    }
    public static void pauseMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }
    public static void resumeMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }
    public static void playSound(String pathFile) {
        //create new Thread to play sound (to avoid pausing the game)
        new Thread(() -> {
            File soundFile = new File(pathFile);
            Media sound = new Media(soundFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(normalVolume);
            System.out.println("Playing sound: " + pathFile);
            viewer.setMediaPlayer(mediaPlayer);//to avoid garbage collection
            mediaPlayer.play();
        }).start();
    }
}
