package kogvet.eye.BookingFragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;

/**
 * Created by Loldator on 2018-02-27.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<EventClass> allEvents;
    private final ArrayList<EventClass> allMeetings;
    private final LocalDateTime currentTime;

    public BookingAdapter(Context context, ArrayList<EventClass> allEvents) {
        this.allEvents = allEvents;
        this.allMeetings = getMeetings(allEvents);
        this.context = context;
        this.currentTime =  MainActivity.getCurrentTime();

    }

    private ArrayList<EventClass> getMeetings(ArrayList<EventClass> allEvents) {
        ArrayList<EventClass> meetings = new ArrayList<>();
        for (int i = 0; i < allEvents.size(); i++) {
            EventClass event = allEvents.get(i);
            if (event.getIsMeeting())
/*                if (event.getIsAttending())
                    meetings.add(event);
                else if (event.getNumOfAcceptedAttendees()<1) */
                meetings.add(event);
        }
        return meetings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = li.inflate(R.layout.menu_item, null);
        return new ViewHolder(view);
    }

    //Set text for each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.position = position;
        holder.tvSubject.setText(allMeetings.get(position).getSubject());
        holder.tvLocation.setText(allMeetings.get(position).getLocation().getDisplayName());
        holder.tvDayInWeek.setText(allMeetings.get(position).getShortDayInWeek());
        holder.tvDayInWeek.setVisibility(View.VISIBLE);

        ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context,R.color.bookingColor));

        //Set time and date
        if(allMeetings.get(position).getIsAllDay()) {
            holder.tvTimes.setText(context.getResources().getString(R.string.timeWholeDay));
            holder.tvDate.setText(allMeetings.get(position).getStartDate());
        }
        else{
            //get time and put in format (see strings)
            String times = context.getResources().getString(R.string.times, allMeetings.get(position).getStartTime(), allMeetings.get(position).getEndTime());
            holder.tvTimes.setText(times);
            holder.tvDate.setText(allMeetings.get(position).getEndDate());
        }
        // Set gray if date has passed.
        if (currentTime.isAfter(allMeetings.get(position).getStartTimeObj())) {
            ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            (holder.itemView).setAlpha((float) 0.4);
        }
    }

    @Override
    public int getItemCount() {
        return allMeetings.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvId,tvSubject,tvTimes,tvLocation,tvDate,tvDayInWeek,tvResponseStatus;
        int position;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDayInWeek = itemView.findViewById(R.id.tvDayInWeek);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("eventObject", allMeetings.get(position));

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment openFragment = new FragmentOpenMeeting();
                    openFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment, "booking").addToBackStack(null).commit();
                }
            });
        }
    }

}
