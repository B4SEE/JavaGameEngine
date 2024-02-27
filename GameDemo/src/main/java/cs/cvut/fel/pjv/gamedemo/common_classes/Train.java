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
                wagonsArray[i] = wagon;
                return;
            }
        }
        //if no empty space is found, remove the oldest wagon (with the lowest id)
        removeWagon(wagonsArray[findMinId()]);
        addWagon(wagon);
    }

    public void removeWagon(Wagon wagon) {
        for (int i = 0; i < wagonsArray.length; i++) {
            if (wagonsArray[i] == wagon) {
                wagonsArray[i] = null;
            }
        }
    }

    private int findMinId() {
        int minId = wagonsArray[0].id;
        for (int i = 1; i < wagonsArray.length; i++) {
            if (wagonsArray[i].id < minId) {
                minId = wagonsArray[i].id;
            }
        }
        return minId;
    }
}