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
 * Created by delaroy on 10/27/17.
 */

public class AlarmCursorAdapter extends CursorAdapter {

    private TextView mTitleText,mDescriptionText, mStartDateAndTimeText, mRepeatInfoText,mCategory_month;
    private ImageView mActiveImage , mThumbnailImage;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    public AlarmCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mTitleText = (TextView) view.findViewById(R.id.recycle_title);
        mDescriptionText=(TextView)view.findViewById(R.id.recycle_description);
        mStartDateAndTimeText = (TextView) view.findViewById(R.id.recycle_start_date_time);
        mCategory_month= (TextView) view.findViewById(R.id.recycle_category);
        mRepeatInfoText = (TextView) view.findViewById(R.id.recycle_repeat_info);
        mActiveImage = (ImageView) view.findViewById(R.id.active_image);
        mThumbnailImage = (ImageView) view.findViewById(R.id.thumbnail_image);

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
            String descriptn = description;
            mDescriptionText.setText(descriptn);

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
    public void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);
    }

    // Set date and time views
    public void setStartReminderDateTime(String start_datetime,String category_month) {
        mStartDateAndTimeText.setText(start_datetime);
        mCategory_month.setText(category_month);

    }


    // Set repeat views
    public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Repeat Off");
        }
    }

    // Set active image as on or off
    public void setActiveImage(String active){
        if(active.equals("true")){
            mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        }else if (active.equals("false")) {
            mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
        }

    }
}
