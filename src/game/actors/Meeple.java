package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.BasicInventory;
import game.states.CreatureState;
import game.states.meeple.NomadState;
import game.statuses.DizzyStatus;
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
 * When {@link DizzyStatus} is applied by an EngineerState Repelling Pulse,
 * the Meeple skips its action for that turn.
 *
 * @author Ethan
 * @author Tommy
 */
public class Meeple extends StatefulCreature {
    private static final int INITIAL_HIT_POINTS = 40;

    /** Constructor. */
    public Meeple() {
        super("Meeple", 'Ψ', INITIAL_HIT_POINTS, new BasicInventory(), new NomadState());
        this.setIntrinsicWeapon(new MeepleFist());
    }

    @Override
    public boolean skipTurn(GameMap map) {
        return hasStatus(DizzyStatus.class); // Dizzy check — skip turn if disoriented
    }
}
