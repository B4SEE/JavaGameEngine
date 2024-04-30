module cs.cvut.fel.pjv.gamedemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires log4j;

    opens cs.cvut.fel.pjv.gamedemo.engine to com.fasterxml.jackson.databind;
    opens cs.cvut.fel.pjv.gamedemo.common_classes to com.fasterxml.jackson.databind;

    exports cs.cvut.fel.pjv.gamedemo;
    exports cs.cvut.fel.pjv.gamedemo.engine;
    exports cs.cvut.fel.pjv.gamedemo.common_classes to com.fasterxml.jackson.databind;
    opens cs.cvut.fel.pjv.gamedemo to com.fasterxml.jackson.databind, javafx.fxml;
    exports cs.cvut.fel.pjv.logging;
    opens cs.cvut.fel.pjv.logging to com.fasterxml.jackson.databind, javafx.fxml;
    exports cs.cvut.fel.pjv.gamedemo.engine.gamelogic;
    opens cs.cvut.fel.pjv.gamedemo.engine.gamelogic to com.fasterxml.jackson.databind;
    exports cs.cvut.fel.pjv.gamedemo.engine.utils;
    opens cs.cvut.fel.pjv.gamedemo.engine.utils to com.fasterxml.jackson.databind;
}