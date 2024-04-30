package cs.cvut.fel.pjv.logging;

import org.apache.log4j.PatternLayout;

public class MyPatternLayout extends PatternLayout {

    @Override
    public String getHeader() {
        return """
                ---------------------------------
                GameDemo, last update: 2024-04-30
                Author: virycele@fel.cvut.cz
                ---------------------------------



                """;
    }

}
