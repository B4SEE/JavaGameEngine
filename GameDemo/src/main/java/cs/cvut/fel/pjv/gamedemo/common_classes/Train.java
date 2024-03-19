package cs.cvut.fel.pjv.gamedemo.common_classes;

public class Train {
    public final Wagon[] wagonsArray;

    public Train() {
        wagonsArray = new Wagon[Constants.TRAIN_WAGONS];
    }

    public Train(Wagon[] wagonsArray) {
        this.wagonsArray = wagonsArray;
    }

    public void addWagon(Wagon wagon) {
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == null) {
                System.out.println("Wagon " + wagon.getId() + " added to the train.");
                wagonsArray[i] = wagon;
                return;
            }
        }
        //if no empty space is found, remove the oldest wagon (with the lowest id)
        removeWagon(wagonsArray[findMinWagonId()]);
        addWagon(wagon);
    }

    public void removeWagon(Wagon wagon) {
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == wagon) {
                wagonsArray[i] = null;
            }
        }
    }

    private int findMinWagonId() {
        int minId = wagonsArray[0].getId();
        for (int i = 1; i < wagonsArray.length; i++) {
            if (wagonsArray[i].getId() < minId) {
                minId = wagonsArray[i].getId();
            }
        }
        return minId;
    }

    public int findMaxWagonId() {
        int maxId = wagonsArray[0].getId();
        for (int i = 1; i < wagonsArray.length; i++) {
            if (wagonsArray[i] != null && wagonsArray[i].getId() > maxId) {
                maxId = wagonsArray[i].getId();
            }
        }
        return maxId;
    }
}