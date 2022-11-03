package myApp.blackjack.domain;

import java.util.ArrayList;
import java.util.List;

public class Seat {

    private int bet;
    private final List<Card> cardsDealt;
    private int aces;
    private boolean blackJack;
    private boolean split;
    private Seat splitSeat;

    public Seat(int bet) {
        this.bet = bet;
        this.cardsDealt = new ArrayList<>();
        this.aces = 0;
        this.blackJack = false;
        this.split = false;
    }

    public void addCard(Card card) {
        cardsDealt.add(card);
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public List<Card> getCardsDealt() {
        return cardsDealt;
    }

    public boolean isBlackJack() {
        return blackJack;
    }

    public int getAces() {
        return aces;
    }

    public void setAces(int aces) {
        this.aces = aces;
    }

    public void setBlackJack(boolean blackJack) {
        this.blackJack = blackJack;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public void split() {
        this.splitSeat = new Seat(this.getBet());
        splitSeat.addCard(cardsDealt.get(1));
        this.split = true;
        splitSeat.setSplit(true);
        cardsDealt.remove(1);
    }

    public Seat getSplitSeat() {
        return splitSeat;
    }

    public void setSplitSeat(Seat splitSeat) {
        this.splitSeat = splitSeat;
    }
}
