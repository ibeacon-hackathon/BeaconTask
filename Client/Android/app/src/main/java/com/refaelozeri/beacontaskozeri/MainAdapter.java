package com.beacontask.android;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Refael Ozeri on 19/12/2014.
 */
public class MainAdapter extends ArrayAdapter<Task> {

    private Activity mActivity;

    public MainAdapter(Activity activity, int textViewResourceId, List<Task> objects) {
        super(activity, textViewResourceId, objects);

        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TaskRow row = null;
        View rowView = convertView;
        if(rowView == null) {
            row = new TaskRow(mActivity);
        } else {
            row = (TaskRow) rowView;
        }

        Task task = getItem(position);
        row.setName(task.getName());
        row.setCredits(task.getCredit());
        row.setIsAssigned(task.isAssigned());
        return row;
    }

}