module cs.cvut.fel.pjv.gamedemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens cs.cvut.fel.pjv.gamedemo to javafx.fxml;
    exports cs.cvut.fel.pjv.gamedemo;
    exports cs.cvut.fel.pjv.gamedemo.engine;
}