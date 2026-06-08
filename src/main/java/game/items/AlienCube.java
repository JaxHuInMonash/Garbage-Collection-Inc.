package game.items;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.CutAlienCubeAction;
import game.actions.UseAlienCubeAction;
import game.actors.Undead;
import game.capabilities.Ability;
import game.capabilities.Sellable;
import game.spawning.SpawnEffectManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A mysterious alien artifact that can teleport its user to one of three
 * randomly offered locations on the current map. Displays as {@code ◈}.
 * <p>
 * Using the cube corrupts all adjacent ground tiles at the source into
 * {@link game.grounds.ToxicWaste}. The cube can also be sold at the
 * Supercomputer for {@value CREDIT_VALUE} credits — selling is offered via
 * {@link game.grounds.Supercomputer}'s {@code allowableActions}, so it can
 * only be sold while standing next to the Supercomputer. Selling causes
 * an Undead to materialise nearby.
 *
 * @author zhengjun Yang
 * @author Mary Chuang
 * @author Jax
 */
public class AlienCube extends Item implements Sellable {

    private static final int WEIGHT = 2;
    private static final int CREDIT_VALUE = 25;
    private static final int DESTINATION_CHOICES = 3;

    private static final Random RANDOM = new Random();

    /**
     * Constructor.
     */
    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(CREDIT_VALUE));
        this.makePortable();
    }

    /**
     * Called when the cube is sold to the Supercomputer.
     * Removes the cube from the actor's inventory and spawns an
     * {@link Undead} at a random adjacent passable location near the actor.
     *
     * @param actor the actor selling the cube
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        Location actorLocation = map.locationOf(actor);
        for (Exit exit : actorLocation.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor() && adjacent.canActorEnter(new Undead())) {
                try {
                    Undead undead = new Undead();
                    adjacent.addActor(undead);
                    SpawnEffectManager.getInstance().onCreatureSpawned(undead, adjacent);
                } catch (GameEngineException e) {
                    // adjacent tile became occupied between check and addActor; skip
                }
                break;
            }
        }
        return actor + " sold the Alien Cube for " + CREDIT_VALUE + " credits — an Undead materialises nearby!";
    }

    /**
     * Returns the credit value when sold.
     *
     * @return the sell value in credits
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }

    /**
     * Offers up to {@value DESTINATION_CHOICES} pre-selected teleport
     * destinations as separate menu actions, drawn at random from all
     * passable, unoccupied tiles on the owner's current map. Selling is
     * intentionally NOT offered here — it goes through the Supercomputer.
     *
     * @param owner the actor carrying this cube
     * @param map   the current game map
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        for (Location dest : pickRandomDestinations(map, DESTINATION_CHOICES)) {
            actions.add(new UseAlienCubeAction(this, dest));
        }

        boolean hasCutter = owner.getInventory().getItems().stream()
                .anyMatch(i -> i.hasAbility(Ability.CAN_CUT));
        if (hasCutter) {
            actions.add(new CutAlienCubeAction(this));
        }

        return actions;
    }



    /**
     * Collects every passable, unoccupied tile on the given map and returns
     * up to {@code count} of them, shuffled. If the map has fewer candidates
     * than {@code count}, returns whatever is available.
     */
    private List<Location> pickRandomDestinations(GameMap map, int count) {
        List<Location> candidates = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (!loc.getGround().hasAbility(Ability.BLOCKS_ENTRY) && !loc.containsAnActor()) {
                    candidates.add(loc);
                }
            }
        }

        Collections.shuffle(candidates, RANDOM);
        return candidates.subList(0, Math.min(count, candidates.size()));
    }
}
