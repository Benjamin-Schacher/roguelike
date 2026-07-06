package com.eltim.rogue.system;

public class diceRollSysteme {

    public static int getModifier(int stat) {
        int modifier = 0;
        if (stat == 10 || stat == 11) {
            modifier = stat / 2;
        } else if (stat == 9 || stat == 8) {
            modifier = -1;
        } else if (stat == 7 || stat == 6) {
            modifier = -2;
        } else if (stat == 5 || stat == 4) {
            modifier = -3;
        } else if (stat == 3 || stat == 2) {
            modifier = -4;
        } else if (stat == 1) {
            modifier = -5;
        } else if (stat == 12 || stat == 13) {
            modifier = 1;
        } else if (stat == 14 || stat == 15) {
            modifier = 2;
        } else if (stat == 16 || stat == 17) {
            modifier = 3;
        } else if (stat == 18 || stat == 19) {
            modifier = 4;
        } else if (stat == 20) {
            modifier = 5;
        }
        return modifier;
    }

    
    public static boolean rollDice(int sides, int modifier, int difficulty) {
        int result = (int) (Math.random() * sides) + 1 + modifier;
        if (result >= difficulty) {
            return true;
        }
        return false;
    }
   
    public static boolean roll4(int modifier, int difficulty) {
        return rollDice(4, modifier, difficulty);
    }

    public static boolean roll8(int modifier, int difficulty) {
        return rollDice(8, modifier, difficulty);
    }

    public static boolean roll12(int modifier, int difficulty) {
        return rollDice(12, modifier, difficulty);
    }

    public static boolean roll20(int modifier, int difficulty) {
        return rollDice(20, modifier, difficulty);
    }

    public static boolean roll100(int modifier, int difficulty) {
        return rollDice(100, modifier, difficulty);
    }

    public static boolean roll6(int modifier, int difficulty) {
        return rollDice(6, modifier, difficulty);
    }      
    

}
