package com.dright;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
        setContentView(R.layout.fragment_dilema_options);


        dim = findViewById(R.id.frag_dil_options);
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

class CommentsModel {

    public String name;
    public String hash;
    public String image;
    public String comment;


    public CommentsModel(String name, String image , String comment, String hash) {
        this.name = name;
        this.image = image;
        this.comment = comment;
        this.hash = hash;

    }

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public String getImage() {
        return image;
    }

    public String getComment() {
        return comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}


