package kogvet.eye.CalendarFragment;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import kogvet.eye.EventClass;
import kogvet.eye.MainActivity;
import kogvet.eye.Notifications.NotificationService;
import kogvet.eye.R;


/**
 * Fragment class for Open Event Fragment
 */
public class FragmentOpenEvent extends Fragment {

    private EventClass event;
    private String eventDate;
    private String eventTime;
    private CheckBox checkBox;

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
        
        View inf = inflater.inflate(R.layout.fragment_open_event, container, false);

        setText(inf);

        // Changes title to the subject name
        ((MainActivity) getActivity()).setActionBarTitle(event.getSubject());
        ((MainActivity) getActivity()).showBackButton();

        //Buttons
        checkBox = inf.findViewById(R.id.checkBox);
        showBox();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = event.getId();
                String url = "https://graph.microsoft.com/beta/me/events/"+id;
                String importance = event.getImportance();

                ((MainActivity) getActivity()).patchGraphAPI(url, importance);
                Log.i("Information", "Button pressed");
                Log.i("ID", id);
            }
        });

        Log.i("Importance", event.getImportance());

        return inf;
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
        textTime.setText(eventTime);

        TextView textLocation = inf.findViewById(R.id.textLocation);
        textLocation.setText(event.getLocation().getDisplayName());
        textLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showMap(event);
            }
        });
    }

    public void showBox()
    {
        String eventImportance = event.getImportance();
        switch (eventImportance) {
            case "normal":
                break;
            case "low":
                checkBox.setChecked(true);
                break;
            default:
                break;
        }
    }

}
