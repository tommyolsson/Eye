package kogvet.eye;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Loldator on 2018-02-27.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final Context context;

    String [] subjects = {"Aktivitet 1","Aktivitet 2","Aktivitet 3","Aktivitet 4","Aktivitet 5","Aktivitet 6"};

    String [] startTimes = {"8:00","8:00","8:00","8:00","8:00","8:00"};

    String [] endTimes = {"9:00","9:00","9:00","9:00","9:00","9:00",};

    public MyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = li.inflate(R.layout.menu_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvSubject.setText(subjects[position]);
        holder.tvStart.setText(startTimes[position]+" - ");
        holder.tvEnd.setText(endTimes[position]);
    }

    @Override
    public int getItemCount() {
        return subjects.length;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvSubject,tvStart,tvEnd;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvStart = (TextView) itemView.findViewById(R.id.tvStart);
            tvEnd = (TextView) itemView.findViewById(R.id.tvEnd);
            
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
