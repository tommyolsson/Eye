package kogvet.eye;

import android.app.Fragment;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Created by Loldator on 2018-02-27.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> allEvents;
    private final ArrayList<Event> allMeetings;
    private final LocalDateTime currentTime;

    public BookingAdapter(Context context, ArrayList<Event> allEvents) {
        this.allEvents = allEvents;
        this.allMeetings = getMeetings(allEvents);
        this.context = context;
        this.currentTime = getCurrentTime();
    }

    private ArrayList<Event> getMeetings(ArrayList<Event> allEvents) {
        ArrayList<Event> allMeetings = new ArrayList<>();
        for (int i = 0; i < allEvents.size(); i++) {
            if (allEvents.get(i).isMeeting)
                allMeetings.add(allEvents.get(i));
        }
        return allMeetings;
    }

    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now().truncatedTo((ChronoUnit.MINUTES));
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
        holder.tvId.setText(allMeetings.get(position).id);
        holder.tvSubject.setText(allMeetings.get(position).subject);
        holder.tvBodyPreview.setText(allMeetings.get(position).bodyPreview);
        holder.tvLocation.setText(allMeetings.get(position).location.displayName);
        holder.tvResponseStatus.setText(allEvents.get(position).responseStatus.response);

        //Set time and date
        if(allMeetings.get(position).isAllDay) {
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
        if (currentTime.isAfter(allMeetings.get(position).startTimeObj)) {
            ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            (holder.itemView).setAlpha((float) 0.4);
        }
    }

    @Override
    public int getItemCount() {
        return allMeetings.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvId,tvSubject,tvBodyPreview,tvTimes,tvLocation,tvDate,tvResponseStatus;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvId = itemView.findViewById(R.id.tvId);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvBodyPreview = itemView.findViewById(R.id.tvBodyPreview);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResponseStatus = itemView.findViewById(R.id.tvResponseStatus);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //EXAMPLE ON CLICK FUNCTION
                    Bundle bundle = new Bundle();
                    bundle.putString("id", tvId.getText().toString());
                    bundle.putString("subject", tvSubject.getText().toString());
                    bundle.putString("bodyPreview", tvBodyPreview.getText().toString());
                    bundle.putString("date", tvDate.getText().toString());
                    bundle.putString("time", tvTimes.getText().toString());
                    bundle.putString("location", tvLocation.getText().toString());
                    bundle.putString("responseStatus", tvResponseStatus.getText().toString());

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment openFragment = new FragmentOpenMeeting();
                    openFragment.setArguments(bundle);
                    activity.getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).addToBackStack(null).commit();
                }
            });
        }
    }

}
