package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.util.List;
import java.util.Map;

public class Constants {
    public static final int PLAYER_START_POS_X = 0;
    public static final int PLAYER_START_POS_Y = 0;
    public static final int PLAYER_BASIC_SPEED_X = 7;
    public static final int PLAYER_BASIC_SPEED_Y = 7;
    public static final int PLAYER_BASIC_DAMAGE = 5;
    public static final int PLAYER_HITBOX = 1;
    public static final int PLAYER_MAX_HEALTH = 8;//100
    public static final int PLAYER_MAX_HUNGER = 100;
    public static final int PLAYER_INVENTORY_SIZE = 20;
    public static final int ENTITY_BASIC_MAX_HEALTH = 100;
    public static final int ENTITY_BASIC_SPEED_X = 3;
    public static final int ENTITY_BASIC_SPEED_Y = 3;
    public static final int ENEMY_BASIC_MAX_HEALTH_MIN = 70;
    public static final int ENEMY_BASIC_MAX_HEALTH_MAX = 230;
    public static final int ENEMY_BASIC_HEIGHT = 2;
    public static final int ENEMY_BASIC_SPEED_X_MAX = 3;
    public static final int ENEMY_BASIC_SPEED_X_MIN = ENTITY_BASIC_SPEED_X;
    public static final int ENEMY_BASIC_SPEED_Y_MAX = 3;
    public static final int ENEMY_BASIC_SPEED_Y_MIN = ENTITY_BASIC_SPEED_Y;
    public static final int ENEMY_BASIC_HITBOX = 1;
    public static final int ENEMY_BASIC_ATTACK_RANGE = 1;
    public static final int ENEMY_BASIC_DAMAGE = PLAYER_BASIC_DAMAGE - 1;
    public static final int ENEMY_BASIC_COOLDOWN = 2;
    public static final int TRAIN_WAGONS = 10;
    public static final int INVENTORY_MAX_WIDTH = 10;
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 800;
    public static final int SLIP_X = 10;
    public static final int SLIP_Y = 10;

    //!!! should consider creating a map editor, maybe with color coding

    //string constants for interactive objects and spawns
    public static final String WAGON_DOOR = "WD";
    public static final String LOCKABLE_DOOR = "LD";
    public static final String CHEST_OBJECT = "CO";
    public static final String ENEMY_SPAWN = "EN";
    public static final String TRAP = "TR";
    //string constants for wagon types
    //string constants for entity's behaviour
    public static final String NEUTRAL = "NEUTRAL";
    public static final String AGGRESSIVE = "AGGRESSIVE";
    //string constants for entity's type
    public static final String PLAYER = "PLAYER";
    public static final String ENEMY = "ENEMY";
    public static final String NPC = "NPC";
    //char constants for map's int codes
    public static final String MAP_COLUMN_SEPARATOR = "_";
    public static final String MAP_ROW_SEPARATOR = "-";
    public static final Character RANDOM = '3';
    public static final Character FLOOR = '0';
    public static final Character WALL = '1';
    public static final Character INTERACTIVE_OBJECT = '2';
    public static final Character[] ALLOWED_CODES = {FLOOR, WALL, INTERACTIVE_OBJECT};
    public static final Character FLOOR_HEIGHT = '0';
    public static final Character MIN_WALL_HEIGHT = '1';
    public static final Character[] ALLOWED_HEIGHTS = {'0', '1', '2', '3'};
    public static final int INVENTORY_LEFT_CORNER_X = 300;
    public static final int INVENTORY_LEFT_CORNER_Y = 100;

    public static final int SLOT_SIZE = 64;
    public static final int SLOT_GAP = 20;
    //consider enum
    public static final Map <String, String> OBJECT_IDS = Map.of(
            "BB", "0.png",
            "BG", "0.png",
            "TF", "tile_floor.png",
            "BW", "0.png",
            "SW", "1h_wall.png",
//            "HW", "block_wagon_wall.png",
            "WW", "3h_block.png",
            "SS", "seat_1.png",
            "CA", "carpet_1.png",
            ENEMY_SPAWN, "block_wall.png",
            TRAP, "block_wall.png"
    );

    public static final Map <String, String> OBJECT_NAMES = Map.of(
            "BB", "blank_0",
            "BG", "blank_gap_0",
            "TF", "floor_0",
            "BW", "blank_wall_3",
            "SW", "1h_wall",
            "HW", "wagon_wall_2",
            "WW", "wagon_wall_3",
            "SS", "seat",
            ENEMY_SPAWN, "enemy_spawn",
            TRAP, "trap"
    );
    public static final Map <String, String> INTERACTIVE_OBJECTS = Map.of(
            WAGON_DOOR, "wagon_door.png",//wagon_door.png
            LOCKABLE_DOOR, "lockable_door_closed.png",
            CHEST_OBJECT, "chest_object_1.png"//chest_object.png
    );
    public static final Map <String, String> INTERACTIVE_OBJECTS_NAMES = Map.of(
            WAGON_DOOR, "wagon_door",
            LOCKABLE_DOOR, "lockable_door",
            CHEST_OBJECT, "chest_object"
    );
    public static final Map <String, String[]> WAGON_TYPE_ENTITIES = Map.of(
            "DEFAULT", new String[]{"zombie"},//new String[]{"zombie", "robot"},
            "COMPARTMENT", new String[]{"zombie", "robot", "bully"},
            "RESTAURANT", new String[]{"gentleman"}
    );

    public static final Map <List<String>, String> CRAFT_RECIPES = Map.of(
            List.of("block", "seat"), "box"
    );

    public static final Map <String, String> ITEM_TEXTURES = Map.of(
            "block", "block_wall.png",
            "seat", "seat_1.png",
            "box", "chest_object_1.png"
    );
}