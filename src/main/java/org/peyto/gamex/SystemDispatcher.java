package org.peyto.gamex;

import org.peyto.redux.Action;
import org.peyto.redux.State;

public interface SystemDispatcher<TState extends State<TState>> {

    boolean isSystemAction(TState state, StaticData staticData);

    Action makeSystemAction(TState state, StaticData staticData);
}
