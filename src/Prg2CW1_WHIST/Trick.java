/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class contains structures and methods for modelling
 *                      Whist tricks player are counted as cards are played
 *                      ie lead player is always player who played first card
 *                      in the trick
 *
 *  Class:              Trick.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190208
 *
 *  Version history:    v0.0    190130   Began skeleton
 *                      v1.0    190130  completed and tested class,
 *                                      some questions remain
 *                      v1.0a   190203  removed card suit use in constructor
 *                      v1.0b   190204  added variable for winning card play
 *                      v1.1    190207  changed getWinner trump-fining logic
 *                      v1.2    190208  changed getWinner logic to check for 
 *                                      trump OR lead suit: fixed high invalid 
 *                                      card winning hand, changed temp to null
 *                                      as lowest card approach could win trick 
 *                                      as trump
 *                      v1.2a   190208  added winningID update to 0-th card as 
 *                                      temp is assigned in getWinner()
 * Notes:
 *              -considering adding a custom exception to getPlayerCard, but i
 *              don't see any additional benefit. may reconsider this.
 *
 **************************************************************************** */
package Prg2CW1_WHIST;

//imports

import java.util.Arrays;

public class Trick {
//instance variables:
    //records trump suit
    private Card.Suit trumpSuit;
    //records cards played this trick (fixed length of 4)
    private final Card[] playedCards;
    //records lead suit
    private Card.Suit leadSuit;
    //winning player of the current trick
    private int winnerID;

//Class constructor(s):
    //default 
    public Trick() {
        playedCards = new Card[4];
    }

//Accessor methods:
    public Card.Suit getLeadSuiit() {
        return leadSuit;
    }

    public Card[] getPlayedCards() {
        return playedCards;
    }

    public Card.Suit getTrumpSuit() {
        return trumpSuit;
    }

//Setter methods:
    //sets leadSuit variable to passed value
    public void setLeadSuit(Card.Suit lead) {
        leadSuit = lead;
    }

    public void setTrumpSuit(Card.Suit trump) {
        trumpSuit = trump;
    }

    //sets and returns specific card (i) to passed value (mostly for debugging)
    public Card setCard(int i, Card setCard) {
        playedCards[i] = setCard;
        return playedCards[i];
    }

    //returns winning card play order
    public int getwinningID() {
        return winnerID;
    }

//toString override:
    @Override
    public String toString() {
        return "cards played: " + Arrays.toString(playedCards);
    }

//Class methods:
    //adds passed card to array of four cards played this turn
    public void addToTrick(Card card) {
        //breaks loop once card has been played
        boolean played = false;
        //sets first card as lead suit
        if (playedCards[0] == null) {
            setLeadSuit(card.getSuit());
        }
        //adds cards in order they were played
        for (int i = 0; i < playedCards.length; i++) {
            if (played == false && playedCards[i] == null) {
                playedCards[i] = card;
                played = true;
            }
        }
    }

//returns card played by passed player-number this trick
    public Card getPlayerCard(int playerNo) {
        if (playedCards[playerNo] == null) {
            System.out.println("Card not played");
        }
        return playedCards[playerNo];
    }

//returns highest value card in the trick
    public Card getWinner() {
        //temp initialised to null (any card value has potential to trump)
        Card temp = null;
        for (int i = 0; i < playedCards.length; i++) {
            //check trick card has been played
            if (playedCards[i] != null) {
                //if temp is null, set first card as temp winner
                if (temp==null) {
                    temp=playedCards[i];
                    winnerID=i;
                }
                //check if played card is trump suit
                else if (playedCards[i].getSuit().equals(trumpSuit)) {
                    //check if temp is also a trumpcard
                    if (temp.getSuit().equals(trumpSuit)) {
                        //IS trump, compare rank and replace if greater
                        if (playedCards[i].compareTo(temp) == 1) {
                            //replace temp
                            temp = playedCards[i];
                            //record winning card play number
                            winnerID = i;
                        }
                    } else {
                        //temp is NOT trump, replace with trump card
                        temp = playedCards[i];
                        //record winning card play number
                        winnerID = i;
                    }
                } else {
                    //played card is NOT a trump card
                    //check temp is NOT trump suit
                    if (!temp.getSuit().equals(trumpSuit)) {
                        //neither card is trump, check for lead suit
                        if (playedCards[i].getSuit().equals(leadSuit)) {
                            //card is not trump but IS lead suit, compare rank
                            if (playedCards[i].compareTo(temp) == 1) {
                                //played card greater than temp, replace
                                temp = playedCards[i];
                                //record winning card play number
                                winnerID = i;
                            }
                            //if played card is NOT trump, NOT leadSuit: 
                            //not valid card to play this trick, discard  
                        }
                    }
                    //if temp IS trump, greater than played card, do nothing      
                }
            }
        }
        return temp;
    }

//------------------------------------------------------------------------------    
    //test harness
    public static void main(String[] args) {

        //testing constructor
        System.out.println("testing constructor: ");
        Trick testTrick = new Trick();
        System.out.println("trick with no played cards: "
                + Arrays.toString(testTrick.playedCards)
                + "\nsetting trump Suit: ");
        testTrick.setTrumpSuit(Card.Suit.SPADE);
        System.out.println("trump suit: " + testTrick.trumpSuit
                + "\nlength of possible cards in trick: "
                + testTrick.playedCards.length);
        System.out.println("");

        //testing addtotrick and setleadsuit
        //create cards to play
        Card cards[] = new Card[4];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(Card.Rank.randomRank(), Card.Suit.randomSuit());
        }
        System.out.println("testing addToTrick() using setLeadSuit():\n"
                + "cards to 'play':\n" + Arrays.toString(cards));
        for (int i = 0; i < testTrick.playedCards.length; i++) {
            testTrick.addToTrick(cards[i]);
        }
        System.out.println("trick after adding four cards as above:\n"
                + Arrays.toString(testTrick.playedCards)
                + "\nlead suit: " + testTrick.leadSuit);
        System.out.println("");

        //test tostring override
        System.out.println("testing toString:\n   " + testTrick);
        System.out.println("");

        //test setCard()
        //create known value cards
        Card forcedValue1 = new Card(Card.Rank.THREE, Card.Suit.CLUB);
        Card forcedValue2 = new Card(Card.Rank.ACE, Card.Suit.CLUB);
        Card forcedValue3 = new Card(Card.Rank.ACE, Card.Suit.HEART);
        Card forcedValue4 = new Card(Card.Rank.SEVEN, Card.Suit.CLUB);

        //manually assign to array
        testTrick.playedCards[0] = forcedValue1;
        testTrick.playedCards[1] = forcedValue2;
        testTrick.playedCards[2] = forcedValue3;
        testTrick.playedCards[3] = forcedValue4;

        System.out.println("testing setCard()\n"
                + "setting cards to\n   " + testTrick);
        System.out.println("");

        //test findWinner()
        System.out.println("testing findWinner on above trick: "
                + testTrick.getWinner()
                + "\n setting first card to "
                + testTrick.setCard(0,
                        new Card(Card.Rank.THREE, Card.Suit.SPADE))
                + "\n retesting getWinner(): " + testTrick.getWinner());
        System.out.println("");

        //double-checking revised winner method comparison
        //create empty trick and known trump cards to add
        Trick trumpTrick = new Trick();
        trumpTrick.addToTrick(new Card(Card.Rank.SIX, Card.Suit.SPADE));
        trumpTrick.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        trumpTrick.addToTrick(new Card(Card.Rank.TWO, Card.Suit.SPADE));
        trumpTrick.addToTrick(new Card(Card.Rank.QUEEN, Card.Suit.SPADE));
        System.out.println("Re-testing wetwinner:\n"
                + "  trick created with played cards:\n"
                + trumpTrick + "\n  setting trump suit to SPADE");
        trumpTrick.setTrumpSuit(Card.Suit.SPADE);
        System.out.println("Expected winner outcome: QUEEN\n"
                + "actual outcome: " + trumpTrick.getWinner());
        System.out.println("");
        
        //retesting that a non lead/trump card is invalid
        System.out.println("Creating trick to test for non-valid card bug:");
        Trick NVtrick = new Trick();
        NVtrick.setTrumpSuit(Card.Suit.CLUB);
        NVtrick.addToTrick(new Card(Card.Rank.SIX, Card.Suit.SPADE));
        NVtrick.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        NVtrick.addToTrick(new Card(Card.Rank.TWO, Card.Suit.SPADE));
        NVtrick.addToTrick(new Card(Card.Rank.QUEEN, Card.Suit.DIAMOND));
        System.out.println("cards played in trick:\n" +NVtrick
                +"\ntrick trump suit: "+NVtrick.getTrumpSuit()
                +"\ntrick lead suit: "+NVtrick.getLeadSuiit()
                +"\nExpected winner: EIGHT of SPADE\n"
                + "Actual outcome: "+NVtrick.getWinner());

    }
}
