package kogvet.eye.BookingFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kogvet.eye.MainActivity;
import kogvet.eye.R;


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
    private String eventResponseStatus;
    private Button bookButton;
    private Button cancelButton;

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
            eventResponseStatus = bundle.getString("responseStatus");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_open_meeting, container, false);
        setText(inf);

        //Buttons
        bookButton = inf.findViewById(R.id.bookButton);
        cancelButton = inf.findViewById(R.id.cancelButton);
        showButton();

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = eventId;
                String url="https://graph.microsoft.com/beta/me/events/"+id+"/accept";
                ((MainActivity) getActivity()).postGraphAPI(url);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = eventId;
                String url="https://graph.microsoft.com/beta/me/events/"+id+"/tentativelyAccept";
                ((MainActivity) getActivity()).postGraphAPI(url);
            }
        });

        // Changes title to the subject name
        ((MainActivity) getActivity()).setActionBarTitle(eventSubject);
        ((MainActivity) getActivity()).showBackButton();

        return inf;
    }

    private void setText(View inf) {
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

        TextView textResponseStatus = inf.findViewById(R.id.textResponseStatus);
        textResponseStatus.setText(eventResponseStatus);
    }

    public void showButton()
    {
        if (eventResponseStatus.equals("accepted"))
        {
            // Makes cancel button visible
            cancelButton.setVisibility(View.VISIBLE);
        }
        else
        {
            // Makes book button visible
            bookButton.setVisibility(View.VISIBLE);
        }
    }

}
