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
    private final LocalDateTime currentTime;

    public MyAdapter(Context context,  ArrayList<Event> allEvents) {
        this.allEvents = allEvents;
        this.context = context;
        this.currentTime = getCurrentTime();
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
        holder.tvSubject.setText(allEvents.get(position).subject);
        holder.tvLocation.setText(allEvents.get(position).location.displayName);
        //Set time and date
        if(allEvents.get(position).isAllDay) {
            holder.tvTimes.setText(context.getResources().getString(R.string.timeWholeDay, allEvents.get(position).startTime));
            holder.tvDate.setText(allEvents.get(position).startDate);
        }
        else{
            //get time and put in format (see strings)
            String times = context.getResources().getString(R.string.times, allEvents.get(position).startTime, allEvents.get(position).endTime);
            holder.tvTimes.setText(times);
            holder.tvDate.setText(allEvents.get(position).endDate);
        }

        String stringEventTime = allEvents.get(position).startDate + "T" + allEvents.get(position).startTime;
        LocalDateTime eventTime = LocalDateTime.parse(stringEventTime);
        if (currentTime.isAfter(eventTime)) {
            ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            ((CardView) holder.itemView).setAlpha((float) 0.4);
        }
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvSubject,tvTimes,tvLocation,tvDate;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvTimes = (TextView) itemView.findViewById(R.id.tvTimes);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //EXAMPLE ON CLICK FUNCTION
                  //  Toast.makeText(context, tvSubject.getText().toString(), Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("subject", tvSubject.getText().toString());
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
