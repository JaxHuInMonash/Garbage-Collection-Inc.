package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.behaviours.EnforcerBehaviour;
import game.penalty.InfractionManager;
import game.statuses.LockedOutStatus;
/**
 * Extends {@link EnforcerBehaviour}.
 * <p>
 * Each turn, moves the {@link game.actors.EnforcerBot} one step toward the
 * target Worker via greedy pathfinding. When adjacent, deducts Company Credits
 * proportional to infraction level (L1: 50, L2: 150, L3: 300). At level 3,
 * also applies {@link LockedOutStatus} for 5 turns. After fining, resets the
 * infraction level and removes the bot from the map.
 *
 * @author Zhengjun Yang
 */
public class ChaseAndFineAction extends EnforcerBehaviour {
    private static final int FINE_L1 = 50, FINE_L2 = 150, FINE_L3 = 300;
    private static final int LOCKOUT_TURNS = 5;
    /**
     * Constructor.
     *
     * @param target the Worker being pursued
     */
    public ChaseAndFineAction(Actor target) { super(target); }
    /**
     * Each turn: despawn if target gone, fine if adjacent, otherwise move closer.
     *
     * @param bot    the EnforcerBot actor
     * @param botLoc the bot's current location
     * @return the action to perform this turn
     */
    @Override
    public Action operate(Actor bot, Location botLoc) {
        GameMap map = botLoc.map();
        if (!map.contains(target) || !target.isConscious())
            return new DespawnAction(bot, target);

        Location targetLoc = map.locationOf(target);
        Action move = moveToward(bot, botLoc, targetLoc);

        for (Exit exit : botLoc.getExits())
            if (exit.getDestination().equals(targetLoc))
                return new FineAction(bot, target, map);

        if (move == null) return new DoNothingAction();
        return move;
    }
    /**
     * Returns a move action toward the target using greedy pathfinding.
     *
     * @param bot  the bot actor
     * @param from the bot's current location
     * @param to   the target's location
     * @return a move action, or null if no valid move exists
     */
    private Action moveToward(Actor bot, Location from, Location to) {
        double best = Double.MAX_VALUE;
        Action move = null;
        for (Exit exit : from.getExits()) {
            Location d = exit.getDestination();
            if (!d.canActorEnter(bot) || d.containsAnActor()) continue;
            double dist = Math.hypot(d.x() - to.x(), d.y() - to.y());
            if (dist < best) { best = dist;
                move = d.getMoveAction(bot, exit.getName(), exit.getHotKey()); }
        }
        return move;
    }
    /**
     * Inner action: deducts credits, optionally applies LockedOutStatus,
     * resets infraction level, and removes the bot.
     */
    private static class FineAction extends Action {
        private final Actor bot; private final Actor target; private final GameMap map;
        FineAction(Actor b, Actor t, GameMap m) { bot=b; target=t; map=m; }
        @Override public String execute(Actor actor, GameMap gm) {
            InfractionManager mgr = InfractionManager.getInstance();
            int level = mgr.getLevel(target);
            int fine = level==1?FINE_L1:level==2?FINE_L2:FINE_L3;
            target.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, fine);
            String msg = bot+" catches "+target+"! Fined "+fine+" credits (level "+level+").";
            if (level >= 3) { target.addStatus(new LockedOutStatus(LOCKOUT_TURNS));
                msg += " Locked out for "+LOCKOUT_TURNS+" turns!"; }
            mgr.reset(target);
            mgr.clearTarget(target);
            map.removeActor(bot);
            return msg;
        }
        @Override public String menuDescription(Actor a) { return "Fine and despawn"; }
    }
    /**
     * Inner action: clears the active target and removes the bot from the map.
     */
    public static class DespawnAction extends Action {
        private final Actor bot;
        private final Actor target;
        /**
         * Constructor.
         *
         * @param b the EnforcerBot to remove
         * @param t the target Worker to clear from active targets
         */
        public DespawnAction(Actor b, Actor t) { bot=b; target=t; }
        @Override
        public String execute(Actor a, GameMap m) {
            InfractionManager.getInstance().clearTarget(target);
            m.removeActor(bot);
            return bot+" despawns.";
        }
        @Override
        public String menuDescription(Actor a) { return "Despawn"; }
    }
}