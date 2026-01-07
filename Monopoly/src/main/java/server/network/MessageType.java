package server.network;

public enum MessageType {

    HELLO,
    WELCOME,
    ERROR,

    ROLL_DICE,
    BUY_PROPERTY,
    BUILD,
    MORTGAGE,
    UNMORTGAGE,

    JAIL_PAY_FINE,
    JAIL_TRY_DOUBLE,

    PROPOSE_TRADE,
    ACCEPT_TRADE,
    REJECT_TRADE,

    END_TURN,
    UNDO,
    REDO,

    STATE_UPDATE,
    EVENT_LOG,
    TURN_INFO,
    REPORT_UPDATE,
    GAME_OVER
}
