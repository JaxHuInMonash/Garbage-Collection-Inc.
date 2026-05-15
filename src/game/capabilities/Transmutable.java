package game.capabilities;

/**
 * Marker interface for items that can be transmuted by the Meeple's
 * TransmuteAction. Implemented by {@link game.items.UndeadCorpse}.
 * Using an interface rather than instanceof allows the Meeple to detect
 * transmutable items via {@code location.getItemsAs(Transmutable.class)}
 * without coupling to any concrete item class.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public interface Transmutable {
}
