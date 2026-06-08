package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Depositable;
import game.quota.QuotaManager;

/**
 * Action that deposits a {@link Depositable} item at the Supercomputer,
 * contributing its value as <em>Company Credits</em> toward the current quota.
 *
 * <p>Structure deliberately mirrors {@code SellAction} (connascence of
 * convention / DRY at the design level):
 * <ol>
 *   <li>Award company credits via {@link QuotaManager}.</li>
 *   <li>Delegate side-effects and item removal to {@link Depositable#onDeposited}.</li>
 * </ol>
 *
 * <p>Worker credits are <em>not</em> touched here; company credits and worker
 * credits are entirely separate concerns (SRP).
 *
 * @author Mary
 */
public class DepositAction extends Action {

    private final Depositable depositable;

    /**
     * Constructor.
     *
     * @param depositable the item to deposit
     */
    public DepositAction(Depositable depositable) {
        this.depositable = depositable;
    }

    /**
     * Awards company credits to the quota and applies the item's deposit
     * side-effects. The item is responsible for removing itself from the
     * actor's inventory inside {@link Depositable#onDeposited}.
     *
     * @param actor the actor performing the deposit
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        int value = depositable.getDepositValue();
        QuotaManager.getInstance().addCompanyCredits(value);
        return depositable.onDeposited(actor, map);
    }

    /**
     * Menu description shown to the player.
     *
     * @param actor the actor performing the deposit
     * @return menu label string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " deposits " + depositable + " for " + depositable.getDepositValue()
                + " company credits";
    }
}