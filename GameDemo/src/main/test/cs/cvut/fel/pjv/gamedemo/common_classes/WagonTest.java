package cs.cvut.fel.pjv.gamedemo.common_classes;

import cs.cvut.fel.pjv.gamedemo.engine.utils.Checker;
import cs.cvut.fel.pjv.gamedemo.engine.EntitiesCreator;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class WagonTest extends ApplicationTest {
    private Wagon wagon;
    @Override
    public void start(Stage stage) {
        wagon = new Wagon(0, "DEFAULT");
    }
    @BeforeEach
    void setUp() {
        wagon.generateWagon();
    }
    @Nested
    class wagonGenerationTests {
        @Test
        void shouldGenerateWagonSuccessfully() {
            Assertions.assertEquals(0, wagon.getId());
            Assertions.assertEquals("DEFAULT", wagon.getType());
            Assertions.assertNotNull(wagon.getSeed());
            Assertions.assertTrue(Checker.checkMap(wagon.getSeed()));
        }
        @Nested
        class wagonObjectsTests {
            @Test
            void shouldGetObjectsArray() {
                Assertions.assertNotNull(wagon.getObjectsArray());
            }
            @Test
            void shouldGetInteractiveObjects() {
                Assertions.assertNotNull(wagon.getInteractiveObjects());
                //At least 2 interactive objects should be generated (wagon doors)
                Assertions.assertTrue(wagon.getInteractiveObjects().length >= 2);
            }
            @Test
            void shouldGetDoorLeft() {
                Assertions.assertNotNull(wagon.getDoorLeft());
            }
            @Test
            void shouldGetDoorRight() {
                Assertions.assertNotNull(wagon.getDoorRight());
            }
        }
        @Nested
        class wagonEntitiesTests {
            @Test
            void shouldGetEntities() {
                Assertions.assertNotNull(wagon.getEntities());
            }
            @Test
            void shouldAddEntity() {
                Entity entity = EntitiesCreator.createZombie();
                wagon.addEntity(entity);
                Assertions.assertTrue(wagon.getEntities().contains(entity));
            }
        }
        @Nested
        class WagonLinksTests {
            @Test
            void shouldGenerateNextWagonRight() {
                Wagon nextWagon = new Wagon(1, "DEFAULT");
                nextWagon.generateNextWagon(wagon, false);
                //Check if the wagons link correctly (new wagon is at the right of the current wagon)
                Assertions.assertEquals(wagon.getDoorRight().getTargetId(), nextWagon.getId());
            }
            @Test
            void shouldGenerateNextWagonLeft() {
                Wagon nextWagon = new Wagon(1, "DEFAULT");
                nextWagon.generateNextWagon(wagon, true);
                //Check if the wagons link correctly (new wagon is at the left of the current wagon)
                Assertions.assertEquals(wagon.getDoorLeft().getTargetId(), nextWagon.getId());
            }
        }
    }
    @Nested
    class wagonInvalidTests {
        @Test
        void shouldGenerateWagonSuccessfullyWithNullType() {
            wagon.setType(null);
            wagon.generateWagon();
            Assertions.assertNotNull(wagon.getSeed());
            Assertions.assertTrue(Checker.checkMap(wagon.getSeed()));
        }

        @Test
        void shouldGenerateWagonSuccessfullyWithNullSeed() {
            wagon.setSeed(null);
            wagon.generateWagon();
            Assertions.assertNotNull(wagon.getSeed());
            Assertions.assertTrue(Checker.checkMap(wagon.getSeed()));
        }
        @Test
        void shouldGenerateWagonSuccessfullyWithInvalidSeed() {
            wagon.setSeed("INVALID");
            wagon.generateWagon();
            Assertions.assertNotNull(wagon.getSeed());
            Assertions.assertTrue(Checker.checkMap(wagon.getSeed()));
        }
        @Test
        void shouldGenerateWagonSuccessfullyWithInvalidType() {
            wagon.setType("INVALID");
            wagon.generateWagon();
            Assertions.assertNotEquals("INVALID", wagon.getType());
            Assertions.assertNotNull(wagon.getSeed());
            Assertions.assertTrue(Checker.checkMap(wagon.getSeed()));
        }
    }
}
