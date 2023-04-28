package com.catdog.help.exception;

public class MsgRoomNotFoundException extends RuntimeException {
    private static final String MESSAGE = "존재하지 않는 메시지 룸입니다.";

    public MsgRoomNotFoundException() {
        super(MESSAGE);
    }

    public MsgRoomNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
