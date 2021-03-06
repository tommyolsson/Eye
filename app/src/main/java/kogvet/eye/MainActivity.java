package kogvet.eye;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.microsoft.graph.extensions.DayOfWeek;
import com.microsoft.identity.client.*;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;

import kogvet.eye.BookingFragment.FragmentBooking;
import kogvet.eye.BookingFragment.FragmentOpenMeeting;
import kogvet.eye.CalendarFragment.CalendarAdapter;
import kogvet.eye.CalendarFragment.FragmentCalendar;
import kogvet.eye.CalendarFragment.FragmentCreateEvent;
import kogvet.eye.CalendarFragment.FragmentOpenEvent;
import kogvet.eye.CalendarFragment.FragmentWeek;
import kogvet.eye.CalendarFragment.TabFragment;
import kogvet.eye.HomeFragment.FragmentHome;
import kogvet.eye.LoginFragment.FragmentLogin;


public class MainActivity extends AppCompatActivity {

    private ArrayList<EventClass> allEvents = new ArrayList<>();
    boolean menuVisible=true;

    /* Azure AD v2 Configs */
    final static String CLIENT_ID = "fac1a20e-54f5-49d2-ae55-724b980a2eb9";
    final static String SCOPES [] = {"User.Read", "Calendars.Read", "Calendars.Read.Shared", "Calendars.ReadWrite"};

    public static String startDate;
    public static String endDate;

    static String msGraph_URL;

    /* UI & Debugging Variables */
    private static final String TAG = MainActivity.class.getSimpleName();

    /* Azure AD Variables */
    private PublicClientApplication sampleApp;
    private AuthenticationResult authResult;

    //-------Methods-------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiate AndroidThreeTen to run Date and Time
        AndroidThreeTen.init(this);

        /* Azure AD v2 Configs for Graph URL*/
        startDate = LocalDate.now().toString();
        endDate = LocalDate.now().plusWeeks(1).toString();

        msGraph_URL = "https://graph.microsoft.com/beta/me/calendar/calendarView?startDateTime="+startDate+"T00:00:00.0000000&endDateTime="+endDate+"T00:00:00.0000000&$orderby=start/dateTime";


        // Runs Bottom Navigation Bar
        setupNavigationView();

        /* Configure your sample app and save state for this activity */
        sampleApp = null;
        if (sampleApp == null) {
            sampleApp = new PublicClientApplication(this.getApplicationContext(), CLIENT_ID);
        }

        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        List<User> users=null;
        try {
            users = sampleApp.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */
                sampleApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
            } else {
                /* We have no user */
                updateSignedOutUI();
            }
        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    //
    // Core Identity methods used by MSAL
    // ==================================
    // onActivityResult() - handles redirect from System browser
    // onCallGraphClicked() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // onSignOutClicked() - Signs user out of the app & updates UI
    // callGraphAPI() - called on successful token acquisition which makes an HTTP request to graph
    //

    /* Handles the redirect from the System Browser */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

    /* Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    private void onCallGraphClicked() {
        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
    }

    /* Clears a user's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     */
    private void onSignOutClicked() {

        /* Attempt to get a user and remove their cookies from cache */
        List<User> users = null;

        try {
            users = sampleApp.getUsers();

            if (users == null) {
                /* We have no users */

            } else if (users.size() == 1) {
                /* We have 1 user */
                /* Remove from token cache */
                sampleApp.remove(users.get(0));
                updateSignedOutUI();

            }
            else {
                /* We have multiple users */
                for (int i = 0; i < users.size(); i++) {
                    sampleApp.remove(users.get(i));
                }
            }

            Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                    .show();

        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    /*Returns the latest mondays date as a string*/
    private String getThisWeeksMonday(LocalDate today) {
        org.threeten.bp.DayOfWeek weekDay = today.getDayOfWeek();

        switch (weekDay) {
            case MONDAY:
                return today.toString();
            case TUESDAY:
                return today.minusDays(1).toString();
            case WEDNESDAY:
                return today.minusDays(2).toString();
            case THURSDAY:
                return today.minusDays(3).toString();
            case FRIDAY:
                return today.minusDays(4).toString();
            case SATURDAY:
                return today.minusDays(5).toString();
            case SUNDAY:
                return today.minusDays(6).toString();
            default:
                return today.toString();
        }

    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token
     * Used for fetching data from api */
    public void callGraphAPI() {
        /*Update Url to request events from today to one month ahead*/
        startDate = getThisWeeksMonday(LocalDate.now());
        endDate = LocalDate.now().plusMonths(1).toString();
        msGraph_URL = "https://graph.microsoft.com/beta/me/calendar/calendarView?startDateTime="+startDate+"T00:00:00.0000000&endDateTime="+endDate+"T00:00:00.0000000&$top=500&$orderby=start/dateTime";

        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, msGraph_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());
                try {
                    getAllEvents(response);
                    updateUIOnResponse("current_fragment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toastSuccessGraph();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                headers.put("Prefer", "outlook.timezone=\"Central European Standard Time\"");
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /* Send object to api */
    public void postGraphAPI(String url) {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        Map<String, String> jsonParams = new HashMap<>();
//        jsonParams.put("comment", "test");

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Successfully called graph, process data and send to UI */
                        Log.d(TAG, "Response: " + response.toString());
                        updateUIOnResponse("booking");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String,String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("key", "value");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP POST to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /* Update object through api */
    public void patchGraphAPI(String url, String currentImportance) {
        Log.d(TAG, "Starting volley request to graph");
        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        Map<String, String> jsonParams = new HashMap<>();

        /* Importance helps CheckBox to show if a event is finished */
        if(currentImportance.equals("normal")) {
            jsonParams.put("importance", "low");
        }
        else{
            jsonParams.put("importance", "normal");
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH,
                url,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Successfully called graph, process data and send to UI */
                        Log.d(TAG, "Response: " + response.toString());
                        updateUIOnResponse("current_fragment");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String,String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("key", "value");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP PATCH to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /* Creates a new event as a hashmap and posts to API */
    public void createEventGraphAPI(String eventSubject, String eventLocation, String eventDate, String eventEndDate, String eventStart, String eventEnd, boolean eventIsAllDay) {
        Log.d(TAG, "Starting volley request to graph");
        String url = "https://graph.microsoft.com/v1.0/me/events";

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        String eventStartDateTime = eventDate + "T" + eventStart + ":00";
        Map<String, Object> startTime = new HashMap<>();
        startTime.put("dateTime", eventStartDateTime);
        startTime.put("timeZone", "Europe/Paris");

        String eventEndDateTime = eventDate + "T" + eventEnd + ":00";

        if (eventIsAllDay)
        {
            /* Changes end date to date + 1 */
            eventEndDateTime = eventEndDate + "T" + eventEnd + ":00";
        }

        Map<String, Object> endTime = new HashMap<>();
        endTime.put("dateTime", eventEndDateTime);
        endTime.put("timeZone", "Europe/Paris");

        Map<String, Object> location = new HashMap<>();
        location.put("displayName", eventLocation);

        Map<String, Object> jsonParams = new HashMap<>();
        jsonParams.put("subject", eventSubject);
        jsonParams.put("start", startTime);
        jsonParams.put("end", endTime);
        jsonParams.put("location", location);
        if (eventIsAllDay)
        {
            jsonParams.put("isAllDay", "true");
        }
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /* Successfully called graph, process data and send to UI */
                        Log.d(TAG, "Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String,String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("key", "value");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP POST to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /* Toast after graph response */
    private void toastSuccessGraph() {
        Toast.makeText(this, R.string.graphUpdate, Toast.LENGTH_SHORT).show();
    }

    /* Fetches all events from api response */
    private void getAllEvents(JSONObject graphResponse) throws JSONException {
        allEvents.clear();
        JSONArray array = graphResponse.getJSONArray("value");
        JSONObject object;
        for(int i=0; i<array.length(); i++) {
            object = array.getJSONObject(i);
            EventClass event = buildEvent(object);
            //IF event is a meeting, skip meetings already attended by other people..
            if (event.isMeeting)
                if (event.isAttending)
                    allEvents.add(event);
                if (event.getNumOfAcceptedAttendees()<1)
                    allEvents.add(event);
            else
                allEvents.add(event);
        }
    }

    /* Takes a JSONObject and constructs a EventClass object  */
    private EventClass buildEvent(JSONObject object) throws JSONException {
        // get event information
        String id = object.getString("id");
        String subject = object.getString("subject");
        String bodyPreview = object.getString("bodyPreview");
        String importance = object.getString("importance");

        // whole day activity
        Boolean isAllDay = Boolean.parseBoolean(object.getString("isAllDay"));

        // Check if event is a meeting (if event marked private it is a meeting)
        Boolean isMeeting=false;
        int numOfAcceptedAttendees=0;
        Boolean isAttending = false;

        if (object.getString("sensitivity").equals("private")) {
            isMeeting = true;
            JSONArray attendees = object.getJSONArray("attendees");

            for(int i=0; i<attendees.length(); i++) {
                String userId = authResult.getUser().getDisplayableId();
                if (attendees.getJSONObject(i).getJSONObject("emailAddress").getString("address").equals(userId)){
                    if(attendees.getJSONObject(i).getJSONObject("status").getString("response").equals("accepted"))
                        isAttending = true;
                    else if(attendees.getJSONObject(i).getJSONObject("status").getString("response").equals("tentativelyAccepted"))
                        isAttending = true;
                }
                if (attendees.getJSONObject(i).getJSONObject("status").getString("response").equals("tentativelyAccepted"))
                    numOfAcceptedAttendees++;
            }
        }

        //Get string and create local date time objects (start and end date+time)
        LocalDateTime startTimeObj = getDateTimeFromString(object.getString("start"));
        LocalDateTime endTimeObj = getDateTimeFromString(object.getString("end"));

        //Get event location
        EventClass.Location location;
        try {
            location = getLocationFromJson(object.getJSONObject("location"));
        } catch (JSONException e) {
            location = new EventClass.Location();
        }

        //Get event ResponseStatus
        EventClass.ResponseStatus responseStatus;
        try {
            responseStatus = getResponseStatusFromJson(object.getJSONObject("responseStatus"));
        } catch (JSONException e) {
            responseStatus = new EventClass.ResponseStatus();
        }

        return new EventClass(id, subject, bodyPreview, isAllDay, isMeeting,isAttending, startTimeObj, endTimeObj, location, responseStatus, numOfAcceptedAttendees, importance);
    }

    /* Takes a JSONObject and constucts a Location object */
    private EventClass.Location getLocationFromJson(JSONObject jsonObject) throws JSONException {
        EventClass.Location location = new EventClass.Location();
        JSONObject adress = jsonObject.getJSONObject("address");
        location.displayName = jsonObject.getString("displayName");
        location.street = adress.getString("street");
        location.city = adress.getString("city");
        location.state = adress.getString("state");
        location.country = adress.getString("countryOrRegion");
        location.postalCode = adress.getString("postalCode");
        return  location;
    }

    /* Takes a JSONObject and constucts a ResponseStatus object */
    private EventClass.ResponseStatus getResponseStatusFromJson(JSONObject jsonObject) throws JSONException {
        EventClass.ResponseStatus responseStatus = new EventClass.ResponseStatus();
        responseStatus.response = jsonObject.getString("response");
        responseStatus.time = jsonObject.getString("time");
        return  responseStatus;
    }

    /* Converts a String to a LocalDateTime Object */
    private LocalDateTime getDateTimeFromString(String string) {
        //Get startDate and startTime from JsonString
        String [] segments = string.split("\"");
        LocalDateTime ldt = LocalDateTime.parse(segments[3].substring(0, 16));
        // Add one extra hour to correct for daylight savings (!!)
        ldt.plusHours(1);
        return ldt;
    }

    /* Get the devices local time */
    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now().truncatedTo((ChronoUnit.MINUTES));
    }

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("allevents", allEvents);

        Fragment openFragment = new FragmentHome();
        openFragment.setArguments(bundle);
        pushFragment(openFragment);

        findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);

        menuVisible=true;
        invalidateOptionsMenu();
    }

    /* Update UI after Api response */
    public void updateUIOnResponse(String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("allevents", allEvents);
        Fragment newFragment = getNewFragment(fragment);

        newFragment.setArguments(bundle);
        pushFragment(newFragment);
    }

    /*Fetch new fragment of the same type, used for updating UI*/
    private Fragment getNewFragment(Fragment fragment) {

        if(fragment instanceof FragmentLogin)
            return new FragmentLogin();
        else if(fragment instanceof TabFragment)
            return new TabFragment();
        else if (fragment instanceof FragmentCalendar)
            return new FragmentCalendar();
        else if (fragment instanceof FragmentBooking)
            return new FragmentBooking();
        else if (fragment instanceof FragmentOpenMeeting)
            return new FragmentOpenMeeting();
        else if (fragment instanceof FragmentOpenEvent)
            return new FragmentOpenEvent();
        else if (fragment instanceof FragmentWeek)
            return new FragmentWeek();
        else if (fragment instanceof FragmentCreateEvent)
            return new FragmentCreateEvent();
        else
            return new FragmentHome();
    }

    /* Set the UI for signed out user */
    private void updateSignedOutUI() {

        Fragment openFragment = new FragmentLogin();
        getSupportFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).commit();

        findViewById(R.id.bottom_navigation).setVisibility(View.INVISIBLE);
        menuVisible=false;
        invalidateOptionsMenu();
    }

    //
    // App callbacks for MSAL
    // ======================
    // getActivity() - returns activity so we can acquireToken within a callback
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case
    //

    public Activity getActivity() {
        return this;
    }

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Function handling all onClick methods in mainActivity. */
    public void buttonClicked(View view)
    {
        //Intent intent;
        switch(view.getId())
        {
            case R.id.connectButton:
                onCallGraphClicked();
                break;
            case R.id.menu_button:
                onSignOutClicked();
                break;
            case R.id.button1:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.iris.se/")));
                break;
            case R.id.button2:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.arbetsformedlingen.se/For-arbetssokande/Lediga-jobb/Platsbanken/")));
                break;
            case R.id.button3:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/")));
                break;
            case R.id.button4:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.iris.se/")));
                break;
            default:
                break;
        }
    }

    /* Inflates the menu, this adds items to the action bar if it is present. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_button);
        if(menuVisible)
            item.setVisible(true);
        else
            item.setVisible(false);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.menu_button:
                pushFragment(new FragmentLogin());
                onSignOutClicked();
                break;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Bottom Navigation bar and fragments  */
    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            // Select first menu item by default and show Fragment accordingly.
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    /* Clears BackStack and Opens up a new fragment selected from the navigation menu. */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);
        Bundle bundle;

        FragmentManager fragmentManager = getSupportFragmentManager();
        // CLEAR back stack of fragments
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        switch (item.getItemId()) {
            case R.id.menu_home:
                // Action to perform when Home Menu item is selected.
                bundle = new Bundle();
                bundle.putParcelableArrayList("allevents", allEvents);

                Fragment fragmentHome = new FragmentHome();
                fragmentHome.setArguments(bundle);
                pushFragment(fragmentHome);
                break;
            case R.id.menu_calendar:
                // Action to perform when Calendar Menu item is selected.
                // Sends a Bundle of all events
                bundle = new Bundle();
                bundle.putParcelableArrayList("allevents", allEvents);

//                Fragment fragmentCalendar = new FragmentCalendar();
                Fragment fragmentCalendar = new TabFragment();
                fragmentCalendar.setArguments(bundle);
                pushFragment(fragmentCalendar);
                break;
            case R.id.menu_booking:
                // Action to perform when Booking Menu item is selected.
                bundle = new Bundle();
                bundle.putParcelableArrayList("allevents", allEvents);

                Fragment fragmentBooking = new FragmentBooking();
                fragmentBooking.setArguments(bundle);
                pushFragment(fragmentBooking);
                break;
        }

        setActionBarTitle(item.getTitle());

    }

    /**
     * Opens up fragment, used when updating the UI without user interaction.
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment, "current_fragment");
                ft.commit();
            }
        }
    }

    /* Updates Title text in top part of the screen */
    public void setActionBarTitle(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    /* Adds back button in actionbar */
    public void showBackButton() {
        ActionBar actionBar = getSupportActionBar();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.rootLayout);

        if (currentFragment instanceof FragmentOpenEvent || currentFragment instanceof FragmentOpenMeeting)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /* Tries to open up Google Maps at the adress of the supplied event. */
    public void showMap(EventClass event) {
        //GET SEARCH URI
        String location = event.location.getDisplayName()+" "+event.location.getCountry();
        Uri geoLocation = Uri.parse("geo:0,0?q="+location);

        Intent intent = new Intent(Intent.ACTION_VIEW, geoLocation);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

