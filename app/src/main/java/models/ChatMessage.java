package models;

import com.orm.SugarRecord;

/**
 * Created by thrymr on 2/3/16.
 */
public class ChatMessage extends SugarRecord<ChatMessage> {

    String message;

    boolean isMine;

    boolean isStatusMessage;

    public ChatMessage() {

    }

    public ChatMessage(boolean isMine, String message) {
        this.isMine = isMine;
        this.message = message;
    }

    public ChatMessage(String message, boolean isStatusMessage) {
        this.isStatusMessage = isStatusMessage;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isStatusMessage() {
        return isStatusMessage;
    }

    public void setStatusMessage(boolean statusMessage) {
        isStatusMessage = statusMessage;
    }


    @Override
    public String toString() {
        return "ChatMessage{" +
                "message='" + message + '\'' +
                ", isMine=" + isMine +
                ", isStatusMessage=" + isStatusMessage +
                '}';
    }
}