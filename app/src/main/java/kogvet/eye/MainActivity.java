package kogvet.eye;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public void changeActivity(View view)
    {
        Intent intent;
        switch(view.getId())
        {
            case R.id.viewButton:
                intent = new Intent(getApplicationContext(), viewCalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.bookTimeButton:
                intent = new Intent(getApplicationContext(), bookTimeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
