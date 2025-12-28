package server.action;

import server.model.CardType;

public abstract class Action {
    protected final int actionId;
    protected final server.action.ActionType actionType;
    protected final int actorId;
    protected final Object affectedEntities;
    protected Action(int actionId, server.action.ActionType actionType, int actorId, Object affectedEntities){
        this.actionId = actionId;
        this.actionType = actionType;
        this.actorId = actorId;
        this.affectedEntities = affectedEntities;
    }
    public abstract void undo();
    public abstract void redo();
    public int getActionId() {
        return actionId;
    }
    public int getActorId() {
        return actorId;
    }
    public Object getAffectedEntities() {
        return affectedEntities;
    }
    public server.action.ActionType getActionType(){
        return actionType;
    }
}
