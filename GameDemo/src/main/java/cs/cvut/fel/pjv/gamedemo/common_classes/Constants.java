package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.util.Map;

public class Constants {
    public static final int PLAYER_START_POS_X = 0;
    public static final int PLAYER_START_POS_Y = 0;
    public static final int PLAYER_BASIC_SPEED_X = 5;
    public static final int PLAYER_BASIC_SPEED_Y = 5;
    public static final int PLAYER_BASIC_DAMAGE = 5;
    public static final int PLAYER_HITBOX = 1;
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final int PLAYER_MAX_HUNGER = 100;
    public static final int PLAYER_INVENTORY_SIZE = 15;
    public static final int ENTITY_BASIC_SPEED_X = 4;
    public static final int ENTITY_BASIC_SPEED_Y = 4;
    public static final int TRAIN_WAGONS = 10;

    public static final Map <String, String> OBJECT_IDS = Map.of(
            "BB", "0.png",
            "BG", "0.png",
            "TF", "tile_floor.png",
            "BW", "0.png",
            "SW", "block_wall.png",
            "HW", "block_wagon_wall.png",
            "WW", "3h_block.png"
    );

    public static final Map <String, String> OBJECT_NAMES = Map.of(
            "BB", "blank_0",
            "BG", "blank_gap_0",
            "TF", "floor_0",
            "BW", "blank_wall_3",
            "SW", "wall_1",
            "HW", "wagon_wall_2",
            "WW", "wagon_wall_3"
    );
}