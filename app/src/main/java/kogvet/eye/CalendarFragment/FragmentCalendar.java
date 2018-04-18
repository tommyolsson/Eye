package kogvet.eye.CalendarFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;


/**
 * Fragment class for Calendar Fragment
 */
public class FragmentCalendar extends Fragment {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<EventClass> allEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;

//    public static FragmentCalendar newInstance(Bundle bundle) {
//        FragmentCalendar fragment = new FragmentCalendar();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

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
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.menu_calendar));
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

        detectSwipe();

        CalendarAdapter calendarAdapter = new CalendarAdapter(context, allEvents);
        recyclerView.setAdapter(calendarAdapter);

    }

    private void myUpdateOperation() {
        ((MainActivity)getActivity()).callGraphAPI();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void detectSwipe() {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            private float x1,x2;
            static final int MIN_DISTANCE = 150;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            if (x2 > x1)
                            {
                                Log.i("Information", "Swipe right");
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("allevents", allEvents);

                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                Fragment openFragment = new FragmentWeek();
                                openFragment.setArguments(bundle);
                                activity.getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment, "main").commit();

                            }
                            else
                            {
                                Log.i("Information", "Swipe left");
                            }
                        }
                        else
                        {
                            // consider as something else - a screen tap for example
                        }
                        return true;
                }
                return false;
            }
        });
    }

}
