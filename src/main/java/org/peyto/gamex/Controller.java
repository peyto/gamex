package org.peyto.gamex;

import org.peyto.redux.Action;
import org.peyto.redux.State;

public interface Controller<TState extends State<TState>> {

    Action makeTurn(TState gameState, int playerIndex, StaticData gameStaticData);

}
