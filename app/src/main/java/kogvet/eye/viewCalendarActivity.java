package kogvet.eye;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class viewCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Context context;
    private BottomNavigationView mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);
        ArrayList<Event> allEvents= getIntent().getParcelableArrayListExtra("allevents");

        context = this;

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        MyAdapter myAdapter = new MyAdapter(context, allEvents);
        recyclerView.setAdapter(myAdapter);

        // Bottom navigation bar
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // handle desired action here
                // One possibility of action is to replace the contents above the nav bar
                // return true if you want the item to be displayed as the selected item
                Intent intent;
                switch (item.getItemId()) {

                    case R.id.menu_home:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.menu_calendar:
                        intent = new Intent(getApplicationContext(), viewCalendarActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.menu_booking:
                        intent = new Intent(getApplicationContext(), bookTimeActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }


}
