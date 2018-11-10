package kogvet.eye.CalendarFragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Locale;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;

/**
 * Helper class for CalendarFragment.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<EventClass> allEvents;
    private final ArrayList<EventClass> allActivities;
    private final LocalDateTime currentTime;

    public CalendarAdapter(Context context, ArrayList<EventClass> allEvents) {
        this.allEvents = allEvents;
        this.context = context;
        this.currentTime =  MainActivity.getCurrentTime();
        this.allActivities = getTodaysActivities(allEvents);
    }

    private ArrayList<EventClass> getTodaysActivities(ArrayList<EventClass> allEvents) {
        ArrayList<EventClass> allActivites = new ArrayList<>();
        String day = currentTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        for (int i = 0; i < allEvents.size(); i++) {
            EventClass event = allEvents.get(i);
            if (!event.getIsMeeting())
                if(event.getDayInWeek().equals(day))
                    allActivites.add(event);
        }
        return allActivites;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.menu_day_item, null);
        return new ViewHolder(view);
    }

    //Set text for each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.position = position;
        EventClass event = allActivities.get(position);
        holder.tvSubject.setText(event.getSubject());
        holder.tvLocation.setText(event.getLocation().getDisplayName());

        if (event.getLocation().getDisplayName().isEmpty()) {
            holder.tvLocation.setVisibility(View.GONE);
            holder.tvSubject.setPadding(0,40,0,40);
            holder.tvSubject.setGravity(Gravity.CENTER_VERTICAL);
        }

        //Set time and date
        if(event.getIsAllDay()) {
            holder.tvTimes.setText(R.string.timeWholeDay);
        }
        else{
            //get time and put in format (see strings)
            String times = context.getResources().getString(R.string.times, event.getStartTime(), event.getEndTime());
            holder.tvTimes.setText(times);
        }

        if (currentTime.isAfter(event.getStartTimeObj()) || event.getImportance().equals("low")) {
            ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            (holder.itemView).setAlpha((float) 0.4);
        }
    }

    @Override
    public int getItemCount() {
        return allActivities.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        int position;
        TextView tvSubject,tvTimes,tvLocation,tvDate;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("eventObject", allActivities.get(position));

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment openFragment = new FragmentOpenEvent();
                    openFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).addToBackStack(null).commit();
                }

            });
        }
    }
}