package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.items.FirstAidKit;

/**
 * An action that uses a FirstAidKit to permanently increase the actor's
 * maximum health by 1 point and restore their health to full.
 * After use, the kit enters a 20-turn cooldown.
 *
 * @author Jax
 */
public class UseFirstAidKitAction extends Action {

    private final FirstAidKit firstAidKit;

    /**
     * Constructor.
     *
     * @param firstAidKit the FirstAidKit to use
     */
    public UseFirstAidKitAction(FirstAidKit firstAidKit) {
        this.firstAidKit = firstAidKit;
    }

    /**
     * Increases the actor's maximum health by 1, restores health to full,
     * and starts the kit's cooldown timer.
     *
     * @param actor the actor using the first aid kit
     * @param map   the map the actor is on
     * @return a description of the result of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Increase maximum health by 1
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, 1);

        // Start cooldown
        firstAidKit.startCooldown();

        return actor + " uses the First Aid Kit. Maximum health increased by 1 and health restored to full! ("
                + actor.getStatistic(ActorStatistics.HEALTH) + "/"
                + actor.getMaximumStatistic(ActorStatistics.HEALTH) + ")";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses First Aid Kit (max HP +1, full heal)";
    }
}
