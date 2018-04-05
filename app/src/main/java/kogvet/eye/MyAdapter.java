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
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Created by Loldator on 2018-02-27.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> allEvents;
    private final ArrayList<Event> allActivities;
    private final LocalDateTime currentTime;

    public MyAdapter(Context context,  ArrayList<Event> allEvents) {
        this.allEvents = allEvents;
        this.allActivities = getAllActivities(allEvents);
        this.context = context;
        this.currentTime = getCurrentTime();
    }

    private ArrayList<Event> getAllActivities(ArrayList<Event> allEvents) {
        ArrayList<Event> allActivites = new ArrayList<>();
        for (int i = 0; i < allEvents.size(); i++) {
            if (!allEvents.get(i).isMeeting)
                allActivites.add(allEvents.get(i));
        }
        return allActivites;
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
        holder.tvSubject.setText(allActivities.get(position).subject);
        holder.tvBodyPreview.setText(allActivities.get(position).bodyPreview);
        holder.tvLocation.setText(allActivities.get(position).location.displayName);
        //Set time and date
        if(allActivities.get(position).isAllDay) {
            holder.tvTimes.setText(context.getResources().getString(R.string.timeWholeDay));
            holder.tvDate.setText(allActivities.get(position).getStartDate());
        }
        else{
            //get time and put in format (see strings)
            String times = context.getResources().getString(R.string.times, allActivities.get(position).getStartTime(), allActivities.get(position).getEndTime());
            holder.tvTimes.setText(times);
            holder.tvDate.setText(allActivities.get(position).getEndDate());
        }

        if (currentTime.isAfter(allActivities.get(position).startTimeObj)) {
            ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            (holder.itemView).setAlpha((float) 0.4);
        }
    }

    @Override
    public int getItemCount() {
        return allActivities.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvSubject,tvBodyPreview,tvTimes,tvLocation,tvDate;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvBodyPreview = itemView.findViewById(R.id.tvBodyPreview);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //EXAMPLE ON CLICK FUNCTION
                    Bundle bundle = new Bundle();
                    bundle.putString("subject", tvSubject.getText().toString());
                    bundle.putString("bodyPreview", tvBodyPreview.getText().toString());
                    bundle.putString("date", tvDate.getText().toString());
                    bundle.putString("time", tvTimes.getText().toString());
                    bundle.putString("location", tvLocation.getText().toString());

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment openFragment = new FragmentOpenEvent();
                    openFragment.setArguments(bundle);
                    activity.getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).addToBackStack(null).commit();
                }
            });
        }
    }

}
