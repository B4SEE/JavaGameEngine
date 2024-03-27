package cs.cvut.fel.pjv.gamedemo.common_classes;

public class QuestNPC extends Entity {
    private boolean questCompleted = false;
    private Item questItem;

    public QuestNPC(String name, String texturePath) {
        super(name, texturePath);
        super.setAsDefaultNPC();
    }
    public QuestNPC(String name, String texturePath, Item questItem) {
        super(name, texturePath);
        super.setAsDefaultNPC();
        this.questItem = questItem;
    }
    public boolean isQuestCompleted() {
        return questCompleted;
    }

    public void setQuestCompleted(boolean questCompleted) {
        this.questCompleted = questCompleted;
    }
}
