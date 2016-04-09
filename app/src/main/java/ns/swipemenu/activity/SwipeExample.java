package ns.swipemenu.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ns.swipemenu.R;
import view.SwipeMenu;

/**
 * Created by Nicolas on 09/04/2016.
 * (c) Touchnote Ltd., 2015
 */
public class SwipeExample extends AppCompatActivity {
    public static final String                      INTENT_TAG_LAYOUT = "Layout";

    private static final float                      ALPHA_MIN = 0.5f;
    private static final float                      ALPHA_MAX = 1.0f;

    private TextView                                mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int layoutId = intent.getIntExtra(INTENT_TAG_LAYOUT, R.layout.activity_main);
        setContentView(layoutId);

        mTextView = (TextView) findViewById(R.id.textField);

        SwipeMenu revealLayout = (SwipeMenu) findViewById(R.id.revealLayout);
        if (revealLayout != null) {
            revealLayout.setCallback(new SwipeMenu.DropDownInterface() {
                @Override
                public void onUpdate(float percentage) {
                    updateTextColor(percentage);
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateTextColor(float percentage) {
        float targetAlpha = ((ALPHA_MAX - ALPHA_MIN) * percentage) + ALPHA_MIN;
        mTextView.setAlpha(targetAlpha);
    }
}
