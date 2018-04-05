package kogvet.eye;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.time.LocalDateTime;

/**
 * Created by Loldator on 2018-02-28.
 */

public class Event implements Parcelable {

    String subject;
    String bodyPreview;
    Boolean isAllDay;
    Boolean isMeeting;
    LocalDateTime startTimeObj;
    LocalDateTime endTimeObj;
    Location location;

    public static class Location implements  Parcelable {
        String displayName;
        String street;
        String city;
        String state;
        String country;
        String postalCode;

        public Location() {
            this.displayName = "";
            this.street = "";
            this.city = "";
            this.street = "";
            this.state = "";
            this.country = "";
            this.postalCode = "";
        }

        @Override
        public  String toString() {
            return "[ displayName="+displayName+", street="+street+", city="+city+", country="+country+", postalCode="+postalCode+"]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(displayName);
            dest.writeString(street);
            dest.writeString(city);
            dest.writeString(state);
            dest.writeString(country);
            dest.writeString(postalCode);
        }

        public Location(Parcel in) {
            displayName = in.readString();
            street = in.readString();
            city = in.readString();
            state = in.readString();
            country = in.readString();
            postalCode = in.readString();
        }

        public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
            public Location createFromParcel(Parcel in) {
                return new Location(in);
            }

            public Location[] newArray(int size) {
                return new Location[size];
            }
        };
    }

    public Event(String subject, String bodyPreview, Boolean isAllDay, Boolean isMeeting, LocalDateTime startTimeObj, LocalDateTime endTimeObj, Location location) {
        this.subject=subject;
        this.bodyPreview=bodyPreview;
        this.isAllDay = isAllDay;
        this.isMeeting = isMeeting;
        this.startTimeObj = startTimeObj;
        this.endTimeObj = endTimeObj;
        this.location=location;
    }

    //Parcel implementation
    private Event(Parcel in) {
        subject = in.readString();
        bodyPreview = in.readString();
        isAllDay = in.readInt() == 1;
        startTimeObj = LocalDateTime.parse(in.readString());
        endTimeObj = LocalDateTime.parse(in.readString());
        location = in.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subject);
        dest.writeString(bodyPreview);
        dest.writeInt(isAllDay ? 1 : 0);
        dest.writeString(startTimeObj.toString());
        dest.writeString(endTimeObj.toString());
        dest.writeParcelable(location, flags);
    }

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


    public String getStartDate() {
        String string = this.startTimeObj.toString();
        String[] segments = string.split("T");
        return segments[0];
    }

    public  String getStartTime() {
        String string = this.startTimeObj.toString();
        String[] segments = string.split("T");
        return segments[1];
    }

    public  String getEndDate() {
        String string = this.endTimeObj.toString();
        String[] segments = string.split("T");
        return segments[0];
    }

    public  String getEndTime() {
        String string = this.endTimeObj.toString();
        String[] segments = string.split("T");
        return segments[1];
    }

    @Override
    public  String toString() {
        return "[ startdate="+this.getStartDate()+", enddate="+getEndDate()+", startTime="+this.getStartTime()+", endTime="+getEndTime()+"]";
    }


}