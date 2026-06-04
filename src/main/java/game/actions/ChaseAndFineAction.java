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

public class ChaseAndFineAction extends EnforcerBehaviour {
    private static final int FINE_L1 = 50, FINE_L2 = 150, FINE_L3 = 300;
    private static final int LOCKOUT_TURNS = 5;

    public ChaseAndFineAction(Actor target) { super(target); }

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

    public static class DespawnAction extends Action {
        private final Actor bot;
        private final Actor target;
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