package cs.cvut.fel.pjv.gamedemo.engine.gamelogic;

import cs.cvut.fel.pjv.gamedemo.common_classes.Constants;
import cs.cvut.fel.pjv.gamedemo.common_classes.Door;
import cs.cvut.fel.pjv.gamedemo.common_classes.Object;
import cs.cvut.fel.pjv.gamedemo.engine.Events;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Atmospheric;
import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TrapLogic {
    private static final Logger logger = LogManager.getLogger(TrapLogic.class);
    private static GameData gameData;
    public static void setGameData(GameData gameData) {
        TrapLogic.gameData = gameData;
    }

    //region Trap methods
    /**
     * Generate and set the random trap event (trap with enemies, boss, time loop, silence).
     */
    public static void generateAndSetTrap() {
        if (gameData.getEventStartTime() == 0) {
            gameData.setEventStartTime(gameData.getTime());
            gameData.setEventDuration(Constants.TIME_TO_ESCAPE_TRAP);

            int trapType = (int) (Math.random() * 4) + 1;
            switch (trapType) {
                case 1:
                    Events.setNextEvent(Constants.Event.TRAP_EVENT);
                    break;
                case 2:
                    Events.setNextEvent(Constants.Event.BOSS_EVENT);
                    Atmospheric.fadeOutMusic(0.00);
                    break;
                case 3:
                    Events.setNextEvent(Constants.Event.TIME_LOOP_EVENT);
                    int randomCounter = (int) (Math.random() * Constants.MAX_TIME_LOOP_COUNTER);
                    Events.setTimeLoopCounter(randomCounter);
                    break;
                case 4:
                    Events.setNextEvent(Constants.Event.SILENCE_EVENT);
                    gameData.setEventDuration(Constants.TIME_TO_ESCAPE_SILENCE);
                    Atmospheric.fadeOutMusic(0.05);
                    break;
            }
            logger.debug("Trap event generated: " + Events.getNextEvent());
        }
        activateTrap();
    }

    /**
     * Activate the trap event if the time is up (if the player does not escape from the trap in time).
     */
    private static void activateTrap() {
        if ((gameData.getTime() - gameData.getEventStartTime() != 0)
                && (gameData.getTime() - gameData.getEventStartTime()) >= gameData.getEventDuration()
                && Events.getCurrentEvent() == Constants.Event.DEFAULT_EVENT) {
            logger.info("Trap event activated: " + Events.getNextEvent());
            Events.setCurrentEvent(Events.getNextEvent());
            Events.setNextEvent(Constants.Event.DEFAULT_EVENT);
            switch (Events.getCurrentEvent()) {
                case TRAP_EVENT -> EntitiesLogic.spawnEnemies();
                case BOSS_EVENT -> handleBossEvent();
                case SILENCE_EVENT -> handleSilenceEvent();
                default -> logger.error("Invalid trap event");
            }
        }
    }

    /**
     * Handle the boss event.
     */
    private static void handleBossEvent() {
        Events.setBossFight(true);
        Events.setCurrentEvent(Constants.Event.TRAP_EVENT);//to lock the doors
        String bossName = Constants.WAGON_TYPES_BOSSES.get(gameData.getWagon().getType());
        EntitiesLogic.spawnBoss(bossName);
    }

    /**
     * Handle time loop event.
     * @param object door to be opened
     */
    public static void handleTimeLoop(Door object) {
        logger.debug("Time loop event loops count: " + Events.getTimeLoopCounter());
        Door door = (object == gameData.getWagon().getDoorLeft()) ? gameData.getWagon().getDoorLeft() : gameData.getWagon().getDoorRight();
        Object actualTeleport = door.getTeleport();
        door.setTeleport((object == gameData.getWagon().getDoorLeft()) ? gameData.getWagon().getDoorRightTarget() : gameData.getWagon().getDoorLeftTarget());
        door.teleport(gameData.getPlayer());
        door.setTeleport(actualTeleport);

        Events.decrementTimeLoopCounter();
        if (Events.getTimeLoopCounter() == 0) {
            logger.info("Time loop event ended");
            Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
            gameData.getWagon().removeTrap();
            gameData.setEventStartTime(0);
            gameData.setEventDuration(0);
        }
    }

    /**
     * Handle the Silence event.
     */
    private static void handleSilenceEvent() {
        gameData.setDeathMessage("You were killed by the silence");
        gameData.getPlayer().setHealth(-1);
        gameData.getWagon().removeTrap();
        gameData.setEventStartTime(0);
        gameData.setEventDuration(0);
        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);// Silence event is over once the player dies or moves to another wagon
    }

    /**
     * Generate the reward for the trap event (money and ammo), and add it to the player's inventory.
     */
    public static void generateReward() {
        if (!(Events.getCurrentEvent() == Constants.Event.TRAP_EVENT || Events.getCurrentEvent() == Constants.Event.BOSS_EVENT)) {
            return; // Only reward the player if there is a trap or boss event
        }

        logger.debug("Generating trap event reward...");

        int moneyReward = (int) (Math.random() * Constants.MAX_TRAP_REWARD) + Constants.MIN_TRAP_REWARD;
        int ammoReward = (int) (Math.random() * Constants.MAX_TRAP_REWARD) + Constants.MIN_TRAP_REWARD;

        Events.setCurrentEvent(Constants.Event.DEFAULT_EVENT);
        Events.setBossFight(false);

        if (gameData.getBoss() != null) {
            logger.debug("Boss defeated, increasing the reward...");
            moneyReward += Constants.MAX_TRAP_REWARD;
            ammoReward += Constants.MAX_TRAP_REWARD;
            logger.info("Boss defeated, reward increased");
            Events.setCanSpawnTicket(true);
            logger.debug("Player can now find the ticket");
        }

        logger.info("Trap event reward: " + moneyReward + " money, " + ammoReward + " ammo");

        gameData.getPlayer().getPlayerInventory().setMoney(gameData.getPlayer().getPlayerInventory().getMoney() + moneyReward);
        gameData.getPlayer().getPlayerInventory().setAmmo(gameData.getPlayer().getPlayerInventory().getAmmo() + ammoReward);

        logger.info("Trap event ended");
    }

    /**
     * Deactivate the trap event.
     */
    public static void deactivateTrap() {
        if (Checker.checkIfWagonHasTrap(gameData.getWagon().getObjectsArray())) {
            gameData.setDeathMessage("");
            gameData.setBoss(null);
            Atmospheric.stopBossMusic();
            gameData.setEventStartTime(0);
            gameData.setEventDuration(0);
            gameData.getWagon().removeTrap();
            WagonLogic.unlockWagonDoors();
            gameData.getIsometric().setObjectsToDraw(gameData.getWagon().getObjectsArray());
            gameData.getIsometric().updateAll();
            logger.debug("Trap event - doors unlocked");
        }
    }
    //endregion
}
