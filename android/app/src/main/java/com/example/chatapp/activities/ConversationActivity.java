package com.example.chatapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.MessageAdapter;
import com.example.chatapp.database.ChatDatabaseHelper;
import com.example.chatapp.database.Message;
import com.example.chatapp.database.MessageDao;
import com.example.chatapp.utils.Constants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    private EditText messageEditText;
    private Button sendButton;
    private ImageButton cameraAttachButton;
    private TextView statusTextView;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private MessageDao messageDao;

    private WebSocketClient webSocketClient;
    private String currentUser = Constants.CURRENT_USER;
    private String otherUser = Constants.OTHER_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Retrieve current logged in user from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        currentUser = prefs.getString(Constants.KEY_LOGGED_IN_USER, Constants.CURRENT_USER);

        messageEditText = findViewById(R.id.message);
        sendButton = findViewById(R.id.btn_send);
        cameraAttachButton = findViewById(R.id.btn_camera_attach);
        statusTextView = findViewById(R.id.chat_status);
        recyclerView = findViewById(R.id.recyclerView);

        messageDao = ChatDatabaseHelper.getInstance(this).messageDao();

        List<Message> messages = messageDao.getAllMessages();
        messageAdapter = new MessageAdapter(this, messages);
        messageAdapter.setCurrentUser(currentUser);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        // Scroll to the latest message
        if (messageAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }

        // Connect to Cloud WebSocket Server for Physical Device-to-Device Messaging
        initWebSocket();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = messageEditText.getText().toString().trim();
                if (!content.isEmpty()) {
                    Message message = new Message();
                    message.setSender(currentUser);
                    message.setReceiver(otherUser);
                    message.setContent(content);
                    message.setTimestamp(System.currentTimeMillis());

                    // Save locally to SQLite/Room Database
                    messageDao.insertMessage(message);

                    // Update UI immediately
                    messageAdapter.addMessage(message);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    messageEditText.setText("");

                    // Broadcast in real-time over WebSocket to the other physical device
                    sendWebSocketMessage(content);
                }
            }
        });

        if (cameraAttachButton != null) {
            cameraAttachButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ConversationActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initWebSocket() {
        try {
            URI serverUri = new URI(Constants.WEBSOCKET_URL);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i(TAG, "WebSocket Connected to Cloud Relay");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (statusTextView != null) {
                                statusTextView.setText("● Live Cloud Relay Connected (" + currentUser + ")");
                                statusTextView.setBackgroundColor(0xFFD1FADF);
                                statusTextView.setTextColor(0xFF027A48);
                            }
                        }
                    });

                    // Send initial handshake / join frame
                    try {
                        JSONObject joinObj = new JSONObject();
                        joinObj.put("action", "join");
                        joinObj.put("username", currentUser);
                        send(joinObj.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Join error: " + e.getMessage());
                    }
                }

                @Override
                public void onMessage(String rawMessage) {
                    Log.i(TAG, "WebSocket Received: " + rawMessage);
                    try {
                        JSONObject obj = new JSONObject(rawMessage);
                        String sender = obj.optString("sender", "");
                        String content = obj.optString("content", "");
                        long ts = obj.optLong("timestamp", System.currentTimeMillis());

                        // Don't duplicate self-sent echo if the server broadcasts it back
                        if (sender.equalsIgnoreCase(currentUser)) {
                            return;
                        }

                        final Message incoming = new Message();
                        incoming.setSender(sender.isEmpty() ? otherUser : sender);
                        incoming.setReceiver(currentUser);
                        incoming.setContent(content);
                        incoming.setTimestamp(ts);

                        // Save incoming message to Room Database
                        messageDao.insertMessage(incoming);

                        // Render on UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.addMessage(incoming);
                                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "Parse error on incoming message: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.w(TAG, "WebSocket Closed: " + reason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (statusTextView != null) {
                                statusTextView.setText("○ Cloud Relay Offline (Local Storage Active)");
                                statusTextView.setBackgroundColor(0xFFFEE4E2);
                                statusTextView.setTextColor(0xFFB42318);
                            }
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket Error: " + ex.getMessage());
                }
            };

            webSocketClient.connect();

        } catch (Exception e) {
            Log.e(TAG, "WebSocket initialization failed: " + e.getMessage());
        }
    }

    private void sendWebSocketMessage(String content) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JSONObject msgObj = new JSONObject();
                msgObj.put("action", "message");
                msgObj.put("sender", currentUser);
                msgObj.put("recipient", otherUser);
                msgObj.put("content", content);
                msgObj.put("timestamp", System.currentTimeMillis());

                webSocketClient.send(msgObj.toString());
            } catch (Exception e) {
                Log.e(TAG, "Send error: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
