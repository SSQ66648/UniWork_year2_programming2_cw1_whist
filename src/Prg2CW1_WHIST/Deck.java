/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class contains attributes and methods for modelling
 *                      playing card Deck (52 cards).
 *                      If cards had needed to be actually removed from deck
 *                      upon dealing, a bottom-up arraylist would have been used
 *                      however simply tracking the position of the 'topCard'
 *                      has the same effect from the user's standpoint
 *
 *  Class:              Deck.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190205
 *
 *  Version history:    v0.0    190108  Began class framework
 *                      v0.1    190127  completed constructor
 *                      v0.1.1  190127  attempted iterator with not much success
 *                      v1.0    190129  iterator complete,
 *                                      odd functionality to debug
 *                      v1.1    190129  added spadeIterator and testing
 *                      v1.1a   190201  attempted to change data structure.
 *                                      had to revert to version 1.1
 *                      v1.2    190202  changed and completed both iterators,
 *                                      added and tested peripheral methods
 *                                      successfully tested read/write objects
 *                      v1.2.1  190205  added check for duplicate cards on deck 
 *                                      creation
 *  Notes:
 *      possible changes include:
 *                      rewriting newDeck to re-populate, as in constructor
 *                          instead of using constructor.
 *                      move to arrayList aborted.
 *                      may add printDeck method.
 *
 **************************************************************************** */
package Prg2CW1_WHIST;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Deck implements Serializable, Iterable {

//Serialisation ID:
    static final long serialVersionUID = 49;

//instance variables:
    private final Card[] deck;
    //the current position in the deck can be cosidered the "top card" and 
    //all previous cards as "removed". This prevents the need to ACTUALLY 
    //remove them, but as far as the user/code is concerned, they are gone.
    private int topCard = 0;
    //count of remaining cards in deck
    private final int remaining = 51;

//constructor(s)
    //default
    public Deck() throws DeckLengthException {
        //initialize deck and remaining length
        deck = new Card[Card.Rank.values().length * Card.Suit.values().length];
        if (deck.length != 52) {
            throw new DeckLengthException("Deck not initialised to length 52");
        }
        //counter for deck population(below)
        int i = 0;
        //populate deck and remaining array
        for (Card.Suit s : Card.Suit.values()) {
            for (Card.Rank r : Card.Rank.values()) {
                deck[i] = new Card(r, s);
                i++;
            }
        }
        
        //shuffle deck
        for (int j = 0; j < deck.length; j++) {
            //random int for switching position in deck 
            int randomPos = ThreadLocalRandom.current().nextInt(deck.length);
            Card temp = deck[j];
            deck[j] = deck[randomPos];
            deck[randomPos] = temp;
        }
        
        //check for duplicate cards
        for (int j = 0; j < deck.length; j++) {
            for (int k = 0; k < deck.length; k++) {
                if (j!=k && deck[j].equals(k)) {
                    //placeholder output for testing (never reaches here)
                    System.out.println("DUPLICATE FOUND");
                }
            }
        }
    }

//get methods:
    public Card getCard(int cardNo) {
        return deck[cardNo];
    }

    public int getDeckPosition() {
        return topCard;
    }

    public int getRemaining() {
        return remaining - topCard;
    }

//set methods (for testing)
    //used in testing the iterator (ie iterating through deck again from 0)
    public void setPosition(int newPos) {
        this.topCard = newPos;
    }

//exception class(es)
    //ensure Deck is correct number of cards
    class DeckLengthException extends Exception {
        public DeckLengthException(String message) {
            super(message);
        }
    }

//Iterator(s):
    //default iterable implementation returning custom iterator
    @Override
    public Iterator<Card> iterator() {
        DeckIterator defaultIt = new DeckIterator();
        return defaultIt;
    }

    public class DeckIterator implements Iterator<Card> {

        @Override
        public boolean hasNext() {
            //true if current position in deck has not reached final card+1
            //deck is zero indexed from 0-51
            return topCard < 52;
        }

        @Override
        public Card next() {
            //unneeded check here if used as in 'while hasnext()' 
            //but included as extra layer of errorchicking regardless
            if (hasNext()) {
                //return card and increment position in deck
                return deck[topCard++];
            } else {
                //should never reack this point due to check for hasNext
                return null;
            }
        }

        @Override
        public void remove() {
            //increment position in deck to simulate removal of top Card
            topCard++;
        }
    }

    //each method checks for SPADE card
    public class SpadeIterator implements Iterator<Card> {

        //temp holders for current upcoming spade card and its position
        private Card nextSpade;

        @Override
        public boolean hasNext() {
            //loop remainder of deck (remaining cards - current position) 
            //check for spades
            for (int i = topCard; i < remaining; i++) {
                if (deck[i].suit.equals(Card.Suit.SPADE)) {
                    //update value of next SPADE card
                    nextSpade = deck[i];
                    //increment position to begin next search from
                    topCard = i + 1;
                    //return here as SPADE has been found and values updated
                    return true;
                }
            }
            //no remaining SPADE cards, return false
            return false;
        }

        @Override
        public Card next() {
            //return SPADE card
            return nextSpade;
        }

        @Override
        public void remove() {
            //only if the current card in the deck is a spade, remove(incr. pos)
            if (deck[topCard].suit.equals(Card.Suit.SPADE)) {
                topCard++;
            } else {
                System.out.println("Cannot remove current card: not a SPADE.");
            }
        }
    }

//Class methods:
    //returns next card and moves deck position (using iterator as in spec)
    public Card deal() {
        //create method member iterator
        Iterator<Card> itr;
        itr = new DeckIterator();
        //copy next card 
        Card currentCard = itr.next();
        return currentCard;
    }

    //returns number of cards remaining in deck
    public int size() {
        return deck.length - topCard;
    }

    //resets the deck (and shuffles it)
    public static final Deck newDeck(Deck old) throws DeckLengthException {
        //this could just be a new deck but spec sheet specifies 'reinitialises 
        //the deck' so presumed the deck in question was to be manipulated.
        old = new Deck();
        return old;
    }

//test harness    
//-------------------------------------------------------------------------
    public static void main(String[] args) throws DeckLengthException,
            FileNotFoundException, IOException, ClassNotFoundException {
        //test deck constructor
        System.out.println("testing Deck():");
        Deck testDeck = new Deck();
        for (int i = 0; i < testDeck.deck.length; i++) {
            System.out.println(testDeck.deck[i]);
        }
        System.out.println("");

        //test refreshDeck
        System.out.println("\nrefreshing deck");
        testDeck = newDeck(testDeck);
        System.out.println("new deck:");
        for (Card deck1 : testDeck.deck) {
            System.out.println(deck1);
        }
        System.out.println("");

        //test iterator
        System.out.println("default custom iterator test:\n"
                + "using next() -including test for hasNext()-");
        Iterator<Card> itr = testDeck.new DeckIterator();
        while (itr.hasNext()) {
            System.out.println("Card found manually: "
                    + testDeck.getCard(testDeck.topCard)
                    + "\n position in deck: " + testDeck.topCard
                    + "\n Cards remaining: " + testDeck.getRemaining()
                    + "\n matching next() output: " + itr.next());
        }

        //testing remove()
        System.out.println("\ntesting remove():\n"
                + "final remaining card value of -1 is expected due to deck "
                + "being 0-51 indexed.\n   "
                + "ie. if the final card ('0-th remaining card') is removed, "
                + "the decrement reaches -1.\n   this could be fixed by "
                + "workaround, eg printing '(remaining+1)', but is unnnessisary"
                + " for testing purposes.\n\n"
                + "Iterator reset to first card position\n");
        //resetting deck index(testing purposes)     
        testDeck.setPosition(0);
        while (itr.hasNext()) {
            System.out.println(" checking Current topCard: "
                    + testDeck.getCard(testDeck.topCard));
            System.out.println("attempting remove()");
            itr.remove();
            System.out.println("card removed. remaining cards: "
                    + testDeck.getRemaining());
        }
        System.out.println("");

        //deal() test
        //reset deck position
        testDeck.setPosition(0);
        System.out.println("testing deal() -(loop of five for brevity):\n");
        for (int i = 0; i < 5; i++) {
            System.out.println("Dealing from deck posiotion: " +testDeck.topCard
                    + "\n Card: " + testDeck.deal()
                    + "\n cards remaining: " + testDeck.size());
        }
        System.out.println("");

        //test spade iterator
        System.out.println("testing SPADE iterator on above deck:");
        //reset deck position
        testDeck.setPosition(0);
        Iterator<Card> spadeIter = testDeck.new SpadeIterator();
        System.out.println("Spade list: ");
        while (spadeIter.hasNext()) {
            System.out.println(spadeIter.next());
        }
        System.out.println("");

        //test serializable
        //reset deck position 
        testDeck.setPosition(0);
        String filename = "spadeSerial.ser";
        System.out.println("testing write spadeIterator to file "
                + "(serializable):\n"
                + "Filename: " + filename);
        //create file and object streams
        FileOutputStream fileOut = new FileOutputStream(filename);
        try (ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            while (spadeIter.hasNext()) {
                objOut.writeObject(spadeIter.next());
            }
        } catch (IOException e) {
            System.out.println("IO error printing stacktrace:");
            e.printStackTrace();
        }
        System.out.println("test write complete");
        System.out.println("");

        //read in file to ensure accuracy/ successful write
        System.out.println("Reading in serializable file to verify contents:");
        //holding arrayList
        ArrayList<Card> readCardsIn = new ArrayList<Card>();
        //EoF trigger
        boolean more = true;
        //read file
        FileInputStream fileIn = new FileInputStream(filename);
        try (ObjectInputStream objIn = new ObjectInputStream(fileIn)) {
            while (more = true) {
                Card inputCard = (Card) objIn.readObject();
                System.out.println(inputCard);
                readCardsIn.add(inputCard);
            }
            objIn.close();
        } catch (EOFException e) {
            more = false;
            System.out.println("Reached end of file");
        }
        //print arraylist
        System.out.println("confirming successful read in to arraylist:\n"
                + readCardsIn);

    }

}
