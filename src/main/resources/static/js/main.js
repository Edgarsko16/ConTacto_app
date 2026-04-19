document.addEventListener("DOMContentLoaded", () => {
    const usernameInput = document.getElementById("username");
    if (usernameInput) {
        usernameInput.focus();
    }

    const vibrationMessage = document.getElementById("vibration-message");
    const appToast = document.getElementById("app-toast");
    const panelUsername = document.getElementById("panel-username");
    const messageDisplay = document.getElementById("message-display");
    let toastTimeout;

    const showToast = (message) => {
        if (!appToast) {
            return;
        }
        appToast.textContent = message;
        appToast.classList.add("is-visible");
        clearTimeout(toastTimeout);
        toastTimeout = setTimeout(() => {
            appToast.classList.remove("is-visible");
        }, 2200);
    };

    const parsePattern = (patternText) => {
        if (!patternText || !patternText.trim()) {
            return [];
        }
        return patternText
            .split(",")
            .map((value) => Number(value.trim()))
            .filter((value) => Number.isFinite(value) && value > 0);
    };

    const speakMessage = (text) => {
        if (!("speechSynthesis" in window)) {
            showToast("Este navegador no soporta voz");
            return;
        }
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = "es-ES";
        utterance.rate = 0.95;
        utterance.pitch = 1;
        window.speechSynthesis.cancel();
        window.speechSynthesis.speak(utterance);
    };

    const saveMessage = async (contenido) => {
        const usuario = panelUsername?.textContent?.trim() || "Invitado";
        try {
            await fetch("/api/mensajes", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    contenido: contenido,
                    usuario: usuario
                })
            });
        } catch (error) {
            showToast("No se pudo guardar el mensaje");
        }
    };

    const panelButtons = document.querySelectorAll(".need-btn");
    panelButtons.forEach((button) => {
        button.addEventListener("click", async () => {
            const needLabel = button.querySelector(".need-text")?.textContent || "Necesidad";
            const speechMessage = button.dataset.speech || ("Necesito " + needLabel.toLowerCase());
            const pattern = parsePattern(button.dataset.pattern || "");
            await saveMessage(needLabel);
            if (messageDisplay) {
                messageDisplay.textContent = speechMessage;
            }
            speakMessage(speechMessage);

            if (!pattern) {
                showToast("Seleccionado: " + needLabel);
                return;
            }

            if ("vibrate" in navigator) {
                navigator.vibrate(pattern);
                if (vibrationMessage) {
                    vibrationMessage.textContent = "Vibracion activada para " + needLabel + ".";
                }
                showToast("Seleccionado: " + needLabel);
            } else if (vibrationMessage) {
                vibrationMessage.textContent = "Este dispositivo no soporta vibracion.";
                showToast("Seleccionado: " + needLabel + " (sin vibracion en este dispositivo)");
            }
        });
    });
});
