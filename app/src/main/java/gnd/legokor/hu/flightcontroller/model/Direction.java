package gnd.legokor.hu.flightcontroller.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Direction implements Parcelable {
    public int azimuth = 0;
    public int elevation = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.azimuth);
        dest.writeInt(this.elevation);
    }

    public Direction() {
    }

    protected Direction(Parcel in) {
        this.azimuth = in.readInt();
        this.elevation = in.readInt();
    }

    public static final Parcelable.Creator<Direction> CREATOR = new Parcelable.Creator<Direction>() {
        @Override
        public Direction createFromParcel(Parcel source) {
            return new Direction(source);
        }

        @Override
        public Direction[] newArray(int size) {
            return new Direction[size];
        }
    };
}