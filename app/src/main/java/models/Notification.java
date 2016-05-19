package models;


import com.orm.SugarRecord;

/**
 * Created by thrymr on 10/2/16.
 */
public class Notification extends SugarRecord<Notification> {

    public String message;
    private Long notificationTime;

    public Notification() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Long notificationTime) {
        this.notificationTime = notificationTime;
    }


}
