package gnd.legokor.hu.flightcontroller;

import gnd.legokor.hu.flightcontroller.model.Coordinates;

public interface CoordinatesReceiver {
    void receiveCoordinates(Coordinates coordinates);
    void onConnectionFailure();
}
