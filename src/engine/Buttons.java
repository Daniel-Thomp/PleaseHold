package engine;

public enum Buttons {
    HANG_UP,
    SPEAKER,
    MUTE,
    DIAL,
    ADD("+"),
    HOLD,
    BTH,
    BACK,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    START,
    HOME,
    ZERO(0), ONE(1), TWO(2), THREE(3),
    FOUR(4), FIVE(5), SIX(6),
    SEVEN(7), EIGHT(8), NINE(9),
    MULT("x"),
    DIV("/"),
    MIN("-");

    public int number;
    public String op = null;

    Buttons(){}

    Buttons(String op) {
        this.op = op;
    }

    Buttons(int number) {
        this.number = number;
    }
}
