package kogvet.eye.BookingFragment;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;


/**
 * Fragment class for Booking Fragment
 */
public class FragmentBooking extends Fragment {

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

        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.menu_booking));
        ((MainActivity) getActivity()).showBackButton();

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
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        BookingAdapter myAdapter = new BookingAdapter(context, allEvents);
        recyclerView.setAdapter(myAdapter);
    }

    private void myUpdateOperation() {
        ((MainActivity)getActivity()).callGraphAPI();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
