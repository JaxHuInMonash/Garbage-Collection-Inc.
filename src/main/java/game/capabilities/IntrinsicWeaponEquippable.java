package game.capabilities;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Capability for actors that allow another component to install or swap their
 * intrinsic weapon at runtime.
 * <p>
 * The engine declares {@link edu.monash.fit2099.engine.actors.Actor#getIntrinsicWeapon()
 * Actor.getIntrinsicWeapon()} as {@code public} but
 * {@link edu.monash.fit2099.engine.actors.Actor setIntrinsicWeapon()} as
 * {@code protected}, so by default only the actor itself can change its weapon.
 * Upgrades like the Plasma Gauntlet (REQ3 Combat T3) need to swap the worker's
 * weapon from <em>outside</em> the actor class hierarchy — they discover this
 * capability via {@link edu.monash.fit2099.engine.GameEntity#asCapability(Class)}
 * and call {@link #equipIntrinsicWeapon(IntrinsicWeapon)} polymorphically.
 * <p>
 * Implementations forward the call to the protected setter, which they have
 * subclass access to.
 *
 * @author Jax
 */
public interface IntrinsicWeaponEquippable {

    /**
     * Replace this actor's intrinsic weapon with the given one.
     *
     * @param weapon the weapon to install (must not be null)
     */
    void equipIntrinsicWeapon(IntrinsicWeapon weapon);
}
