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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.mike4christ.medmanager.data.AlarmReminderContract;

/**
 * Created by Alu Michael on 9/4/18.
 */

class AlarmCursorAdapter extends CursorAdapter {

    private TextView mTitleText;
    private TextView mStartDateAndTimeText;
    private TextView mRepeatInfoText;
    private TextView mCategory_month;
    private ImageView mActiveImage , mThumbnailImage;
    private final ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

    public AlarmCursorAdapter(Context context) {
        super(context, null, 0 /* flags */);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mTitleText = view.findViewById(R.id.recycle_title);
        TextView mDescriptionText = view.findViewById(R.id.recycle_description);
        mStartDateAndTimeText = view.findViewById(R.id.recycle_start_date_time);
        mCategory_month= view.findViewById(R.id.recycle_category);
        mRepeatInfoText = view.findViewById(R.id.recycle_repeat_info);
        mActiveImage = view.findViewById(R.id.active_image);
        mThumbnailImage = view.findViewById(R.id.thumbnail_image);

        int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
        int descriptionColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION);
        int dateStartColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_START_DATE);
        int timeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TIME);
        int repeatColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT);
        int repeatNoColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO);
        int repeatTypeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE);
        int activeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE);

        String title = cursor.getString(titleColumnIndex);
        String description = cursor.getString(descriptionColumnIndex);
        String startdate = cursor.getString(dateStartColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatNo = cursor.getString(repeatNoColumnIndex);
        String repeatType = cursor.getString(repeatTypeColumnIndex);
        String active = cursor.getString(activeColumnIndex);




        setReminderTitle(title);

        if (startdate != null){
            String startdateTime = startdate + " " + time;
            if(startdate.contains("/1/")){
                String category_month="January";
                setStartReminderDateTime(startdateTime,category_month);
            }else if(startdate.contains("/2/")){
                String category_month="February";
                setStartReminderDateTime(startdateTime,category_month);
            }else if(startdate.contains("/3/")){
                String category_month="March";
                setStartReminderDateTime(startdateTime,category_month);
            }else if(startdate.contains("/4/")){
                String category_month="April";
                setStartReminderDateTime(startdateTime,category_month);
            }else if(startdate.contains("/5/")){
                String category_month="May";
                setStartReminderDateTime(startdateTime,category_month);

            }else if(startdate.contains("/6/")){
                String category_month="June";
                setStartReminderDateTime(startdateTime,category_month);

            }else if(startdate.contains("/7/")){
                String category_month="July";
                setStartReminderDateTime(startdateTime,category_month);

            }else if(startdate.contains("/8/")){
                String category_month="August";
                setStartReminderDateTime(startdateTime,category_month);

            }else if(startdate.contains("/9/")){
                String category_month="September";
                setStartReminderDateTime(startdateTime,category_month);

            }else if(startdate.contains("/10/")){
                String category_month="October";
                setStartReminderDateTime(startdateTime,category_month);

            }else if(startdate.contains("/11/")){
                String category_month="November";
                setStartReminderDateTime(startdateTime,category_month);


            }else if(startdate.contains("/12/")){
                String category_month="December";
                setStartReminderDateTime(startdateTime,category_month);
            }else{
                String category_month="None";
                setStartReminderDateTime(startdateTime,category_month);
            }

        }else{
            mStartDateAndTimeText.setText("Date not set");
            mCategory_month.setText("Category not set");
        }
        if (description != null){
            mDescriptionText.setText(description);

        }else{
            mStartDateAndTimeText.setText("No Description yet !");
        }


        if(repeat != null){
            setReminderRepeatInfo(repeat, repeatNo, repeatType);
        }else{
            mRepeatInfoText.setText("Repeat Not Set");
        }

        if (active != null){
            setActiveImage(active);
        }else{
            mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
        }






    }

    // Set reminder title view
    private void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        TextDrawable mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);
    }

    // Set date and time views
    private void setStartReminderDateTime(String start_datetime, String category_month) {
        mStartDateAndTimeText.setText(start_datetime);
        mCategory_month.setText(category_month);

    }


    // Set repeat views
    private void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Repeat Off");
        }
    }

    // Set active image as on or off
    private void setActiveImage(String active){
        if(active.equals("true")){
            mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        }else if (active.equals("false")) {
            mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
        }

    }
}
