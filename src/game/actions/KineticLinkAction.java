package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statuses.KineticLinkStatus;

/**
 * The Meeple's GuardianState unique action.
 * <p>
 * Tethers the Meeple to the given Worker by applying a
 * {@link KineticLinkStatus} to the Worker. While active, 50% of each
 * point of damage the Worker takes per tick is forwarded to the Meeple.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class KineticLinkAction extends Action {

    private final Actor worker;

    /**
     * Constructor.
     *
     * @param worker the Worker to tether
     */
    public KineticLinkAction(Actor worker) {
        this.worker = worker;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        worker.addStatus(new KineticLinkStatus(actor));
        return actor + " establishes a Kinetic Link with " + worker + ".";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " establishes Kinetic Link with " + worker;
    }
}
