package cs.cvut.fel.pjv.gamedemo.engine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Atmospheric {
    private static Clip clip;
    private static float normalVolume;
    public static void updateBackgroundMusic() {
        int chance = (int) (Math.random() * 100);
        //check if music is playing
        if (clip != null) {
            return;//do not play music if it is already playing
        }
        if (chance < 100) {
            System.out.println("Atmospheric music is playing.");
            //get random atmospheric music
            File musicFile = RandomHandler.getRandomMusicFile("background/");
            //play music
            playMusic(musicFile);
            normalVolume = getVolume();
        }
    }
    public static void playMusic(File musicFile) {
        try {
            System.out.println(musicFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void fadeOutMusic(float minVolume) {
        //fade out music with parallel thread (to avoid pausing the game)
        new Thread(() -> {
            float volume = getVolume();
            while (volume > minVolume) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                volume -= 0.005f;
                setVolume(volume);
            }
            System.out.println("Music faded out.");
        }).start();
    }
    private static float getVolume() {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }
    private static void setVolume(float volume) {
        if (volume < 0f || volume > 1f)
            return;
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            try {
                gainControl.setValue(20f * (float) Math.log10(volume));
            } catch (Exception e) {
                clip.stop();
                clip.close();
                clip = null;
            }
        }
    }
    public static void getNormalVolume() {
        setVolume(normalVolume);
    }
    public static void resetVolume() {
        if (clip != null) {
            if (normalVolume < getVolume()) {
                new Thread(() -> {
                    float volume = getVolume();
                    while (volume > normalVolume) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        volume -= 0.01f;
                        setVolume(volume);
                    }
                    System.out.println("Music faded out.");
                }).start();
            } else {
                new Thread(() -> {
                    float volume = getVolume();
                    while (volume < normalVolume) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        volume += 0.01f;
                        setVolume(volume);
                    }
                    System.out.println("Music faded in.");
                }).start();
            }
        }
    }
}
