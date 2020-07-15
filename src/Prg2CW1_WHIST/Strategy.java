/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Interface contains methods choodeCard() and updateData()
 *                      -strategy contained within a player.
 *
 *  Class:              Strategy.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190127
 *
 *  Version history:    v0.0   190130   Began interface framework based on given 
 *                                      example
 *
 **************************************************************************** */
package Prg2CW1_WHIST;
//imports

public interface Strategy {
//interface methods:
    //chooses card from hand to play in trick
    Card chooseCard(Hand h, Trick t);
    
    //update internal memory to include trick c
    void updateData(Trick c);
    
}
