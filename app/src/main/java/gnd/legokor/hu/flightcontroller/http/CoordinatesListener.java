package gnd.legokor.hu.flightcontroller.http;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import gnd.legokor.hu.flightcontroller.CoordinatesReceiver;
import gnd.legokor.hu.flightcontroller.model.Coordinates;
import okhttp3.Response;
import okhttp3.WebSocketListener;

public class CoordinatesListener extends WebSocketListener {

    private CoordinatesReceiver receiver;

    public CoordinatesListener(CoordinatesReceiver r) {
        receiver = r;
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Coordinates coordinates = new Gson().fromJson(text, Coordinates.class);
        receiver.receiveCoordinates(coordinates);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
    }
}
