package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.AlienArtifact;
import game.items.AlienCube;
import game.statuses.PoisonStatus;

/**
 * Action for using the Plasma Cutter on an {@link AlienCube} held in the
 * worker's inventory.
 *
 * <p>Effects:
 * <ul>
 *   <li>The Alien Cube is destroyed and removed from the inventory.</li>
 *   <li>An {@link AlienArtifact} is added to the worker's inventory.</li>
 *   <li>The worker is inflicted with {@link PoisonStatus} (1 dmg/turn, 5 turns).</li>
 * </ul>
 *
 * <p>Kept separate from {@link CutAction} (which handles ground-tile targets)
 * to avoid a Divergent Change smell — the two cut contexts have fundamentally
 * different targets and side-effects (SRP).
 *
 * @author Mary
 */
public class CutAlienCubeAction extends Action {

    private static final int POISON_DAMAGE   = 1;
    private static final int POISON_DURATION = 5;

    private final AlienCube cube;

    /**
     * Constructor.
     *
     * @param cube the Alien Cube to cut
     */
    public CutAlienCubeAction(AlienCube cube) {
        this.cube = cube;
    }

    /**
     * Destroys the Alien Cube, produces an Alien Artifact, and poisons the worker.
     *
     * @param actor the worker performing the cut
     * @param map   the current map
     * @return description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // 1. Remove cube from inventory (destroyed)
        actor.getInventory().remove(cube);

        // 2. Drop Alien Artifact into inventory
        AlienArtifact artifact = new AlienArtifact();
        actor.getInventory().add(artifact);

        // 3. Inflict Poison
        actor.addStatus(new PoisonStatus(POISON_DAMAGE, POISON_DURATION));

        String msg = actor + " cuts the Alien Cube with the Plasma Cutter — "
                + "an Alien Artifact is extracted! " + actor + " is poisoned by the toxic residue!";
        System.out.println(msg);
        return msg;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " cuts the Alien Cube with the Plasma Cutter";
    }
}