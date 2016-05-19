package com.example.thrymr.chatapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import models.ChatMessage;
import models.Notification;

/**
 * Created by thrymr on 22/1/16.
 */
public class StringCallBackSocket implements WebSocket.StringCallback {

    public static JSONObject jsonObject;
    Context context;
    private JSONArray jsonArray;
    private JSONObject jsonObject1;
    private Notification notification;
    private SessionManager sessionManager;
    private NotificationManager mNotificationManager;
    public static int NOTIFICATION_ID = 0;

    public StringCallBackSocket() {
        super();
    }


    public StringCallBackSocket(Context context) {
        this.context = context;
        Log.d("oooooo", "oooo1");

    }

    @Override
    public void onStringAvailable(String jsonStr) {
        sessionManager = new SessionManager(this.context);
        Log.d("oooooo", "oooo1");
        Object json = null;
        try {
            json = new JSONTokener(jsonStr).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (json instanceof JSONObject) {

        } else if (json instanceof JSONArray) {
            Log.d("inside String Available", "availabel");
            Intent launchIntent = null;

            try {
                jsonArray = new JSONArray(jsonStr);
                jsonObject = jsonArray.getJSONObject(1);
                jsonObject1 = jsonArray.getJSONObject(0);
                Log.d("StringCallBackSocket", "jsonObject" + jsonObject);
                if (jsonObject.getString("kind").equalsIgnoreCase("notification")) {
                    Log.d("inside notification", "notification");
                    if (sessionManager.isLoggedIn().equalsIgnoreCase("true") && sessionManager.getUserDetails().get(SessionManager.APP_USER_ID).equalsIgnoreCase(jsonObject.getString("msg_rec_id"))) {
                        ChatMessage chatMessage = new ChatMessage(true, jsonObject.getString("message"));
                        chatMessage.save();
                        if (ChatActivity.isScreenOn) {
                            updateMyActivity(context, jsonObject.getString("message"));
                        } else {
                            this.sendNotification();
                        }
                    } else {
                        Log.d("Logged out", "no notification");
                    }
                } else {
                    Log.d("else  Kind", "" + jsonObject.getString("kind"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void sendNotification() {
        this.mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent launchIntent = null;
        try {
            launchIntent = new Intent(context, ChatActivity.class);
            launchIntent.putExtra("json_obj", jsonObject.toString());

            final PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            final Uri alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.download)
                    .setContentTitle("Message")
                    .setStyle(
                            new NotificationCompat.BigTextStyle().bigText(
                                    jsonObject.getString("message")))
                    .setLights(Color.BLUE, 500, 500)
                    .setContentText(jsonObject.getString("message"))
                    .setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                    .setSound(alarmSound);
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setNumber(1);
            this.mNotificationManager.notify(NOTIFICATION_ID++, mBuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    private void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }


}
