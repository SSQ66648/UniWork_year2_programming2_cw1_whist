/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Interface contains attributes and methods for modelling 
 *                      a whist game player
 *
 *  Class:              Player.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190127
 *
 *  Version history:    v0.0   190130  Began interface framework based on 
 *                                     example given, untested 
 *                      v1.0    190207  added support methods to be overridden 
 *                                      (allows array of generic Player objects 
 *                                      to access the methods)
 *
 **************************************************************************** */
package Prg2CW1_WHIST;
//imports

public interface Player {
//interface methods:
    //deals the passed Card to players hand
    void dealCard(Card c);
    
    //sets strategy 
    void setStrategy(Strategy setStrat);
    
    //determines which card to play based on current trick
    Card playCard(Trick trick);

    void viewTrick(Trick t);

    void setTrumps(Card.Suit s);

    int getID();
    
    int getPartnerID();
    
    public Hand getPlayerHand();
    
    public boolean isHuman();
    
//------------------------------------------------------------------------------    
    //test harness
    public static void main(String[] args) {}

}
