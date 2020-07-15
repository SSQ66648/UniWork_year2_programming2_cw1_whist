/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class implements Player interface and models basic a 
 *                      basic player of whist. uses basic strategy class.
 *
 *  Class:              BasicPlayer.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190211
 *
 *  Version history:    v1.0    190204  implemented and tested methods
 *                                      bar: viewTrick and setStrategy
 *                      v1.1    190207  minor tweaks, added isHuman method.
 *                                      viewTrick still not implemented other 
 *                                      than simple output
 *                      v1.2    190210  implemented viewTrick()
 *  Notes:
 **************************************************************************** */
package Prg2CW1_WHIST;

public class BasicPlayer implements Player {
//instance variables:
    private Strategy strat;
    private final int playerID;
    private final int partnerID;
    private final Hand playerHand;
    //this is not implemented in player (yet) and is only here due to spec
    private Card.Suit trumpSuit;

    
//constructor
    public BasicPlayer(int id, int part){
        playerID = id;
        partnerID = part;
        playerHand = new Hand();
    }
    
//accessor methods
    @Override
    public Hand getPlayerHand(){
        return playerHand;
    }
    
    //checks if current strat is a HumanStrategy object
        //mostly for minor match-output changes. could be useful elsewhere
    @Override
    public boolean isHuman(){
        return this.strat instanceof HumanStrategy;
    }
    
    
//override methods:    
    @Override
    public void dealCard(Card c) {
        //deal card to player and add to hand
        playerHand.addSingleCard(c);
    }

    @Override
    public void setStrategy(Strategy setStrat) {
        strat = setStrat;
    }

    @Override
    public Card playCard(Trick trick) {
        //get card to play
        Card playCard = strat.chooseCard(playerHand, trick);
        //remove card form hand and play it
        playerHand.removeSingleCard(playCard);
        return playCard;
    }

    @Override
    public void viewTrick(Trick t) {
        //use this to 'send' trick to updatedata() in advancedStrat
        this.strat.updateData(t);
    }

    //not implemented (only here due to example - may remove)
    @Override
    public void setTrumps(Card.Suit s) {
        trumpSuit = s;
    }

    @Override
    public int getID() {
        return playerID;
    }
    
    @Override
    public int getPartnerID(){
        return partnerID;
    }
 
    
    //--------------------------------------------------------------------------    
    //test harness
    public static void main(String[] args) {
    
        BasicPlayer testplay = new BasicPlayer(1,3);
        System.out.println("testing playcard():\n"
                + "  creating hand and incomplete trick:");
        testplay.dealCard(new Card(Card.Rank.FOUR,Card.Suit.SPADE));
        testplay.dealCard(new Card(Card.Rank.KING,Card.Suit.DIAMOND));
        testplay.dealCard(new Card(Card.Rank.TEN,Card.Suit.HEART));
        testplay.dealCard(new Card(Card.Rank.FOUR,Card.Suit.HEART));
        System.out.println("test of hand populated with dealcard():\n"
                + testplay.playerHand +"\ntesting with trick:");
        Trick testTrick = new Trick();
        testTrick.addToTrick(new Card(Card.Rank.FIVE,Card.Suit.SPADE));
        testTrick.addToTrick(new Card(Card.Rank.TEN,Card.Suit.SPADE));
        testTrick.addToTrick(new Card(Card.Rank.THREE,Card.Suit.HEART));
        testTrick.setTrumpSuit(Card.Suit.HEART);
        System.out.println(testTrick);
        System.out.println("lead suit: "+testTrick.getLeadSuiit());
        System.out.println("trump suit: "+testTrick.getTrumpSuit());
        System.out.println("");
        
        testplay.setStrategy(new BasicStrategy());
        
        System.out.println("expected result:\n"
                + "   partner HAS played.\n"
                + "   partner is NOT winning.\n"
                + "   lead suit IS in hand.\n"
                + "   expected card: FOUR of SPADE.\n"
                + "played card: "); 
        Card testP = testplay.playCard(testTrick);
        System.out.println("");
        
        //testing viewtrick
        System.out.println("testing viewTrick():\n");
        testplay.viewTrick(testTrick);

    }
    
}
