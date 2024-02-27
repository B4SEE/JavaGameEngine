public class Game {
    private Player player;
    private currentWagon;

    public Game(Player player) {
        this.player = player;
    }

    public Game(Player player, Wagon currentWagon) {
        this.player = player;
        this.currentWagon = currentWagon;
    }

    public void setCurrentWagon(Wagon currentWagon) {
        this.currentWagon = currentWagon;
    }

    public Wagon getCurrentWagon() {
        return currentWagon;
    }

    public void saveGame() {
        // save Train class
        //save Player class
        //save currentWagon
    }

    public void loadGame() {
        // load Train class
        // load Player class
    }

    public void startGame() {
        // start the game
    }
}