package gnd.legokor.hu.flightcontroller.http;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import gnd.legokor.hu.flightcontroller.CoordinateReceiver;
import gnd.legokor.hu.flightcontroller.model.Coordinates;
import okhttp3.Response;
import okhttp3.WebSocketListener;

public class CoordinatesListener extends WebSocketListener {

    private CoordinateReceiver receiver;

    public CoordinatesListener(CoordinateReceiver r) {
        receiver = r;
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Coordinates coordinates = new Gson().fromJson(text, Coordinates.class);
        Log.i("WS", "MSG: " + text);
        receiver.receiveCoordinates(coordinates);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
    }
}
