/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class implements strategy interface and contains methods
 *                      for basic game play strategies
 *
 *  Class:              BasicStrategy.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190210
 *
 *  Version history:    v1.0    190204  implemented and tested chooseCard
 *                                      code still naive and needs to be tidied
 *                                      -no implementation of updateData yet
 *                      v1.1    190207  added priorisation of trump cards
 *                      v2.0    190208  overhauled code into abstracted methods
 *                      v2.1    190210  minor changes in syntax, deleted most
 *                                      unused helper methods
 *  Notes:
 *              -does not utilise partner id from containing basicPlayer object,
 *              creates its own based on how many cards have been played.
 *              -may need to change method of finding partner ID from using null
 *              as implemented here, to getting that data from basicPlayer.
 *              -while most repeated code has been abstracted into methods for
 *              legibility, some that could have been further reduced has been
 *              left as-is to make the flow of choices easier to follow.
 **************************************************************************** */
package Prg2CW1_WHIST;

//imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BasicStrategy implements Strategy {
//instance variables:
    private int partnerID;
    private boolean partnerPlayed;

//Override methods:    
    @Override
    public Card chooseCard(Hand h, Trick t) {
        //card to play: set to first card in hand
        Card playCard = h.getHand().get(0);

        //check if player is first to play card in trick
        if (t.getPlayedCards()[0] == null) {
            partnerID = 2;
            //play highest non-trump card
            playCard = getHighNonT(h, t);
            //if playcard still trumpsuit, ONLY trump cards remain, get highest
            if (playCard.getSuit().equals(t.getTrumpSuit())) {
                playCard = Card.max(h.getHand());
            }

        } else {
            //if not first player, check if hand contains lead suit
            if (h.hasSuit(t.getLeadSuiit())) {
                //check if partner has played 
                if (t.getPlayedCards()[1] != null) {
                    partnerPlayed = true;
                    if (t.getPlayedCards()[2] != null) {
                        partnerID = 1;
                    } else {
                        partnerID = 0;
                    }
                } else {
                    partnerID = 3;
                    partnerPlayed = false;
                }

                //if partner HAS played
                if (partnerPlayed) {
                    //check if partner winning
                    if (t.getWinner().equals(t.getPlayedCards()[partnerID])) {
                        //parner IS winning play lowest lead card
                        playCard = getLowLead(h, t);
                    } else {
                        //partner played but NOT winning
                        //find highest lead card
                        playCard = getHighLead(h, t);
                        //check if can win based on current cards in trick
                        if (!canWin(t, playCard)) {
                            //cannot win, play lowest of lead suit
                            playCard = getLowLead(h, t);
                        }
                    }
                } else {
                    //partner NOT played, play high card of lead suit
                    playCard = getHighLead(h, t);
                    //check if can win based on current cards in trick
                    if (!canWin(t, playCard)) {
                        //cannot win, play lowest of lead suit
                        playCard = getLowLead(h, t);
                    }
                }
//end of hand-contains:lead-suit options

            } else {
                //no matching lead suit in hand
                //check hand for trump suit
                if (h.hasSuit(t.getTrumpSuit())) {
                    //check for existing trump cards in trick
                    //highest trump copy (for multiple trumpcards)
                    Card trumpHigh = null;
                    //can win flag
                    boolean canWinBool = false;
                    //trump card already in play flag
                    boolean trumpfound = false;

                    for (int i = 0; i < t.getPlayedCards().length; i++) {
                        //loop through played cards looking for trump suit, 
                        //assign highest found to copy
                        if (t.getPlayedCards()[i] != null
                                && t.getPlayedCards()[i].getSuit()
                                        .equals(t.getTrumpSuit())) {
                            //if found flag false, first card must be high value
                            if (!trumpfound) {
                                trumpHigh = t.getPlayedCards()[i];
                            } else {
                                //check next found trump against trumpHigh
                                if (t.getPlayedCards()[i].compareTo(trumpHigh)
                                        == 1) {
                                    trumpHigh = t.getPlayedCards()[i];
                                }
                            }
                            trumpfound = true;
                        }
                    }
                    //sort hand into descending order (experiment to see if any 
                    //improvement on checking for lower cards in order)
                    Collections.sort(h.getHand(), new Card.CompareDescending());
                    if (trumpfound) {
                        //compare descending order of trumpcards in hand to 
                        //highest trumpcard in play
                        for (int i = 0; i < h.getHand().size(); i++) {
                            //assign playCard to smallest value trumpcard that 
                            //beats any in play
                            if (h.getHand().get(i).getSuit()
                                    .equals(t.getTrumpSuit()) && h.getHand()
                                    .get(i).compareTo(trumpHigh) == 1) {
                                canWinBool = true;
                                playCard = h.getHand().get(i);
                            }
                        }
                        //return lowest trumpcard that can win
                        if (canWinBool) {
                            return playCard;
                        } else {
                            //trump cards cannot win, no lead suit,  
                            //discard lowest value invalid-suit card
                            playCard = discard(h, t);
                        }

                    } else {
                        //no trumpcards in play, check if partner has played
                        if (partnerPlayed) {
                            //check if partner NOT winning
                            if (!t.getWinner().equals(t
                                    .getPlayedCards()[partnerID])) {
                                //partner is NOT winning, play smallest trump 
                                for (int i = 0; i < h.getHand().size(); i++) {
                                    //still in descending order
                                    if (h.getHand().get(i).getSuit().equals(t
                                            .getTrumpSuit())) {
                                        playCard = h.getHand().get(i);
                                    }
                                }
                            } else {
                                //parner IS winning, but no lead suit in hand:
                                //play lowest non lead/non trump card
                                playCard = discard(h, t);
                            }

                        } else {
                            //partner NOT played, play smallest trump card
                            for (int i = 0; i < h.getHand().size(); i++) {
                                //still in descending order
                                if (h.getHand().get(i).getSuit()
                                        .equals(t.getTrumpSuit())) {
                                    playCard = h.getHand().get(i);
                                }
                            }
                        }

                    }
                } else {
                    //no lead-suit, no trump-suit, discard lowest invalid-suit 
                    playCard = discard(h, t);
                }
            }
        }
        return playCard;
    }

//unimplemented override methods:    
    @Override
    public void updateData(Trick c) {
        //not used in BasicStrategy
    }

//class methods:
    //returns lowest value non-lead and non-trump suit card
    private static Card discard(Hand h, Trick t) {
        //initialise to first card
        Card discard = h.getHand().get(0);
        for (int i = 0; i < h.getHand().size(); i++) {
            //check card not trump, not lead, and lower value than discard
            if (!h.getHand().get(i).getSuit().equals(t.getLeadSuiit())
                    && !h.getHand().get(i).getSuit().equals(t.getTrumpSuit())
                    && h.getHand().get(i).compareTo(discard) == -1) {
                discard = h.getHand().get(i);
            }
        }
        return discard;
    }

    //checks if passed card could win the trick (up to this point of play)
    private static boolean canWin(Trick t, Card playCard) {
        //duplicate trick (preserve original trick object's integrety)
        Trick checkWin = new Trick();
        for (int i = 0; i < t.getPlayedCards().length; i++) {
            checkWin.addToTrick(t.getPlayedCards()[i]);
        }
        checkWin.setLeadSuit(t.getLeadSuiit());
        checkWin.setTrumpSuit(t.getTrumpSuit());
        //add potential card to duplicate-trick
        checkWin.addToTrick(playCard);
        //check potential card can wins trick
        return playCard.compareTo(checkWin.getWinner()) == 0;
    }

    //returns highest lead-suit card from hand (check exists first)
    private static Card getHighLead(Hand h, Trick t) {
        Card temp = h.getHand().get(0);
        for (int i = 0; i < h.getHand().size(); i++) {
            if (!temp.getSuit().equals(t.getLeadSuiit())) {
                //first lead suit card replaces temp
                temp = h.getHand().get(i);
            }
            //check subsequent cards ARE lead and GREATER than temp
            if (h.getHand().get(i).getSuit().equals(t.getLeadSuiit())
                    && h.getHand().get(i).compareTo(temp) == 1) {
                //replace temp
                temp = h.getHand().get(i);
            }
        }
        return temp;
    }

    //returns lowest lead-suit card from hand
    private static Card getLowLead(Hand h, Trick t) {
        Card temp = h.getHand().get(0);
        for (int i = 0; i < h.getHand().size(); i++) {
            //assign first lead suit
            if (!temp.getSuit().equals(t.getLeadSuiit())) {
                //first lead suit card replaces temp
                temp = h.getHand().get(i);
            }
            //check card IS lead and is LOWER than temp
            if (h.getHand().get(i).getSuit().equals(t.getLeadSuiit())
                    && h.getHand().get(i).compareTo(temp) == -1) {
                //replace temp
                temp = h.getHand().get(i);
            }
        }
        return temp;
    }

    //get highest non-trump card 
    private static Card getHighNonT(Hand h, Trick t) {
        Card temp = h.getHand().get(0);
        for (int i = 0; i < h.getHand().size(); i++) {
            //check card IS NOT trump and is GREATER than temp
            if (!h.getHand().get(i).getSuit().equals(t.getTrumpSuit())
                    && h.getHand().get(i).compareTo(temp) == 1) {
                //replace temp
                temp = h.getHand().get(i);
            }
        }
        return temp;
    }


    //returns any card in hand higher than passed
    private static Card findHigh(Hand h, Card currentCard) {
        Card playCard = currentCard;
        for (int i = 0; i < h.getHand().size(); i++) {
            if (h.getHand().get(i).compareTo(playCard) == 1) {
                //assign to playCard if higher
                playCard = h.getHand().get(i);
            }
        }
        return playCard;
    }

//experimental re-written methods (unused)    
    private static Card findHigh2(Hand h, Card currentCard) {
        ArrayList<Card> greaterList = Card.chooseGreater(h.getHand(), 
                new Card.CompareRank(), currentCard);
        Card playCard = Card.max(greaterList);
        return playCard;
    }

    private static Card findLow(Hand h, Card currentCard) {
        Card playCard = currentCard;
        for (int i = 0; i < h.getHand().size(); i++) {
            if (h.getHand().get(i).compareTo(playCard) == -1) {
                //assign to playCard if higher
                playCard = h.getHand().get(i);
            }
        }
        return playCard;
    }

    private static Card findLow2(Hand h) {
        Collections.sort(h.getHand(), new Card.CompareDescending());
        Card playCard = h.getHand().get(h.getHand().size() - 1);
        return playCard;
    }

    //test harness    
//-------------------------------------------------------------------------
    public static void main(String[] args) {

        System.out.println("testing chooseCard():\n"
                + "creating trick");
        Trick t = new Trick();
        System.out.println("checking playedCards are null\n"
                + Arrays.toString(t.getPlayedCards()));
        System.out.println("setting trum suit to SPADE");
        t.setTrumpSuit(Card.Suit.SPADE);
        System.out.println("confirming trump suit:\n"
                + t.getTrumpSuit());
        //create two cards
        Card testCard1 = new Card(Card.Rank.NINE, Card.Suit.CLUB);
        Card testCard2 = new Card(Card.Rank.FOUR, Card.Suit.CLUB);

        System.out.println("''playing'' two cards:\n"
                + testCard1 + "\n" + testCard2);
        t.addToTrick(testCard1);
        t.addToTrick(testCard2);
        System.out.println("current played cards:\n"
                + Arrays.toString(t.getPlayedCards()));
        System.out.println("testing lead suit:\n"
                + t.getLeadSuiit());
        System.out.println("creating hand to choose card to play:");

        //create card array to add to hand
        Card[] cards = new Card[]{
            new Card(Card.Rank.TWO, Card.Suit.CLUB),
            new Card(Card.Rank.FOUR, Card.Suit.HEART),
            new Card(Card.Rank.SIX, Card.Suit.SPADE),
            new Card(Card.Rank.THREE, Card.Suit.SPADE),
            new Card(Card.Rank.ACE, Card.Suit.CLUB)
        };
        Hand testHand = new Hand(cards);
        System.out.println("testing hand:\n"
                + testHand);
        System.out.println("");
        //create basicstrategy object
        BasicStrategy testStrat = new BasicStrategy();
        System.out.println("testing choosecard():");
        System.out.println("expected outcome:\n"
                + "  partner HAS played\n"
                + "  hand DOES contain lead suit\n"
                + "  play lowest value card of lead suit: TWO of CLUB\n"
                + "card played:"
                + testStrat.chooseCard(testHand, t));
        System.out.println("checking partnerID is 0:\n"
                + testStrat.partnerID);
        //create card to remove
        Card removeTwo = new Card(Card.Rank.TWO, Card.Suit.CLUB);
        System.out.println("removing " + removeTwo + " from hand");
        testHand.removeSingleCard(removeTwo);

        System.out.println("checking remove successful:\n"
                + testHand);
        System.out.println("");

        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS played\n"
                + "  hand DOES contain lead suit\n"
                + "  play lowest value card of lead suit: ACE of CLUB\n"
                + "card played:"
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        System.out.println("removing ACE of CLUB from hand");
        testHand.removeSingleCard(new Card(Card.Rank.ACE, Card.Suit.CLUB));

        System.out.println("checking correct card removed:\n"
                + testHand);
        System.out.println("");

        System.out.println(t);
        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS played\n"
                + "  hand DOES NOT contain lead suit\n"
                + "  play lowest value card of non-trump suit: FOUR of HEART\n"
                + "card played:"
                + testStrat.chooseCard(testHand, t));
        System.out.println("this does not play trump card as partner (NINE of "
                + "CLUB) is currently winning");
        System.out.println("");

        System.out.println("setting second card played in trick to null "
                + "(simulate partner NOT having played):");
        t.setCard(1, null);
        System.out.println("confirming change:\n"
                + t + "\n");

        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS NOT played\n"
                + "  hand DOES NOT contain lead suit\n"
                + "  play lowest value card of non-lead suit: FOUR of HEART\n"
                + "card played:"
                + testStrat.chooseCard(testHand, t));
        System.out.println("\n"
                + "removing all trump cards, to verify correct discard of "
                + "low-value non trump card");
        //testing use of removespecific()
        testHand.removeSpecific(0);
        //index moves from 2 to 1 after first removal
        testHand.removeSpecific(1);
        //remplacement cards
        ArrayList<Card> replace = new ArrayList<Card>();
        replace.add(new Card(Card.Rank.KING, Card.Suit.HEART));
        replace.add(new Card(Card.Rank.QUEEN, Card.Suit.DIAMOND));
        testHand.addCollection(replace);
        System.out.println("trumps removed and replacements added");
        System.out.println("repeating previous chooseCard() test:\n"
                + "expected outcome:\n"
                + "  partner HAS NOT played\n"
                + "  hand DOES NOT contain lead suit\n"
                + "  play lowest value card of non-lead suit: FOUR of HEART\n"
                + "card played:"
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        //create cards of lead suit to add to hand
        Card addsuit = new Card(Card.Rank.SEVEN, Card.Suit.CLUB);
        Card addsuit2 = new Card(Card.Rank.ACE, Card.Suit.CLUB);
        System.out.println("adding cards: " + addsuit
                + " and " + addsuit2 + " to hand:");
        testHand.addSingleCard(addsuit);
        testHand.addSingleCard(addsuit2);
        System.out.println("confirming successful add:\n"
                + "current hand:\n"
                + testHand);

        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS NOT played\n"
                + "  hand DOES contain lead suit\n"
                + "  play highest value card of lead suit: ACE of CLUB\n"
                + "card played:"
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        System.out.println("changing second played card to TEN of CLUB "
                + "(to simulate parner's card being beaten");
        t.setCard(1, new Card(Card.Rank.TEN, Card.Suit.CLUB));
        System.out.println("confirming change:\n"
                + t);
        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS played but is NOT winning\n"
                + "  hand DOES contain lead suit\n"
                + "  lead suit card in hand IS capable of winning\n"
                + "  play highest value card of lead suit: ACE of CLUB\n"
                + "card played: "
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        System.out.println("removing ACE of CLUB from hand");
        testHand.removeSpecific(4);
        System.out.println("confirming hand:\n"
                + testHand);
        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS played but is NOT winning\n"
                + "  hand DOES contain lead suit\n"
                + "  lead suit card in hand IS NOT capable of winning\n"
                + "  play lowest value card of lead suit: SEVEN of CLUB\n"
                + "card played: "
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        //test for FIRST play avoids trump cards
        System.out.println("testing that the FIRST played card will avoid trump"
                + " suit if hand contains alternatives:\n"
                + "setting trick to null (first card to play)\n"
                + "  confirm trick:");
        t.setCard(0, null);
        t.setCard(1, null);
        System.out.println(t);
        System.out.println("adding ace of trump suit to hand:");
        testHand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.SPADE));
        System.out.println("  confirm hand:\n"
                + testHand);
        System.out.println("");

        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner has NOT played\n"
                + "  hand DOES contain non-trump cards\n"
                + "  play highest value card of non-trump suit: KING of HEART\n"
                + "card played: "
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        System.out.println("duplicating hand without ace to use to remove all "
                + "cards EXCEPT ace from original hand");
        //duplicating hand and removing ace of spades
        Hand testHand2 = new Hand(testHand);
        testHand2.removeSingleCard(new Card(Card.Rank.ACE, Card.Suit.SPADE));
        System.out.println("confirm duplication:\n"
                + "  " + testHand2 + "\n"
                + "using duplicate to remove all other cards from "
                + "original hand");
        testHand.removeHand(testHand2);
        System.out.println("confirm:\n"
                + "  " + testHand + "\n"
                + "adding two more trump cards");
        testHand.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.SPADE));
        testHand.addSingleCard(new Card(Card.Rank.TEN, Card.Suit.SPADE));
        System.out.println("Confirm:\n"
                + "  " + testHand);
        System.out.println("");

        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner has NOT played\n"
                + "  hand DOES NOT contain non-trump cards\n"
                + "  play highest value card of trump suit: ACE of SPADE\n"
                + "card played: "
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        System.out.println("replacing ACE of SPADE with ACE of HEART");
        testHand.removeSingleCard(new Card(Card.Rank.ACE, Card.Suit.SPADE));
        testHand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.HEART));

        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner has NOT played\n"
                + "  hand DOES contain non-trump cards\n"
                + "  play highest value card of non-trump suit: ACE of HEART\n"
                + "card played: "
                + testStrat.chooseCard(testHand, t));
        System.out.println("");

        //test for recognising that a trumped hand cannot win
        System.out.println("testing for bug: not recognising that a trump card "
                + "cannot be beaten, prioritize lower lead suit if hand has no "
                + "higher trump cards:\n"
                + "creating trick to test with:");
        Trick lowTrump = new Trick();
        lowTrump.setTrumpSuit(Card.Suit.SPADE);
        lowTrump.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.CLUB));
        lowTrump.addToTrick(new Card(Card.Rank.FOUR, Card.Suit.SPADE));
        System.out.println(lowTrump + "\n  trick trumpsuit: " 
                + lowTrump.getTrumpSuit()
                + "\n  trick lead suit: " + lowTrump.getLeadSuiit()
                + "\ncreating hand to test:");
        Hand lowThand = new Hand();
        lowThand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.CLUB));
        lowThand.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        lowThand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.HEART));
        lowThand.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.HEART));
        lowThand.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.SPADE));
        System.out.println(lowThand);
        System.out.println("retesting chooseCard()\n"
                + "expected outcome:\n"
                + "  partner HAS played\n"
                + "  hand DOES contain lead suit cards\n"
                + "  hand does NOT contain any HIGHER lead suit so cannot win\n"
                + "  hand does NOT contain any HIGHER trump card than in play\n"
                + "  play lowest value card of lead suit: TWO of CLUB\n"
                + "card played: \n");
        Card chosenCard = testStrat.chooseCard(lowThand, lowTrump);
        System.out.println(chosenCard);
        System.out.println("");

        //test abstraction methods
        System.out.println("testing findHigh():");
        System.out.println("applied to hand:\n  "
                + testHand);
        System.out.println(findHigh(testHand, testHand.getHand().get(0)));
        System.out.println("repeating using Card.max() and choosegreater()");
        System.out.println(findHigh2(testHand, testHand.getHand().get(0)));
        System.out.println("testing findLow() on same hand:\n  "
                + findLow(testHand, testHand.getHand().get(0)));
        System.out.println("repeating using descending comparator\n"
                + findLow2(testHand));
        System.out.println("");

        //testing gethighnont
        System.out.println("testing for highest non trump card:\n"
                + "trump: " + lowTrump.getTrumpSuit());
        System.out.println("highest non trump card: " 
                + getHighNonT(testHand, lowTrump));
        System.out.println("adding second non-trump card to verify:");
        testHand.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.DIAMOND));
        System.out.println(testHand);
        System.out.println("repeat test:\n"
                + getHighNonT(testHand, lowTrump));
        System.out.println("");

        System.out.println("testing gethighlead():\n"
                + "lead suit: " + lowTrump.getLeadSuiit());
        System.out.println(lowTrump);
        System.out.println("adding two lead suit cards to hand");
        testHand.addSingleCard(new Card(Card.Rank.THREE, Card.Suit.CLUB));
        testHand.addSingleCard(new Card(Card.Rank.JACK, Card.Suit.CLUB));
        System.out.println(testHand);
        System.out.println("choosing highest lead suit: " 
                + getHighLead(testHand, lowTrump));

    }

}
