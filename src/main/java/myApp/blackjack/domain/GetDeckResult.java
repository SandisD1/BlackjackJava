package myApp.blackjack.domain;

public class GetDeckResult {

    private boolean success;
    private String deckId;
    private boolean shuffled;
    private int remaining;

    public GetDeckResult(boolean success, String deckId, boolean shuffled, int remaining) {
        this.success = success;
        this.deckId = deckId;
        this.shuffled = shuffled;
        this.remaining = remaining;
    }

    public GetDeckResult() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public boolean isShuffled() {
        return shuffled;
    }

    public void setShuffled(boolean shuffled) {
        this.shuffled = shuffled;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
