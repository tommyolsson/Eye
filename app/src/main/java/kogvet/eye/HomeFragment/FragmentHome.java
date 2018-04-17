package kogvet.eye.HomeFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;


/**
 * Fragment class for Home Fragment
 */
public class FragmentHome extends Fragment {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<EventClass> allEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.menu_home));
        ((MainActivity) getActivity()).showBackButton();

        TextView currentDate = view.findViewById(R.id.dateToday);
        currentDate.setText(getCurrentDate());

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myUpdateOperation();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (allEvents!=null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            HomeAdapter homeAdapter = new HomeAdapter(context, allEvents);
            recyclerView.setAdapter(homeAdapter);
        }
    }

    private void myUpdateOperation() {
        ((MainActivity)getActivity()).callGraphAPI();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public String getCurrentDate()
    {
        //DateFormat df = new SimpleDateFormat("EEEE, d MMMM yyyy");
        DateFormat df = new SimpleDateFormat("EEEE\nMMMM dd");
//        DateFormat df = new SimpleDateFormat("EEEE \n yyyy MMMM dd");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

}
