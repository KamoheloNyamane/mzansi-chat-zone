package com.example.chatapp.utils;

public class Constants {
    // Deliverable 1: Constant definition required by assignment
    public static final String CURRENT_USER = "current_user";
    public static final String OTHER_USER = "other_user";

    // Application preferences
    public static final String PREFS_NAME = "ChatAppPrefs";
    public static final String KEY_LOGGED_IN_USER = "logged_in_username";

    // Live Cloud WebSocket and HTTP Backend hosted on Render:
    // Enables two physical devices outside the emulator to exchange messages in real-time
    public static final String WEBSOCKET_URL = "wss://mzansi-chat-zone.onrender.com/chat-endpoint";
    public static final String SERVER_URL = "https://mzansi-chat-zone.onrender.com";
}
