package game.grounds;

/**
 * Its primary purpose in the universe is to halt the progress of underpaid
 * contracted workers until they can produce the correct rectangular
 * piece of plastic.
 * <p>
 * A door starts locked and can be unlocked by an actor with clearance of {@value REQUIRED_CLEARANCE}.
 *
 * @author Tommy
 */
public class TitaniumDoor extends Door {
    private static final int REQUIRED_CLEARANCE = 3;

    /**
     * Constructor.
     */
    public TitaniumDoor() {
        super('M', "Titanium Door", REQUIRED_CLEARANCE);
    }
}
