package game.capabilities;

/**
 * Marker interface for items that represent scrap material or organic remains
 * (e.g., ScrapPile, UndeadCorpse). Used by the Meeple's NomadState to detect
 * whether a nearby tile holds workable material, triggering a transition to
 * EngineerState.
 */
public interface Scrap {
}
