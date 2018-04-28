package org.peyto.gamex;

import org.peyto.redux.Action;
import org.peyto.redux.Store;

import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class EngineThread<TState extends GameState<TState>> implements Runnable {

    private final Store<TState> store;
    private final GameEnvironment<TState> gameEnvironment;

    public EngineThread(GameEnvironment<TState> gameEnvironment) {
        checkEnironment(gameEnvironment);
        this.gameEnvironment = gameEnvironment;
        this.store = new Store<TState>(gameEnvironment.getReducers(), gameEnvironment.getInitialState());
    }

    private void checkEnironment(GameEnvironment<TState> gameEnvironment) {
        checkNotNull(gameEnvironment);
        checkNotNull(gameEnvironment.getReducers(), "Reducers not found");
        checkNotNull(gameEnvironment.getStaticData(), "Static Data not found");
        checkNotNull(gameEnvironment.getTurnValidator(), "Turn Validator not found");
        checkNotNull(gameEnvironment.getInitialState(), "Initial State not defined");
        checkNotNull(gameEnvironment.getTechnicalConfig(), "Technical Configuration not defined");
        for (int i=0; i< gameEnvironment.getStaticData().numberOfPlayers(); i++) {
            checkNotNull(gameEnvironment.getController(i), "Controller for player %s is not defined", i);
        }
    }

    public void run() {
        TState currentState = store.getState();
        while (!currentState.isCompleted()) {
            try {
                Action action = null;
                if (getSystemDispatcher().isSystemAction(store.getState(), getStaticData())) {
                    action = getSystemDispatcher().makeSystemAction(store.getState(), getStaticData());
                } else {
                    int currentPlayerIndex = currentState.getCurrentPlayerIndex();
                    Controller controller = getCurrentController(currentPlayerIndex);
                    Action actionRequest = makeControlledTurn(controller, getStateCopy(), currentPlayerIndex, getStaticData());
                    action = getTurnValidator().validateTurn(getStateCopy(), currentPlayerIndex, getStaticData(), actionRequest);
                }
                store.getDispatcher().dispatch(action);
                store.getDispatcher().dispatch(SwitchPlayerAction.SWITCH_PLAYER_ACTION);
            } catch (RuntimeException e) {
                System.out.println("[ERROR] Some unexpected exception!");
                e.printStackTrace();
            }
        }
        // count and write game results
    }

    /**
     * Make turn with respect to exception-failing and time-management
     * I.e, each player is given max of 5-10 seconds to make a turn, and if no turn in that timeframe,
     * the game should continue
     * @param controller
     * @param gameState
     * @param playerIndex
     * @param gameStaticData
     * @return
     */
    private Action makeControlledTurn(Controller controller, TState gameState, int playerIndex, StaticData gameStaticData) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Action> future = executor.submit(() -> controller.makeTurn(gameState, playerIndex, gameStaticData));
            try {
                return future.get(gameEnvironment.getTechnicalConfig().getTurnTimeoutMs(gameState), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                return null;
            }
    }

    private Controller getCurrentController(int currentPlayer) {
        return gameEnvironment.getController(currentPlayer);
    }

    private TState getStateCopy() {
        return store.getState().getCopy();
    }

    private SystemDispatcher getSystemDispatcher() {
        return gameEnvironment.getSystemDispatcher();
    }

    private StaticData getStaticData() {
        return gameEnvironment.getStaticData();
    }

    private TurnValidator getTurnValidator() {
        return gameEnvironment.getTurnValidator();
    }

    public TState getState() {
        return store.getState();
    }

    public GameEnvironment<TState> getGameEnvironment() {
        return gameEnvironment;
    }
}
