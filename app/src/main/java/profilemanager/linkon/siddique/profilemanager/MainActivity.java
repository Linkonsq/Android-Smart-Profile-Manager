package profilemanager.linkon.siddique.profilemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, MyService.class);
    }

    public void onClickStart(View view) {
        startService(intent);
    }

    public void onClickStop(View view) {
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}