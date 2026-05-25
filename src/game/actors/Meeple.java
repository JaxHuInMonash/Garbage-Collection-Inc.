package game.actors;

import game.BasicInventory;
import game.capabilities.Ability;
import game.states.meeple.NomadState;
import game.weapons.MeepleFist;

/**
 * A compact, friendly support automaton designed to assist Workers.
 * <p>
 * Display character: {@code Ψ}. Starting HP: 40.
 * <p>
 * The Meeple uses a formal State Pattern. Its four states are:
 * {@link game.states.meeple.NomadState},
 * {@link game.states.meeple.EngineerState},
 * {@link game.states.meeple.GuardianState}, and
 * {@link game.states.meeple.OverloadedState}.
 * <p>
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class Meeple extends StatefulCreature {
    private static final int INITIAL_HIT_POINTS = 40;

    /** Constructor. */
    public Meeple() {
        super("Meeple", 'Ψ', INITIAL_HIT_POINTS, new BasicInventory(), new NomadState());
        this.enableAbility(Ability.IS_MEEPLE);
        this.setIntrinsicWeapon(new MeepleFist());
    }
}
