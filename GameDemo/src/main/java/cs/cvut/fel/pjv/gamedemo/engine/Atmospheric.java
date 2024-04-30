package cs.cvut.fel.pjv.gamedemo.engine;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

public class Atmospheric {
    private static final Logger logger = LogManager.getLogger(Atmospheric.class);
    private static MediaPlayer backgroundMusic;
    private static boolean bossMusicPlaying = false;
    private static boolean pauseScreenMusicPlaying = false;
    private static MediaView viewer = new MediaView();
    private static final double normalVolume = 0.5;
    public static void updateBackgroundMusic() {
        logger.debug("Updating background music...");
        int chance = (int) (Math.random() * 100);
        //check if music is playing
        if (backgroundMusic != null) {
            logger.debug("Music is already playing");
            return;//do not play music if it is already playing
        }
        if (chance < 100) {
            //get random atmospheric music
            List<File> musicFiles = RandomHandler.getAllFilesFromDirectory("game_resources/sounds/background/");
            logger.debug("Background music files found: " + musicFiles.size());
            musicFiles.addAll(RandomHandler.getAllFilesFromDirectory("game_resources/sounds/pauseScreen/"));//take music files from pause screen music too
            logger.debug("Pause screen music files found: " + musicFiles.size());
            if (!musicFiles.isEmpty()) {
                File randomMusicFile = musicFiles.get((int) (Math.random() * musicFiles.size()));
                playMusic(randomMusicFile, 0);
                logger.info("Atmospheric background music is now playing.");
                return;
            }
            logger.error("No music files found.");
        }
    }
    public static void playMusic(File musicFile, int loops) {
        try {
            logger.debug("Now playing music: " + musicFile.getName());
            Media sound = new Media(musicFile.toURI().toString());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setVolume(0.00);
            backgroundMusic.setCycleCount(loops);
            backgroundMusic.play();
            //fade in music
            resetVolume();
        } catch (Exception e) {
            logger.error("Error while playing music: " + musicFile.getName());
        }
    }
    public static void fadeOutMusic(double minVolume) {
        //fade out music with parallel thread (to avoid pausing the game)
        if (backgroundMusic == null) {
            return;
        }
        new Thread(() -> {
            double volume = getVolume();
            while (volume > minVolume) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    logger.error("Error while fading out music.");
                }
                volume -= 0.005;
                setVolume(volume);
            }
            if (minVolume == 0.00) {
                resetAll();
            }
            logger.debug("Music faded out.");
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
                            logger.error("Error while fading in music.");
                        }
                        volume[0] -= 0.007;
                        setVolume(volume[0]);
                    }
                    logger.debug("Music faded in.");
                }).start();
            } else {
                new Thread(() -> {
                    while (volume[0] < normalVolume) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            logger.error("Error while fading in music.");
                        }
                        volume[0] += 0.007;
                        setVolume(volume[0]);
                    }
                    logger.debug("Music faded in.");
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
            logger.debug("Playing sound: " + pathFile);
            File soundFile = new File(pathFile);
            Media sound = new Media(soundFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(normalVolume);
            viewer.setMediaPlayer(mediaPlayer);//to avoid garbage collection
            mediaPlayer.play();
        }).start();
    }
    public static void startBossMusic(String bossName) {
        //play boss music
        if (bossMusicPlaying) {
            return;
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        bossMusicPlaying = true;
        File musicFile = RandomHandler.getRandomMusicFile("bosses/" + bossName + "/");
        playMusic(musicFile, 30);
    }
    public static void stopBossMusic() {
        if (bossMusicPlaying) {
            fadeOutMusic(0.00);
            bossMusicPlaying = false;
        }
    }
    public static void playPauseScreenMusic() {
        if (pauseScreenMusicPlaying) {
            return;
        }
        if (bossMusicPlaying) {
            return;//do not play pause screen music if boss music is playing
        }
        if (backgroundMusic != null) {
            fadeOutMusic(0.00);
        }
        pauseScreenMusicPlaying = true;
        //play with delay
        Thread pauseScreenMusicThread = new Thread(() -> {
            try {
                Thread.sleep(7000);//delay
            } catch (InterruptedException e) {
                logger.error("Error while playing pause screen music.");
            }
            if (pauseScreenMusicPlaying) {
                logger.debug("Playing pause screen music...");
                File musicFile = RandomHandler.getRandomMusicFile("pauseScreen/");
                playMusic(musicFile, 30);
            }
        });
        pauseScreenMusicThread.start();
    }
    public static void stopPauseScreenMusic() {
        if (pauseScreenMusicPlaying) {
            pauseScreenMusicPlaying = false;
        }
        if (backgroundMusic != null && !bossMusicPlaying) {
            fadeOutMusic(0.00);
        }
    }
}
