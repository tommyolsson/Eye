package kogvet.eye.CalendarFragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    EditText startEditText;
    EditText endEditText;
    CheckBox checkBox;
    boolean isAllDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inf = inflater.inflate(R.layout.fragment_create_event, container, false);

        // Changes title to the subject name
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.newActivity));
        ((MainActivity) getActivity()).showBackButton();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        subjectEditText =  (EditText) inf.findViewById(R.id.subjectEditText);
        locationEditText = (EditText) inf.findViewById(R.id.locationEditText);
        dateEditText = (EditText) inf.findViewById(R.id.dateEditText);
        startEditText = (EditText) inf.findViewById(R.id.startEditText);
        endEditText = (EditText) inf.findViewById(R.id.endEditText);

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                final Calendar mCalendar=Calendar.getInstance();
                int mYear=mCalendar.get(Calendar.YEAR);
                int mMonth=mCalendar.get(Calendar.MONTH);
                int mDay=mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        /*  Set picked date in textedit  */

                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        mCalendar.set(Calendar.YEAR, selectedyear);
                        mCalendar.set(Calendar.MONTH, selectedmonth);
                        mCalendar.set(Calendar.DAY_OF_MONTH, selectedday);

                        dateEditText.setText(sdf.format(mCalendar.getTime()));

                    }
                },mYear, mMonth, mDay);
                mDatePicker.getDatePicker().setCalendarViewShown(false);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });

        startEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        checkBox = inf.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    //Disables editText for time if event is all day
                    startEditText.setEnabled(false);
                    endEditText.setEnabled(false);
                    isAllDay = true;
                }
                else
                {
                    startEditText.setEnabled(true);
                    endEditText.setEnabled(true);
                    isAllDay = false;
                }

            }
        });

        createButton = inf.findViewById(R.id.createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subject = subjectEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String start = startEditText.getText().toString();
                String end = endEditText.getText().toString();

                Log.i("Ämne", subject);
                Log.i("Plats", location);
                Log.i("Datum", date);
                Log.i("Start", start);
                Log.i("Slut", end);
                Log.i("Heldag", Boolean.toString(isAllDay));

                if (subject.matches("")) {
                    Toast.makeText(getContext(), "Namn på aktivitet saknas", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isAllDay) {
                        LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        String endDate = ld.plusDays(1).toString();

                        ((MainActivity) getActivity()).createEventGraphAPI(subject, location, date, endDate,"00", "00", isAllDay);
                    }
                    else {
                        ((MainActivity) getActivity()).createEventGraphAPI(subject, location, date, date, start, end, isAllDay);
                    }

                    Toast.makeText(getContext(), "Aktivitet skapad", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

            }
        });

        return inf;
    }


}
