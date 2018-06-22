package kogvet.eye.CalendarFragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;

import static java.time.LocalDate.now;


/**
 * Fragment class for Week Fragment
 */
public class FragmentWeek extends Fragment  {

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<EventClass> allEvents;

    private ArrayList<EventClass> activitiesMonday;
    private RecyclerView mondayRecyclerView;

    private ArrayList<EventClass> activitiesTuesday;
    private RecyclerView tuesdayRecyclerView;

    private ArrayList<EventClass> activitiesWednesday;
    private RecyclerView wednesdayRecyclerView;

    private ArrayList<EventClass> activitiesThursday;
    private RecyclerView thursdayRecyclerView;

    private ArrayList<EventClass> activitiesFriday;
    private RecyclerView fridayRecyclerView;

    /* Puts all events in the correct arraylist according to weekday */
    private void getWeekDayEvents(ArrayList<EventClass> allEvents) {
        activitiesMonday = new ArrayList<>();
        activitiesTuesday = new ArrayList<>();
        activitiesWednesday = new ArrayList<>();
        activitiesThursday = new ArrayList<>();
        activitiesFriday = new ArrayList<>();
        for(int i = 0; i < allEvents.size(); i++) {
            java.time.DayOfWeek weekDay = allEvents.get(i).getStartTimeObj().getDayOfWeek();
            switch (weekDay) {
                case MONDAY:
                    activitiesMonday.add(allEvents.get(i));
                    break;
                case TUESDAY:
                    activitiesTuesday.add(allEvents.get(i));
                    break;
                case WEDNESDAY:
                    activitiesWednesday.add(allEvents.get(i));
                    break;
                case THURSDAY:
                    activitiesThursday.add(allEvents.get(i));
                    break;
                case FRIDAY:
                    activitiesFriday.add(allEvents.get(i));
                    break;
                default:
                    break;

            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            allEvents = bundle.getParcelableArrayList("allevents");
        }
        getWeekDayEvents(allEvents);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
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
        mondayRecyclerView = view.findViewById(R.id.fragment_recycler_view_Monday);
        prepareRecyclerView(mondayRecyclerView, activitiesMonday);

        tuesdayRecyclerView = view.findViewById(R.id.fragment_recycler_view_Tuesday);
        prepareRecyclerView(tuesdayRecyclerView, activitiesTuesday);

        wednesdayRecyclerView = view.findViewById(R.id.fragment_recycler_view_Wednesday);
        prepareRecyclerView(wednesdayRecyclerView, activitiesWednesday);

        thursdayRecyclerView = view.findViewById(R.id.fragment_recycler_view_Thursday);
        prepareRecyclerView(thursdayRecyclerView, activitiesThursday);

        fridayRecyclerView = view.findViewById(R.id.fragment_recycler_view_Friday);
        prepareRecyclerView(fridayRecyclerView, activitiesFriday);

        ScrollView scrollView = view.findViewById(R.id.scrollview);

        underlineDayInWeek(view);

    }

    /* Underlines the current day in the UI*/
    private void underlineDayInWeek(View view) {
        DayOfWeek weekDay = LocalDateTime.now().getDayOfWeek();
        TextView textView = null;
        switch (weekDay) {
            case MONDAY:
                textView = view.findViewById(R.id.monday);
                break;
            case TUESDAY:
                textView = view.findViewById(R.id.tuesday);
                break;
            case WEDNESDAY:
                textView = view.findViewById(R.id.wednesday);
                break;
            case THURSDAY:
                textView = view.findViewById(R.id.thursday);
                break;
            case FRIDAY:
                textView = view.findViewById(R.id.friday);
                break;
            default:
                break;
        }
        if (textView != null) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }


    }

    private void prepareRecyclerView(RecyclerView mondayRecyclerView, ArrayList<EventClass> activitiesMonday) {
        mondayRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        WeekAdapter mondayAdapter = new WeekAdapter(context, activitiesMonday);
        mondayRecyclerView.setAdapter(mondayAdapter);
    }

    private void myUpdateOperation() {
        ((MainActivity)getActivity()).callGraphAPI();
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
