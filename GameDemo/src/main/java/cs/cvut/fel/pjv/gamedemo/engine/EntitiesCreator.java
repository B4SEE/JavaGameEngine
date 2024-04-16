package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EntitiesCreator {
    private static final Logger logger = LogManager.getLogger(EntitiesCreator.class);

    /**
     * Sets the entity as the default enemy.
     * Speed and health are set to random values within the specified range.
     * Note: hitbox size, attack range size, damage and cooldown are set to default values, but it will be changed in the future (randomized).
     */
    private static void setAsDefaultEnemy(Entity entity) {
        logger.info("Setting entity " + entity.getName() + " as default enemy...");
        entity.setType(Constants.EntityType.ENEMY);
        entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        entity.setInitialBehaviour(entity.getBehaviour());
        entity.setHeight(Constants.ENEMY_BASIC_HEIGHT);
        entity.setSpeedX((int) (Math.random() * (Constants.ENEMY_BASIC_SPEED_X_MAX - Constants.ENEMY_BASIC_SPEED_X_MIN) + Constants.ENEMY_BASIC_SPEED_X_MIN));
        entity.setSpeedY((int) (Math.random() * (Constants.ENEMY_BASIC_SPEED_Y_MAX - Constants.ENEMY_BASIC_SPEED_Y_MIN) + Constants.ENEMY_BASIC_SPEED_Y_MIN));
        int maxHealth = (int) (Math.random() * (Constants.ENEMY_BASIC_MAX_HEALTH_MAX - Constants.ENEMY_BASIC_MAX_HEALTH_MIN) + Constants.ENEMY_BASIC_MAX_HEALTH_MIN);
        maxHealth = (maxHealth / 10) * 10;
        entity.setMaxHealth(maxHealth);
        entity.setHealth(maxHealth);
        entity.setHitBoxSize(Constants.ENEMY_BASIC_HITBOX);
        entity.setAttackRangeSize(Constants.ENEMY_BASIC_ATTACK_RANGE);
        entity.setDamage(Constants.ENEMY_BASIC_DAMAGE);
        entity.setCooldown(Constants.ENEMY_BASIC_COOLDOWN);
        logger.info("Entity " + entity.getName() + " set as default enemy");
    }
    public static Entity createZombie() {
        logger.info("Creating zombie...");
        Entity zombie = new Entity(Constants.ZOMBIE, "zombie_front.png");
        setAsDefaultEnemy(zombie);
        zombie.setIntelligence(0);
        logger.info("Zombie created");
        return zombie;
    }
    public static Entity createRobot() {
        logger.info("Creating robot...");
        Entity robot = new Entity(Constants.ROBOT, "zombie_front.png");
        setAsDefaultEnemy(robot);
        robot.setIntelligence(2);
        logger.info("Robot created");
        return robot;
    }
    public static Entity createGentleman() {
        logger.info("Creating gentleman...");
        Entity gentleman = new Entity(Constants.GENTLEMAN, "zombie_front.png");
        setAsDefaultEnemy(gentleman);
        gentleman.setIntelligence(1);
        logger.info("Gentleman created");
        return gentleman;
    }
    /**
     * Sets the entity as the default NPC.
     * Speed and health are set to random values within the specified range.
     * Note: hitbox size, attack range size, damage and cooldown are set to default values, but it will be changed in the future (randomized).
     */
    public static void setAsDefaultNPC(Entity npc) {
        logger.info("Setting entity " + npc.getName() + " as default NPC...");
        setAsDefaultEnemy(npc);
        npc.setType(Constants.EntityType.NPC);
        npc.setBehaviour(Constants.Behaviour.NEUTRAL);
        npc.setInitialBehaviour(npc.getBehaviour());
        npc.setIntelligence((int) (Math.random() * 2));
        if (npc.getDialoguePath() == null) {
            npc.setDialoguePath(RandomHandler.getRandomDialogueThatStartsWith(npc.getName()));
        }
        npc.setNegativeThreshold((int) (Math.random() * 10 + 1));
        logger.info("Entity " + npc.getName() + " set as default NPC");
    }
    public static Entity createNPC(String type) {
        logger.info("Creating NPC...");
        String[] names = Constants.WAGON_TYPE_NPC.get(type);
        String name = names[(int) (Math.random() * names.length)];
        Entity npc = new Entity(name, "zombie_front.png");
        setAsDefaultNPC(npc);
        logger.info("NPC created");
        return npc;
    }

    public static void setAsDefaultPlayer(Player player) {
        logger.info("Setting entity " + player.getName() + " as default player...");
        player.setType(Constants.EntityType.PLAYER);
        player.setBehaviour(Constants.Behaviour.NEUTRAL);
        player.setInitialBehaviour(player.getBehaviour());
        player.setHealth(Constants.PLAYER_MAX_HEALTH);
        player.setDamage(Constants.PLAYER_BASIC_DAMAGE);
        player.setHitBoxSize(Constants.PLAYER_HITBOX);
        player.setAttackRangeSize(Constants.PLAYER_ATTACK_RANGE);
        player.setCooldown(Constants.PLAYER_COOLDOWN);
        player.setHeight(2);
        logger.info("Entity " + player.getName() + " set as default player");
    }
    public static Entity createBully() {
        logger.info("Creating bully...");
        Entity bully = new Entity(Constants.BULLY, "zombie_front.png");
        setAsDefaultEnemy(bully);
        bully.setBehaviour(Constants.Behaviour.BULLY);
        bully.setIntelligence(1);
        bully.setCooldown(4);//kidnapping cooldown
        bully.setSpeedX(Constants.PLAYER_BASIC_SPEED_X - 1);
        bully.setSpeedY(Constants.PLAYER_BASIC_SPEED_Y - 1);
        bully.setInitialBehaviour(bully.getBehaviour());
        logger.info("Bully created");
        return bully;
    }

    public static void setAsBoss(Entity entity, int[] bossStats) {
        logger.info("Setting entity " + entity.getName() + " as boss...");
        int health = bossStats[0];
        int damage = bossStats[1];
        int hitbox = bossStats[2];
        int attackRange = bossStats[3];
        int cooldown = bossStats[4];
        entity.setType(Constants.EntityType.BOSS);
        entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        entity.setInitialBehaviour(entity.getBehaviour());
        entity.setHeight(Constants.BOSS_BASIC_HEIGHT);
        entity.setSpeedX(Constants.BOSS_BASIC_SPEED_X);
        entity.setSpeedY(Constants.BOSS_BASIC_SPEED_Y);
        entity.setMaxHealth(health);
        entity.setHealth(health);
        entity.setHitBoxSize(hitbox);
        entity.setAttackRangeSize(attackRange);
        entity.setDamage(damage);
        entity.setCooldown(cooldown);
        logger.info("Entity " + entity.getName() + " set as boss");
    }
    public static Entity createGuard() {
        logger.info("Creating guard...");
        Entity guard = new Entity("guard", "zombie_front.png");
        EntitiesCreator.setAsGuard(guard);
        logger.info("Guard created");
        return guard;
    }

    public static void setAsGuard(Entity guard) {
        logger.info("Setting entity " + guard.getName() + " as guard...");
        setAsDefaultNPC(guard);
        guard.setType(Constants.EntityType.GUARD);
        if (Events.isPlayerKilledGuard()) {
            guard.setBehaviour(Constants.Behaviour.BULLY);
        }
        guard.setIntelligence(2);
        guard.setCooldown(1);
        guard.setSpeedX(Constants.PLAYER_BASIC_SPEED_X - 1);
        guard.setSpeedY(Constants.PLAYER_BASIC_SPEED_Y - 1);
        logger.info("Entity " + guard.getName() + " set as guard");
    }

    public static void createGrandmother(Wagon wagon) {
        logger.info("Creating grandmother...");
        Entity grandmother = new Vendor(Constants.GRANDMOTHER, "zombie_front.png");
        grandmother.setType(Constants.EntityType.CONDUCTOR);
        grandmother.setDialoguePath("grandmother_default.json");
        initConductor(wagon, grandmother);
        logger.info("Grandmother created");
    }
    public static void createConductor(Wagon wagon) {
        logger.info("Creating conductor...");
        Entity conductor = new Entity(Constants.CONDUCTOR, "zombie_front.png");
        EntitiesCreator.setAsDefaultNPC(conductor);
        conductor.setType(Constants.EntityType.CONDUCTOR);
        initConductor(wagon, conductor);
        logger.info("Conductor created");
    }

    private static void initConductor(Wagon wagon, Entity conductor) {
        logger.info("Initializing conductor...");
        conductor.setCurrentWagon(wagon);
        conductor.setPositionX(wagon.getDoorRightTarget().getIsoX() - 32);
        conductor.setPositionY(wagon.getDoorRightTarget().getIsoY() - 80);
        conductor.setHitBoxSize(1);
        conductor.setAttackRangeSize(2);
        conductor.setSpeedX(3);
        conductor.setSpeedY(3);
        conductor.getPreviousPositions().clear();
        conductor.setCounter(0);
        wagon.addEntity(conductor);
        logger.info("Conductor initialized");
    }

}
