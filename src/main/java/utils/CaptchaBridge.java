package utils;

import java.util.function.Consumer;

public class CaptchaBridge {

    private final Consumer<String> callback;

    public CaptchaBridge(Consumer<String> callback) {
        this.callback = callback;
    }

    // Called from JS
    public void onCaptchaSolved(String token) {
        System.out.println("Captcha solved! Token: " + token);
        callback.accept(token);

    }

}
