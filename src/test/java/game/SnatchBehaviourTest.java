package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.SnatchAction;
import game.actors.ScrapSnatcher;
import game.behaviours.SnatchItemBehaviour;
import game.capabilities.Sellable;
import game.items.AlienArtifact;
import game.items.AluminiumScrap;
import game.items.Flask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the snatching pipeline:
 * SnatchItemBehaviour → SnatchAction → ground-item removal.
 */
class SnatchBehaviourTest {

    @Test
    public void behaviourReturnsNullWhenNothingToSnatch() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);

        Action action = new SnatchItemBehaviour().operate(snatcher, centre);
        assertNull(action);
    }

    @Test
    public void behaviourReturnsSnatchActionWhenSellablePresent() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);
        centre.addItem(new AluminiumScrap());

        Action action = new SnatchItemBehaviour().operate(snatcher, centre);
        assertInstanceOf(SnatchAction.class, action);
    }

    @Test
    public void snatchActionRemovesAllSellableItemsFromGround() throws GameEngineException {
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);
        centre.addItem(new AluminiumScrap());
        centre.addItem(new AlienArtifact());

        Action action = new SnatchItemBehaviour().operate(snatcher, centre);
        action.execute(snatcher, map);

        assertTrue(centre.getItems().isEmpty());
    }

    @Test
    public void snatchActionLeavesNonSellableItemsAlone() throws GameEngineException {
        // Flask is Consumable but NOT Sellable, so the Snatcher should ignore it.
        GameMap map = TestMaps.floor5x5();
        Location centre = map.at(2, 2);
        ScrapSnatcher snatcher = new ScrapSnatcher();
        map.addActor(snatcher, centre);
        centre.addItem(new AluminiumScrap());
        centre.addItem(new Flask());

        Action action = new SnatchItemBehaviour().operate(snatcher, centre);
        action.execute(snatcher, map);

        assertEquals(1, centre.getItems().size());
        Item remaining = centre.getItems().get(0);
        assertFalse(remaining instanceof Sellable);
    }
}