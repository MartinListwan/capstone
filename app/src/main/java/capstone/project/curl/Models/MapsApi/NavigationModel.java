package capstone.project.curl.Models.MapsApi;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NavigationModel implements Parcelable {
    @Override
    public String toString() {
        return "NavigationModel{" +
                "startingAddress='" + startingAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", directionMessages=" + directionMessages +
                ", errorWhileParsing=" + errorWhileParsing +
                '}';
    }

    public String startingAddress = "";
    public String endAddress = "";
    public List<DirectionsMessage> directionMessages =  new ArrayList<>();
    public boolean errorWhileParsing;

    private int startAdressIndex = 0;
    private int endAddressIndex = 1;

    public NavigationModel(String unparsedApiResponseForNavigation){
        try{
            Log.d("Martin", unparsedApiResponseForNavigation);
            String [] deliminatedApiResponse = unparsedApiResponseForNavigation.split("\\|");
            Log.d("Martin", Arrays.deepToString(deliminatedApiResponse));
            this.startingAddress = deliminatedApiResponse[startAdressIndex].substring(deliminatedApiResponse[startAdressIndex].indexOf("start_address:") + "start_address:".length());
            this.endAddress = deliminatedApiResponse[endAddressIndex].substring(deliminatedApiResponse[endAddressIndex].indexOf("end_address:") + "end_address:".length());

            Log.d("Martin", "Sarted parsing addresses");
            for (int directionsIndex = endAddressIndex + 1; directionsIndex < deliminatedApiResponse.length;directionsIndex++){
                try{
                    DirectionsMessage specificDirection = new DirectionsMessage(deliminatedApiResponse[directionsIndex]);
                    if (!specificDirection.errorWhileParsing){
                        directionMessages.add(specificDirection);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (this.directionMessages.size() == 0){
                this.errorWhileParsing = true;
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