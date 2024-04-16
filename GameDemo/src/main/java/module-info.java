module cs.cvut.fel.pjv.gamedemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires log4j;

    opens cs.cvut.fel.pjv.gamedemo to javafx.fxml;
    opens cs.cvut.fel.pjv.gamedemo.engine to com.fasterxml.jackson.databind;
    opens cs.cvut.fel.pjv.gamedemo.common_classes to com.fasterxml.jackson.databind;

    exports cs.cvut.fel.pjv.gamedemo;
    exports cs.cvut.fel.pjv.gamedemo.engine;
    exports cs.cvut.fel.pjv.gamedemo.common_classes to com.fasterxml.jackson.databind;
}