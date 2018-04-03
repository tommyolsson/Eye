package kogvet.eye;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Loldator on 2018-02-28.
 */

//, Comparable<Event>
public class Event implements Parcelable {

    String subject;
    String bodyPreview;
    String startDate;
    String startTime;
    Boolean isAllDay;
    String endDate;
    String endTime;
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

    //Parcel implementation
    public Event() {
        this.subject="";
        this.bodyPreview="";
        this.startDate="";
        this.startTime="";
        this.isAllDay = false;
        this.endDate="";
        this.endTime="";
        this.location=new Location();
    }

    public Event(String subject, String bodyPreview, String startDate, String endDate, Boolean isAllDay, String startTime, String endTime, Location location) {
        this.subject=subject;
        this.bodyPreview=bodyPreview;
        this.startDate=startDate;
        this.startTime=startTime;
        this.isAllDay = isAllDay;
        this.endDate=endDate;
        this.endTime=endTime;
        this.location=location;
    }

    private Event(Parcel in) {
        subject = in.readString();
        bodyPreview = in.readString();
        startDate = in.readString();
        startTime = in.readString();
        isAllDay = in.readInt() == 1;
        endDate = in.readString();
        endTime = in.readString();
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
        dest.writeString(startDate);
        dest.writeString(startTime);
        dest.writeInt(isAllDay ? 1 : 0);
        dest.writeString(endDate);
        dest.writeString(endTime);
        dest.writeParcelable(location, flags);
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

//    no need to sort anymore bcuz of query parameters
//    @Override
//    public int compareTo(Event compareEvent) {
//        if(startDate.compareToIgnoreCase(compareEvent.startDate) ==0)
//            return startTime.compareToIgnoreCase(compareEvent.startTime);
//        else
//            return startDate.compareToIgnoreCase(compareEvent.startDate);
//
//    }

    public String getStartDate() {
        return startDate;
    }

    public  String getStartTime() {
        return startTime;
    }

    public void setTimesToLocal() {
        Integer start = Integer.parseInt(this.startTime.substring(0, 2))+1;
        Integer end = Integer.parseInt(this.endTime.substring(0, 2))+1;

        if(start>=24)
            start=0;
        if(end>=24)
            end=0;

        if(start<10)
            this.startTime = "0" + String.valueOf(start) + this.startTime.substring(2);
        else
            this.startTime = String.valueOf(start) + this.startTime.substring(2);

        if(end<10)
            this.endTime = "0" + String.valueOf(end) + this.endTime.substring(2);
        else
            this.endTime = String.valueOf(end) + this.endTime.substring(2);
    }

    @Override
    public  String toString() {
        return "[ startdate="+startDate+", enddate="+endDate+", startTime="+startTime+", endTime="+endTime+"]";
    }


}