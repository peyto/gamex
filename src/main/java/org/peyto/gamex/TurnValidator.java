package org.peyto.gamex;

import org.peyto.redux.Action;
import org.peyto.redux.State;

public interface TurnValidator<TState extends State<TState>> {

    /**
     * Validates if the turn made by Player/AI is valid
     * If it is not, the validator should return void system action, (i.e., ignore action or end turn)
     * @param actionRequest
     * @return
     */
    Action validateTurn(TState gameState, int playerIndex, StaticData gameStaticData, Action actionRequest);
}
