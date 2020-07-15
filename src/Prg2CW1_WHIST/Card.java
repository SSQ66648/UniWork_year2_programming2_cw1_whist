/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class contains attributes and methods for modelling
 *                      playing cards.
 *
 *  Class:              Card.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190208
 *
 *  Version history:    v0.0   190108  Began class framework
 *                      v0.1   190125  recovered work. implemented and tested
 *                                     except: comparators and task8
 *                      v1.0    190127  Comparators complete and tested
 *                      v1.1    190127  Complete pending compD check with TA
 *                      v1.1.1  190202  minor layout adjustments. complete.
 *                      v1.2    190208  added compareSuit comparator
 * Notes:
 *      possible changes:
 *          -may add a randomCard method as this has been repeated in testing
 *
 **************************************************************************** */
package Prg2CW1_WHIST;

//imports
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Comparator;

public class Card implements Serializable, Comparable<Card> {

//Serialisation ID:
    static final long serialVersionUID = 100;

//instance variables:
    Rank rank;
    Suit suit;

//enum definition of Rank
    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9),
        TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);

        //enum value declaration and constructor
        private final int value;

        private Rank(int value) {
            this.value = value;
        }

        //enum method(s):
        //getNext returns next (increasing) rank in list
        //populates list using enum implicit static method: values()
        //if called on ACE, loops back to start of list and returns TWO.
        Rank getNext() {
            Rank[] values = Rank.values();

            if (this.ordinal() == 12) {
                return Rank.TWO;
            } else {
                return values[this.ordinal() + 1];
            }
        }

        //returns previous rank
        Rank getLast() {
            Rank[] values = Rank.values();
            if (this.ordinal() != 0) {
                return values[this.ordinal() - 1];
            }
            else{
                return null;
            }
        }

        //get random rank (not specified but possibly useful)
        static Rank randomRank() {
            //get random int
            int randomInt = ThreadLocalRandom.current().nextInt(0, 13);
            //get list of Rank enums
            Rank[] values = Rank.values();
            //return enum value based on random int            
            return values[randomInt];
        }

        //returns the int-value of the enum
        int getValue() {
            return this.value;
        }
    }

//enum definition of Suit
    public enum Suit {
        CLUB(0), DIAMOND(1), HEART(2), SPADE(3);

        //enum value declaration and constructor
        private final int value;

        private Suit(int value) {
            this.value = value;
        }

        //suit enum methods:
        static Suit randomSuit() {
            //get random int
            int randomInt = ThreadLocalRandom.current().nextInt(0, 4);
            //get list of Suit enums
            Suit[] values = Suit.values();
            //return enum value based on random int            
            return values[randomInt];
        }

        //int value of suit enum (not requested but possibly useful)
        int getValue() {
            return this.value;
        }
    }

//Class constructor (default)
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

//Accessor methods:
    public Rank getRank() {
        return this.rank;
    }

    public Suit getSuit() {
        return this.suit;
    }

//overrides:
    //toString
    @Override
    public String toString() {
        return this.rank + " of " + this.suit;
    }

    //compareTo (default sort Ascending): 
    @Override
    public int compareTo(Card checkCard) {
        //cards of same rank (priority ordering); compare suit:
        if (this.rank.ordinal() == checkCard.getRank().ordinal()) {
            if (this.suit.ordinal() < checkCard.getSuit().ordinal()) {
                return -1;
            } else if (this.suit.ordinal() > checkCard.getSuit().ordinal()) {
                return 1;
            } else {
                return 0;
                //while identical cards are not usually possible
                //(exception to this effect thrown in Deck class)
                //a game might be played using more than one Deck
            }
        } //cards not of identical rank; compare:
        else if (this.rank.ordinal() < checkCard.getRank().ordinal()) {
            return -1;
        } else {
            return 1;
        }
    }

//Comparitor Classes:
    //sorts cards into decending order
    public static class CompareDescending implements Comparator<Card> {

        @Override
        public int compare(Card c, Card cNxt) {
            //same rank, compare suits:
            if (c.getRank() == cNxt.getRank()) {
                if (c.getSuit().ordinal() < cNxt.getSuit().ordinal()) {
                    return 1;
                } else if (c.getSuit().ordinal() > cNxt.getSuit().ordinal()) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (c.getRank().ordinal() < cNxt.getRank().ordinal()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    //sorts into ascending order of rank (all 2s, 3s, etc)
    //does not include suit-comparison (as not stated in spec sheet and to 
    //differentiate its function from the compareTo method)
    public static class CompareRank implements Comparator<Card> {

        @Override
        public int compare(Card c, Card cNxt) {
            if (c.getRank().ordinal() == cNxt.getRank().ordinal()) {
                return 0;
            } else if (c.getRank().ordinal() < cNxt.getRank().ordinal()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    //additional comparator to sort by suit, THEN by rank, for human player to 
    //better view hand on output console
    public static class CompareSuit implements Comparator<Card> {

        @Override
        public int compare(Card c, Card cNxt) {
            if (c.getSuit().ordinal() == cNxt.getSuit().ordinal()) {
                if (c.getRank().ordinal() == cNxt.getRank().ordinal()) {
                    return 0;
                } else if (c.getRank().ordinal() < cNxt.getRank().ordinal()) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (c.getSuit().ordinal() < cNxt.getSuit().ordinal()) {
                return -1;
            } else {
                return 1;
            }

        }
    }

//Class methods:
    //returns a random card
    public static Card randomCard(){
        return new Card(Rank.randomRank(),Suit.randomSuit());
    }
    
    
    //find and return hightest value card in a list:
    public static Card max(ArrayList<Card> sortList) {
        //placeholder for max value card in list
        Card max = sortList.get(0);
        Iterator<Card> cardIt = sortList.iterator();
        while (cardIt.hasNext()) {
            //get next card iteration from list
            Card nxtCardIt = cardIt.next();
            //check if greater than max; if so assign.
            if (nxtCardIt.compareTo(max) == 1) {
                max = nxtCardIt;
            }
        }
        return max;
    }

    //return a list of all cards within a list greater than a specified card
    public static ArrayList<Card> chooseGreater(ArrayList<Card> toCheck,
            Comparator compR, Card fulcrum) {
        ArrayList<Card> greaterList = new ArrayList<>();
        for (int i = 0; i < toCheck.size(); i++) {
            //iterate and if card is 'greater' than fulcrum, add to greaterList
            //-comparitor determines definition of 'greater'
            if (compR.compare(toCheck.get(i), fulcrum) == 1) {
                greaterList.add(toCheck.get(i));
            }
        }
        return greaterList;
    }

    //tests functionality of comapritors with additional lambda
    public static void selectTest() {
        //create unsorted list of (non-random) Cards
        ArrayList<Card> selectList = new ArrayList<>();
        selectList.add(new Card(Rank.ACE, Suit.SPADE));
        selectList.add(new Card(Rank.TWO, Suit.CLUB));
        selectList.add(new Card(Rank.KING, Suit.CLUB));
        selectList.add(new Card(Rank.SEVEN, Suit.SPADE));
        selectList.add(new Card(Rank.JACK, Suit.HEART));
        selectList.add(new Card(Rank.THREE, Suit.DIAMOND));
        selectList.add(new Card(Rank.FIVE, Suit.DIAMOND));
        selectList.add(new Card(Rank.FIVE, Suit.SPADE));

        //create single Card to use as argument
        Card stCard = new Card(Rank.TEN, Suit.HEART);

        System.out.println(" selectTest():"
                + " List to sort:");
        //print unsorted list
        for (int i = 0; i < selectList.size(); i++) {
            System.out.println("  " + selectList.get(i));
        }
        System.out.println(" card used as fulcrum: " + stCard);
        //test of CompareDescending:
        //prints list with element LESS THAN the fulcrum Card; this is behaviour
        //dictated by CompareDescending (the lower-value card is "greater")
        System.out.println(" selectTest() of chooseGreater, "
                + "using CompareDescending:");
        Comparator<Card> cDesc = new CompareDescending();
        ArrayList<Card> selectD = chooseGreater(selectList, cDesc, stCard);
        //NB ArrayList could be printed directly, but vertical list is clearer
        for (int i = 0; i < selectD.size(); i++) {
            System.out.println("  " + selectD.get(i));
        }

        //test of CompareRank
        //prints list with elements having a HIGHER RANK than fulcrum Card
        System.out.println(" selectTest() of chooseGreater, "
                + "using CompareRank:");
        Comparator<Card> cRnk = new CompareRank();
        ArrayList<Card> selectR = chooseGreater(selectList, cRnk, stCard);
        //NB ArrayList could be printed directly, but vertical list is clearer
        for (int i = 0; i < selectR.size(); i++) {
            System.out.println("  " + selectR.get(i));
        }
        //test of Lambda (Rank A > Rank B -or- equal rank and Suit A > Suit B)
        Comparator<Card> lamb = (c1, c2) -> {
            if (c1.getRank().equals(c2.getRank())) {
                //same rank, comapre suit
                if (c1.getSuit().equals(c2.getSuit())) {
                    return 0;
                } else if ((c1.getSuit().ordinal()) > c2.getSuit().ordinal()) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (c1.getRank().ordinal() > c2.getRank().ordinal()) {
                return 1;
            } else {
                return -1;
            }
        };
        ArrayList<Card> lamList = chooseGreater(selectList, lamb, stCard);

        System.out.println(" selectTest() of chooseGreater using lambda:");
        for (int i = 0; i < lamList.size(); i++) {
            System.out.println("  " + lamList.get(i));
        }

    }

//test harness
//------------------------------------------------------------------------------    
    public static void main(String[] args) {

        //rank enum check:
        System.out.println("Rank enum check: ");
        for (Rank r : Rank.values()) {
            System.out.println(" " + r + "(" + r.value + ")");
        }
        //output break
        System.out.println("");

        //suit enum, check:
        System.out.println("Suit enum check:");
        for (Suit s : Suit.values()) {
            System.out.println(" " + s + "(" + s.value + ")");
        }
        System.out.println("");

        //creating test card
        Card testCard = new Card(Rank.SIX, Suit.DIAMOND);
        System.out.println("test card: " + testCard);

        //testing rank and suit
        System.out.println("testing accessors:"
                + "\n getRank: " + testCard.getRank()
                + "\n getSuit: " + testCard.getSuit());
        System.out.println("");

        //testing value
        System.out.println("testing int getValues:"
                + "\n rank: (" + testCard.getRank() + ") int value: "
                + testCard.getRank().getValue()
                + "\n suit: (" + testCard.getSuit() + ") int value: "
                + testCard.getSuit().getValue());
        System.out.println("");
        Card randomCard = new Card(Rank.randomRank(), Suit.randomSuit());
        //create list of cards for random methods testing
        ArrayList<Card> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testList.add(new Card(Rank.randomRank(), Suit.randomSuit()));
        }
        //this does not take into account the possibility of duplicate cards
        //this will be handled by the Deck class 
        //(single deck has 52 cards, but a game may use more than one deck)

        //testing methods
        System.out.println("testing methods:"
                + "\n getNext(): " + testCard.rank.getNext()
                + "\n toString(): " + testCard.toString()
                + "\n randomRank() and randomSuit():");
        for (int i = 0; i < testList.size(); i++) {
            System.out.println("  " + testList.get(i));
        }
        System.out.println(" max() -applied to above list: "
                + "\n  " + max(testList));
        System.out.println("");

        System.out.println("Comparitor testing:\n"
                + " CompareDescending -applied to above list:");
        //create comparitor and arrayList to test:
        Comparator compD = new CompareDescending();
        ArrayList<Card> compDtest = testList;
        Collections.sort(compDtest, compD);
        for (int i = 0; i < testList.size(); i++) {
            System.out.println("  " + compDtest.get(i));
        }
        //repeat above for CompareRank
        System.out.println("\n CompareRank -applied to above list:");
        Comparator compR = new CompareRank();
        ArrayList<Card> compRtest = testList;
        Collections.sort(compRtest, compR);
        for (int i = 0; i < testList.size(); i++) {
            System.out.println("  " + compRtest.get(i));
        }
        System.out.println("");

        //test chooseGreater() 
        //(seperate from other test methods as it relies on the comparitors)
        System.out.println("compartmental testing of chooseGreater()\n"
                + "  -applied to above list\n"
                + "  -using fulcrum card: " + randomCard
                + "\n using CompareDescending:");
        ArrayList<Card> chooseList1
                = chooseGreater(testList, compD, randomCard);
        for (int i = 0; i < chooseList1.size(); i++) {
            System.out.println("  " + chooseList1.get(i));
        }
        System.out.println(" using CompareRank:");
        ArrayList<Card> chooseList2
                = chooseGreater(testList, compR, randomCard);
        for (int i = 0; i < chooseList2.size(); i++) {
            System.out.println("  " + chooseList2.get(i));
        }
        System.out.println("");

        //testing complete selectTest (including lambda)
        System.out.println("testing selectTest():");
        selectTest();
        System.out.println("");

        //testing suit-first comparator
        System.out.println("creating hand to test CompareSuit comaparator:");
        //create array of random cards to pass into hand
        Card[] suitCards = new Card[13];
        for (int i = 0; i < suitCards.length; i++) {
            suitCards[i] = new Card(Card.Rank.randomRank(),
                    Card.Suit.randomSuit());
        }
        Hand compHand = new Hand(suitCards);
        System.out.println("Hand to sort by suit:");
        for (int i = 0; i < compHand.getHand().size(); i++) {
            System.out.println(compHand.getHand().get(i));
        }
        System.out.println("\nSorting using comparator");
        Comparator suitTest = new CompareSuit();
        Collections.sort(compHand.getHand(), suitTest);
        for (int i = 0; i < compHand.getHand().size(); i++) {
            System.out.println(compHand.getHand().get(i));
        }
    }
}
