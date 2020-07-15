/** ****************************************************************************
 *
 *  Programming 2 Coursework 2018-2019
 *
 *  Title:              WHIST
 *
 *  Description:        Class contains attributes and methods for modelling
 *                      a game of Whist
 *
 *  Class:              BasicWhist.java
 *
 *  Author:             100166648 / SSQ16SHU
 *
 *  Last modified:      190211
 *
 *  Version history:    v0.0   190130  Began class framework as per example,
 *                                      conflicting methodology, confusion as
 *                                      to purpose, need to clarify
 *                      v1.0    190204  completed class pending explanation of
 *                                      purpose of update and viewTrick?
 *                      v2.0    190207  multiple minor changes since 1.0,
 *                                      output changed, test constructor added,
 *                                      support for human players added/tested,
 *                                      playerSelect method added
 *                      v2.1    190209  fixed wrong winner ID bug. added static
 *                                      method to play 1human v 3 basicPlayer.
 *                                      changed formatting of output.
 *                                      extensive testing. added error checking
 *                                      to user input to play again.
 *                      v2.2    190210  added advancedPlayer to playerSelect,
 *                                      modified ai constructor to also populate
 *                                      2basic v 2 advanced if given a '2'
 *                      v2.3    190210  added static method for statistic test
 *                                      of AdvancedStrategy
 *
 *  Notes:
 *              -multiple changes between v1.0 to v2.0, will check commit notes
 *                  for anything notable to add here
 *
 **************************************************************************** */
package Prg2CW1_WHIST;

//imports
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class BasicWhist {

//instance variables:
    private final int playerNo = 4;
    private final int trickNo = 13;
    private final int winPoints = 7;
    private int teamAtotal = 0;
    private int teamBtotal = 0;
    //human player game flag (mostly for output change)
    private boolean humanGame = false;

    private final Player[] players;
    private Card.Suit trumpSuit;

//Class constructor(s):
    //default
    public BasicWhist() {
        players = new Player[playerNo];
        //add players by ID
        for (int i = 0; i < players.length; i++) {
            players[i] = new BasicPlayer(i, (i + 2) % 4);
            playerSelect(players[i], i);
            //check for human added to players
            if (players[i].isHuman()) {
                humanGame = true;
                //if human sort hand
                Collections.sort(players[i].getPlayerHand().getHand(),
                        new Card.CompareSuit());
            }
        }
        //output spacing
        System.out.println("");
    }

    //(auto populates 4 players)-pass int ( 1: 4basic or 2:adv v basic )
    public BasicWhist(int test) {
        players = new Player[playerNo];
        //add players by ID
        for (int i = 0; i < players.length; i++) {
            players[i] = new BasicPlayer(i, (i + 2) % 4);
            if (test == 1) {
                players[i].setStrategy(new BasicStrategy());
            } //create alternating advanced/basic strats
            else if (test == 2 && (i % 2) == 0) {
                players[i].setStrategy(new BasicStrategy());
            } else if (test == 2 && (i % 2) == 1) {
                players[i].setStrategy(new AdvancedStrategy());
            }
        }
    }

    //for humanGame()
    public BasicWhist(String humanG) {
        players = new Player[playerNo];
        //add players by ID
        for (int i = 0; i < players.length; i++) {
            players[i] = new BasicPlayer(i, (i + 2) % 4);
        }
        //set single human player (Player 0)
        players[0].setStrategy(new HumanStrategy());
        //set remaining players to BasicStrategy
        for (int j = 1; j < players.length; j++) {
            players[j].setStrategy(new BasicStrategy());
        }
        //flag as human game
        humanGame = true;
        //output spacing
        System.out.println("");
    }

//Accessor methods:
    public int getTeamAtotal() {
        return teamAtotal;
    }

    public int getTeamBtotal() {
        return teamBtotal;
    }

    public Card.Suit getTrumpSuit() {
        return trumpSuit;
    }

    public Player[] getPlayers() {
        return players;
    }

//Class methods:
    //creates a game consisting of 1 human and 3 basicStrategy players
    //uses own constructor: avoid breaking playerselect option in default 
    public static void humanGame() throws Deck.DeckLengthException {
        //create game with 1 human, 3 basicStrategy
        BasicWhist humanVbasic = new BasicWhist("humanGame");
        //play game (spec did not ask to play entire match)
        humanVbasic.playGame();
    }

    public static void advancedGame() throws Deck.DeckLengthException {
        BasicWhist adv = new BasicWhist(2);
        adv.playGame();
    }

    //essentially same as advancedGame() but loops and displays win statistics 
    public static void advTest() throws Deck.DeckLengthException {
        //carry total
        int bas = 0;
        int adv = 0;
        //number storage
        int[] storeA = new int[21];
        int[] storeB = new int[21];
        int storecount =0;
        //loop 1k
        for (int i = 0; i < 1001; i++) {
            BasicWhist stat = new BasicWhist(2);
            stat.playGame();
            //sum scores
            if (stat.teamAtotal > stat.teamBtotal) {
                bas = bas + stat.teamAtotal;
            } else if (stat.teamBtotal > stat.teamAtotal) {
                adv = adv + stat.teamBtotal;
            }
            //get output every 50 games
            if (i % 50 == 0) {
                //large print bar to make scanning output quicker
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 8; k++) {
                        System.out.print("==========");
                    }
                    System.out.print("\n");
                }
                System.out.println("Game number: " + i);
                System.out.println("Scores:\n"
                        + "  Team A (basic):" + stat.teamAtotal + "\n"
                        + "  Team B (advanced): " + stat.teamBtotal);
                System.out.println("----------------------------------------");
                System.out.println("Running totals:\n"
                        + "  Basic: " + bas + "\n"
                        + "Advanced: " + adv+"\n"
                                + "----------------------------------------");
                //output too cluttered with game output:
                //collect scores
                storeA[storecount] = bas;
                storeB[storecount] = adv;
                storecount++;
            }
        }
        System.out.println("\n\n\n\nFinal score output:\n"
                + "Basic: ");
        for (int i = 0; i < storeA.length; i++) {
            System.out.print(storeA[i]+",");
        }
        System.out.println("\nAdvanced: ");
        for (int i = 0; i < storeB.length; i++) {
            System.out.print(storeB[i]+",");
        }
    }

    //returns a player type for game based on user input (passed arg)
    private void playerSelect(Player playerInput, int no) {
        int input = 0;
        //check for valid choice
        boolean validPlayer = false;
        do {
            try {
                System.out.println("Please enter number to choose type of "
                        + "player\n  (" + (no + 1) + " of 4)\n"
                        + "Enter:\n"
                        + "  1: BasicPlayer\n"
                        + "  2: HumanPlayer\n"
                        + "  3: AdvancedPlayer");
                Scanner inputSc = new Scanner(System.in);
                input = inputSc.nextInt();
                //accept if user input is beween 0 and hand size -1 (0 indexing)
                if (input == 1 || input == 2 || input == 3) {
                    validPlayer = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input format. Interger required\n");
            }
        } while (!validPlayer);
        //output spacing
        System.out.println("");

        switch (input) {
            case 1:
                playerInput.setStrategy(new BasicStrategy());
                break;
            case 2:
                playerInput.setStrategy(new HumanStrategy());
                System.out.println("----------");
                System.out.println("Welcome Player: " + playerInput.getID()
                        + "!\nyou are parnered with: Player "
                        + playerInput.getPartnerID());
                System.out.println("----------");
                break;
            case 3:
                playerInput.setStrategy(new AdvancedStrategy());
                break;
        }
    }

    //get playerID from winning card in trick
    public int winningPlayer(Trick t, int leadPlayerID) {
        //update winner in trick
        System.out.println("   Winning card: " + t.getWinner());
        int winner = (leadPlayerID + t.getwinningID()) % playerNo;
        //(first player number + winning card number)%4 
        //= player number of winning card
        System.out.println("   by Player: " + winner);
        return winner;
    }

    //deals hands to players
    public void dealHands(Deck newDeck) {
        //dealt cards counter
        int dealt = 0;
        //13 tricks
        while (newDeck.getRemaining() >= 0) {
            //loop players dealing cards
            players[dealt % playerNo].dealCard(newDeck.deal());
            dealt++;
        }
    }

    //plays single trick 
    public Trick playTrick(Player lead) {
        Trick t = new Trick();
        t.setTrumpSuit(trumpSuit);
        int playerID = lead.getID();
        for (int i = 0; i < playerNo; i++) {
            //get 'next' player number
            int next = (playerID + i) % playerNo;
            if (players[next].isHuman()) {
                System.out.println("-----");
                System.out.println("Your turn, Player "
                        + players[next].getID());
            }
            //add players card to trick using strategy
            t.addToTrick(players[next].playCard(t));
        }
        return t;
    }

    //plays a game (round of 13 tricks) 
    public void playGame() throws Deck.DeckLengthException {
        //set random trumpsuit for game
        trumpSuit = Card.Suit.randomSuit();
        //reset any existing round scores and game points
        int gamepoints = 0;
        int teamAround = 0;
        int teamBround = 0;
        //create deck to deal from
        Deck deck = new Deck();
        dealHands(deck);
        //set trumps for each player 
        //(for later player-interface use: not implemented this way)
        for (int i = 0; i < playerNo; i++) {
            players[i].setTrumps(trumpSuit);
        }
        //get random player number for lead player
        int firstPlayer;
        //for each trick loop
        for (int i = 0; i < trickNo; i++) {
            firstPlayer = ThreadLocalRandom.current().nextInt(0, 4);
            System.out.println("----------------------------------------");
            System.out.println("Trick number : " + i);
            Trick trick = playTrick(players[firstPlayer]);
            //display for ai-only game information (human always displays)
            if (!humanGame) {
                System.out.println("TRUMP suit: " + trumpSuit);
                System.out.println("LEAD suit: " + trick.getLeadSuiit());
            }
            System.out.println("--------------------");
            System.out.println("trick played:");
            for (int j = 0; j < trick.getPlayedCards().length; j++) {
                System.out.println(trick.getPlayedCards()[j]);
            }
            //print winning play details
            int winner = winningPlayer(trick, firstPlayer);
            System.out.println("--------------------");
            if (winner == 0 || winner == 2) {
                teamAround++;
            } else if (winner == 1 || winner == 3) {
                teamBround++;
            }

            //"send the trick to each player"
            for (int j = 0; j < playerNo; j++) {
                players[j].viewTrick(trick);
            }

            //display current running game score
            System.out.println("Current round scores:\n"
                    + "   Team A: " + teamAround
                    + "\n   Team B: " + teamBround);
            System.out.println("----------------------------------------");
        }
        //get game points for winner (number of tricks above six) add to total
        System.out.println("****************************************");
        if (teamAround > 6) {
            gamepoints = teamAround - 6;
            teamAtotal = teamAtotal + gamepoints;
            System.out.println("Team A wins the game, earning " + gamepoints
                    + " points");
        } else if (teamBround > 6) {
            gamepoints = teamBround - 6;
            teamBtotal = teamBtotal + gamepoints;
            System.out.println("Team B wins the game, earning " + gamepoints
                    + " points");
        }
        System.out.println("Current match scores:\n"
                + "   Team A: " + teamAtotal + "\n"
                + "   Team B: " + teamBtotal);
        System.out.println("****************************************");
    }

    //plays the match (first to 7) 
    public void playMatch() throws Deck.DeckLengthException {
        //set score to zero (reset in event of multiple calls on same object)
        teamAtotal = 0;
        teamBtotal = 0;
        while (teamAtotal < winPoints && teamBtotal < winPoints) {
            playGame();
        }
        System.out.println("========================================");
        if (teamAtotal >= winPoints) {
            System.out.println("Team A has won the match with a total of "
                    + teamAtotal + " points!");
        }
        if (teamBtotal >= winPoints) {
            System.out.println("Team B has won the match with a total of "
                    + teamBtotal + " points!");
        }
        System.out.println("========================================");
        //ask user for another match
        System.out.println("\nWould you like to play another match with these "
                + "players?");
        boolean validChoice = false;
        int in = -1;
        do {
            try {
                System.out.println("Please enter 1-yes, 0-no");
                Scanner inputCh = new Scanner(System.in);
                in = inputCh.nextInt();
                //accept if user input is beween 0 and hand size -1 (0 indexing)
                if (in == 1 || in == 0) {
                    validChoice = true;
                    inputCh.close();
                } else {
                    System.out.println("sorry, that was not a valid choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input format. Interger required\n");
            }
        } while (!validChoice);

        if (in == 1) {
            System.out.println("Beginning another match");
            this.playMatch();
        } else {
            System.out.println("End of program");
        }
    }

//------------------------------------------------------------------------------    
    //test harness
    public static void main(String[] args) throws Deck.DeckLengthException {

        //winner test
        System.out.println("testing winningplayer()\n"
                + "plain modulo test of previous bug for 0,3");
        System.out.println((0 + 3) % 4);
        System.out.println("values subbed into method.\n"
                + "expected result:\n"
                + "  lead player: 0\n"
                + "  winning card ID: 3\n"
                + "  winning player number: 3\n"
                + "actual values:");
        Trick wintrick = new Trick();
        //set winning card to element that caused bug
        wintrick.setCard(3, new Card(Card.Rank.ACE, Card.Suit.CLUB));
        //create object to bypass need for static method
        BasicWhist winWhist = new BasicWhist("human");
        System.out.println("trick : " + wintrick);
        wintrick.getWinner();
        System.out.println("trick card winnerID: " + wintrick.getwinningID());
        //attempt to get winning player number from method
        System.out.println("winning trick player ID: "
                + winWhist.winningPlayer(wintrick, 0));
        System.out.println("Bug fixed\n");

//Commented out testing for debugging: solution found, but left code to use 
        //again if it resurfaces:
//        System.out.println("Bug still exists: will return the wrong player "
//                + "number (winning player -1: ie player 0 instead of 3)");
//        System.out.println("Problem persists VERY rarely, explicitly testing"
//                + " ALL POSSIBLE COMBINATIONS:\n");
//        //4 player numbers * 4 winning card numbers
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                int win = (i + j) % 4;
//                System.out.println("1st Player: " + i + ", Winning Card: " + j
//                        +", Winning player number: " + win);
//            }
//        }
//        System.out.println("");
//
//        System.out.println("Attempting to brute-force bug replication:\n"
//                + "creating multiple complete random BasicPlayer games:");
//        //create object
//        for (int i = 0; i < 10; i++) {
//            BasicWhist bugloop = new BasicWhist(1);
//            bugloop.playGame();
//        }
        //test humanGame()
        System.out.println("testing humanGame() -above previous tests to avoid "
                + "needing to complete previous tests (including an entire "
                + "match)\n Calling static method:");
        humanGame();

        BasicWhist testgame = new BasicWhist(1);
        System.out.println("testing player population:");
        for (int i = 0; i < testgame.getPlayers().length; i++) {
            System.out.println("player ID: " + testgame.getPlayers()[i].getID()
                    + " partner ID: "
                    + testgame.getPlayers()[i].getPartnerID());
        }
        System.out.println("");

        System.out.println("checking object variables:\n"
                + "  trumpsuit for game: " + testgame.getTrumpSuit()
                + "\n  Team A score: " + testgame.getTeamAtotal()
                + "\n  Team B score: " + testgame.getTeamBtotal()
                + "\n  trickNo: " + testgame.trickNo
                + "\n  player number: " + testgame.playerNo);
        System.out.println("");

        System.out.println("testing methods:\n"
                + "dealHands():");
        Deck newdeck = new Deck();
        testgame.dealHands(newdeck);
        for (int i = 0; i < testgame.playerNo; i++) {
            System.out.println(testgame.players[i].getPlayerHand()
                    + "\n total number of cards dealt to hand: "
                    + testgame.players[i].getPlayerHand().getHand().size()
                    + "\n number of CLUB cards in hand "
                    + "(checking shuffling of deck): "
                    + testgame.players[i].getPlayerHand()
                            .countSuit(Card.Suit.CLUB));
            System.out.println("");
        }
        System.out.println("Creating new BasicWhist object");
        BasicWhist testgame2 = new BasicWhist(1);
        System.out.println("testing playgame() - "
                + "(and by association playtrick() ):");
        testgame2.playGame();
        System.out.println("\n");

        System.out.println("testing playMatch():\n"
                + "creating new object.\nplaying match");
        BasicWhist testgame3 = new BasicWhist();
        testgame3.playMatch();

    }

}
