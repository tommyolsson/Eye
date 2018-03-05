package kogvet.eye;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Loldator on 2018-02-27.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> allEvents;

    public MyAdapter(Context context,  ArrayList<Event> allEvents) {
        this.allEvents = allEvents;
        this.context = context;
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
            holder.tvTimes.setText("heldag");
            holder.tvDate.setText(allEvents.get(position).startDate);
        }
        else{
            //get time and put in format (see strings)
            String times = context.getResources().getString(R.string.times, allEvents.get(position).startTime, allEvents.get(position).endTime);
            holder.tvTimes.setText(times);
            holder.tvDate.setText(allEvents.get(position).endDate);
        }

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

                    Toast.makeText(context, tvSubject.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
