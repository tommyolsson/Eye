package kogvet.eye;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.identity.client.*;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;

import kogvet.eye.BookingFragment.FragmentBooking;
import kogvet.eye.CalendarFragment.FragmentCalendar;
import kogvet.eye.CalendarFragment.FragmentOpenEvent;
import kogvet.eye.HomeFragment.FragmentHome;
import kogvet.eye.LoginFragment.FragmentLogin;


public class MainActivity extends AppCompatActivity {

    private ArrayList<EventClass> allEvents = new ArrayList<>();
    boolean menuVisible=true;

    /* Azure AD v2 Configs */
    final static String CLIENT_ID = "fac1a20e-54f5-49d2-ae55-724b980a2eb9";
    final static String SCOPES [] = {"User.Read", "Calendars.Read", "Calendars.Read.Shared", "Calendars.ReadWrite"};

    final static String MSGRAPH_URL = "https://graph.microsoft.com/beta/me/calendar/calendarView?startDateTime=2018-01-01T00:00:00.0000000&endDateTime=2025-01-01T00:00:00.0000000&$orderby=start/dateTime";
    //final static String MSGRAPH_URL = "https://graph.microsoft.com/beta/me/calendar/calendarView?startDateTime=2018-01-01T00:00:00.0000000&endDateTime=2025-01-01T00:00:00.0000000&$select=subject,isAllDay,start,end,location&$orderby=start/dateTime";

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
//                sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());

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

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());
                try {
                    getAllEvents(response);
                    updateSuccessUI();
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

    public void postGraphAPI(String url) {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("comment", "test");

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

    /* Sets the graph response */
    private void toastSuccessGraph() {
        Toast.makeText(this, R.string.graphUpdate, Toast.LENGTH_SHORT).show();
    }

    private void getAllEvents(JSONObject graphResponse) throws JSONException {

        allEvents.clear();
        JSONArray array = graphResponse.getJSONArray("value");
        JSONObject object;
        for(int i=0; i<array.length(); i++) {
            object = array.getJSONObject(i);
            EventClass event = buildEvent(object);
            allEvents.add(event);
        }
    }

    private EventClass buildEvent(JSONObject object) throws JSONException {
        // get event information
        String id = object.getString("id");
        String subject = object.getString("subject");
        String bodyPreview = object.getString("bodyPreview");

        // whole day activity
        Boolean isAllDay = Boolean.parseBoolean(object.getString("isAllDay"));

        // Check if event has a category (one or more category is a meeting)
        Boolean isMeeting=false;
        if(object.getJSONArray("categories").length() > 0)
            isMeeting = true;

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

        return new EventClass(id, subject, bodyPreview, isAllDay, isMeeting, startTimeObj, endTimeObj, location, responseStatus);
    }

    private EventClass.Location getLocationFromJson(JSONObject jsonObject) throws JSONException {
        EventClass.Location location = new EventClass.Location();

        location.displayName = jsonObject.getString("displayName");
        JSONObject adress = jsonObject.getJSONObject("address");
        location.street = adress.getString("street");
        location.city = adress.getString("city");
        location.state = adress.getString("state");
        location.country = adress.getString("countryOrRegion");
        location.postalCode = adress.getString("postalCode");

        return  location;
    }

    private EventClass.ResponseStatus getResponseStatusFromJson(JSONObject jsonObject) throws JSONException {
        EventClass.ResponseStatus responseStatus = new EventClass.ResponseStatus();

        responseStatus.response = jsonObject.getString("response");
        responseStatus.time = jsonObject.getString("time");

        return  responseStatus;
    }

    private LocalDateTime getDateTimeFromString(String string) {
        //Get startDate and startTime from JsonString
        String [] segments = string.split("\"");
        LocalDateTime ldt = LocalDateTime.parse(segments[3].substring(0, 16));
        // Add one extra hour to correct for timezone
        ldt.plusHours(1);
        return ldt;
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

//        getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).commit();
//        openFragment.onViewCreated(openFragment.getView(), openFragment.getArguments());

        findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);

        menuVisible=true;
        invalidateOptionsMenu();
    }

    /* Set the UI for signed out user */
    private void updateSignedOutUI() {

        Fragment openFragment = new FragmentLogin();
        getFragmentManager().beginTransaction().replace(R.id.rootLayout, openFragment).commit();

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
//                updateSuccessUI();
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
//                updateSuccessUI();
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

    /**
     * Function handling all onClick methods in mainActivity.
     * @param view
     */
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
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);
        Bundle bundle;

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

                Fragment fragmentCalendar = new FragmentCalendar();
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
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }

    /* Updates Title text in top bar */
    public void setActionBarTitle(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    /* Adds back button in actionbar */
    public void showBackButton() {
        ActionBar actionBar = getSupportActionBar();
        Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.rootLayout);

        if (currentFragment instanceof FragmentOpenEvent)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void bookMeeting(View view) {

       String id = "AQMkADAwATMwMAItMTA3NC1mMWY5LTAwAi0wMAoARgAAA5qOMtqUVcdGjwE9YSdMJv0HAI8BHxCuIO1Hp_cDF_CLsbMAAAIBDQAAAI8BHxCuIO1Hp_cDF_CLsbMAAAABrWg9AAAA";
       String url="https://graph.microsoft.com/beta/me/events/"+id+"/accept";

       //String url="https://graph.microsoft.com/beta/me/events/"+id+"/cancel";

       postGraphAPI(url);

    }

}

