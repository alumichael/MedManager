/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mike4christ.medmanager;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mike4christ.medmanager.data.AlarmReminderContract;


/**
 * {@link EmployeeCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of employee data as its data source. This adapter knows
 * how to create list items for each row of employee data in the {@link Cursor}.
 */
class EmployeeCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link EmployeeCursorAdapter}.
     *  @param context The context
     *
     */
    public EmployeeCursorAdapter(Context context) {
        super(context, null, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the employee data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current employee can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView drugnameTextView = view.findViewById(R.id.drug_name);
        TextView descriptionTextView = view.findViewById(R.id.drug_description);
        TextView date = view.findViewById(R.id.date);

        // Find the columns of employee attributes that we're interested in
        int drugnameColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
        int descriptionColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION);
        int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_START_DATE);

        // Read the employee attributes from the Cursor for the current employee
        String drugFirstName = cursor.getString(drugnameColumnIndex);
        String drugeDescription = cursor.getString(descriptionColumnIndex);
        String drugDate = cursor.getString(dateColumnIndex);


        // If the employee drugname is empty string or null, then use some default text
        // that says "Unknown", so the TextView isn't blank.
        if (TextUtils.isEmpty(drugFirstName)) {
            drugeDescription = context.getString(R.string.unknown);
        }

        if (TextUtils.isEmpty(drugDate)) {
            drugDate = context.getString(R.string.unknown);
        }

        // Update the TextViews with the attributes for the current drug

        drugnameTextView.setText(drugFirstName);
        descriptionTextView.setText(drugeDescription);
        date.setText(drugDate);

    }
}
