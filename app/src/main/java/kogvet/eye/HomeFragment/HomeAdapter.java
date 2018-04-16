package kogvet.eye.HomeFragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import kogvet.eye.CalendarFragment.FragmentOpenEvent;
import kogvet.eye.EventClass;
import kogvet.eye.R;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<EventClass> currentEvents;
    private final LocalDateTime currentTime;

    public HomeAdapter(Context context,  ArrayList<EventClass> allEvents) {
        this.context = context;
        this.currentTime = getCurrentTime();
        this.currentEvents = getCurrentEvents(allEvents);
    }

    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now().truncatedTo((ChronoUnit.DAYS));
    }

    private ArrayList<EventClass> getCurrentEvents(ArrayList<EventClass> allEvents) {
        ArrayList<EventClass> currentEvents = new ArrayList<>();
        for (int i = 0; i < allEvents.size(); i++) {
            if ((allEvents.get(i).getStartTimeObj()).isAfter(currentTime))
                currentEvents.add(allEvents.get(i));
        }
        return currentEvents;
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
        holder.tvSubject.setText(currentEvents.get(position).getSubject());
        holder.tvBodyPreview.setText(currentEvents.get(position).getBodyPreview());
        holder.tvLocation.setText(currentEvents.get(position).getLocation().getDisplayName());
        //Set time and date
        if(currentEvents.get(position).getIsAllDay()) {
            holder.tvTimes.setText(context.getResources().getString(R.string.timeWholeDay));
            holder.tvDate.setText(currentEvents.get(position).getStartDate());
        }
        else{
            //get time and put in format (see strings)
            String times = context.getResources().getString(R.string.times, currentEvents.get(position).getStartTime(), currentEvents.get(position).getEndTime());
            holder.tvTimes.setText(times);
            holder.tvDate.setText(currentEvents.get(position).getEndDate());
        }

//        if (currentTime.isAfter(currentEvents.get(position).startTimeObj)) {
//            ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
//            (holder.itemView).setAlpha((float) 0.4);
//        }
    }

    @Override
    public int getItemCount() {
        if(currentEvents.size()<3)
            return currentEvents.size();
        else
            return 3;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        int position;
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
                    bundle.putParcelable("eventObject", currentEvents.get(position));
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