package com.example.mike4christ.medmanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike4christ.medmanager.data.EmployeeContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


/**
 * Created by delaroy on 8/14/17.
 */

public class EmployeeDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentEmployeeUri;


    private TextView mFirstName;
    private TextView mLastName;
    private TextView mTitle;
    private TextView mDepartment;
    private TextView mCity;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mGender;
    private ImageView imageView;

    FirebaseAuth mAuth;
    DatabaseReference mUserDatabse;
    StorageReference mStorageRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_details);

        Intent intent = getIntent();
        mCurrentEmployeeUri = intent.getData();

        // If the intent DOES NOT contain a employee content URI, then we know that we are
        //having no details
        if (mCurrentEmployeeUri == null) {

            Toast.makeText(this, " No Employee Data", Toast.LENGTH_SHORT).show();
        } else {

            // Initialize a loader to read the employee data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(0, null, this);
        }

        mFirstName = (TextView) findViewById(R.id.employee_firstname);
        mLastName = (TextView) findViewById(R.id.employee_lastname);
        mTitle = (TextView) findViewById(R.id.employee_title);
        mDepartment = (TextView) findViewById(R.id.employee_department);
        mCity = (TextView) findViewById(R.id.employee_city);
        mPhone = (TextView) findViewById(R.id.employee_phone);
        mEmail = (TextView) findViewById(R.id.employee_email);
        mGender = (TextView) findViewById(R.id.employee_gender);
        imageView = (ImageView) findViewById(R.id.profileImageView);


        if(mUserDatabse!=null){
            //FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().;

        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                EmployeeContract.EmployeeEntry._ID,
                EmployeeContract.EmployeeEntry.COLUMN_FIRSTNAME,
                EmployeeContract.EmployeeEntry.COLUMN_LASTNAME,
                EmployeeContract.EmployeeEntry.COLUMN_TITLE,
                EmployeeContract.EmployeeEntry.COLUMN_DEPARTMENT,
                EmployeeContract.EmployeeEntry.COLUMN_CITY,
                EmployeeContract.EmployeeEntry.COLUMN_PHONE,
                EmployeeContract.EmployeeEntry.COLUMN_IMAGE,
                EmployeeContract.EmployeeEntry.COLUMN_EMAIL,
                EmployeeContract.EmployeeEntry.COLUMN_EMPLOYEE_GENDER,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentEmployeeUri,         // Query the content URI for the current employee
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of employee attributes that we're interested in
            int firstnameColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_FIRSTNAME);
            int lastnameColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_LASTNAME);
            int titleColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_TITLE);
            int departmentColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_DEPARTMENT);
            int cityColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_CITY);
            int phoneColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_IMAGE);
            int emailColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_EMAIL);
            int genderColumnIndex = cursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_EMPLOYEE_GENDER);


            // Extract out the value from the Cursor for the given column index
            String firstName = cursor.getString(firstnameColumnIndex);
            String lastName = cursor.getString(lastnameColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String department = cursor.getString(departmentColumnIndex);
            String city = cursor.getString(cityColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);


            // Update the views on the screen with the values from the database
            setTitle(firstName);
            mFirstName.setText(firstName);
            mLastName.setText(lastName);
            mTitle.setText(title);
            mDepartment.setText(department);
            mCity.setText(city);
            mPhone.setText(phone);
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,
                    200, false));
            mEmail.setText(email);

           switch (gender) {
                case EmployeeContract.EmployeeEntry.GENDER_MALE:
                    mGender.setText("Male");
                    break;
                case EmployeeContract.EmployeeEntry.GENDER_FEMALE:
                    mGender.setText("Female");
                    break;
                default:
                    mGender.setText("Unknown");
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
