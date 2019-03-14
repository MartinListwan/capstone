package capstone.project.curl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class NavigationModel implements Parcelable {
    public String startingAddress = "";
    public String endAddress = "";
    public List<DirectionsMessage> directionMessages;
    public boolean errorWhileParsing;

    private int startAdressIndex = 0;
    private int endAddressIndex = 1;

    public NavigationModel(String unparsedApiResponseForNavigation){
        try{
            String [] deliminatedApiResponse = unparsedApiResponseForNavigation.split("|");
            this.startingAddress = deliminatedApiResponse[startAdressIndex].substring(deliminatedApiResponse[startAdressIndex].indexOf("start_address:") + "start_address:".length() + 1);
            this.endAddress = deliminatedApiResponse[endAddressIndex].substring(deliminatedApiResponse[endAddressIndex].indexOf("end_address:") + "end_address:".length() + 1);
            for (int directionsIndex = endAddressIndex + 1; directionsIndex < deliminatedApiResponse.length;directionsIndex++){
                DirectionsMessage directionsMessage = new DirectionsMessage(deliminatedApiResponse[directionsIndex]);
                if (!directionsMessage.errorWhileParsing){
                    directionMessages.add(directionsMessage);
                }
            }
        } catch(Exception e){
            this.errorWhileParsing = true;
            e.printStackTrace();
        }
    }

    protected NavigationModel(Parcel in) {
        startingAddress = in.readString();
        endAddress = in.readString();
        if (in.readByte() == 0x01) {
            directionMessages = new ArrayList<DirectionsMessage>();
            in.readList(directionMessages, DirectionsMessage.class.getClassLoader());
        } else {
            directionMessages = null;
        }
        errorWhileParsing = in.readByte() != 0x00;
        startAdressIndex = in.readInt();
        endAddressIndex = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startingAddress);
        dest.writeString(endAddress);
        if (directionMessages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(directionMessages);
        }
        dest.writeByte((byte) (errorWhileParsing ? 0x01 : 0x00));
        dest.writeInt(startAdressIndex);
        dest.writeInt(endAddressIndex);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NavigationModel> CREATOR = new Parcelable.Creator<NavigationModel>() {
        @Override
        public NavigationModel createFromParcel(Parcel in) {
            return new NavigationModel(in);
        }

        @Override
        public NavigationModel[] newArray(int size) {
            return new NavigationModel[size];
        }
    };
}