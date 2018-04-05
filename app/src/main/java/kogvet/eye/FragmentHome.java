package kogvet.eye;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



/**
 * Fragment class for Home Fragment
 */
public class FragmentHome extends Fragment {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<Event> allEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            allEvents = bundle.getParcelableArrayList("allevents");
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity) getActivity()).showBackButton();

        TextView currentDate = inf.findViewById(R.id.dateToday);
        currentDate.setText(getCurrentDate());

        return inf;
    }

    public String getCurrentDate()
    {
        //DateFormat df = new SimpleDateFormat("EEEE, d MMMM yyyy");
        DateFormat df = new SimpleDateFormat("yyyy MMMM dd, EEEE");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.setAdapter(new SampleRecycler());

        if (allEvents!=null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            HomeAdapter homeAdapter = new HomeAdapter(context, allEvents);
            recyclerView.setAdapter(homeAdapter);
        }

    }

}
