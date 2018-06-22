package kogvet.eye.CalendarFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import kogvet.eye.EventClass;

/**
 * Helper class for the TabFragment class.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private ArrayList<EventClass> allEvents;

    public PagerAdapter(FragmentManager fm, ArrayList<EventClass> arrayList) {
        super(fm);
        allEvents = arrayList;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("allevents", allEvents);
        if (position == 0) {
            FragmentCalendar fragment = new FragmentCalendar();
            fragment.setArguments(bundle);
            return fragment;
        } else {
            FragmentWeek fragment = new FragmentWeek();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "dag";
            case 1:
                return "vecka";
            default:
                return null;
        }
    }


}
