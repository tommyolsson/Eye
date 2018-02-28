package kogvet.eye;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Loldator on 2018-02-28.
 */

public class Event implements Parcelable {
    String subject;
    String startDate;
    String startTime;
    String endDate;
    String endTime;

    //Parcel implementation
    public Event(String subject, String startDate, String endDate, String startTime, String endTime) {
        this.subject=subject;
        this.startDate=startDate;
        this.startTime=startTime;
        this.endDate=endDate;
        this.endTime=endTime;
    }

    protected Event(Parcel in) {
        subject = in.readString();
        startDate = in.readString();
        startTime = in.readString();
        endDate = in.readString();
        endTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subject);
        dest.writeString(startDate);
        dest.writeString(startTime);
        dest.writeString(endDate);
        dest.writeString(endTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}