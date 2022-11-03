package myApp.blackjack.domain;

public enum CardValue {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("JACK", 10),
    QUEEN("QUEEN", 10),
    KING("KING", 10),
    ACE("ACE", 11);

    private final String stringValue;

    private final int blacjackValue;

    CardValue(String stringValue, int blacjackValue) {
        this.stringValue = stringValue;
        this.blacjackValue = blacjackValue;
    }

    public int getBlacjackValue() {
        return blacjackValue;
    }

    public static CardValue getCardValueByString(String value) {
        for (CardValue cv : values()) {
            if (cv.stringValue.equals(value)) {
                return cv;
            }
        }
        throw new IllegalArgumentException("Invalid card value was passed!");
    }
}
