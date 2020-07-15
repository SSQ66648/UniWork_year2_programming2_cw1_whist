/**
 * ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class contains attributes and methods for modelling
 *                      a hand (group) of Card.
 *
 *  Class:              Hand.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190203
 *
 *  Version history:    v0.0    190108  Began class framework
 *                      v0.1    190127  completed constructors
 *                      v0.2    190127  changed hand structure to arrayList,
 *                                      completed toString, add methods
 *                                      (collection issues), removeSingle,
 *                                      removeHand, removeSpecific, sortByRank,
 *                                      countSuit, countRank, hasSuit
 *                      v1.0    190203  added methods: aceOrVale, accessors,
 *                                      copyHandOrder, handValues, sort.
 *                                      change structure of most existing method
 *                                      testing of previous methods for change
 *                                      errors. added iterator/ Iterable.
 *                                      extensive testing and debugging values.
 *                                      complete pending few questions
 *                                      confirming functionality
 *  Notes:
 *          -handOrder is a copy of the original hand to preserve its ordering.
 *          This is also updated with any add or remove methods but does not
 *          change the order in which the cards were added.
 *              (cards are removed if present, and added to end of list)
 *              Any sorting or shuffling of the hand will not affect the copy.
 *          -currently the getValues returns with the total value of non-ace
 *          cards in position 0. this could be omitted or removed after use, but
 *          as it is not actually used at any point (aces only high in whist),
 *          i lave left it as it is incase it could be useful at a later point.
 ****************************************************************************
 */
package Prg2CW1_WHIST;

//imports:
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Hand implements Serializable, Iterable {

//Serialisation ID:
    static final long serialVersionUID = 300;

//instance variables
    private ArrayList<Card> hand;
    private ArrayList<Integer> values;
    private ArrayList<Card> handOrder;

//constructor(s)
    //default (empty)
    public Hand() {
        hand = new ArrayList<Card>();
        //initialise hand order-copy
        copyHandOrder();
        //hand is empty, no value calculation
    }

    //array of Cards
    public Hand(Card[] cards) {
        hand = new ArrayList<Card>();
        //add each of passed cards to hand
        for (int i = 0; i < cards.length; i++) {
            hand.add(cards[i]);
        }
        //calculate values of hand
        handValues();
        //preserve order
        copyHandOrder();
    }

    //receiving another hand
    public Hand(Hand altHand) {
        hand = new ArrayList<Card>();
        //add each of passed hand cards to current hand
        for (int i = 0; i < altHand.hand.size(); i++) {
            hand.add(altHand.hand.get(i));
        }
        //caluclate hand total values
        handValues();
        //preserve order
        copyHandOrder();
    }

//accessor methods:
    public ArrayList<Card> getHand() {
        return hand;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public ArrayList<Card> getHandOrder() {
        return handOrder;
    }

//Iterator(s)
    @Override
    public Iterator<Card> iterator() {
        HandIterator defaultHandIt = new HandIterator();
        return defaultHandIt;
    }

    public class HandIterator implements Iterator<Card> {
        //gurrent position in hand
        int handPos = 0;

        @Override
        //true if hand has 1+ cards present
        public boolean hasNext() {
            return handPos < handOrder.size();
        }

        @Override
        public Card next() {
            //return current card and increment posiotion in hand
            return handOrder.get(handPos++);
        }

        @Override
        public void remove() {
            Iterator.super.remove();
            handValues();
        }
    }

//Overrides:    
    //toString
    @Override
    public String toString() {
        return "Cards in Hand: " + hand;
    }

//Class methods
    //copies hand to preserve order of cards
    private void copyHandOrder() {
        handOrder = new ArrayList<Card>();
        for (int i = 0; i < hand.size(); i++) {
            //copy each card in current had
            handOrder.add(hand.get(i));
        }
    }

    //checks total number of aces present and sums non ace total
    //returns int (0-4) as indicater of how many are found
    private int aceOrValue() {
        //reset hand values to empty arraylist (for later re-calculations)
        values = new ArrayList<Integer>();
        int ACEtotal = 0;
        //sum of non-ace values
        int handTotal = 0;
        for (int i = 0; i < hand.size(); i++) {
            //if ace, increment count, else add to current total
            if (hand.get(i).rank.equals(Card.Rank.ACE)) {
                ACEtotal++;
            } else {
                handTotal = handTotal + hand.get(i).rank.getValue();
            }
        }
        //add non-ace total to first position of hand values arraylist
        values.add(handTotal);
        return ACEtotal;
    }

    //sets instance arrayList (values) with all possible total values for hand
    private void handValues() {
        //get number of aces 
        int aceNo = aceOrValue();
        //check if ace was found
        if (aceNo > 0) {
            //calculate number of possible value combinations
            int aceValues = aceNo + 1;
            for (int i = 0; i < aceValues; i++) {
                //add 10*(possible value number aka 'i') to number of aces
                //add this to non-ace total in values arraylist: position 0
                //add this to values arraylist
                values.add(values.get(0) + ((i * 10) + aceNo));
            }
        }
    }

    //adds a single passed card to hand
    public void addSingleCard(Card addC) {
        hand.add(addC);
        //recalculate hand values
        handValues();
        //add to end of order-preservation
        handOrder.add(addC);
    }

    //adds a non-specific collection of cards to hand (as per spec sheet)
    public void addCollection(Collection<Card> collect) {
        Card[] arrayConvert = collect.toArray(new Card[collect.size()]);
        //add all cards from passed colection to hand
        hand.addAll(collect);
        //recalculate hand values
        handValues();
        //add to end of order-preservation
        handOrder.addAll(collect);
    }

    //adds all cards from a passed hand to current hand
    public void addHand(Hand addHand) {
        //add each passed card from hand
        for (int i = 0; i < addHand.hand.size(); i++) {
            hand.add(addHand.hand.get(i));
            //mimic add to ordered hand copy
            handOrder.add(addHand.hand.get(i));
        }
        //recalculate values
        handValues();
    }

    //removes specific (passed) card from current hand if present
    public boolean removeSingleCard(Card discard) {
        boolean removed = false;
        Iterator<Card> checkIt = hand.iterator();
        while (checkIt.hasNext()) {
            //get current card from iterator
            Card temp = checkIt.next();
            if (temp.compareTo(discard) == 0) {
                //if card passed matches one from iterator, remove it
                removed = true;
                handOrder.remove(temp);
                return hand.remove(temp);
            }
        }
        //recalculate hand value
        handValues();
        return removed;

    }

    //removes any cards found in passed hand from current hand if present
    public boolean removeHand(Hand removeHand) {
        boolean removed = false;
        for (int i = 0; i < removeHand.hand.size(); i++) {
            removed = removeSingleCard(removeHand.getHand().get(i));
        }
        //returns if ANY of the cards from that hand have been removed
        return removed;
    }


    //removes and returns specified card (from passed position number)from hand
    public Card removeSpecific(int position) {
        Card returnCard = hand.get(position);
        hand.remove(position);
        //recalculate hand value
        handValues();
        //mimic ordercopy 
        handOrder.remove(position);
        return returnCard;
    }

    //sorts passed hand
    public void sort() {
        Collections.sort(hand);
    }

    //sorts the hand using Card CompareRank comparitor
    public void sortByRank() {
        Comparator<Card> compR = new Card.CompareRank();
        hand.sort(compR);
    }

    //returns number of passed Suit found in current hand
    public int countSuit(Card.Suit countSuit) {
        int totalSuit = 0;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).suit.equals(countSuit)) {
                totalSuit++;
            }
        }
        return totalSuit;
    }

    //returns number of passed Rank found in hand
    public int countRank(Card.Rank countRank) {
        int totalRank = 0;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).rank.equals(countRank)) {
                totalRank++;
            }
        }
        return totalRank;
    }

    //returns true if passed suit is found in current hand
    public boolean hasSuit(Card.Suit hasSuit) {
        boolean found = false;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).suit.equals(hasSuit)) {
                found = true;
            }
        }
        return found;
    }

//test harness    
//-------------------------------------------------------------------------
    public static void main(String[] args) {
        //constructor testing

        //create testHand and testHand 2
        Card cards[] = new Card[5];
        //five random cards
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(Card.Rank.randomRank(), Card.Suit.randomSuit());
        }
        //array of cards constructor(s)
        Hand testHand = new Hand(cards);
        //five different random cards
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(Card.Rank.randomRank(), Card.Suit.randomSuit());
        }
        Hand testHand2 = new Hand(cards);

        //default constructor
        Hand emptyHand = new Hand();

        //hand constructor
        Hand passedHand = new Hand(testHand);

        System.out.println("testing hand constructors:\n"
                + "empty hand:\n"
                + "  " + emptyHand + "\n");

        System.out.println("testing passed array of card constructor(testHand):"
                + "\n  " + testHand + "\n");

        System.out.println("testing passed hand (above hand) constructor\n"
                + "  " + passedHand + "\n");

        //addSingleCard() test
        System.out.println("testing addSingleCard() to empty hand:");
        //create test card
        Card testCard = new Card(Card.Rank.randomRank(),Card.Suit.randomSuit());
        emptyHand.addSingleCard(testCard);
        System.out.println("Additional card: " + testCard
                + "\nempty hand with additional card: " + emptyHand);
        System.out.println("");

        //test addcollection()
        //create collections
        ArrayList<Card> passCollect = new ArrayList<Card>();
        for (int i = 0; i < 3; i++) {
            passCollect.add(new Card(Card.Rank.randomRank(),
                    Card.Suit.randomSuit()));
        }
        System.out.println("testing addCollection():\nCollection to be added:"
                + passCollect);
        testHand.addCollection(passCollect);
        System.out.println("testHand with added collection: " + testHand);
        System.out.println("");

        //testaddHand()
        testHand.addHand(testHand2);
        System.out.println("testing addHand():\n"
                + "hand to add: " + testHand2
                + "\ntestHand with additional hand: " + testHand);
        System.out.println("");

        //test removeSingleCard()
        Card removeCard = new Card(Card.Rank.ACE, Card.Suit.DIAMOND);
        System.out.println("testing removeSingleCard():\n"
                + "adding ace of diamonds to hand:");
        testHand.hand.add(removeCard);
        System.out.println(testHand);
        testHand.removeSingleCard(removeCard);
        System.out.println("card removed:\n" + testHand + "\n"
                + "retesting method with seperate object (different memory "
                + "address):");
        //new card to check for:
        Card rem = new Card(Card.Rank.NINE,Card.Suit.HEART);
        testHand.removeSingleCard(rem);
        System.out.println("removing NINE of HEART. confirming:\n"
                + testHand+"\n");
        
        //test sort()
        System.out.println("testing sort():");
        System.out.println("hand as before:\n"
                + testHand);
        testHand.sort();
        System.out.println("hand after sort():\n"
                + testHand);
        System.out.println("");

        //testing removeHand
        testHand.removeHand(testHand2);
        System.out.println("testing removeHand():\n"
                + "removing hand previously added by addHand():\n"
                + testHand);
        System.out.println("");

        //testing removeSpecific()
        int removePos = 3;
        System.out.println("tetsting removeSpecific(): removing card from "
                + "position " + removePos + " (zero indexed):");
        System.out.println("card removed: " + testHand.removeSpecific(removePos)
                + "\n hand after position removal:\n" + testHand);
        System.out.println("");

        //testing sortByRank()
        testHand.sortByRank();
        System.out.println("hand sorted by sortByRank():\n"
                + testHand);
        System.out.println("");

        //testing countSuit
        System.out.println("testing hand for card suit: CLUB\n"
                + "number of CLUB cards found: "
                + testHand.countSuit(Card.Suit.CLUB));
        System.out.println("");

        //testing countrank
        System.out.println("testing hand for card rank: ACE\n"
                + "number of ACE cards found: "
                + testHand.countRank(Card.Rank.ACE));
        System.out.println("");

        //test hasSuit()
        System.out.println("tetsing hasSuit():\n"
                + "checking hand for suit: DIAMOND:\n"
                + "diamond found: " + testHand.hasSuit(Card.Suit.DIAMOND));
        System.out.println("");

        //test accessor methods
        System.out.println("testing accessor methods:\n"
                + "  getHand(): " + testHand.getHand()
                + "\n  getValues(): " + testHand.getValues()
                + "\n  getHandOrder(): " + testHand.getHandOrder());
        System.out.println("");

        System.out.println("further testing getvalues():");
        //create known list of cards
        Card[] valuetestcards = {
            new Card(Card.Rank.ACE, Card.Suit.CLUB),
            new Card(Card.Rank.ACE, Card.Suit.SPADE),
            new Card(Card.Rank.FIVE, Card.Suit.CLUB),
            new Card(Card.Rank.SIX, Card.Suit.CLUB),
            new Card(Card.Rank.ACE, Card.Suit.DIAMOND)};
        Hand valueTestHand = new Hand(valuetestcards);
        System.out.println("known hand:\n" + valueTestHand
                + "\nassumed values of this hand:\n"
                + " total possible value combinations of 3 ACE cards: 4\n"
                + " list of values: 5 (including position 0 (total of "
                + "non-ace cards)\n"
                + "   assumed values: 11, 14, 24, 34, 44\n"
                + "Method values:\n"
                + valueTestHand.values);
        System.out.println("");

        //test iterator/iterable
        System.out.println("testing iterable:\n");
        //create iterator object
        Iterator<Card> testIt = testHand.new HandIterator();
        System.out.println("using iterator while hasNext() to print next():");
        while (testIt.hasNext()) {
            System.out.println(testIt.next());
        }
        System.out.println("");
        
        
    }

}
