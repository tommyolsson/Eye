package kogvet.eye.CalendarFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kogvet.eye.MainActivity;
import kogvet.eye.R;

/**
 * Fragment class for Create Event Fragment
 */
public class FragmentCreateEvent extends Fragment {

    private Button createButton;
    EditText subjectEditText;
    EditText locationEditText;
    EditText dateEditText;


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

        subjectEditText =  (EditText) inf.findViewById(R.id.subjectEditText);
        locationEditText = (EditText) inf.findViewById(R.id.locationEditText);
        dateEditText = (EditText) inf.findViewById(R.id.dateEditText);

        createButton = inf.findViewById(R.id.createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subject = subjectEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String date = dateEditText.getText().toString();

                Log.i("Ämne", subject);
                Log.i("Plats", location);
                Log.i("Datum", date);
                ((MainActivity) getActivity()).createEventGraphAPI(subject, location);

                Toast.makeText(getContext(), "Aktivitet skapad", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        return inf;
    }


}
