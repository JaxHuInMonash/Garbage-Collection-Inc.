package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.capabilities.Ability;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 * <p>
 * The ContractedWorker is the player-controlled actor in the game. All allowable
 * actions (item interactions, ground interactions, movement) are handled by the
 * engine's {@link edu.monash.fit2099.engine.positions.World#run()} loop, which
 * delegates to Item and Ground allowableActions methods. This class focuses
 * solely on consciousness checking and menu display.
 *
 * @author Jax
 */
public class ContractedWorker extends Actor {
    private static final int MAX_CREDITS = 1000;
    private static final int INITIAL_CREDITS = 0;

    /**
     * Constructor.
     *
     * @param name        the name of this worker
     * @param displayChar the character representing this worker on the map
     * @param hitPoints   the starting health of this worker
     * @param inventory   the inventory this worker carries
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        this.enableAbility(Ability.ATTACKABLE_BY_CREATURES);

        this.addNewStatistic(ActorStatistics.CREDITS, new BaseStatistic(MAX_CREDITS));
        this.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.UPDATE, INITIAL_CREDITS);
    }

    /**
     * Selects and returns an action for this worker to perform on the current turn.
     * <p>
     * If the worker is unconscious, they are removed from the map.
     * Otherwise, the engine-provided actions list (already populated by the World
     * with item, ground, and movement actions) is presented as a menu for the player.
     * Multi-turn actions are handled by checking for a continuation from the last action.
     *
     * @param actions    collection of possible Actions for this Actor (populated by the engine)
     * @param lastAction the Action this Actor took last turn
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action chosen for this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        // Handle multi-turn Actions
        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // Display the menu and let the player choose
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}
