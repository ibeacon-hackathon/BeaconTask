package com.beacontask.android;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TaskRow extends RelativeLayout {

    private TextView name;
    private TextView credits;
    private ImageView isAssigned;

    // Constructor to set the row
    public TaskRow(Activity activity) {
        super(activity);

        LayoutInflater layout = ((Activity)activity).getLayoutInflater();
        layout.inflate(R.layout.task_row, this);

        name = (TextView) findViewById(R.id.taskName);
        credits = (TextView) findViewById(R.id.taskCredits);
        isAssigned = (ImageView) findViewById(R.id.assigned);
    }

    // Methods to set the TextViews in the Task row
    public void setName(String title) {
        this.name.setText(title);
    }

    public void setCredits(int credits) {
        this.credits.setText(Integer.toString(credits));
    }
    
    public boolean setIsAssigned(boolean assigned) {
        if (assigned) {
            isAssigned.setVisibility(VISIBLE);
        } else {
            isAssigned.setVisibility(GONE);
        }
        return false;
    }

}
