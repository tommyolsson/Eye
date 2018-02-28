package kogvet.eye;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class viewCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        context = this;

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        MyAdapter myAdapter = new MyAdapter(context);
        recyclerView.setAdapter(myAdapter);

        ArrayList<String> allEvents = getIntent().getStringArrayListExtra("allevents");
        Toast toast = Toast.makeText(this, allEvents.toString(), Toast.LENGTH_LONG);toast.show();

    }


}
