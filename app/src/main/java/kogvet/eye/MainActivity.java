package kogvet.eye;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.microsoft.identity.client.*;

import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> allEvents = new ArrayList<>();
    boolean menuVisible=true;

    /* Azure AD v2 Configs */ //old id : 074d69f8-eed5-46ed-b577-13a834d0a716
    final static String CLIENT_ID = "7c1e027b-60d3-44ef-a3af-686d432785f0"; //Tool0035.student.umu.se ID: fac1a20e-54f5-49d2-ae55-724b980a2eb9
    final static String SCOPES [] = {"User.Read", "Calendars.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";
    final static String MSGRAPH_URL2 = "https://graph.microsoft.com/v1.0/me/calendar/events?$select=subject,start,end,location";
//    final static String MSGRAPH_URL2 = "https://graph.microsoft.com/v1.0/me/calendar";
    // RADEN OVAN ÄR TILLFÄLLIGT BYTT till MSGRAPH_URL2 för att testa calendar view. Glöm ej att byta tillbaka på Rad 171 tillbaka till MSGRAPH_URL

    /* UI & Debugging Variables */
    private static final String TAG = MainActivity.class.getSimpleName();
//    Button callGraphButton;
//    Button signOutButton;

    /* Azure AD Variables */
    private PublicClientApplication sampleApp;
    private AuthenticationResult authResult;

    //-------Methods-------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        callGraphButton = (Button) findViewById(R.id.connectButton);
//        signOutButton = (Button) findViewById(R.id.clearCache);


//        callGraphButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onCallGraphClicked();
//            }
//        });


//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onSignOutClicked();
//            }
//        });

        /* Configure your sample app and save state for this activity */
        sampleApp = null;
        if (sampleApp == null) {
            sampleApp = new PublicClientApplication(this.getApplicationContext(), CLIENT_ID);
        }

        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        List<User> users = null;

        try {
            users = sampleApp.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */

                sampleApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
            } else {
                /* We have no user */
                Log.d("debug", "no users");
                sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());

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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL2,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());
                updateGraphUI(response);
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

    //
    // Helper methods manage UI updates
    // ================================
    // updateGraphUI() - Sets graph response in UI
    // updateSuccessUI() - Updates UI when token acquisition succeeds
    // updateSignedOutUI() - Updates UI when app sign out succeeds
    //

    /* Sets the graph response */
    private void updateGraphUI(JSONObject graphResponse) {
        TextView graphText = (TextView) findViewById(R.id.graphData);
        String test= "";
        try {
            test = getAllEvents(graphResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        graphText.setText(test);

    }

    private String getFirstEvent(JSONObject graphResponse) throws JSONException {
        JSONArray array = graphResponse.getJSONArray("value");
        JSONObject event = null;
        for(int i=0; i<array.length(); i++) {
//            Log.d("Loldator", array.get(i).toString());
            event = array.getJSONObject(i);
//            Log.d("Loldator", event.getString("subject"));
//            Log.d("Loldator", event.getString("start"));
//            Log.d("Loldator", event.getString("end"));
//            Log.d("Loldator", event.getString("location"));
        }
        if (event != null)
            return event.getString("subject")+"\n"+event.getString("start")+"\n"+event.getString("location");
        else
            return "";
    }

    private String getAllEvents(JSONObject graphResponse) throws JSONException {
        JSONArray array = graphResponse.getJSONArray("value");
        JSONObject event = null;
        ArrayList<String> listOfEvents = new ArrayList<>();

        for(int i=0; i<array.length(); i++) {
            event = array.getJSONObject(i);
            Log.d("Loldator", event.getString("subject"));
            Log.d("Loldator", event.getString("start"));
            Log.d("Loldator", event.getString("end"));
            Log.d("Loldator", event.getString("location"));

            String eventInfo = (event.getString("subject") + " " + event.getString("start") + " " + event.getString("end") + " " + event.getString("location"));

            listOfEvents.add(eventInfo);
            allEvents.add(eventInfo);
        }
        if (event != null)
            return listOfEvents.toString();
        else
            return "";
    }

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {
        findViewById(R.id.imageView).setVisibility(View.INVISIBLE);
        findViewById(R.id.connectButton).setVisibility(View.INVISIBLE);
        menuVisible=true;
        invalidateOptionsMenu();
//        callGraphButton.setVisibility(View.INVISIBLE);
//        findViewById(R.id.clearCache).setVisibility(View.VISIBLE);
//        signOutButton.setVisibility(View.VISIBLE);
        findViewById(R.id.welcome).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.welcome)).setText("Welcome, " + authResult.getUser().getName());
        findViewById(R.id.graphData).setVisibility(View.VISIBLE);
    }

    /* Set the UI for signed out user */
    private void updateSignedOutUI() {
        findViewById(R.id.imageView).setVisibility(View.VISIBLE);
        findViewById(R.id.connectButton).setVisibility(View.VISIBLE);
//        callGraphButton.setVisibility(View.VISIBLE);
        menuVisible=false;
        invalidateOptionsMenu();
//        findViewById(R.id.clearCache).setVisibility(View.INVISIBLE);
//        signOutButton.setVisibility(View.INVISIBLE);
//        findViewById(R.id.action_settings).setVisibility(View.INVISIBLE);
        findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
        findViewById(R.id.graphData).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.graphData)).setText("No Data");
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

    /**
     * Function handling all onClick methods in mainActivity.
     * @param view
     */
    public void buttonClicked(View view)
    {
        Intent intent;
        switch(view.getId())
        {
            case R.id.viewButton:
                intent = new Intent(getApplicationContext(), viewCalendarActivity.class);

                //Sends ArrayList allEvents to viewCalendarActivity.java
                intent.putStringArrayListExtra("allevents", allEvents);

                startActivity(intent);
                break;
            case R.id.bookTimeButton:
                intent = new Intent(getApplicationContext(), bookTimeActivity.class);
                startActivity(intent);
                break;
            case R.id.connectButton:
                onCallGraphClicked();
                break;
            case R.id.clearCache:
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
        MenuItem item = menu.findItem(R.id.action_settings);
        if(menuVisible)
            item.setVisible(true);
        else
            item.setVisible(false);

//        Log.d("debug", "on create options menu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
//                Log.d("debug", "action settings");
                onSignOutClicked();
                break;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
