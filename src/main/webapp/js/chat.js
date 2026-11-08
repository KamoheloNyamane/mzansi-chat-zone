/**
 * Drives the conversation thread page: opens the WebSocket connection,
 * renders incoming frames, sends outgoing text messages, and reconnects
 * with backoff if the connection drops. Runs only on conversation.jsp —
 * the server already knows who's connected via the HTTP session, so
 * nothing here needs to (or should) assert its own identity.
 */
(function () {
    "use strict";

    const body = document.body;
    const contextPath = body.dataset.contextPath || "";
    const conversationId = parseInt(body.dataset.conversationId, 10);
    const currentUsername = body.dataset.username;

    const messageList = document.getElementById("messageList");
    const composerForm = document.getElementById("composerForm");
    const messageInput = document.getElementById("messageInput");
    const connectionPill = document.getElementById("connectionPill");
    const threadError = document.getElementById("threadError");

    let socket = null;
    let reconnectDelayMs = 1000;
    const MAX_RECONNECT_DELAY_MS = 15000;

    function wsUrl() {
        const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
        return protocol + "//" + window.location.host + contextPath + "/chat";
    }

    function setConnectionState(state) {
        if (!connectionPill) return;
        if (state === "connected") {
            connectionPill.textContent = "Connected";
            connectionPill.classList.remove("offline");
        } else if (state === "connecting") {
            connectionPill.textContent = "Connecting…";
            connectionPill.classList.remove("offline");
        } else {
            connectionPill.textContent = "Reconnecting…";
            connectionPill.classList.add("offline");
        }
    }

    function connect() {
        setConnectionState("connecting");
        socket = new WebSocket(wsUrl());

        socket.addEventListener("open", function () {
            reconnectDelayMs = 1000;
            setConnectionState("connected");
        });

        socket.addEventListener("message", function (event) {
            let payload;
            try {
                payload = JSON.parse(event.data);
            } catch (e) {
                return;
            }
            handleIncoming(payload);
        });

        socket.addEventListener("close", function () {
            setConnectionState("offline");
            scheduleReconnect();
        });

        socket.addEventListener("error", function () {
            socket.close();
        });
    }

    function scheduleReconnect() {
        setTimeout(connect, reconnectDelayMs);
        reconnectDelayMs = Math.min(reconnectDelayMs * 2, MAX_RECONNECT_DELAY_MS);
    }

    function handleIncoming(payload) {
        if (payload.type === "ERROR") {
            showThreadError(payload.content || "Something went wrong.");
            return;
        }
        if (payload.conversationId !== conversationId) {
            // A message for a different thread — out of scope for this page.
            // (The inbox re-queries on next visit, so nothing is lost.)
            return;
        }
        appendMessage(payload);
        scrollToBottom();
    }

    function showThreadError(message) {
        if (!threadError) return;
        threadError.textContent = message;
        threadError.style.display = "block";
        setTimeout(function () {
            threadError.style.display = "none";
        }, 4000);
    }

    function appendMessage(payload) {
        const mine = payload.senderUsername === currentUsername;

        const row = document.createElement("div");
        row.className = "bubble-row " + (mine ? "mine" : "theirs");

        const bubble = document.createElement("div");
        bubble.className = "bubble";

        if (payload.type === "IMAGE" && payload.imagePath) {
            const img = document.createElement("img");
            img.src = contextPath + "/" + payload.imagePath;
            img.alt = "Shared photo";
            bubble.appendChild(img);
        } else {
            const text = document.createElement("span");
            text.textContent = payload.content || "";
            bubble.appendChild(text);
        }

        const time = document.createElement("span");
        time.className = "bubble-time";
        time.textContent = formatTime(payload.sentAt);
        bubble.appendChild(time);

        row.appendChild(bubble);
        messageList.appendChild(row);
    }

    function formatTime(isoString) {
        if (!isoString) return "";
        const date = new Date(isoString);
        if (isNaN(date.getTime())) return "";
        return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    }

    function scrollToBottom() {
        messageList.scrollTop = messageList.scrollHeight;
    }

    if (composerForm) {
        composerForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const text = messageInput.value.trim();
            if (!text) return;

            if (!socket || socket.readyState !== WebSocket.OPEN) {
                showThreadError("Not connected — hang on and try again.");
                return;
            }

            socket.send(JSON.stringify({
                type: "TEXT",
                conversationId: conversationId,
                content: text
            }));
            messageInput.value = "";
        });
    }

    scrollToBottom();
    connect();
})();
