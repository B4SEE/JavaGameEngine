package cs.cvut.fel.pjv.gamedemo.engine;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Entity;
import cs.cvut.fel.pjv.gamedemo.common_classes.Player;

public class EntitiesCreator {
    /**
     * Sets the entity as the default enemy.
     * Speed and health are set to random values within the specified range.
     * Note: hitbox size, attack range size, damage and cooldown are set to default values, but it will be changed in the future (randomized).
     */
    public static void setAsDefaultEnemy(Entity entity) {
        entity.setType(Constants.EntityType.ENEMY);
        entity.setBehaviour(Constants.Behaviour.AGGRESSIVE);
        entity.setInitialBehaviour(entity.getBehaviour());
//        entity.setIntelligence((int) (Math.random() * 2));
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
    }
    /**
     * Sets the entity as the default NPC.
     * Speed and health are set to random values within the specified range.
     * Note: hitbox size, attack range size, damage and cooldown are set to default values, but it will be changed in the future (randomized).
     */
    public static void setAsDefaultNPC(Entity entity) {
        setAsDefaultEnemy(entity);
        entity.setType(Constants.EntityType.NPC);
        entity.setBehaviour(Constants.Behaviour.NEUTRAL);
        entity.setInitialBehaviour(entity.getBehaviour());
//        if (entity.getDialoguePath() == null) {
//            entity.setDialoguePath(RandomHandler.getRandomDialogueThatStartsWith(entity.getName()));
//        }
        entity.setNegativeThreshold((int) (Math.random() * 10 + 1));
    }

    public static void setAsDefaultPlayer(Player player) {
        player.setType(Constants.EntityType.PLAYER);
        player.setBehaviour(Constants.Behaviour.NEUTRAL);
        player.setInitialBehaviour(player.getBehaviour());
        player.setHealth(Constants.PLAYER_MAX_HEALTH);
        player.setDamage(Constants.PLAYER_BASIC_DAMAGE);
        player.setHitBoxSize(Constants.PLAYER_HITBOX);
        player.setAttackRangeSize(Constants.PLAYER_ATTACK_RANGE);
        player.setCooldown(Constants.PLAYER_COOLDOWN);
        player.setHeight(2);
    }
}
