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

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mike4christ.medmanager.data.AlarmReminderContract;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alu Michael on 8/4/18.
 */

public class SearchableActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // implement Up Navigation with caret in front of App icon in the Action Bar
      // getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        checkIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        // update the activity launch intent
        setIntent(newIntent);
        // handle it
        checkIntent(newIntent);
    }

    private void checkIntent(Intent intent) {
        String query = "";
        String intentAction = intent.getAction();
        if (Intent.ACTION_SEARCH.equals(intentAction)) {
            query = intent.getStringExtra(SearchManager.QUERY);
        } else if (Intent.ACTION_VIEW.equals(intentAction)) {

            Uri details = intent.getData();
            Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
            startActivity(detailsIntent);

        }
        fillList(query);

    }

    private void fillList(String query) {

        String wildcardQuery = "%" + query + "%";

        Cursor cursor = getContentResolver().query(
                AlarmReminderContract.AlarmReminderEntry.CONTENT_URI,
                null,
                AlarmReminderContract.AlarmReminderEntry.KEY_TITLE + " LIKE ? OR " + AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION + " LIKE ?",
                new String[] { wildcardQuery, wildcardQuery },
                null);


        if(cursor.getCount() == 0){
            Toast.makeText(this, " NO SEARCH RESULT " , Toast.LENGTH_LONG).show();
            int timeout = 3000; // make the activity visible for 4 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    finish();
                    Intent intent = new Intent(SearchableActivity.this, SearchDisplayActivity.class);
                    startActivity(intent);
                }
            }, timeout);
        }

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[] { AlarmReminderContract.AlarmReminderEntry.KEY_TITLE, AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION },
                new int[] { android.R.id.text1, android.R.id.text2 },
                0);

        setListAdapter(adapter);
        cursor.close();

    }

    @Override
    protected void onListItemClick(ListView l, View view, int position, long id) {
        Intent intent = new Intent(SearchableActivity.this, AddReminderActivity.class);

        Uri details = Uri.withAppendedPath(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, "" + id);
        intent.setData(details);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
            Intent parentActivityIntent = new Intent(this, SearchDisplayActivity.class);
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}