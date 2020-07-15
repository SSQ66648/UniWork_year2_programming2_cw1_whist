/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class implements strategy interface and contains methods
 *                      for advanced game play strategies
 *
 *  Class:              AdvancedStrategy.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190211
 *
 *  Version history:    v1.0    190209  began class structure. implemented first
 *                                      versions of updateData and chooseCard
 *                                      added support methods to compare record,
 *                                      in process of adding analyseTrick
 *                                      implemented setMax
 *                      v1.1    190210  completed implementation and testing of
 *                                      first version of analyseTrick(). record
 *                                      of tricks and max updates correctly
 *                      v1.2    190211  class complete (as time allows) did not
 *                                      implement parter-play co-operation.
 *                                      class tested and extensively debugged
 *
 *  Notes:
 *          -VERY rarely, i get a null exception in setMax(), i have tried
 *          debugging it for over an hour and i cannot locate its cause further
 *          than top.getSuit() is 'possible dereference of null pointer ', i
 *          cannot work out HOW this would be the case or what causes it to
 *          happen/not randomly. as i am very pushed for time, i will have to
 *          abandon my hunt but would greatly appreciate feedback if the cause
 *          is obvious? (debug variables always HAS values whenever i could
 *          catch it misbehaving)
 *          -the methods handling 'check the trick up to the point of ' could be
 *          combined by each calling a single 'duplicate up to ' method
 *          -reuses code from basic strategy, not sure if i could share methods
 *          between them?
 *          -the bulk of chooseCard() is an adapted form of that used in basic
 *          with additional record referring, some rewritten more efficiently
 *
 **************************************************************************** */
package Prg2CW1_WHIST;

//imports:
import java.util.Arrays;
import java.util.Collections;

public class AdvancedStrategy implements Strategy {
//instance variables:
    //record potential highest cards of other players (each suit)

    private final Card[] partnerMax
            = {new Card(Card.Rank.ACE, Card.Suit.CLUB),
                new Card(Card.Rank.ACE, Card.Suit.DIAMOND),
                new Card(Card.Rank.ACE, Card.Suit.HEART),
                new Card(Card.Rank.ACE, Card.Suit.SPADE)};
    private final Card[] opponent1Max
            = {new Card(Card.Rank.ACE, Card.Suit.CLUB),
                new Card(Card.Rank.ACE, Card.Suit.DIAMOND),
                new Card(Card.Rank.ACE, Card.Suit.HEART),
                new Card(Card.Rank.ACE, Card.Suit.SPADE)};
    private final Card[] opponent2Max
            = {new Card(Card.Rank.ACE, Card.Suit.CLUB),
                new Card(Card.Rank.ACE, Card.Suit.DIAMOND),
                new Card(Card.Rank.ACE, Card.Suit.HEART),
                new Card(Card.Rank.ACE, Card.Suit.SPADE)};
    //record the cards played during each trick (13 tricks, 4 cards)
    private final Card[][] trickCards = new Card[13][4];
    //trick counter
    private int round = 0;
    //own play number from previous trick
    private int myPlay;
    //card numbers of other players from previous trick
    private int partPlay;
    private int op1Play;
    private int op2Play;

//Set methods:
    //set round counter (back to zero after testing)
    public void setRound(int r) {
        this.round = r;
    }
    //set own card play number (testing)
    public void setMyPlay(int play) {
        this.myPlay = play;
    }

//Class methods:    
//Override methods:
    @Override
    public Card chooseCard(Hand h, Trick t) {
        //initally first card from hand
        Card playCard = h.getHand().get(0);

        //first update other players potential highest cards based on own hand
        //used for first play and to compare own unplayed cards ontop of record 
        handUpdateAll(h);
        //get play order, after reset (used duing updatedata)
        myPlay = 0;
        for (int i = 0; i < t.getPlayedCards().length; i++) {
            if (t.getPlayedCards()[i] != null) {
                //own play number is always 1+ all(any) currently played cards
                myPlay = i + 1;
            }
        }

        //set other player's card numbers
        findPlays();

        //check if first playing card
        if (myPlay != 0) {
            //NOT first,  check hand for lead suit
            if (h.hasSuit(t.getLeadSuiit())) {
                //check if partner has played
                if (t.getPlayedCards()[(myPlay + 2) % 4] != null) {
                    //partner HAS played
                    //check if partner's card is winning
                    if (t.getWinner().equals(t.getPlayedCards()[partPlay])) {
                        //parner IS winning play lowest lead card
                        playCard = getLowLead(h, t);
                    } else {
                        //partner NOT winning, check if am last to play
                        if (myPlay == 3) {
                            //final card to play, no point checking max
                            //default to if can win:high, cant: low
                            playCard = getHighLead(h, t);
                            if (canWin(t, playCard)) {
                                return playCard;
                            } else {
                                playCard = getLowLead(h, t);
                                return playCard;
                            }
                        } else {
                            //check hand for lowest lead-suit (that can win) 
                            //that outranks opponents potential
                            if (compareToMax(h, t, applyMax(myPlay + 1))
                                    != null) {
                                //returned a card: lowest winning outrank
                                playCard = compareToMax(h, t,
                                        applyMax(myPlay + 1));
                                return playCard;
                            } else {
                                //returned null, no winning/outrank card found
                                //play lowest lead suit
                                playCard = getLowLead(h, t);
                                return playCard;
                            }
                        }
                    }
                } else {
                    //partner has NOT played, play highest lead suit if can win
                    playCard = getHighLead(h, t);
                    if (canWin(t, playCard)) {
                        return playCard;
                    } else {
                        //cannot win, play lowest lead
                        playCard = getLowLead(h, t);
                        return playCard;
                    }

                }
            } else {
                //hand does NOT contain lead suit
                //check hand for trump suit
                if (h.hasSuit(t.getTrumpSuit())) {
                    //hand contains trump suit: check for others in play
                    //placeholder for highest found trump in play
                    Card trumpHigh = null;
                    boolean trumpFound = false;

                    for (int i = 0; i < t.getPlayedCards().length; i++) {
                        //loop through played cards looking for trump suit, 
                        //assign highest found to copy
                        if (t.getPlayedCards()[i] != null
                                && t.getPlayedCards()[i].getSuit()
                                        .equals(t.getTrumpSuit())) {
                            //if found flag false, first card must be high value
                            if (!trumpFound) {
                                trumpHigh = t.getPlayedCards()[i];
                            } else {
                                //check next found trump against trumpHigh
                                if (t.getPlayedCards()[i].compareTo(trumpHigh)
                                        == 1) {
                                    trumpHigh = t.getPlayedCards()[i];
                                }
                            }
                            trumpFound = true;
                        }
                    }
                    Collections.sort(h.getHand(), new Card.CompareDescending());
                    if (trumpFound) {
                        boolean canWinBool = false;
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
                            return playCard;
                        }
                    } else {
                        //no trumpcards in play, check if partner has played
                        if (t.getPlayedCards()[partPlay] != null) {
                            //check if partner winning
                            if (!t.getWinner()
                                    .equals(t.getPlayedCards()[partPlay])) {
                                //partner NOT winning, 
                                //play smallest trump that outranks
                                if (compareToMax(h, t, applyMax(op1Play))
                                        != null) {
                                    //card (trump) found that can win, outranks
                                    playCard = compareToMax(h, t,
                                            applyMax(op1Play));
                                    return playCard;
                                } else {
                                    //no winning/outranking trump found, discard
                                    playCard = discard(h, t);
                                }
                            } else {
                                //partner winning play lowest discard
                                playCard = discard(h, t);
                            }
                        } else {
                            //partner NOT played
                            //play smallest trumpcard that outranks opponent
                            if (compareToMax(h, t,
                                    applyMax(myPlay + 1)) != null) {
                                playCard = compareToMax(h, t,
                                        applyMax(myPlay + 1));
                            } else {
                                //opponent can still potentially outrank trump
                                //play (safe) discard - may edit later 
                                playCard = discard(h, t);
                            }
                        }
                    }
                } else {
                    //no lead suit, no trump suit: discard
                    playCard = discard(h, t);
                }
            }
        } else {
            //first to play
            //play highest non-trump card
            playCard = getHighNonT(h, t);
            //if playcard still trumpsuit, ONLY trump cards remain, get highest
            if (playCard.getSuit().equals(t.getTrumpSuit())) {
                playCard = Card.max(h.getHand());
            }
        }
        return playCard;
    }

    //returns lowest value non-lead and non-trump suit card(reused)
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

    //returns lowest lead-suit card from hand (reused from basic)
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

    //updates the recods of played cards and potential player max ranks
    //trick received via viewTrick() from BasicPlayer
    @Override
    public void updateData(Trick c) {
        //record the cards played in completed trick
        for (int i = 0; i < 4; i++) {
            //record card
            trickCards[round][i] = c.getPlayedCards()[i];
        }
        //check played cards against max values and decrement as needed
        updateMax();
        analyseTrick(c);
        round++;
    }

    //compares to potentail max remaing  of a player
    private Card compareToMax(Hand h, Trick t, Card[] opp) {
        boolean outrank = false;
        Card temp = null;
        for (int i = 0; i < h.getHand().size(); i++) {
            //get correct suit
            for (int j = 0; j < 4; j++) {
                if (h.getHand().get(i).getSuit().equals(opp[j].getSuit())) {
                    //check if card from hand outranks potential max 
                    //AND can win current trick
                    try {
                        if (h.getHand().get(i).compareTo(opp[j]) == 1
                                && canWin(t, h.getHand().get(i))) {
                            //if an outranking card has already been found, 
                            //check if this one is lower
                            if (outrank && h.getHand().get(i)
                                    .compareTo(temp) == -1) {
                                temp = h.getHand().get(i);
                            } else if (!outrank) {
                                //first found outranking card
                                outrank = true;
                                //assign to temp
                                temp = h.getHand().get(i);
                            }
                        }
                    } catch (NullPointerException e) {
                        //opponent suit max already at null: cannot be outranked
                        //behave as above
                        if (outrank && h.getHand().get(i)
                                .compareTo(temp) == -1) {
                            temp = h.getHand().get(i);
                        } else if (!outrank) {
                            //first found outranking card
                            outrank = true;
                            //assign to temp
                            temp = h.getHand().get(i);
                        }

                    }
                }
            }
        }
        return temp;
    }

    //returns highest lead-suit card from hand (reused)
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

    //get highest non-trump card (reused from basic)
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

//Class methods:
    //checks over all recorded tricks played so far and updates potential Maxes
    private void updateMax() {
        //create temp 'hand' from played cards (avoid re-writing updatefromhand)
        Hand temp = new Hand();
        for (int i = 0; i < 13; i++) {
            //pass through trick record
            for (int j = 0; j < 4; j++) {
                //check trick has been played
                if (trickCards[i][j] != null) {
                    //add card to temp hand
                    temp.addSingleCard(trickCards[i][j]);
                }
            }
        }
        //use all played played cards so far as hand to update potentials
        handUpdateAll(temp);
    }

    //removes need for repition for all three in calling method
    private void handUpdateAll(Hand h) {
        updateFromHand(h, partnerMax);
        updateFromHand(h, opponent1Max);
        updateFromHand(h, opponent2Max);
    }

    //checks cards from given hand against possible max player ranks and updates
    private void updateFromHand(Hand h, Card[] player) {
        boolean finished = true;
        //for each suit, compare card in hand for match to current max rank
        for (int i = 0; i < player.length; i++) {
            do {
                finished = true;
                for (int j = 0; j < h.getHand().size(); j++) {
                    try {
                        //if card from hand is a match, decrement potential max
                        if (h.getHand().get(j).compareTo(player[i]) == 0) {
                            player[i] = new Card(player[i].getRank().getLast(),
                                    player[i].getSuit());
                            //check hand again for new potential max card    
                            finished = false;
                        }
                    } catch (NullPointerException e) {
                        //null pointer exception caught but not 'handled'
                        //'null of <suit>' :imply no cards of suit remaining
                        //this is the intended behaviour
                    }
                }
            } while (!finished);
        }
    }

    //analyses passed trick against records and judges potential max remaining
    private void analyseTrick(Trick t) {
        //get updated other player card numbers
        findPlays();
        //find which opponent played first/second (assign for legibility)
        int first = firstOp();
        int second = secondOp();

        //was first opponent lead-suit & winning by the 2nd opponent play
        if (!isWin(t, second) && t.getPlayedCards()[first].getSuit()
                .equals(t.getLeadSuiit())) {
            //first NOT winning and WAS lead-suit,
            //was the winning card (at that point) a trump card
            if (!wasTrump(t, second)
                    || t.getLeadSuiit().equals(t.getTrumpSuit())) {
                //next card was NOT trump, but as winning must be lead-suit
                //OR if IS trump, the lead-suit is ALSO trump, contine as lead.
                //did second opponent play lead-suit lower than winner
                if (t.getPlayedCards()[second].getSuit()
                        .equals(t.getLeadSuiit()) && !canWin(t, second)) {
                    //second opponent WAS lead-suit, played rank couldnt win
                    //ergo: second does not have any outranking lead-suit
                    setMax(getWin(t, second), applyMax(second));
                    //setMax has been applied to opponent's max via applyMax
                }
            }
        }
    }

    //returns playerMax array based on their assiciated play-order variable
    private Card[] applyMax(int cardNo) {
        if (cardNo == op1Play) {
            return opponent1Max;
        } else if (cardNo == op2Play) {
            return opponent2Max;
        } else {
            //unneeded debugging print. no testing ever found this a possibility
            //remaining in code to test as class is still incomplete
            System.out.println("NEITHER OPPONENT MATCHES SECOND CARD ORDER");
            return null;
        }
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

    //returns winning card up-to and including second opponent play
    private Card getWin(Trick t, int copy) {
        //copy over required values
        Trick test = new Trick();
        test.setTrumpSuit(t.getTrumpSuit());
        //duplicate trick cards upto and including card to check
        for (int i = 0; i <= copy; i++) {
            test.addToTrick(t.getPlayedCards()[i]);
        }
        return t.getWinner();
    }

    //check if card played at position (copy) could have won trick
    private boolean canWin(Trick t, int copy) {
        //copy over required values
        Trick test = new Trick();
        test.setTrumpSuit(t.getTrumpSuit());
        //duplicate trick cards upto and including card to check
        for (int i = 0; i <= copy; i++) {
            test.addToTrick(t.getPlayedCards()[i]);
        }
        //check if card could have won trick (up to this point)
        return test.getWinner().equals(test.getPlayedCards()[copy]);
    }

    //check if opponent partner was winning trick when opponent played
    private boolean isWin(Trick t, int response) {
        //copy over required values
        Trick test = new Trick();
        test.setTrumpSuit(t.getTrumpSuit());
        //duplicate trick cards upto response
        for (int i = 0; i < response; i++) {
            test.addToTrick(t.getPlayedCards()[i]);
        }
        //check if call was winning at this point
        return test.getWinner()
                .equals(test.getPlayedCards()[(response + 2) % 4]);
    }

    //sets potential max-rank of suit to one-below passed card
    private void setMax(Card top, Card[] opponent) {
        for (int i = 0; i < 4; i++) {
            //confirm which suit is updating from card
            try {
                if (top.getSuit().equals(opponent[i].getSuit())) {
                    if (opponent[i] != null) {
                        if (opponent[i].compareTo(top) == 1) {
                            //current max is higher than passed val
                            opponent[i] = new Card(top.getRank().getLast(),
                                    top.getSuit());
                        }
                    }
                }
            } catch (NullPointerException e) {
                //do nothing as null is representitive of no more cards
            }
        }
    }

    //gets card number played by other players based on own play number
    private void findPlays() {
        this.partPlay = (myPlay + 2) % 4;
        this.op1Play = (myPlay + 1) % 4;
        this.op2Play = (myPlay + 3) % 4;
    }

    //returns first opponent to play a card 
    private int firstOp() {
        if (op1Play < op2Play) {
            return op1Play;
        } else {
            return op2Play;
        }
    }

    //returns second opponent to play (superfluous, but acts as confirmation)
    private int secondOp() {
        if (op1Play > op2Play) {
            return op1Play;
        } else {
            return op2Play;
        }
    }

    //returns true if the winner (up to point) was doing so with a trump card
    private boolean wasTrump(Trick t, int response) {
        //copy over required values
        Trick test = new Trick();
        test.setTrumpSuit(t.getTrumpSuit());
        //duplicate trick cards upto response
        for (int i = 0; i < response; i++) {
            test.addToTrick(t.getPlayedCards()[i]);
        }
        //check if the winning card at this point WAS a trump card
        return test.getWinner().getSuit().equals(test.getTrumpSuit());
    }

//test harness    
//------------------------------------------------------------------------------
    public static void main(String[] args) throws Deck.DeckLengthException {

        //test playermax cards:
        //create object
        AdvancedStrategy testStrat = new AdvancedStrategy();
        System.out.println("partnerMax card values:");
        for (int i = 0; i < testStrat.partnerMax.length; i++) {
            System.out.println(testStrat.partnerMax[i]);
        }
        System.out.println("");

        //create hand
        Hand testHand = new Hand();
        //add known values to hand
        testHand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.CLUB));
        testHand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.HEART));
        testHand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.DIAMOND));
        testHand.addSingleCard(new Card(Card.Rank.ACE, Card.Suit.SPADE));
        testHand.addSingleCard(new Card(Card.Rank.KING, Card.Suit.CLUB));
        testHand.addSingleCard(new Card(Card.Rank.KING, Card.Suit.SPADE));
        testHand.addSingleCard(new Card(Card.Rank.QUEEN, Card.Suit.CLUB));
        testHand.addSingleCard(new Card(Card.Rank.JACK, Card.Suit.SPADE));

        System.out.println("testing the following cards in updateFromHand():");
        for (int i = 0; i < testHand.getHand().size(); i++) {
            System.out.println(testHand.getHand().get(i));
        }
        System.out.println("");
        System.out.println("updated potential max card ranks of partner:");
        testStrat.updateFromHand(testHand, testStrat.partnerMax);
        for (int i = 0; i < testStrat.partnerMax.length; i++) {
            System.out.println(testStrat.partnerMax[i]);
        }
        System.out.println("");

        System.out.println("repeating for opponent1max:");
        testStrat.updateFromHand(testHand, testStrat.opponent1Max);
        for (int i = 0; i < testStrat.opponent1Max.length; i++) {
            System.out.println(testStrat.opponent1Max[i]);
        }
        System.out.println("");

        //initial test of updatedata
        Trick testTrick = new Trick();
        testTrick.setTrumpSuit(Card.Suit.CLUB);
        System.out.println("testing updateData() with the following (completed)"
                + " trick:");
        for (int i = 0; i < 4; i++) {
            testTrick.addToTrick(Card.randomCard());
            System.out.println(testTrick.getPlayedCards()[i]);
        }
        System.out.println("");
        System.out.println("empty record of tricks:");
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(testStrat.trickCards[i][j] + ",");
            }
            System.out.print("\n");
        }
        System.out.println("");

        System.out.println("recording trick");
        testStrat.updateData(testTrick);
        System.out.println("verifying: ");
        for (int i = 0; i < 4; i++) {
            System.out.print(testStrat.trickCards[0][i] + ",");
        }
        System.out.println("\n");
        System.out.println("recording same trick into each row:");
        //reset round counter used by updatedata()
        testStrat.setRound(0);
        do {
            System.out.println("round number: " + testStrat.round);
            testStrat.updateData(testTrick);
            for (int i = 0; i < 4; i++) {
                //need to use round-1 for print out as round increments at call
                System.out.println("testTrick card to be copied: "
                        + testTrick.getPlayedCards()[i] + "\tcopied value: from"
                        + " [" + (testStrat.round - 1) + "][" + i + "]: "
                        + testStrat.trickCards[testStrat.round - 1][i]);
            }
        } while (testStrat.round < 13);
        System.out.println("");

        System.out.println("creating fresh AdvancedStrategy object");
        AdvancedStrategy ud = new AdvancedStrategy();
        System.out.println("random cards are too erratic to test, creating Deck"
                + " to play tricks from");
        Deck d = new Deck();

        System.out.println("recording 13 tricks and printing updated"
                + "partnerMax values:");
        do {
            System.out.println("round number: " + ud.round);
            //create random trick
            Trick rt = new Trick();
            for (int i = 0; i < 4; i++) {
                rt.addToTrick(d.deal());
            }
            ud.updateData(rt);
            System.out.println("recorded values:");
            for (int i = 0; i < 4; i++) {
                //need to use round-1 for print out as round increments at call
                System.out.print(ud.trickCards[ud.round - 1][i] + ",");
            }
            System.out.println("");
            System.out.println("  parnerMax updated values check:");
            for (int i = 0; i < 4; i++) {
                System.out.println("  " + ud.partnerMax[i]);
            }
            System.out.println("confirming other player potentials have been "
                    + "similarly updated:\n opponent1:\t\t   opponent2:");
            for (int i = 0; i < 4; i++) {
                System.out.print(ud.opponent1Max[i] + "\t\t | "
                        + ud.opponent2Max[i] + "\n");
            }

            System.out.println("-----");
        } while (ud.round < 13);
        System.out.println("");

        System.out.println("testing setMax():\n"
                + "creating fresh object: reset opponent1Max:");
        AdvancedStrategy set = new AdvancedStrategy();
        for (int i = 0; i < set.opponent1Max.length; i++) {
            System.out.println(set.opponent1Max[i]);
        }
        Card setC = Card.randomCard();
        System.out.println("setting maximum with card: " + setC);
        set.setMax(setC, set.opponent1Max);
        System.out.println("verifying:");
        for (int i = 0; i < set.opponent1Max.length; i++) {
            System.out.println(set.opponent1Max[i]);
        }
        System.out.println("");

        //visualise trick
        System.out.println("testing logic for analysing trick:");
        AdvancedStrategy ana = new AdvancedStrategy();
        //force play numbers while testing and trick variables
        System.out.println("testing getPlays():");
        ana.myPlay = 1;
        ana.findPlays();

        Trick anaT = new Trick();
        anaT.setTrumpSuit(Card.Suit.SPADE);
        anaT.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.NINE, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.THREE, Card.Suit.CLUB));
        System.out.println("last complete trick played:");
        System.out.println(anaT);
        System.out.println("own card: " + anaT.getPlayedCards()[ana.myPlay]);
        System.out.println("partner card: "
                + anaT.getPlayedCards()[ana.partPlay]);
        System.out.println("opponent1 card: "
                + anaT.getPlayedCards()[ana.op1Play]);
        System.out.println("opponent2 card: "
                + anaT.getPlayedCards()[ana.op2Play]);

        System.out.println("first opponent card number to play: "
                + ana.firstOp());
        System.out.println("");

        System.out.println("Logic to define:\n"
                + "opponent2 played first of the two.\n"
                + "at the point of opponent1 playing, opponent2 was NOT winning"
                + ".\n"
                + "was a card previous to opponent2 winning? (IS there a card "
                + "before opponent 2?.\n"
                + "the card that WAS winning was NOT a trump-suit."
                + "the card that WAS winning IS a lead-suit.\n"
                + "opponent1 did NOT play a trump-card.\n"
                + "opponent1 DID play a lead-suit."
                + "opponent1 played a lead-suit that did NOT beat cards in play"
                + ".\n"
                + "-ergo: opponent1 does NOT have a lead-suit card that can "
                + "beat the winning lead-suit in play.\n");
        System.out.println("");

        System.out.println("expected results:\n"
                + "second playing opponent (opponent 1) will have their max "
                + "CLUB set to 1 below rank of winning CLUB prior to their turn"
                + " (NINE: set to EIGHT)\n"
                + "runing analyseTrick():");
        ana.analyseTrick(anaT);

        System.out.println("opponent1Max:");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + ana.opponent1Max[i]);
        }
        System.out.println("");

        System.out.println("resetting as above for testing the  opponent "
                + "partner IS winning");
        ana = new AdvancedStrategy();
        ana.myPlay = 1;
        //this manual update of findplays is exclusively for the print out.
        //the call in analysetrick() works, but as the output is written here, 
        //it doesnt update before printing, hence the manual call
        ana.findPlays();
        System.out.println("-------------------------------------------------");
        anaT = new Trick();
        anaT.setTrumpSuit(Card.Suit.SPADE);
        anaT.addToTrick(new Card(Card.Rank.NINE, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.THREE, Card.Suit.CLUB));
        System.out.println("last complete trick played:");
        System.out.println(anaT);
        System.out.println("own card: " + anaT.getPlayedCards()[ana.myPlay]);
        System.out.println("partner card: "
                + anaT.getPlayedCards()[ana.partPlay]);
        System.out.println("opponent1 card: "
                + anaT.getPlayedCards()[ana.op1Play]);
        System.out.println("opponent2 card: "
                + anaT.getPlayedCards()[ana.op2Play]);

        System.out.println("first opponent card number to play: "
                + ana.firstOp());
        System.out.println("");

        System.out.println("expected results:\n"
                + "second playing opponent (opponent 1) will not have any "
                + "changes to max\n"
                + "runing analyseTrick():");
        ana.analyseTrick(anaT);

        System.out.println("opponent1Max:");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + ana.opponent1Max[i]);
        }
        System.out.println("");

        System.out.println("resetting as above to test if logic still holda if"
                + "opponents play positions 1 and 3.");
        System.out.println("-------------------------------------------------");

        ana = new AdvancedStrategy();
        ana.myPlay = 2;
        ana.findPlays();

        anaT = new Trick();
        anaT.setTrumpSuit(Card.Suit.SPADE);
        anaT.addToTrick(new Card(Card.Rank.NINE, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.THREE, Card.Suit.CLUB));
        System.out.println("last complete trick played:");
        System.out.println(anaT);
        System.out.println("own card: " + anaT.getPlayedCards()[ana.myPlay]);
        System.out.println("partner card: "
                + anaT.getPlayedCards()[ana.partPlay]);
        System.out.println("opponent1 card: "
                + anaT.getPlayedCards()[ana.op1Play]);
        System.out.println("opponent2 card: "
                + anaT.getPlayedCards()[ana.op2Play]);

        System.out.println("first opponent card number to play: "
                + ana.firstOp());
        System.out.println("");

        System.out.println("expected results:\n"
                + "second playing opponent (opponent 1) will change max CLUB to"
                + " eight as in first test\n"
                + "runing analyseTrick():");
        ana.analyseTrick(anaT);

        System.out.println("opponent1Max:");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + ana.opponent1Max[i]);
        }
        System.out.println("");

        System.out.println("resetting as above to test behaviour if winning "
                + "card is a trumpcard (SPADE)");
        System.out.println("-------------------------------------------------");

        ana = new AdvancedStrategy();
        ana.myPlay = 2;
        ana.findPlays();

        anaT = new Trick();
        anaT.setTrumpSuit(Card.Suit.SPADE);
        anaT.addToTrick(new Card(Card.Rank.NINE, Card.Suit.SPADE));
        anaT.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.THREE, Card.Suit.CLUB));
        System.out.println("last complete trick played:");
        System.out.println(anaT);
        System.out.println("own card: " + anaT.getPlayedCards()[ana.myPlay]);
        System.out.println("partner card: "
                + anaT.getPlayedCards()[ana.partPlay]);
        System.out.println("opponent1 card: "
                + anaT.getPlayedCards()[ana.op1Play]);
        System.out.println("opponent2 card: "
                + anaT.getPlayedCards()[ana.op2Play]);

        System.out.println("first opponent card number to play: "
                + ana.firstOp());
        System.out.println("");

        System.out.println("expected results:\n"
                + "second playing opponent (opponent 1) will NOT change max\n"
                + "runing analyseTrick():");
        ana.analyseTrick(anaT);

        System.out.println("opponent1Max:");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + ana.opponent1Max[i]);
        }
        System.out.println("");

        System.out.println("testing as above, but setting second play opponent"
                + " card to low SPADE (test if logic follows when lead=trump)");
        System.out.println("-------------------------------------------------");

        ana = new AdvancedStrategy();
        ana.myPlay = 2;
        ana.findPlays();

        anaT = new Trick();
        anaT.setTrumpSuit(Card.Suit.SPADE);
        anaT.addToTrick(new Card(Card.Rank.NINE, Card.Suit.SPADE));
        anaT.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        anaT.addToTrick(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.THREE, Card.Suit.SPADE));
        System.out.println("last complete trick played:");
        System.out.println(anaT);
        System.out.println("own card: " + anaT.getPlayedCards()[ana.myPlay]);
        System.out.println("partner card: "
                + anaT.getPlayedCards()[ana.partPlay]);
        System.out.println("opponent1 card: "
                + anaT.getPlayedCards()[ana.op1Play]);
        System.out.println("opponent2 card: "
                + anaT.getPlayedCards()[ana.op2Play]);

        System.out.println("first opponent card number to play: "
                + ana.firstOp());
        System.out.println("");

        System.out.println("expected results:\n"
                + "second playing opponent (opponent 1) WILL change max\n"
                + "runing analyseTrick():");
        ana.analyseTrick(anaT);

        System.out.println("opponent1Max:");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + ana.opponent1Max[i]);
        }
        System.out.println("");

        System.out.println("testing that partner not winning and discarding "
                + "invalid-suit cards is not affecting curent code");

        ana = new AdvancedStrategy();
        ana.myPlay = 2;
        ana.findPlays();

        anaT = new Trick();
        anaT.setTrumpSuit(Card.Suit.SPADE);
        anaT.addToTrick(new Card(Card.Rank.NINE, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.EIGHT, Card.Suit.DIAMOND));
        anaT.addToTrick(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        anaT.addToTrick(new Card(Card.Rank.THREE, Card.Suit.CLUB));
        System.out.println("last complete trick played:");
        System.out.println(anaT);
        System.out.println("own card: " + anaT.getPlayedCards()[ana.myPlay]);
        System.out.println("partner card: "
                + anaT.getPlayedCards()[ana.partPlay]);
        System.out.println("opponent1 card: "
                + anaT.getPlayedCards()[ana.op1Play]);
        System.out.println("opponent2 card: "
                + anaT.getPlayedCards()[ana.op2Play]);

        System.out.println("first opponent card number to play: "
                + ana.firstOp());
        System.out.println("");

        System.out.println("expected results:\n"
                + "second playing opponent (opponent 1) will NOT change max\n"
                + "runing analyseTrick():");
        ana.analyseTrick(anaT);

        System.out.println("opponent1Max:");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + ana.opponent1Max[i]);
        }
        System.out.println("");

        //testing of choosecard
        System.out.println("testing chooseCard():\n"
                + "creating new objects");
        //create trick
        Trick choT = new Trick();
        choT.setTrumpSuit(Card.Suit.CLUB);
        choT.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.DIAMOND));
        choT.addToTrick(new Card(Card.Rank.SEVEN, Card.Suit.DIAMOND));
        //create hand
        Hand choH = new Hand();
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.DIAMOND));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.DIAMOND));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.HEART));
        //create new playerstrat
        AdvancedStrategy choS = new AdvancedStrategy();
        System.out.println("player to play THIRD (partner played)\n"
                + "current trick:\n"
                + choT + "\n"
                + "lead suit: " + choT.getLeadSuiit() + "\n"
                + "trump suit: " + choT.getTrumpSuit());
        //card to set max
        Card choC = new Card(Card.Rank.TEN, Card.Suit.DIAMOND);
        //set max of opponent1
        choS.setMax(choC, choS.applyMax(choS.op1Play));
        System.out.println("setting opponent to play's max for lead suit to: "
                + Arrays.toString(choS.applyMax(choS.op1Play)));
        System.out.println("\nPlayer hand: " + choH);

        System.out.println("choosecard() expected outcome:\n"
                + "  partner HAS played\n"
                + "partner is NOT winning\n"
                + "hand DOES contain lead suit\n"
                + "hand DOES contain winning lead suit\n"
                + "highest lead suit card IS outranked by opponent's potential"
                + " max\n"
                + "play: LOWEST LEAD card: TWO DIAMOND\n"
                + "outcome: ");
        System.out.println(choS.chooseCard(choH, choT));
        System.out.println("");

        System.out.println("----------------");
        System.out.println("repeating as above but changing the opponent's max "
                + "diamond to seven");
        //create trick
        choT = new Trick();
        choT.setTrumpSuit(Card.Suit.CLUB);
        choT.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.DIAMOND));
        choT.addToTrick(new Card(Card.Rank.SEVEN, Card.Suit.DIAMOND));
        //create hand
        choH = new Hand();
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.DIAMOND));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.DIAMOND));
        choH.addSingleCard(new Card(Card.Rank.TEN, Card.Suit.DIAMOND));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.HEART));
        //create new playerstrat
        choS = new AdvancedStrategy();
        System.out.println("player to play THIRD (partner played)\n"
                + "current trick:\n"
                + choT + "\n"
                + "lead suit: " + choT.getLeadSuiit() + "\n"
                + "trump suit: " + choT.getTrumpSuit());
        //card to set max
        choC = new Card(Card.Rank.EIGHT, Card.Suit.DIAMOND);
        //set max of opponent1
        choS.setMax(choC, choS.applyMax(choS.op1Play));
        System.out.println("setting opponent to play's max for lead suit to: "
                + Arrays.toString(choS.applyMax(choS.op1Play)));
        System.out.println("\nPlayer hand: " + choH);

        System.out.println("choosecard() expected outcome:\n"
                + "  partner HAS played\n"
                + "partner is NOT winning\n"
                + "hand DOES contain lead suit\n"
                + "hand DOES contain winning lead suit\n"
                + "highest lead suit card (TEN) is NOT outranked by opponent's"
                + " potential max\n"
                + "hand DOES contain a lower value lead suit that CAN win and "
                + "IS higher than opponent max\n"
                + "play: EIGHT DIAMOND\n"
                + "outcome: ");
        System.out.println(choS.chooseCard(choH, choT));
        System.out.println("");

        System.out.println("----------------");
        System.out.println("retesting that player has no lead, but has trump");

        //create trick
        choT = new Trick();
        choT.setTrumpSuit(Card.Suit.CLUB);
        choT.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.DIAMOND));
        choT.addToTrick(new Card(Card.Rank.SEVEN, Card.Suit.DIAMOND));
        //create hand
        choH = new Hand();
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.TEN, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.HEART));
        //create new playerstrat
        choS = new AdvancedStrategy();
        System.out.println("player to play THIRD (partner played)\n"
                + "current trick:\n"
                + choT + "\n"
                + "lead suit: " + choT.getLeadSuiit() + "\n"
                + "trump suit: " + choT.getTrumpSuit());
        //card to set max
        choC = new Card(Card.Rank.TEN, Card.Suit.CLUB);
        //set max of opponent1
        choS.setMax(choC, choS.applyMax(choS.op1Play));
        System.out.println("setting opponent to play's max for trump suit to: "
                + Arrays.toString(choS.applyMax(choS.op1Play)));
        System.out.println("\nPlayer hand: " + choH);

        System.out.println("choosecard() expected outcome:\n"
                + "  partner HAS played\n"
                + "partner is NOT winning\n"
                + "hand does NOT contain lead suit\n"
                + "hand DOES contain trump suit\n"
                + "highest trump suit card (EIGHT) IS outranked by opponent's"
                + " potential max\n"
                + "play: LOWEST DISCARD\n"
                + "outcome: ");
        System.out.println(choS.chooseCard(choH, choT));
        System.out.println("");

        System.out.println("----------------");
        System.out.println("retesting: has no lead, but has trump "
                + "(not outranked)");

        //create trick
        choT = new Trick();
        choT.setTrumpSuit(Card.Suit.CLUB);
        choT.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.DIAMOND));
        choT.addToTrick(new Card(Card.Rank.SEVEN, Card.Suit.DIAMOND));
        //create hand
        choH = new Hand();
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.TEN, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.HEART));
        //create new playerstrat
        choS = new AdvancedStrategy();
        System.out.println("player to play THIRD (partner played)\n"
                + "current trick:\n"
                + choT + "\n"
                + "lead suit: " + choT.getLeadSuiit() + "\n"
                + "trump suit: " + choT.getTrumpSuit());
        //card to set max
        choC = new Card(Card.Rank.EIGHT, Card.Suit.CLUB);
        //set max of opponent1
        choS.setMax(choC, choS.applyMax(choS.op1Play));
        System.out.println("setting opponent to play's max for trump suit to: "
                + Arrays.toString(choS.applyMax(choS.op1Play)));
        System.out.println("\nPlayer hand: " + choH);

        System.out.println("choosecard() expected outcome:\n"
                + "  partner HAS played\n"
                + "partner is NOT winning\n"
                + "hand does NOT contain lead suit\n"
                + "hand DOES contain trump suit\n"
                + "highest trump suit card (EIGHT) NOT outranked by opponent's"
                + " potential max\n"
                + "play: EIGHT CLUB\n"
                + "outcome: ");
        System.out.println(choS.chooseCard(choH, choT));
        System.out.println("");

        System.out.println("----------------");
        System.out.println("retesting: has no lead, but has 1+ trump "
                + "(not outranked)");

        //create trick
        choT = new Trick();
        choT.setTrumpSuit(Card.Suit.CLUB);
        choT.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.DIAMOND));
        choT.addToTrick(new Card(Card.Rank.SEVEN, Card.Suit.DIAMOND));
        //create hand
        choH = new Hand();
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.TEN, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.HEART));
        //create new playerstrat
        choS = new AdvancedStrategy();
        System.out.println("player to play THIRD (partner played)\n"
                + "current trick:\n"
                + choT + "\n"
                + "lead suit: " + choT.getLeadSuiit() + "\n"
                + "trump suit: " + choT.getTrumpSuit());
        //card to set max
        choC = new Card(Card.Rank.THREE, Card.Suit.CLUB);
        //set max of opponent1
        choS.setMax(choC, choS.applyMax(choS.op1Play));
        System.out.println("setting opponent to play's max for trump suit to: "
                + Arrays.toString(choS.applyMax(choS.op1Play)));
        System.out.println("\nPlayer hand: " + choH);

        System.out.println("choosecard() expected outcome:\n"
                + "  partner HAS played\n"
                + "partner is NOT winning\n"
                + "hand does NOT contain lead suit\n"
                + "hand DOES contain trump suit\n"
                + "highest trump suit card (EIGHT) NOT outranked by opponent's"
                + " potential max\n"
                + "hand containa LOWER trump card NOT outranked\n"
                + "play: TWO CLUB\n"
                + "outcome: ");
        Card crash = choS.chooseCard(choH, choT);
        System.out.println(crash);
        System.out.println("");

        System.out.println("----------------");
        System.out.println("retesting: HAS lead, partner not played");

        //create trick
        choT = new Trick();
        choT.setTrumpSuit(Card.Suit.CLUB);
        choT.addToTrick(new Card(Card.Rank.FIVE, Card.Suit.HEART));
        //create hand
        choH = new Hand();
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.SPADE));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.TEN, Card.Suit.HEART));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.TWO, Card.Suit.CLUB));
        choH.addSingleCard(new Card(Card.Rank.EIGHT, Card.Suit.HEART));
        //create new playerstrat
        choS = new AdvancedStrategy();
        System.out.println("player to play SECOND (partner NOT played)\n"
                + "current trick:\n"
                + choT + "\n"
                + "lead suit: " + choT.getLeadSuiit() + "\n"
                + "trump suit: " + choT.getTrumpSuit());
        //card to set max
        choC = new Card(Card.Rank.JACK, Card.Suit.HEART);
        //set max of opponent1
        choS.setMax(choC, choS.applyMax(choS.op1Play));
        System.out.println("setting opponent to play's max for lead suit to: "
                + Arrays.toString(choS.applyMax(choS.op1Play)));
        System.out.println("\nPlayer hand: " + choH);

        System.out.println("choosecard() expected outcome:\n"
                + "  partner NOT played\n"
                + "hand DOES contain lead suit\n"
                + "hand DOES contain trump suit\n"
                + "cannot know enough about trick to estimate\n"
                + "play highest lead card: TEN HEART\n"
                + "outcome: ");
        Card chase = choS.chooseCard(choH, choT);
        System.out.println(chase);

        System.out.println("=================================================");
        System.out.println("attempting to create game with advancedstrat:");
        //create basicwhist
        BasicWhist game = new BasicWhist();
        game.playGame();

    }
}
