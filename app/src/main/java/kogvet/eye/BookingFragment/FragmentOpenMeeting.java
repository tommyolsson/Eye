package kogvet.eye.BookingFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.R;


/**
 * Fragment class for Open Meeting Fragment
 */
public class FragmentOpenMeeting extends Fragment {

    private EventClass event;
    private String eventDate;
    private String eventTime;
    private Button bookButton;
    private Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            event = bundle.getParcelable("eventObject");
            eventDate = event.getStartDate();
            eventTime = event.getStartTime();
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
                String id = event.getId();
                String url="https://graph.microsoft.com/beta/me/events/"+id+"/accept";
                ((MainActivity) getActivity()).postGraphAPI(url);
                event.getResponseStatus().setResponse("accepted");
                recreateFragment();
                Toast.makeText(getContext(), "Bokat", Toast.LENGTH_SHORT).show();
//                ((MainActivity)getActivity()).callGraphAPI();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = event.getId();
                String url="https://graph.microsoft.com/beta/me/events/"+id+"/tentativelyAccept";
                ((MainActivity) getActivity()).postGraphAPI(url);
                event.getResponseStatus().setResponse("tentativelyAccepted");
                recreateFragment();
                Toast.makeText(getContext(), "Avbokat", Toast.LENGTH_SHORT).show();
//                ((MainActivity)getActivity()).callGraphAPI();
            }
        });

        // Changes title to the subject name
        ((MainActivity) getActivity()).setActionBarTitle(event.getSubject());
        ((MainActivity) getActivity()).showBackButton();

        return inf;
    }

    private void recreateFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("eventObject", event);
        bundle.putString("date", eventDate);
        bundle.putString("time", eventTime);

        Fragment openFragment = new FragmentOpenMeeting();
        openFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment, "booking").addToBackStack(null).commit();
    }

    private void setText(View inf) {
        TextView textSubject = inf.findViewById(R.id.textSubject);
        textSubject.setText(event.getSubject());

        TextView textBodyPreview = inf.findViewById(R.id.textBodyPreview);
        textBodyPreview.setText(event.getBodyPreview());
        Linkify.addLinks(textBodyPreview, Linkify.WEB_URLS);

        TextView textDate = inf.findViewById(R.id.textDate);
        textDate.setText(eventDate);

        TextView textTime = inf.findViewById(R.id.textTime);
        textTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time_gray_24dp, 0,0,0);
        textTime.setText(eventTime);

        TextView textLocation = inf.findViewById(R.id.textLocation);
        textLocation.setText(event.getLocation().getDisplayName());
        if (!event.getLocation().getDisplayName().isEmpty()) {
            textLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_red_24dp, 0,0,0);
            textLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).showMap(event);
                }
            });
        }
    }


    public void showButton()
    {
        String eventResponse = event.getResponseStatus().getResponse();
        switch (eventResponse) {
            case "accepted":
                cancelButton.setVisibility(View.VISIBLE);
                break;
            case "tentativelyAccepted":
                bookButton.setVisibility(View.VISIBLE);
                break;
            case "notResponded":
                bookButton.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

}
