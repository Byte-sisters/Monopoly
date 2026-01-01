package server.network;

public enum MessageType {

    HELLO,
    WELCOME,
    ERROR,

    TURN_START,
    TURN_END,

    ROLL_DICE,
    END_TURN,

    BUY_PROPERTY,
    PAY_RENT,

    PROPOSE_TRADE,
    ACCEPT_TRADE,
    REJECT_TRADE,

    BUILD,
    MORTGAGE,
    UNMORTGAGE,

    JAIL_PAY_FINE,
    JAIL_TRY_DOUBLE,

    UNDO,
    REDO,

    STATE_UPDATE,
    EVENT_LOG,

    GAME_OVER
}
