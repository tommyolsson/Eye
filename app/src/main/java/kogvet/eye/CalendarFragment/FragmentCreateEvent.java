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

        EditText subjectEditText = (EditText) inf.findViewById(R.id.subjectEditText);
        EditText locationEditText = (EditText) inf.findViewById(R.id.locationEditText);
        createButton = inf.findViewById(R.id.createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //((MainActivity) getActivity()).createEventGraphAPI();
                Toast.makeText(getContext(), "Aktivitet skapad", Toast.LENGTH_SHORT).show();
//                ((MainActivity)getActivity()).callGraphAPI();
            }
        });

        return inf;
    }


}
