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
 * Created by delaroy on 8/23/17.
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
                    Intent intent = new Intent(SearchableActivity.this, EmployeeActivity.class);
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
    }

    @Override
    protected void onListItemClick(ListView l, View view, int position, long id) {
        Intent intent = new Intent(SearchableActivity.this, EmployeeDetails.class);

        Uri details = Uri.withAppendedPath(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, "" + id);
        intent.setData(details);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
            Intent parentActivityIntent = new Intent(this, EmployeeActivity.class);
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}