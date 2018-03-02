package kogvet.eye;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by Loldator on 2018-02-28.
 */

public class Event implements Parcelable, Comparable<Event> {

    public static class Location {
        String displayName;
        String street;
        String city;
        String state;
        String country;
        String postalCode;

        @Override
        public  String toString() {
            return "[ displayName="+displayName+", street="+street+", city="+city+", country="+country+", postalCode="+postalCode+"]";
        }
    }

    String subject;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    Location location;
//    String [] location;

    //Parcel implementation
    public Event(String subject, String startDate, String endDate, String startTime, String endTime, Location location) {
        this.subject=subject;
        this.startDate=startDate;
        this.startTime=startTime;
        this.endDate=endDate;
        this.endTime=endTime;
//        this.location=location;
    }

    protected Event(Parcel in) {
        subject = in.readString();
        startDate = in.readString();
        startTime = in.readString();
        endDate = in.readString();
        endTime = in.readString();
//        location = in.createStringArray();
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
//        dest.writeStringArray(location);
    }

    public String getStartDate() {
        return startDate;
    }

    public  String getStartTime() {
        return startTime;
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

    @Override
    public int compareTo(Event compareEvent) {
        if(startDate.compareToIgnoreCase(compareEvent.startDate) ==0)
            return startTime.compareToIgnoreCase(compareEvent.startTime);
        else
            return startDate.compareToIgnoreCase(compareEvent.startDate);

    }

    @Override
    public  String toString() {
        return "[ startdate="+startDate+", enddate="+endDate+", startTime="+startTime+", endTime="+endTime+"]";
    }


}