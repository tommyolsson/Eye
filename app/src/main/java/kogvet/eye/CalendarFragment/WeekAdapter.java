package kogvet.eye.CalendarFragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import kogvet.eye.EventClass;
import kogvet.eye.R;

/**
 * Created by Loldator on 2018-02-27.
 */

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<EventClass> allActivities;
    private final LocalDateTime currentTime;

    public WeekAdapter(Context context, ArrayList<EventClass> allActivities) {
        this.allActivities = allActivities;
        this.context = context;
        this.currentTime = getCurrentTime();
    }

    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now().truncatedTo((ChronoUnit.MINUTES));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.menu_week_item, null);
        return new ViewHolder(view);
    }

    //Set text for each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.position = position;
        EventClass event = allActivities.get(position);
        holder.tvSubject.setText(event.getSubject());

        //Set time and date
        if(allActivities.get(position).getIsAllDay()) {
            holder.tvStartTime.setText(R.string.timeWholeDay);
            holder.tvEndTime.setText("");
            holder.tvDate.setText(event.getStartDate());
        }
        else{
            //get time and put in format (see strings)
//            String times = context.getResources().getString(R.string.times, event.getStartTime(), event.getEndTime());
//            holder.tvStartTime.setText(event.getStartTime());
            holder.tvStartTime.setText(context.getString(R.string.startTime, event.getStartTime()));
            holder.tvEndTime.setText(context.getString(R.string.endTime, event.getEndTime()));
//            holder.tvEndTime.setText(event.getEndTime());
            holder.tvDate.setText(event.getEndDate());
        }

        if (currentTime.isAfter(event.getStartTimeObj())) {
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
        TextView tvSubject, tvStartTime, tvEndTime,tvLocation,tvDate;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //EXAMPLE ON CLICK FUNCTION
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("eventObject", allActivities.get(position));
//                    bundle.putString("date", tvDate.getText().toString());
//                    bundle.putString("time", tvStartTime.getText().toString());

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment openFragment = new FragmentOpenEvent();
                    openFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).addToBackStack(null).commit();
                }

            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemView.setAlpha((float) 0.5);
                    Log.d("LongClick", "LONG PRESSSSSS");
                    return true;
                }
            });
        }
    }

}
