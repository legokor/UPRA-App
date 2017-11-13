package gnd.legokor.hu.flightcontroller.http;

import android.support.annotation.Nullable;
import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocketListener;

public class WebSocket extends WebSocketListener {
    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.i("WS", "open");
        webSocket.send("Hello...");
        webSocket.send("...World!");
        webSocket.close(1000, null);
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Log.i("WS", "MSG: " + text);
    }

    @Override
    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        Log.i("WS", "BYE");
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
    }
}
