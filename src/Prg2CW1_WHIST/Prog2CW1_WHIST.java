/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:          WHIST
 *
 *  Description:    Code used to model card games contained in three classes:
 *                       Card, Hand, Deck
 *                   Implementation of simulated card game 'whist' using code
 *                   from  above classes as a basis.
 *                  main method class: used to call static methods for testing.
 *
 *  Class:              Prog2CW1_WHIST.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190211
 *
 *  Version history:    v0.0    190108  Created
 *                      v1.0    190210  used to test static methods (hum/adv)
 *                      v1.1    190211  implemented a run of 1k loop test
 * 
 *  Notes:
 *              -the static methods humanGame() and advancedGame() both tested
 *              successfully. They have been commented out as to not interfere
 *              with the gathering of advancedStrategy statistics:
 *                  this was originally going to use my CSV output classes i
 *                  wrote to automate previous DSaA coursework, but as i am out 
 *                  of time, i will have to do it the old fashioned way
 *
 *****************************************************************************/
package Prg2CW1_WHIST;

public class Prog2CW1_WHIST {

    public static void main(String[] args) throws Deck.DeckLengthException {
//attempt to run static humanGame()
//        System.out.println("playing static method: humanGame()\n\n");
//        BasicWhist.humanGame();
//        System.out.println("");

//attempt to run static advancedGame()
//        System.out.println("playing static method: advancedGame()\n\n");
//        BasicWhist.advancedGame();
//        System.out.println("");
        
//run 1k adv loop:
        System.out.println("beginning advanced strat effectiveness test:\n");
        BasicWhist.advTest();
        
        
    }

}
