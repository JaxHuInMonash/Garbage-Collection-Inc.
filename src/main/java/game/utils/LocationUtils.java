package game.utils;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Shared spatial helper methods for Location-based calculations.
 * <p>
 * Centralises common distance and geometry logic used by multiple states
 * (BerserkState knockback, CullingState pull, GuardianState nearest-Worker)
 * to avoid code duplication.
 */
public final class LocationUtils {

    private LocationUtils() {
        // Utility class — not instantiable
    }

    /**
     * Returns the Euclidean distance between two Locations.
     *
     * @param a the first location
     * @param b the second location
     * @return the straight-line distance between {@code a} and {@code b}
     */
    public static double euclidean(Location a, Location b) {
        int dx = a.x() - b.x();
        int dy = a.y() - b.y();
        return Math.sqrt((double) dx * dx + dy * dy);
    }
}
