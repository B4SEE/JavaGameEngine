public class Game {
    private Player player;
    private Wagon currentWagon;
    private Train train;

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
        try {
            FileOutputStream fileOut = new FileOutputStream("game.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void loadGame() {
        // load Train class
        // load Player class
        // load currentWagon
        try {
            FileInputStream fileIn = new FileInputStream("game.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Game game = (Game) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Game class not found");
            c.printStackTrace();
            return;
        }
    }

    public void startGame() {
        // start the game
    }
}