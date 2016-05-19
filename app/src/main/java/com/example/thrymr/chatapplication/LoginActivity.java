package com.example.thrymr.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.koushikdutta.async.http.WebSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {


    private EditText email, password;
    public static boolean screenOn;
    private Button loginButton;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn().equalsIgnoreCase("true")) {
            initializeWebSocket(LoginActivity.this, sessionManager);
            navigateToHomeActivity();
        }
        setContentView(R.layout.login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Please wait while loading.....");
                progressDialog.show();
                loginToApp();
            }
        });
        this.setTitle("User" + sessionManager.getUserDetails().get(SessionManager.KEY_EMAIL));
    }

    public void initializeWebSocket(LoginActivity loginActivity, final SessionManager sessionManager) {
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
                webSocket.setStringCallback(new StringCallBackSocket(LoginActivity.this));
                webSocket.setDataCallback(new DataCallBackSocket());

            }
        });
    }

    private void loginToApp() {
        Log.d("LoginActivity", "loginToApp()");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams map = new RequestParams();
        map.put("email", email.getText().toString().trim().toLowerCase());
        map.put("password", password.getText().toString());
        client.post(Utils.SERVER_URI + "/m/loginprocess",
                map, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("LoginActivity", "/m/loginprocess" + response);
                        try {
                            if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                                String name = response.getString("name");
                                String mobile_login_auth_token = response.getString("mobile_login_auth_token");
                                String appUser_id = response.getString("id");

                                sessionManager.createLoginSession("true", name, appUser_id, mobile_login_auth_token);
                                initializeWebSocket(LoginActivity.this, sessionManager);
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d("LoginActivity", "responseString" + responseString);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        Log.d("LoginActivity", "errorResponse" + errorResponse);
                    }
                });

    }

    private void navigateToHomeActivity() {

        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static boolean isScreenOpen() {
        return screenOn;
    }

    public static void setScreenStatus(boolean status) {
        screenOn = status;
    }

}

