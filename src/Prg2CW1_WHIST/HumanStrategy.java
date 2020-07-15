/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class implements strategy interface and contains methods
 *                      for game play strategies, deferring to user for choice
 *
 *  Class:              HumnanStrategy.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190208
 *
 *  Version history:    v1.0    190207  chooseCard completed, pending final
 *                                      testing, updateData still outstanding
 *                      v1.1    190208  added testing harness and sorting of 
 *                                      hand in chooseCard method
 *                      v1.2    190208  added error checking on user input
 *
 *  Notes:
 *              I had planned to further customise the output for human games:
 *                  -clearing the output window, displaying trick and requesting
 *                   user confirmation to display their hand: for multiple-human
 *                   player games (do not see each other's hand)
 *                  -possibility of ASCII representation of cards: either of 
 *                   hand, of trick, or both.
 *              However i did not have time for this additional, as i had to 
 *              focus on completing as much of AdvancedStrategy as i could
 *
 **************************************************************************** */
package Prg2CW1_WHIST;

//imports
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanStrategy implements Strategy {

//Overrride mthods:    
    @Override
    public Card chooseCard(Hand h, Trick t) {
        //sort hand by suit for player clarity
        Collections.sort(h.getHand(), new Card.CompareSuit());
        //prompt user with current play info
        System.out.println("The current Lead-Suit is: " + t.getLeadSuiit()
                + "\nThe Trump-Suit for this game is: " + t.getTrumpSuit()
                + "\n  The current trick so far:\n  "
                + t + "\n  Cards currently in your hand:");
        //list numerical choices of remaining cards in hand
        for (int i = 0; i < h.getHand().size(); i++) {
            System.out.println(i + ": " + h.getHand().get(i));
        }

        //initialise user input int to -1 as indexing from 0-n
        int choice = -1;
        //check for valid input
        boolean validIn = false;
        do {
            try {
                System.out.println("Please enter number specified to play "
                        + "card:");
                //user input
                Scanner scanner = new Scanner(System.in);
                choice = scanner.nextInt();
                //accept if user input is beween 0 and hand size -1 (0 indexing)
                if (choice >= 0 && choice < h.getHand().size()) {
                    validIn = true;
                }
            } catch (InputMismatchException e) {
                //user has entered something other than an int
                System.out.println("Invalid input format. Interger required.");
            }
            //loop if valid choice is not entered
        } while (!validIn);
        return h.getHand().get(choice);
    }

//unimplemented Override mthods:    
    @Override
    public void updateData(Trick c) {
        //could not think of a use for it in humanStrategy other than printing 
            //to screen, but that is already in place
    }

    //test harness
//------------------------------------------------------------------------------    
    public static void main(String[] args) {

        
        System.out.println("testing choosecard():");
        System.out.println("creating hand to test");
        //create and populate hand
        Card[] suitCards = new Card[13];
        for (int i = 0; i < suitCards.length; i++) {
            suitCards[i] = new Card(Card.Rank.randomRank(),
                    Card.Suit.randomSuit());
        }
        Hand compHand = new Hand(suitCards);
        System.out.println("testing sorting of hand:");

        System.out.println("Hand to sort by suit:");
        for (int i = 0; i < compHand.getHand().size(); i++) {
            System.out.println(compHand.getHand().get(i));
        }
        System.out.println("\nSorting using comparator");
        Comparator suitTest = new Card.CompareSuit();
        Collections.sort(compHand.getHand(), suitTest);
        for (int i = 0; i < compHand.getHand().size(); i++) {
            System.out.println(compHand.getHand().get(i));
        }
        System.out.println("");
        
        System.out.println("creating trick to test");
        //create and play 3 cards in trick
        Trick testTrick = new Trick();
        testTrick.addToTrick(new Card(Card.Rank.FIVE,Card.Suit.CLUB));
        testTrick.addToTrick(new Card(Card.Rank.TEN,Card.Suit.CLUB));
        testTrick.addToTrick(new Card(Card.Rank.TWO,Card.Suit.CLUB));
        testTrick.setTrumpSuit(Card.Suit.SPADE);
        System.out.println("creating player to test");
        //create player
        BasicPlayer testPlayer = new BasicPlayer(0,2);
        //set human strat
        testPlayer.setStrategy(new HumanStrategy());
        //give hand to player
        testPlayer.getPlayerHand().addHand(compHand);
        //test method
        System.out.println(testPlayer.playCard(testTrick));

    }

}
