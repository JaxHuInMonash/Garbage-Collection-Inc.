package game.quota;

/**
 * Pure-function utility that computes the next quota or turn-limit value
 * after a rank-up, always rounding <em>up</em> to the nearest integer.
 *
 * <p>Extracting this logic from {@link QuotaManager} respects SRP and makes
 * the rounding rule trivially unit-testable without standing up the full
 * singleton (KISS + connascence of meaning reduced).
 *
 * @author Mary
 */
public class QuotaScaling {

    private QuotaScaling() { /* utility class – no instances */ }

    /**
     * Returns {@code current} increased by {@code rate} (e.g. 0.05 for +5 %),
     * with the result rounded <em>up</em> to the nearest integer.
     *
     * @param current the current value
     * @param rate    the fractional growth rate (e.g. {@code 0.05})
     * @return the scaled-up value, ceiling-rounded
     */
    public static int scaleUp(int current, double rate) {
        return (int) Math.ceil(current * (1.0 + rate));
    }
}