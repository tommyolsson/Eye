package kogvet.eye;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.microsoft.graph.extensions.DayOfWeek;
import com.microsoft.graph.extensions.Importance;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class EventClass implements Parcelable {

    String id;
    String subject;
    String bodyPreview;
    Boolean isAllDay;
    Boolean isMeeting;
    LocalDateTime startTimeObj;
    LocalDateTime endTimeObj;
    Location location;
    ResponseStatus responseStatus;
    String importance;

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

        public String getDisplayName() {
            return this.displayName;
        }
        public String getStreet() {
            return this.street;
        }
        public String getCity() {
            return this.city;
        }
        public String getState() {
            return this.state;
        }
        public String getCountry() {
            return this.country;
        }
        public String getPostalCode() {
            return this.postalCode;
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

    /* Response Status */
    public static class ResponseStatus implements  Parcelable {
        String response;
        String time;

        public ResponseStatus() {
            this.response = "";
            this.time = "";
        }

        public String getResponse() {
            return this.response;
        }

        public String getTime() {
            return this.time;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        @Override
        public  String toString() {
            return "[ response="+response+", time="+time+"]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(response);
            dest.writeString(time);
        }

        public ResponseStatus(Parcel in) {
            response = in.readString();
            time = in.readString();
        }

        public static final Parcelable.Creator<ResponseStatus> CREATOR = new Parcelable.Creator<ResponseStatus>() {
            public ResponseStatus createFromParcel(Parcel in) {
                return new ResponseStatus(in);
            }

            public ResponseStatus[] newArray(int size) {
                return new ResponseStatus[size];
            }
        };
    }

    public EventClass(String id, String subject, String bodyPreview, Boolean isAllDay, Boolean isMeeting, LocalDateTime startTimeObj, LocalDateTime endTimeObj, Location location, ResponseStatus responseStatus, String importance) {
        this.id=id;
        this.subject=subject;
        this.bodyPreview=bodyPreview;
        this.isAllDay = isAllDay;
        this.isMeeting = isMeeting;
        this.startTimeObj = startTimeObj;
        this.endTimeObj = endTimeObj;
        this.location=location;
        this.responseStatus=responseStatus;
        this.importance=importance;
    }

    //Parcel implementation
    private EventClass(Parcel in) {
        id = in.readString();
        subject = in.readString();
        bodyPreview = in.readString();
        isAllDay = in.readInt() == 1;
        startTimeObj = LocalDateTime.parse(in.readString());
        endTimeObj = LocalDateTime.parse(in.readString());
        location = in.readParcelable(Location.class.getClassLoader());
        responseStatus = in.readParcelable(ResponseStatus.class.getClassLoader());
        importance = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subject);
        dest.writeString(bodyPreview);
        dest.writeInt(isAllDay ? 1 : 0);
        dest.writeString(startTimeObj.toString());
        dest.writeString(endTimeObj.toString());
        dest.writeParcelable(location, flags);
        dest.writeParcelable(responseStatus, flags);
        dest.writeString(importance);
    }

    public static final Parcelable.Creator<EventClass> CREATOR = new Parcelable.Creator<EventClass>() {
        @Override
        public EventClass createFromParcel(Parcel in) {
            return new EventClass(in);
        }

        @Override
        public EventClass[] newArray(int size) {
            return new EventClass[size];
        }
    };

    public String getId() {
        return this.id;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBodyPreview() {
        return this.bodyPreview;
    }

    public Boolean getIsAllDay() {
        return this.isAllDay;
    }

    public Boolean getIsMeeting() {
        return this.isMeeting;
    }

    public LocalDateTime getStartTimeObj() {
        return this.startTimeObj;
    }

    public LocalDateTime getEndTimeObj() {
        return this.endTimeObj;
    }

    public String getStartDate() {
        String string = this.startTimeObj.toString();
        String[] segments = string.split("T");
        return segments[0];
    }

    public String getStartTime() {
        String string = this.startTimeObj.toString();
        String[] segments = string.split("T");
        return segments[1];
    }

    public String getEndDate() {
        String string = this.endTimeObj.toString();
        String[] segments = string.split("T");
        return segments[0];
    }

    public String getEndTime() {
        String string = this.endTimeObj.toString();
        String[] segments = string.split("T");
        return segments[1];
    }

    public Location getLocation() {
        return this.location;
    }

    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public String getImportance() {
        return this.importance;
    }

    @Override
    public  String toString() {
        return "[ startdate="+this.getStartDate()+", enddate="+getEndDate()+", startTime="+this.getStartTime()+", endTime="+getEndTime()+"]";
    }

    public String getDayInWeek() {
        java.time.DayOfWeek day = this.startTimeObj.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    public String getShortDayInWeek() {
        String longName = getDayInWeek();
        String shortName = longName.substring(0, 1).toUpperCase() + longName.substring(1,2);
        return shortName;

    }

}