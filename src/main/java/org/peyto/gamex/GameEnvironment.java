package org.peyto.gamex;

import org.peyto.redux.Action;
import org.peyto.redux.Reducer;
import org.peyto.redux.State;

import java.util.List;

public interface GameEnvironment<TState extends State<TState>> {

    List<Reducer<TState, ? extends Action>> getReducers();

    TurnValidator<TState> getTurnValidator();

    SystemDispatcher<TState> getSystemDispatcher();

    TState getInitialState();

    /**
     * Static Data should be immutable to pass safely to clients
     * @return
     */
    StaticData getStaticData();

    TechnicalConfig<TState> getTechnicalConfig();

    Controller<TState> getController(int index);
}
