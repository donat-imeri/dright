package com.dright;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mancj.slideup.SlideUp;

public class SlideUpActivity extends AppCompatActivity {

    private SlideUp slideUp;
    private View dim;
    private View slideView;
    private FloatingActionButton fab;
    private TextView txtSwipe;
    RelativeLayout swipelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_up);

        swipelayout = findViewById(R.id.swipe_layout);
        slideView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim);
        txtSwipe = findViewById(R.id.txt_swipeup);
        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();




        swipelayout.setOnTouchListener(new OnSwipeTouchListener(SlideUpActivity.this)
        {
            public void onSwipeTop()
            {
                swipelayout.setVisibility(View.INVISIBLE);
                slideUp.animateIn();

            }
        });

        slideUp.setSlideListener(new SlideUp.SlideListener() {

            @Override
            public void onSlideDown(float v)
            {

                dim.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int i) {
                if (i == View.GONE)
                {
                    swipelayout.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
