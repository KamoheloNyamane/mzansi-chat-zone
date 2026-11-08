/**
 * Drives the camera page: live preview via getUserMedia, a canvas grab
 * to freeze the shot, and an upload to ImageUploadServlet. Runs only
 * when a conversationId is present — camera.jsp shows a "pick a chat
 * first" state otherwise and never loads this script.
 */
(function () {
    "use strict";

    const body = document.body;
    const contextPath = body.dataset.contextPath || "";
    const conversationId = body.dataset.conversationId;

    const video = document.getElementById("cameraVideo");
    const canvas = document.getElementById("captureCanvas");
    const capturedImg = document.getElementById("capturedPreview");
    const hint = document.getElementById("cameraHint");
    const shutterBtn = document.getElementById("shutterBtn");
    const retakeBtn = document.getElementById("retakeBtn");
    const sendBtn = document.getElementById("sendBtn");
    const cameraError = document.getElementById("cameraError");

    let stream = null;
    let capturedBlob = null;

    function showError(message) {
        if (!cameraError) return;
        cameraError.textContent = message;
        cameraError.style.display = "block";
    }

    function clearError() {
        if (!cameraError) return;
        cameraError.style.display = "none";
    }

    async function startCamera() {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
            showError("This browser doesn't support camera access. Try a recent Chrome, Firefox or Safari.");
            setStage("unavailable");
            return;
        }
        try {
            stream = await navigator.mediaDevices.getUserMedia({
                video: { facingMode: "environment" },
                audio: false
            });
            video.srcObject = stream;
            setStage("live");
        } catch (err) {
            if (err.name === "NotAllowedError") {
                showError("Camera access was blocked. Allow camera permissions for this site and reload.");
            } else if (err.name === "NotFoundError") {
                showError("No camera was found on this device.");
            } else {
                showError("Couldn't start the camera: " + err.message);
            }
            setStage("unavailable");
        }
    }

    function setStage(stage) {
        // stage: "live" | "captured" | "unavailable"
        video.style.display = stage === "live" ? "block" : "none";
        capturedImg.style.display = stage === "captured" ? "block" : "none";
        hint.style.display = stage === "unavailable" ? "flex" : "none";

        shutterBtn.style.display = stage === "live" ? "inline-flex" : "none";
        retakeBtn.style.display = stage === "captured" ? "inline-flex" : "none";
        sendBtn.style.display = stage === "captured" ? "inline-flex" : "none";
    }

    function capture() {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext("2d");
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

        canvas.toBlob(function (blob) {
            capturedBlob = blob;
            capturedImg.src = URL.createObjectURL(blob);
            setStage("captured");
        }, "image/jpeg", 0.85);
    }

    function retake() {
        capturedBlob = null;
        clearError();
        setStage("live");
    }

    async function send() {
        if (!capturedBlob) return;
        clearError();
        sendBtn.disabled = true;
        sendBtn.textContent = "Sending…";

        const formData = new FormData();
        formData.append("photo", capturedBlob, "capture.jpg");
        formData.append("conversationId", conversationId);

        try {
            const response = await fetch(contextPath + "/upload-image", {
                method: "POST",
                body: formData
            });
            if (response.ok) {
                window.location.href = response.url;
            } else {
                showError("The photo couldn't be sent. Please try again.");
                sendBtn.disabled = false;
                sendBtn.textContent = "Send";
            }
        } catch (err) {
            showError("The photo couldn't be sent — check your connection and try again.");
            sendBtn.disabled = false;
            sendBtn.textContent = "Send";
        }
    }

    if (shutterBtn) shutterBtn.addEventListener("click", capture);
    if (retakeBtn) retakeBtn.addEventListener("click", retake);
    if (sendBtn) sendBtn.addEventListener("click", send);

    window.addEventListener("beforeunload", function () {
        if (stream) {
            stream.getTracks().forEach(function (track) { track.stop(); });
        }
    });

    if (conversationId) {
        startCamera();
    }
})();
