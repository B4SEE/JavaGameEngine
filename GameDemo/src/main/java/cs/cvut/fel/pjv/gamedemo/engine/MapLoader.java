package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class MapLoader {
    private static final Logger logger = Logger.getLogger(MapLoader.class);
    public MapLoader() {
    }

    /**
     * Load the map from the file (path)
     * @param path - path to the file
     * @return - the map
     */
    public static String load(String path) {
        File file = new File(path);
        StringBuilder map = new StringBuilder();
        try {
            java.util.Scanner scanner = new java.util.Scanner(file);
            while (scanner.hasNextLine()) {
                map.append(scanner.nextLine());
                map.append(Constants.MAP_ROW_SEPARATOR);
            }
            scanner.close();
        } catch (IOException e) {
            logger.error("Error while loading the map: " + e);
        }
        return map.toString();
    }
    /**
     * Parse the map; replace the random objects with the actual objects.
     * @param seed - the map
     */
    public static String parseMap(String seed) {
        String result = "";
        try {
            String[] rows = seed.split(Constants.MAP_ROW_SEPARATOR);
            String[] subRows;
            //3 - random; next number: 1 - chest, 2 - trap, 3 - enemy; 4 - NPC; 5 - quest; next two numbers: chance; Note: was tested and works
            for (int i = 0; i < rows.length; i++) {
                subRows = rows[i].split(Constants.MAP_COLUMN_SEPARATOR);
                for (int j = 0; j < subRows.length; j++) {
                    if (subRows[j].charAt(0) == Constants.RANDOM) {
                        int chance = Integer.parseInt(subRows[j].substring(2, 4));
                        int generate = (int) (Math.random() * 100);
                        if (chance > generate) {
                            if (subRows[j].charAt(1) == '1') {
                                // Generate the chest
                                //generate random number from 1 to 9
                                int random = (int) (Math.random() * 9 + 1);
                                String chest = "2" + random + "CO";
                                subRows[j] = chest;
                            } else if (subRows[j].charAt(1) == '2') {
                                // Generate the trap
                                String trap = "00TR";
                                subRows[j] = trap;
                            } else if (subRows[j].charAt(1) == '3') {
                                // Generate the enemy
                                String enemy = "00EN";
                                subRows[j] = enemy;
                            } else if (subRows[j].charAt(1) == '4') {
                                // Generate the NPC
                                String npc = "00NP";
                                subRows[j] = npc;
                            } else if (subRows[j].charAt(1) == '5') {
                                // Generate the quest
                                String quest = "00QU";
                                subRows[j] = quest;
                            }
                        } else {
                            subRows[j] = "00TF";
                        }
                    }
                }
                rows[i] = String.join(Constants.MAP_COLUMN_SEPARATOR, subRows);
            }
            result = String.join(Constants.MAP_ROW_SEPARATOR, rows);
        } catch (Exception e) {
            logger.error("Error while parsing the map: " + e);
        }
        return result;
    }
}
