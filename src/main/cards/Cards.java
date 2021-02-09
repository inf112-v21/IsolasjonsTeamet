package inf112.isolasjonsteamet.roborally.cards;

/**
 * Holds all the different known card types.
 */
public class Cards {

    //public static final CardType FOO = new CardType(123, new MoveForward(1), new RotateLeft());
    public static final CardType FOO = new CardType(84, new MoveOne(18), new MoveTwo(12), new MoveThree(6),
            new BackUp(6), new RotateRight(18), new RotateLeft(18), new UTurn(6));
}