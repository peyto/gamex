package org.peyto.gamex;

import org.peyto.redux.State;

public interface GameState<TState extends State> extends State<TState> {

    boolean isCompleted();

    int getCurrentPlayerIndex();

}
