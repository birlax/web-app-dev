
package com.birlax.indiantrader.patterndetection;


public enum SmoothCurve {

    UPWARD_LINE,
    DOWNWARD_LINE,
    /*---------------
      \    /
       \  /
        \/
     ---------------*/
    UPRIGHT_V,
    /*---------------
           /\
          /  \
         /    \
     ---------------*/
    UPSIDE_DOWN_V,
    UPSIDE_SKEWED_S,
    DOWNWARD_SKEWED_Z,
    /*---------------
      \    /\    /
       \  /  \  /
        \/    \/
      ---------------*/
    UPRIGHT_W,
    UPRIGHT_M,
    UPSIDE_TICK_MARK,
    SEVEN_MARK,
    /*---------------
     __
       |
         __
           |
            __
     */
    UPSTAIR_ESCALATOR,
    DOWNSTAIR_ESCALATOR,
    UNKNOWN
}
