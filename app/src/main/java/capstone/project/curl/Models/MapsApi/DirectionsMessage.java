package capstone.project.curl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DirectionsMessage implements Parcelable {
    public String duration = "";
    public String direction = "";
    public String distance = "";
    public  boolean errorWhileParsing = false;

    public DirectionsMessage(String unparsedDirection){
        try{
            String [] partsOfDirections = unparsedDirection.split("#");
            if (partsOfDirections.length >= 3){
                this.direction = partsOfDirections[0];
                this.duration = partsOfDirections[1];
                this.distance = partsOfDirections[2];
            } else {
                Log.d(MapsSmsParser.class.getCanonicalName(), "Error while parsing direction: " + unparsedDirection);
                errorWhileParsing = true;
            }
        } catch(Exception e){
            this.errorWhileParsing = true;
            e.printStackTrace();

        }
    }

    protected DirectionsMessage(Parcel in) {
        duration = in.readString();
        direction = in.readString();
        distance = in.readString();
        errorWhileParsing = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(duration);
        dest.writeString(direction);
        dest.writeString(distance);
        dest.writeByte((byte) (errorWhileParsing ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DirectionsMessage> CREATOR = new Parcelable.Creator<DirectionsMessage>() {
        @Override
        public DirectionsMessage createFromParcel(Parcel in) {
            return new DirectionsMessage(in);
        }

        @Override
        public DirectionsMessage[] newArray(int size) {
            return new DirectionsMessage[size];
        }
    };
}