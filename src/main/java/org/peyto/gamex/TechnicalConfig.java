package org.peyto.gamex;

import org.peyto.redux.State;

/**
 * Similar to Static Data, but contains more technical information (like timings)
 */
public interface TechnicalConfig<TState extends State<TState>> {

    int getTurnTimeoutMs(TState GameState);
}
