package game.testfixtures;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.BasicInventory;
import game.capabilities.Ability;
import game.capabilities.Progressable;
import game.upgrades.WorkerProgression;

/**
 * Shared test fixtures for REQ3 unit tests.
 * <p>
 * Why this exists: three engine methods are declared {@code final} —
 * {@link Actor#hasAbility(Enum)}, {@link edu.monash.fit2099.engine.GameEntity#asCapability(Class)},
 * and {@link edu.monash.fit2099.engine.positions.Location#getActorAs(Class)}. Mockito 3.2.4 (without
 * the {@code mockito-inline} mock-maker) cannot stub final methods, so tests
 * that need those methods to return controlled values must use real
 * {@link Actor} subclasses with the real ability/capability state set up
 * through the engine's normal mechanisms.
 * <p>
 * Two fixtures are provided:
 * <ul>
 *   <li>{@link TestActor} — bare concrete Actor with a varargs-configurable
 *       ability set. Use for "worker / hostile / non-worker" categorisation.</li>
 *   <li>{@link TestProgressableWorker} — a {@link Progressable} worker with a
 *       configurable {@link WorkerProgression}. Use for any test that exercises
 *       {@code asCapability(Progressable.class)}.</li>
 * </ul>
 */
public final class TestActors {

    private TestActors() {
        // util
    }

    /**
     * Bare concrete Actor with configurable abilities. The starting HP is high
     * enough that incidental damage during a test won't kill the actor.
     */
    public static class TestActor extends Actor {

        private static final int DEFAULT_MAX_HP = 30;

        public TestActor(String name, char displayChar, Enum<?>... abilities) {
            super(name, displayChar, DEFAULT_MAX_HP, new BasicInventory());
            for (Enum<?> ability : abilities) {
                enableAbility(ability);
            }
        }

        @Override
        public Action playTurn(ActionList actions, Action lastAction,
                               GameMap map, Display display) {
            return null;
        }
    }

    /**
     * A real worker that satisfies {@link Progressable} for tests that exercise
     * the {@code asCapability(Progressable.class)} lookup on the
     * {@link game.upgrades.UpgradeStation} base class. Has the
     * {@link Ability#ATTACKABLE_BY_CREATURES} ability pre-enabled (i.e. it
     * "is a worker" by the codebase's usual ability check).
     */
    public static class TestProgressableWorker extends TestActor implements Progressable {

        private final WorkerProgression progression;

        public TestProgressableWorker(WorkerProgression progression) {
            super("worker", 'w', Ability.ATTACKABLE_BY_CREATURES);
            this.progression = progression;
        }

        @Override
        public WorkerProgression getProgression() {
            return progression;
        }
    }
}
