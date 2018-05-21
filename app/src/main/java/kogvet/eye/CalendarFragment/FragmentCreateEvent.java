package kogvet.eye.CalendarFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import kogvet.eye.MainActivity;
import kogvet.eye.R;

/**
 * Fragment class for Create Event Fragment
 */
public class FragmentCreateEvent extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_create_event, container, false);

        // Changes title to the subject name
        ((MainActivity) getActivity()).showBackButton();

        return inf;
    }


}
