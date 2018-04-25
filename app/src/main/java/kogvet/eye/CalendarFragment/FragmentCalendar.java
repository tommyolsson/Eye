package kogvet.eye.CalendarFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.day));
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


        detectSwipe(view);

        //        detectSwipe(recyclerView);

        CalendarAdapter calendarAdapter = new CalendarAdapter(context, allEvents);
        recyclerView.setAdapter(calendarAdapter);

    }

    private void detectSwipe(final View view) {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        //do things
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("allevents", allEvents);

                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment openFragment = new FragmentWeek();
                        openFragment.setArguments(bundle);
                        activity.getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment, "main").commit();
                    }

                    // OVERRIDE SWIPE ANIMATION
                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {

                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void myUpdateOperation() {
        ((MainActivity)getActivity()).callGraphAPI();
        mSwipeRefreshLayout.setRefreshing(false);
    }


}
