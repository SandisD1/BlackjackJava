package myApp.blackjack;

import myApp.blackjack.domain.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class Blackjack {

    private static int balance;
    private static Seat[] gameTable;

    private static final int deckCount = 8;
    private static int cardsDrawn = 0;

    public static void main(String[] args) throws InterruptedException {
        Scanner scn = new Scanner(System.in);
        GetDeckResult deck = getDeck();
        boolean anotherRound = true;

        pause();

        System.out.println("Welcome to the blackjack table!");
        balance = deposit();

        while (anotherRound) {
            reshuffle(deck.getDeckId());
            pause();
            getSeatsAndBets();

            int seats = gameTable.length;

            initialDeal(seats, deck);

            for (int i = 0; i < seats - 1; i++) {
                playerDecision(seats, i, deck);
            }
            int dealerScore = dealerDraws(seats, deck);
            checkWinners(dealerScore);
            System.out.println("Current balance: " + balance);
            System.out.println("Would you like to play another round? Y/N");
            anotherRound = scn.nextLine().equalsIgnoreCase("Y");

        }
        System.out.println("Thank you, come again!");
    }


    private static String callApi(String theURL) {
        try {
            URL url = new URL(theURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder inputData = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                inputData.append(inputLine);
            }
            return inputData.toString();
        } catch (IOException e) {
            System.out.println("Notika kļūda!");
            return "";
        }
    }

    private static GetDeckResult getDeck() {
        String result = callApi("https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=" + deckCount);
        JSONObject rawResult = new JSONObject(result);
        return new GetDeckResult(
                rawResult.getBoolean("success"),
                rawResult.getString("deck_id"),
                rawResult.getBoolean("shuffled"),
                rawResult.getInt("remaining")
        );
    }

    private static Card drawCard(String deckId) {
        String result = callApi("https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=1");
        cardsDrawn++;
        JSONObject rawResult = new JSONObject(result);
        JSONObject cardData = (JSONObject) rawResult.getJSONArray("cards").get(0);
        Suit suit = Suit.valueOf(cardData.getString("suit"));
        CardValue value = CardValue.getCardValueByString(cardData.getString("value"));
        return new Card(suit, value);
    }

    private static void reshuffle(String deckId) throws InterruptedException {
        int halfDeck = deckCount * 52 / 2;
        if (cardsDrawn > halfDeck) {
            System.out.println("Please stand by. Cards are being reshuffled");
            callApi("https://deckofcardsapi.com/api/deck/" + deckId + "/shuffle/");
            pause();
            System.out.println("Shuffle done! Lets continue.");
            cardsDrawn = 0;
        }

    }

    private static void pause() throws InterruptedException {
        Thread.sleep(1000);
    }

    private static int deposit() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please make your deposit. $$$");
        System.out.println("With first time deposit bonus get bonus balance, double your deposit, up to 500$ extra");
        int deposit = scan.nextInt();
        String fix = scan.nextLine();
        System.out.println("Thank you for making you first deposit");
        System.out.println("You deposited " + deposit + "$.");
        if (deposit > 500) {
            deposit = 1000 + deposit - 500;
        } else {
            deposit = deposit * 2;
        }
        System.out.println("Your balance is now: " + deposit + "$.");
        return deposit;
    }

    private static void getSeatsAndBets() throws InterruptedException {
        Scanner scan = new Scanner(System.in);
        System.out.println("How many seats would you like to play on?");
        System.out.println("You can select up to seven seats to play on.");
        pause();
        int seats = scan.nextInt();
        String fix = scan.nextLine();
        gameTable = new Seat[seats + 1];
        pause();
        System.out.println("You chose to play on " + seats + " seats");
        pause();
        System.out.println("Place your bets please.");
        for (int i = 0; i < seats; i++) {
            System.out.println("Choose your bet on seat " + (i + 1));
            int bet = scan.nextInt();
            gameTable[i] = new Seat(bet);
            balance -= bet;
            fix = scan.nextLine();
        }
        Seat dealer = new Seat(0);
        gameTable[seats] = dealer;
    }

    private static void initialDeal(int seats, GetDeckResult deck) throws InterruptedException {
        System.out.println("Let's go!!");
        pause();
        System.out.println("Thank you, no more bets!");
        pause();
        System.out.println("Good luck!");
        pause();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < seats; j++) {
                Card myNewCard = drawCard(deck.getDeckId());
                gameTable[j].addCard(myNewCard);
                if (j == seats - 1 && i != 1) {
                    System.out.println("Dealer drew a: " + myNewCard.getCardValue() + " of " + myNewCard.getSuit());
                } else if (j != seats - 1) {
                    System.out.println("Your new card on seat " + (j + 1) + " is: " + myNewCard.getCardValue() + " of " + myNewCard.getSuit());
                } else {
                    System.out.println("Dealer's last card is hidden!");
                }
                pause();
            }
        }
    }

    private static void playerDecision(int seatCount, int playerSeat, GetDeckResult deck) throws InterruptedException {
        Scanner scn = new Scanner(System.in);
        boolean isNotEnough = true;
        Seat player = gameTable[playerSeat];
        pause();
        System.out.println("Your cards on seat " + (playerSeat + 1) + ": ");
        printCardsOnSeat(playerSeat);
        int score = scoreOnSeat(playerSeat);
        if (score == 21) {
            System.out.println("Blackjack!!");
        } else {
            if (player.getAces() > 0) {
                System.out.println("Current score: " + (score - 10) + "/" + score);
            } else {
                System.out.println("Current score: " + score);
            }
            System.out.println("Dealer's open card is: "
                    + gameTable[seatCount - 1].getCardsDealt().get(0).getCardValue() + " of "
                    + gameTable[seatCount - 1].getCardsDealt().get(0).getSuit());
            pause();
            while (isNotEnough) {
                if (player.getCardsDealt().size() == 2) {
                    System.out.println("What would you like to do? Draw a card? (Y)" +
                            " / Double down? (D) / Or stand? (N)");
                    String decision = scn.nextLine();
                    if (decision.equalsIgnoreCase("D")) {
                        System.out.println("Double down! Only one card will be drawn and your bet is doubled.");
                        int bet = player.getBet();
                        balance -= bet;
                        player.setBet(bet * 2);
                        isNotEnough = false;
                    }
                    if (decision.equalsIgnoreCase("N")) {
                        break;
                    }
                } else {
                    System.out.println("Draw another? Y/N");
                    isNotEnough = scn.nextLine().equalsIgnoreCase("Y");
                    if (!isNotEnough) {
                        break;
                    }
                }
                Card myNewCard = drawCard(deck.getDeckId());
                player.addCard(myNewCard);
                score = scoreOnSeat(playerSeat);
                System.out.println("Your new card is: " + myNewCard.getCardValue()
                        + " of " + myNewCard.getSuit());
                pause();
                if (score > 21) {
                    System.out.println(score + " is too many.");
                    System.out.println("This seat lost.");
                    isNotEnough = false;
                } else if (score == 21) {
                    System.out.println("Perfect! " + score);
                    isNotEnough = false;
                } else {
                    if (player.getAces() > 0) {
                        System.out.println("Current score: " + (score - 10) + "/" + score);
                    } else {
                        System.out.println("Current score: " + score);
                    }
                }
            }
        }
    }

    private static void printCardsOnSeat(int seat) throws InterruptedException {
        for (int j = 0; j < gameTable[seat].getCardsDealt().size(); j++) {
            System.out.println(gameTable[seat].getCardsDealt().get(j).getCardValue()
                    + " of " + gameTable[seat].getCardsDealt().get(j).getSuit());
            Thread.sleep(500);
        }
    }

    private static int scoreOnSeat(int seat) throws InterruptedException {
        int scoreOnSeat = 0;
        int aces = 0;
        Seat player = gameTable[seat];
        for (int j = 0; j < player.getCardsDealt().size(); j++) {
            int cardValue = player.getCardsDealt().get(j).getCardValue().getBlacjackValue();
            if (cardValue == 11) {
                aces++;
                player.setAces(aces);
            }
            scoreOnSeat += cardValue;
            if (scoreOnSeat == 21 && j == 1 && !player.isSplit()) {
                player.setBlackJack(true);
                break;
            }
            while (scoreOnSeat > 21) {
                if (aces > 0) {
                    scoreOnSeat -= 10;
                    aces--;
                    player.setAces(aces);
                } else {
                    break;
                }
            }
        }
        return scoreOnSeat;
    }

    private static int dealerDraws(int seatCount, GetDeckResult deck) throws InterruptedException {
        int dealerSeat = seatCount - 1;
        pause();
        System.out.println("\nDealer's cards are");
        pause();
        printCardsOnSeat(dealerSeat);
        int dealerScore = scoreOnSeat(dealerSeat);
        System.out.println("Dealer's score: " + dealerScore);
        pause();
        while (dealerScore < 17) {
            Card myNewCard = drawCard(deck.getDeckId());
            gameTable[dealerSeat].getCardsDealt().add(myNewCard);
            dealerScore = scoreOnSeat(dealerSeat);
            System.out.println("Dealer's new card is: " + myNewCard.getCardValue() + " of " + myNewCard.getSuit());
            pause();
            if (dealerScore > 21) {
                System.out.println("Too Many!");
                System.out.println("Dealer Bust!");
                System.out.println("All remaining seats win!");
                break;
            }
            System.out.println("Dealer's current score: " + dealerScore);
            if (dealerScore >= 17) {
                break;
            }
        }
        return dealerScore;
    }

    private static void checkWinners(int dealerScore) throws InterruptedException {
        Random rando = new Random();
        int dealerSeat = gameTable.length - 1;
        Seat dealer = gameTable[dealerSeat];
        for (int i = 0; i < dealerSeat; i++) {
            Seat player = gameTable[i];
            int message = rando.nextInt(3);
            if (player.isBlackJack()) {
                if (dealer.isBlackJack()) {
                    System.out.println("On seat " + (i + 1) + ": ");
                    pause();
                    System.out.println("Blackjack against Blackjack. It's a push.");
                    balance += player.getBet();
                } else {
                    System.out.println("On seat " + (i + 1) + ": ");
                    pause();
                    balance += player.getBet() + (player.getBet() * 3) / 2;
                    System.out.println("Blackjack baby!!! You Win!");
                }
            } else {
                int scoreOnSeat = scoreOnSeat(i);
                pause();
                if (scoreOnSeat < 22) {
                    if (scoreOnSeat > dealerScore || dealerScore > 21) {
                        System.out.println("On seat " + (i + 1) + ": ");
                        pause();
                        System.out.println("Player won with " + scoreOnSeat + "!");
                        balance += player.getBet() * 2;
                        pause();
                        if (message == 0) {
                            System.out.println("Congratulations!");
                        }
                        if (message == 1) {
                            System.out.println("Well done!");
                        }
                        if (message == 2) {
                            System.out.println("EZ GAME!!");
                        }

                    } else if (scoreOnSeat == dealerScore) {

                        System.out.println("On seat " + (i + 1) + ": ");
                        balance += player.getBet();
                        if (message == 0) {
                            System.out.println("It's a push.");
                        }
                        if (message == 1) {
                            System.out.println("Your bet is returned");
                        }
                        if (message == 2) {
                            System.out.println("Tied score.");
                        }
                    } else {
                        System.out.println("On seat " + (i + 1) + ": ");
                        System.out.println("Player lost with " + scoreOnSeat + " against the dealer's " + dealerScore);
                        if (message == 0) {
                            System.out.println("Unfortunate");
                        }
                        if (message == 1) {
                            System.out.println("Better luck nekt time.");
                        }
                        if (message == 2) {
                            System.out.println("Tough luck");
                        }
                    }
                }
            }
        }
    }
}
