package com.example.thrymr.chatapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.koushikdutta.async.http.WebSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import models.ChatMessage;
import models.Notification;
import models.User;

/**
 * Created by thrymr on 2/3/16.
 */
public class ChatActivity extends AppCompatActivity {
    private Button buttonSend;
    private ListView listView;
    //private ChatArrayAdapter chatArrayAdapter;
    private EditText chatText;
    private boolean side;
    private SessionManager sessionManager;
    private JSONObject jsonObject;
    private List<ChatMessage> chatMessageList;
    private Notification notification;
    List<Notification> notificationList;
    ChatAdapter chatAdapter;
    public static boolean isScreenOn;
    ChatMessage chatMessage;
    private ListView chatUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(ChatActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ChatApplication");
        setSupportActionBar(toolbar);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        listView = (ListView) findViewById(R.id.listView1);
        chatMessageList = new ArrayList<>();
        chatMessage = new ChatMessage();
        chatAdapter = new ChatAdapter(this, chatMessageList);
        initializeWebSocket(ChatActivity.this, sessionManager);
        chatUserList = (ListView) findViewById(R.id.chat_list);
        getListOfUsersFromServer();
        Log.d("chatMessageList", "ChatActivity" + chatMessageList);
        try {
            if (getIntent().getStringExtra("json_obj") != null) {
                jsonObject = new JSONObject(getIntent().getStringExtra("json_obj"));
                if (jsonObject.has("message")) {
                    Log.d("ChatActivity", "FromNotfi--MessageList" + chatMessageList);
                    chatAdapter.setNotify(chatMessageList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatMessageList = ChatMessage.findWithQuery(ChatMessage.class, "select * from chat_message");
        chatAdapter = new ChatAdapter(this, chatMessageList);
        listView.setAdapter(chatAdapter);

        //listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.chatText);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    private void getListOfUsersFromServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("AppUserId", sessionManager.getUserDetails().get("appUserId"));
        //params.put("updateTimeInMillis", System.currentTimeMillis() + "");
        Log.d("sendChatMessage", "-----" + params);
        client.post(Utils.SERVER_URI + "/m/get-all-users",
                params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("ChatActivity", "/m/get-all-users" + response);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                User user = new User();
                                user.setUserId(response.getJSONObject(i).getString("id"));
                                user.setUserName(response.getJSONObject(i).getString("name"));
                                user.save();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        List<User> userList = User.findWithQuery(User.class, "select * from user");
                        String[] strings = new String[userList.size()];
                        for (int i = 0; i < strings.length; i++) {
                            strings[i] = userList.get(i).getUserName();
                            Log.d("ChatActivity", "strings" + strings[i]);
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter(ChatActivity.this, android.R.layout.simple_list_item_1, strings);
                        chatUserList.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d("ChatActivity", "responseString" + responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.d("ChatActivity", "errorResponse" + errorResponse);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void initializeWebSocket(final Context context, final SessionManager sessionManager) {
        com.koushikdutta.async.http.AsyncHttpClient.getDefaultInstance().websocket(Utils.URL_WEBSOCKET + "/m/web-socket/" + sessionManager.getUserDetails().get(SessionManager.APP_USER_ID)
                + "/" + sessionManager.getUserDetails().get(SessionManager.MOBILE_LOGIN_AUTH_TOKEN), null, new com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback() {

            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    Log.d("Exception", "Exception" + webSocket);
                    Log.d("inside onCompleted", "onCompleted" + Utils.URL_WEBSOCKET + "/m/web-socket/" + sessionManager.getUserDetails().get(SessionManager.APP_USER_ID)
                            + "/" + sessionManager.getUserDetails().get(SessionManager.MOBILE_LOGIN_AUTH_TOKEN));
                    return;
                }
                Log.d("inside onCompleted", "onCompleted" + Utils.URL_WEBSOCKET + "/m/web-socket/" + sessionManager.getUserDetails().get(SessionManager.APP_USER_ID)
                        + "/" + sessionManager.getUserDetails().get(SessionManager.MOBILE_LOGIN_AUTH_TOKEN));
                webSocket.setStringCallback(new StringCallBackSocket(context));
                webSocket.setDataCallback(new DataCallBackSocket());

            }
        });
    }

    private boolean sendChatMessage() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("message", chatText.getText().toString());
        params.put("mobileLoginAuthToken", sessionManager.getUserDetails().get("mobile_login_auth_token"));
        params.put("AppUserId", sessionManager.getUserDetails().get("appUserId"));
        params.put("msgSenderId", 2l);
        //params.put("updateTimeInMillis", System.currentTimeMillis() + "");
        Log.d("sendChatMessage", "-----" + params);
        client.post(Utils.SERVER_URI + "/m/send-message",
                params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("ChatActivity", "/m/send-message" + response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d("ChatActivity", "responseString" + responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.d("ChatActivity", "errorResponse" + errorResponse);
                    }
                });
        chatMessage = new ChatMessage(false, chatText.getText().toString());
        chatMessage.save();
        chatMessageList.add(chatMessage);
        chatAdapter.setNotify(chatMessageList);
        chatText.setText("");
        return true;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("ChatActivity", "message" + message);
            chatMessage = new ChatMessage(true, message);
            chatMessage.save();
            chatMessageList.add(chatMessage);
            Log.d("ChatActivity", "chatMessageList-------" + chatMessageList);
            chatAdapter.setNotify(chatMessageList);
            //do other stuff here
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        isScreenOn = true;
        registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        isScreenOn = false;
        unregisterReceiver(mMessageReceiver);
    }



}
