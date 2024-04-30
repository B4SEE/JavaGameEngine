package cs.cvut.fel.pjv.gamedemo.common_classes;

import cs.cvut.fel.pjv.gamedemo.engine.EntitiesCreator;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Provide a single source of truth for all constants used in the game.
 */
public class Constants {

    //region Basic
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 800;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    //endregion

    //region Map
    public static final String MAP_COLUMN_SEPARATOR = "_";
    public static final String MAP_ROW_SEPARATOR = "-";
    public static final Character FLOOR = '0';
    public static final Character WALL = '1';
    public static final Character INTERACTIVE_OBJECT = '2';
    public static final Character RANDOM = '3';
    public static final Character[] ALLOWED_CODES = {FLOOR, WALL, INTERACTIVE_OBJECT};
    public static final Character[] ALLOWED_HEIGHTS = {'0', '1', '2', '3'};
    //endregion

    //region Objects
    public static final String WAGON_DOOR = "WD";
    public static final String LOCKABLE_DOOR = "LD";
    public static final String CHEST_OBJECT = "CO";
    public static final String ENEMY_SPAWN = "EN";
    public static final String NPC_SPAWN = "NP";
    public static final String VENDOR_SPAWN = "VE";
    public static final String QUEST_SPAWN = "QU";
    public static final String TRAP = "TR";
    public static final Map <String, String> OBJECT_IDS = Map.ofEntries(
            Map.entry("TF", "textures/default/objects/normal_objects/floor/tile_floor.png"),
            Map.entry("BW", "0.png"),
            Map.entry("SW", "textures/default/objects/normal_objects/walls/1h_wall.png"),
            Map.entry("HW", "textures/default/objects/normal_objects/walls/block_wagon_wall.png"),
            Map.entry("WW", "textures/default/objects/normal_objects/walls/3h_block.png"),
            Map.entry("SS", "textures/default/objects/normal_objects/decorations/seat_1.png"),
            Map.entry("CA", "textures/default/objects/normal_objects/floor/carpet_1.png"),
            Map.entry(ENEMY_SPAWN, "textures/default/objects/normal_objects/floor/tile_floor.png"),
            Map.entry(NPC_SPAWN, "textures/default/objects/normal_objects/floor/tile_floor.png"),
            Map.entry(VENDOR_SPAWN, "textures/default/objects/normal_objects/floor/tile_floor.png"),
            Map.entry(QUEST_SPAWN, "textures/default/objects/normal_objects/floor/tile_floor.png"),
            Map.entry(TRAP, "textures/default/objects/normal_objects/walls/block_wall.png")
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
            Map.entry(WAGON_DOOR, "textures/default/objects/interactive_objects/wagon_door/wagon_door_1.png"),
            Map.entry(LOCKABLE_DOOR, "textures/default/objects/interactive_objects/lockable_door/lockable_door_1_closed.png"),
            Map.entry(CHEST_OBJECT, "textures/default/objects/interactive_objects/chest_object/chest_object_1.png")
    );
    public static final Map <String, String> INTERACTIVE_OBJECTS_NAMES = Map.ofEntries(
            Map.entry(WAGON_DOOR, "wagon_door"),
            Map.entry(LOCKABLE_DOOR, "lockable_door"),
            Map.entry(CHEST_OBJECT, "chest_object")
    );
    //endregion

    //region Traps
    public static final int TIME_TO_ESCAPE_TRAP = 5;
    public static final int MAX_TIME_LOOP_COUNTER = 10;
    public static final int TIME_TO_ESCAPE_SILENCE = 15;
    public static final int MAX_TRAP_REWARD = 70;
    public static final int MIN_TRAP_REWARD = 10;
    public static final int MIN_TRAP_ENEMIES_COUNT = 1;
    public static final int MAX_TRAP_ENEMIES_COUNT = 5;
    public enum Event {
        DEFAULT_EVENT,
        TRAP_EVENT,
        BOSS_EVENT,
        SILENCE_EVENT,
        TIME_LOOP_EVENT
    }
    //endregion

    //region Movement
    public static final int SLIP_X = 5;
    public static final int SLIP_Y = 5;
    //endregion

    //region Entity
    public static final int ENTITY_BASIC_MAX_HEALTH = 100;
    public static final int ENTITY_BASIC_SPEED_X = 3;
    public static final int ENTITY_BASIC_SPEED_Y = 3;
    public static final int MAX_PREV_POS_LIST_SIZE = 10;
    public static final int MAX_COUNTER = 20;
    public enum EntityType {
        PLAYER,
        ENEMY,
        NPC,
        VENDOR,
        QUEST_NPC,
        CONDUCTOR,
        BOSS,
        GUARD
    }
    public enum Behaviour {
        NEUTRAL,
        AGGRESSIVE,
        BULLY,
        COWARD//will be implemented later
    }
    //    public static final String[] QUEST_NPC_NAMES = {"ticketless_elder", "lost_toy_lady", "eco_freak"};
    public static final String[] QUEST_NPC_NAMES = {"lost_toy_lady"};
    public static final String CONDUCTOR = "conductor";
    public static final String GRANDMOTHER = "grandmother";
//    public static final String GUARD = "guard";
    public static final String ZOMBIE = "zombie";
    public static final String BULLY = "bully";
    public static final String ROBOT = "robot";
    public static final String GENTLEMAN = "gentleman";
    //endregion

    //region Player
    public static final String PLAYER_TEXTURE_PATH = "textures/default/entities/player/player_front.png";
    public static final int PLAYER_START_POS_X = 350;
    public static final int PLAYER_START_POS_Y = 200;
    public static final int PLAYER_BASIC_SPEED_X = 7;
    public static final int PLAYER_BASIC_SPEED_Y = 7;
    public static final int PLAYER_BASIC_DAMAGE = 5;
    public static final int PLAYER_HITBOX = 1;
    public static final int PLAYER_ATTACK_RANGE = 1;
    public static final int PLAYER_COOLDOWN = 2;
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final int PLAYER_MAX_HUNGER = 100;
    public static final int PLAYER_INVENTORY_SIZE = 20;
    //endregion

    //region Enemy
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
    //endregion

    //region Boss
    public static final int BOSS_BASIC_HEIGHT = 3;
    public static final int BOSS_BASIC_SPEED_X = 0;
    public static final int BOSS_BASIC_SPEED_Y = 0;
    public static final String BEHEMOTH = "Behemoth";
    public static final String BEHEMOTH_DEATH_MESSAGE = "You were absorbed by the Behemoth.";
    public static final String BARTENDER = "Bartender";
    public static final String BARTENDER_DEATH_MESSAGE = "You were poisoned by the Bartender.";
    public static final Map <String, int[]> BOSS_STATS = Map.ofEntries(
            Map.entry(BEHEMOTH, new int[]{1000, 50, 3, 3, 3}),
            Map.entry(BARTENDER, new int[]{500, 30, 2, 2, 2})
    );
    public static final Map <String, String> BOSS_DEATH_MESSAGES = Map.ofEntries(
            Map.entry(BEHEMOTH, BEHEMOTH_DEATH_MESSAGE),
            Map.entry(BARTENDER, BARTENDER_DEATH_MESSAGE)
    );
    //endregion

    //region Inventory & Items
    public static final int INVENTORY_MAX_WIDTH = 10;
    public static final int MIN_KEY_VALUE = 1;
    public static final int MAX_KEY_VALUE = 10;
    public static final int MIN_TICKET_VALUE = 500;
    public static final int MAX_TICKET_VALUE = 1000;
    public static final int INVENTORY_LEFT_CORNER_X = 300;
    public static final int INVENTORY_LEFT_CORNER_Y = 100;
    public static final int SLOT_SIZE = 64;
    public static final int SLOT_GAP = 20;
    public enum ItemType {
        FOOD,
        MELEE_WEAPON,
        FIREARM,
        DEFAULT,
        VALID_TICKET,
        INVALID_TICKET,
        KEY
    }
    //endregion

    //region Train
    public static final int TRAIN_WAGONS = 10;
    public static final int NULL_WAGON_ID = -1;
    public static final String DEFAULT_WAGON_TYPE = "DEFAULT";
    public static final String COMPARTMENT_WAGON_TYPE = "COMPARTMENT";
    public static final String RESTAURANT_WAGON_TYPE = "RESTAURANT";
    public static final String SLEEPER_WAGON_TYPE = "SLEEPER";
    public static final String CARGO_WAGON_TYPE = "CARGO";

    //    public static final String[] WAGON_TYPES = {CARGO_WAGON_TYPE, RESTAURANT_WAGON_TYPE, SLEEPER_WAGON_TYPE, COMPARTMENT_WAGON_TYPE, DEFAULT_WAGON_TYPE};
    public static final String[] WAGON_TYPES = {DEFAULT_WAGON_TYPE};
    public static final Map <String, Method[]> WAGON_TYPE_ENEMIES;
    static {
        try {
            WAGON_TYPE_ENEMIES = Map.ofEntries(
                    Map.entry(DEFAULT_WAGON_TYPE, new Method[]{EntitiesCreator.class.getMethod("createZombie"), EntitiesCreator.class.getMethod("createBully"), EntitiesCreator.class.getMethod("createRobot"), EntitiesCreator.class.getMethod("createGentleman")}),
                    Map.entry(COMPARTMENT_WAGON_TYPE, new Method[]{EntitiesCreator.class.getMethod("createZombie"), EntitiesCreator.class.getMethod("createBully"), EntitiesCreator.class.getMethod("createGentleman")}),
                    Map.entry(RESTAURANT_WAGON_TYPE, new Method[]{EntitiesCreator.class.getMethod("createGentleman")}),
                    Map.entry(SLEEPER_WAGON_TYPE, new Method[]{EntitiesCreator.class.getMethod("createZombie"), EntitiesCreator.class.getMethod("createBully"), EntitiesCreator.class.getMethod("createGentleman")}),
                    Map.entry(CARGO_WAGON_TYPE, new Method[]{EntitiesCreator.class.getMethod("createZombie"), EntitiesCreator.class.getMethod("createRobot")})
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static final Map <String, String[]> WAGON_TYPE_NPC = Map.ofEntries(
            Map.entry(DEFAULT_WAGON_TYPE, new String[]{"gentleman"}),
            Map.entry(COMPARTMENT_WAGON_TYPE, new String[]{"gentleman", "lady", "child"}),
            Map.entry(RESTAURANT_WAGON_TYPE, new String[]{"gentleman", "lady"}),
            Map.entry(SLEEPER_WAGON_TYPE, new String[]{"gentleman", "lady", "child"}),
            Map.entry(CARGO_WAGON_TYPE, new String[]{"gentleman"})
    );
    public static final Map <String, String> WAGON_TYPES_BOSSES = Map.ofEntries(
            Map.entry(CARGO_WAGON_TYPE, BEHEMOTH),
            Map.entry(RESTAURANT_WAGON_TYPE, BARTENDER)
    );
    //endregion

    //region Chances
    public static final int LOCKED_DOOR_SPAWN_CHANCE = 15;
    //endregion
}