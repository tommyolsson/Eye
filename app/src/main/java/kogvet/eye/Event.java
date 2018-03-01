package kogvet.eye;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Loldator on 2018-02-28.
 */

public class Event implements Parcelable, Comparable<Event> {
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


//    public static Comparator<Event> EventComparator = new Comparator<Event>() {
//
//        public int compare(Event e1, Event e2) {
//            String eventStartDate1 = e1.getStartDate().toUpperCase();
//            String eventStartDate2 = e2.getStartDate().toUpperCase();
//
//            //Get starttime and compare??
//
//            /*For ascending order*/
//            return eventStartDate1.compareTo(eventStartDate2);
//
//            /*For descending order*/
//            //eventStartDate2-eventStartDate1;
//        }
//    };


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