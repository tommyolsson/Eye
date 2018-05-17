package kogvet.eye.CalendarFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import kogvet.eye.CalendarFragment.PagerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;


/**
 * Fragment class for Calendar Fragment
 */
public class TabFragment extends Fragment {

    //    private RecyclerView recyclerView;
    ViewPager viewPager;
//    Bundle bundle;
//    private Context context;
    private ArrayList<EventClass> allEvents;
//    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        context = getActivity();

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            allEvents = bundle.getParcelableArrayList("allevents");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.menu_calendar));
        ((MainActivity) getActivity()).showBackButton();

        final TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        DayOfWeek weekDay = LocalDateTime.now().getDayOfWeek();

//        weekDay.getDisplayName(TextStyle.FULL, Locale.getDefault());

        tabLayout.addTab(tabLayout.newTab().setText(weekDay.getDisplayName(TextStyle.FULL, Locale.getDefault())));
        tabLayout.addTab(tabLayout.newTab().setText("Vecka"));

        viewPager = view.findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), allEvents);
//        kogvet.eye.CalendarFragment.PagerAdapter pagerAdapter = new kogvet.eye.CalendarFragment.PagerAdapter(getFragmentManager(), allEvents);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


/*        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myUpdateOperation();
            }
        });*/

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));


//        detectSwipe(view);

        //        detectSwipe(recyclerView);

//        CalendarAdapter calendarAdapter = new CalendarAdapter(context, allEvents);
//        recyclerView.setAdapter(calendarAdapter);

    }

    /*private void detectSwipe(final View view) {
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
    }*/

    /*private void myUpdateOperation() {
        ((MainActivity)getActivity()).callGraphAPI();
        mSwipeRefreshLayout.setRefreshing(false);
    }*/


}
