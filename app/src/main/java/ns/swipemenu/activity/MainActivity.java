package ns.swipemenu.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ns.swipemenu.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb:
                launchSecondActivity(R.layout.layout_top_to_bottom, v);
                break;
            case R.id.bt:
                launchSecondActivity(R.layout.layout_bottom_to_top, v);
                break;
            case R.id.lr:
                launchSecondActivity(R.layout.layout_left_to_right, v);
                break;
            case R.id.rl:
                launchSecondActivity(R.layout.layout_right_to_left, v);
                break;
        }
    }

    private void launchSecondActivity(int layout, View v) {
        Intent i = new Intent(this, SwipeExample.class);
        i.putExtra(SwipeExample.INTENT_TAG_LAYOUT, layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle b = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this,
                    v,
                    getString(R.string.transition_name)).toBundle();
            startActivity(i, b);
        } else {
            startActivity(i);
        }
    }
}
