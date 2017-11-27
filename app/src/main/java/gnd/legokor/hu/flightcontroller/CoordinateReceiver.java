package gnd.legokor.hu.flightcontroller;

import gnd.legokor.hu.flightcontroller.model.Coordinates;

public interface CoordinateReceiver {
    void receiveCoordinates(Coordinates coordinates);
}
