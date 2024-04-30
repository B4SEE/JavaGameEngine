package cs.cvut.fel.pjv.logging;

import org.apache.log4j.PatternLayout;

public class MyPatternLayout extends PatternLayout {

    @Override
    public String getHeader() {
        return """
                ---------------------------------
                Semester project for PJV (programming in Java) summer course 2023/2024
                "Infinity Express - Destination Abyss" demo, last update: 2024-04-30
                Author: virycele@fel.cvut.cz
                ---------------------------------



                """;
    }

}
