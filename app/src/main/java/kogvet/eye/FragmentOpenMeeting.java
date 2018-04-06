package kogvet.eye;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Fragment class for Open Meeting Fragment
 */
public class FragmentOpenMeeting extends Fragment {

    private String eventId;
    private String eventSubject;
    private String eventBodyPreview;
    private String eventDate;
    private String eventTime;
    private String eventLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            eventId = bundle.getString("id");
            eventSubject = bundle.getString("subject");
            eventBodyPreview = bundle.getString("bodyPreview");
            eventDate = bundle.getString("date");
            eventTime = bundle.getString("time");
            eventLocation = bundle.getString("location");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_open_meeting, container, false);
        TextView textSubject = inf.findViewById(R.id.textSubject);
        textSubject.setText(eventSubject);

        TextView textBodyPreview = inf.findViewById(R.id.textBodyPreview);
        textBodyPreview.setText(eventBodyPreview);

        TextView textDate = inf.findViewById(R.id.textDate);
        textDate.setText(eventDate);

        TextView textTime = inf.findViewById(R.id.textTime);
        textTime.setText(eventTime);

        TextView textLocation = inf.findViewById(R.id.textLocation);
        textLocation.setText(eventLocation);

        // Changes title to the subject name
        ((MainActivity) getActivity()).setActionBarTitle(eventSubject);
        ((MainActivity) getActivity()).showBackButton();
        return inf;
    }


}
