package cs.cvut.fel.pjv.gamedemo.common_classes;

import java.util.List;
import java.util.Map;

/**
 * Provide a single source of truth for all constants used in the game.
 */
public class Constants {

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int NULL_WAGON_ID = -1;
    public static final int PLAYER_START_POS_X = 0;
    public static final int PLAYER_START_POS_Y = 0;
    public static final int PLAYER_BASIC_SPEED_X = 7;
    public static final int PLAYER_BASIC_SPEED_Y = 7;
    public static final int PLAYER_BASIC_DAMAGE = 5;
    public static final int PLAYER_HITBOX = 1;
    public static final int PLAYER_ATTACK_RANGE = 1;
    public static final int PLAYER_MAX_HEALTH = 100;//100
    public static final int PLAYER_MAX_HUNGER = 100;
    public static final int PLAYER_INVENTORY_SIZE = 20;
    public static final int ENTITY_BASIC_MAX_HEALTH = 100;
    public static final int ENTITY_BASIC_SPEED_X = 3;
    public static final int ENTITY_BASIC_SPEED_Y = 3;
    public static final int ENEMY_BASIC_MAX_HEALTH_MIN = 70;
    public static final int ENEMY_BASIC_MAX_HEALTH_MAX = 230;
    public static final int ENEMY_BASIC_HEIGHT = 2;
    public static final int ENEMY_BASIC_SPEED_X_MAX = PLAYER_BASIC_SPEED_X - 1;
    public static final int ENEMY_BASIC_SPEED_X_MIN = ENTITY_BASIC_SPEED_X;
    public static final int ENEMY_BASIC_SPEED_Y_MAX = PLAYER_BASIC_SPEED_Y - 1;
    public static final int ENEMY_BASIC_SPEED_Y_MIN = ENTITY_BASIC_SPEED_Y;
    public static final int ENEMY_BASIC_HITBOX = 1;
    public static final int ENEMY_BASIC_ATTACK_RANGE = 1;
    public static final int ENEMY_BASIC_DAMAGE = 5;
    public static final int ENEMY_BASIC_COOLDOWN = 2;
    public static final int TRAIN_WAGONS = 10;
    public static final int INVENTORY_MAX_WIDTH = 10;
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 800;
    public static final int SLIP_X = 5;
    public static final int SLIP_Y = 5;

    //!!! should consider creating a map editor, maybe with color coding

    //string constants for interactive objects and spawns
    public static final String WAGON_DOOR = "WD";
    public static final String LOCKABLE_DOOR = "LD";
    public static final String CHEST_OBJECT = "CO";
    public static final String ENEMY_SPAWN = "EN";
    public static final String NPC_SPAWN = "NP";
    public static final String VENDOR_SPAWN = "VE";
    public static final String QUEST_SPAWN = "QU";
    public static final String TRAP = "TR";
    //string constants for wagon types
    //string constants for entity's behaviour
    public enum Behaviour {
        NEUTRAL,
        AGGRESSIVE,
        COWARD
    }
    //string constants for entity's type
    public enum EntityType {
        PLAYER,
        ENEMY,
        NPC
    }
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
    //item types
    public static final String VALID_TICKET = "VALID_TICKET";
    public static final String[] WAGON_TYPES = {"COMPARTMENT", "RESTAURANT", "SLEEPER", "CARGO", "DEFAULT"};
    public static final int MAX_PREV_POS_LIST_SIZE = 10;
    public static final int MIN_TRAP_ENEMIES_COUNT = 1;
    public static final int MAX_TRAP_ENEMIES_COUNT = 5;

    public static final int MAX_COUNTER = 5;
//    public static final String[] QUEST_NPC_NAMES = {"ticketless_elder", "lost_toy_lady", "eco_freak"};
    public static final String[] QUEST_NPC_NAMES = {"lost_toy_lady"};
    public enum Event {
        DEFAULT_EVENT,
        TRAP_EVENT,
        SILENCE_EVENT,
        TIME_LOOP_EVENT
    }
    //consider enum
    public static final Map <String, String> OBJECT_IDS = Map.ofEntries(
            Map.entry("TF", "tile_floor.png"),
            Map.entry("BW", "0.png"),
            Map.entry("SW", "1h_wall.png"),
            Map.entry("HW", "block_wagon_wall.png"),
            Map.entry("WW", "3h_block.png"),
            Map.entry("SS", "seat_1.png"),
            Map.entry("CA", "carpet_1.png"),
            Map.entry(ENEMY_SPAWN, "tile_floor.png"),
            Map.entry(NPC_SPAWN, "tile_floor.png"),
            Map.entry(VENDOR_SPAWN, "tile_floor.png"),
            Map.entry(QUEST_SPAWN, "tile_floor.png"),
            Map.entry(TRAP, "block_wall.png")
    );

    public static final Map <String, String> OBJECT_NAMES = Map.ofEntries(
            Map.entry("TF", "floor_0"),
            Map.entry("BW", "blank_wall_3"),
            Map.entry("SW", "1h_wall"),
            Map.entry("HW", "wagon_wall_2"),
            Map.entry("WW", "wagon_wall_3"),
            Map.entry("SS", "seat"),
            Map.entry("CA", "carpet_1"),
            Map.entry(ENEMY_SPAWN, "enemy_spawn"),
            Map.entry(NPC_SPAWN, "npc_spawn"),
            Map.entry(VENDOR_SPAWN, "vendor_spawn"),
            Map.entry(QUEST_SPAWN, "quest_spawn"),
            Map.entry(TRAP, "trap")
    );
    public static final Map <String, String> INTERACTIVE_OBJECTS = Map.ofEntries(
            Map.entry(WAGON_DOOR, "default/objects/interactive_objects/wagon_door/wagon_door_1.png"),
            Map.entry(LOCKABLE_DOOR, "default/objects/interactive_objects/lockable_door/lockable_door_1_closed.png"),
            Map.entry(CHEST_OBJECT, "default/objects/interactive_objects/chest_object/chest_object_1.png")
    );
    public static final Map <String, String> INTERACTIVE_OBJECTS_NAMES = Map.ofEntries(
            Map.entry(WAGON_DOOR, "wagon_door"),
            Map.entry(LOCKABLE_DOOR, "lockable_door"),
            Map.entry(CHEST_OBJECT, "chest_object")
    );
    public static final Map <String, String[]> WAGON_TYPE_ENEMIES = Map.ofEntries(
            Map.entry("DEFAULT", new String[]{"zombie"}),
            Map.entry("COMPARTMENT", new String[]{"zombie", "robot", "bully"}),
            Map.entry("RESTAURANT", new String[]{"gentleman"}),
            Map.entry("SLEEPER", new String[]{"lady", "child"}),
            Map.entry("CARGO", new String[]{"zombie"})
    );

    public static final Map <String, String[]> WAGON_TYPE_NPC = Map.ofEntries(
            Map.entry("DEFAULT", new String[]{"gentleman"}),
            Map.entry("COMPARTMENT", new String[]{"gentleman", "lady", "child"}),
            Map.entry("RESTAURANT", new String[]{"gentleman", "lady"}),
            Map.entry("SLEEPER", new String[]{"gentleman", "lady", "child"}),
            Map.entry("CARGO", new String[]{"gentleman", "lady", "child"})
    );

    public static final Map <List<String>, String> CRAFT_RECIPES = Map.ofEntries(
            Map.entry(List.of("block", "seat"), "box")
    );

    public static final Map <String, String> ITEM_TEXTURES = Map.ofEntries(
            Map.entry("block", "block_wall.png"),
            Map.entry("seat", "seat_1.png"),
            Map.entry("box", "chest_object_1.png")
    );
    public static final Map <String, Integer> GAME_STATES = Map.ofEntries(
            Map.entry("default", 0),
            Map.entry("inventory", 1),
            Map.entry("dialogue", 2),
            Map.entry("trap", 3),
            Map.entry("loop", 4),
            Map.entry("silence", 5)
    );

//    public static final Map<Integer, Item> LOOT_TABLE_STANDARD = Map.ofEntries(
//            Map.entry(0, new Food("orange", "orange.png", 15)),
//            Map.entry(1, new MeleeWeapon("knife", "knife.png", 20, 1)),
//            Map.entry(2, new Item("box", "chest_object_1.png"))
//    );//luckily will be moved to .txt file
}