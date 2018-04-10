/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.mike4christ.medmanager.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

/**
 * {@link ContentProvider} for employees app.
 */
public class EmployeeProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = EmployeeProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the employees table */
    private static final int EMPLOYEES = 100;

    /** URI matcher code for the content URI for a single employee in the employees table */
    private static final int EMPLOYEE_ID = 101;

    private static final int SEARCH_SUGGEST = 102;

    private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;
    static {
        SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
        SEARCH_SUGGEST_PROJECTION_MAP.put(EmployeeContract.EmployeeEntry._ID, EmployeeContract.EmployeeEntry._ID);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, EmployeeContract.EmployeeEntry.COLUMN_FIRSTNAME + " AS "   + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2, EmployeeContract.EmployeeEntry.COLUMN_LASTNAME + " AS "    + SearchManager.SUGGEST_COLUMN_TEXT_2);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, EmployeeContract.EmployeeEntry._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final Uri SEARCH_SUGGEST_URI = Uri.parse("content://" + EmployeeContract.CONTENT_AUTHORITY + "/" + EmployeeContract.PATH_EMPLOYEES + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);


    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.employees/employees" will map to the
        // integer code {@link #EMPLOYEE}. This URI is used to provide access to MULTIPLE rows
        // of the employees table.
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, EmployeeContract.PATH_EMPLOYEES, EMPLOYEES);

        // The content URI of the form "content://com.example.android.employees/employees/#" will map to the
        // integer code {@link #EMPLOYEE_ID}. This URI is used to provide access to ONE single row
        // of the employees table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.employees/employees/3" matches, but
        // "content://com.example.android.employees/employees" (without a number at the end) doesn't match.
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, EmployeeContract.PATH_EMPLOYEES + "/#", EMPLOYEE_ID);

    }

    public EmployeeProvider(){
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

    }

    /** Database helper object */
    private EmployeeDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new EmployeeDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(EmployeeContract.EmployeeEntry.TABLE_NAME);

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCH_SUGGEST:
                selectionArgs = new String[] { "%" + selectionArgs[0] + "%", "%" + selectionArgs[0] + "%" };
                queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);

                cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case EMPLOYEES:
                // For the employeeS code, query the employees table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the employees table.
                cursor = database.query(EmployeeContract.EmployeeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EMPLOYEE_ID:
                // For the employee_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.employees/employees/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = EmployeeContract.EmployeeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the employees table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(EmployeeContract.EmployeeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return insertEmployee(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a employee into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertEmployee(Uri uri, ContentValues values) {
        // Check that the name is not null
        String firstname = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_FIRSTNAME);
        if (firstname == null) {
            throw new IllegalArgumentException("Employee requires a firstname");
        }

        String lastname = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_LASTNAME);
        if (lastname == null) {
            throw new IllegalArgumentException("Employee requires a lastname");
        }

        String title = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Employee requires a title");
        }


        String city = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_CITY);
        if (city == null) {
            throw new IllegalArgumentException("Employee requires a city");
        }

        String phone = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Employee requires a phone number");
        }

        String email = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("Employee requires an email");
        }
        // Check that the gender is valid
        Integer gender = values.getAsInteger(EmployeeContract.EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
        if (gender == null || !EmployeeContract.EmployeeEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("employee requires valid gender");
        }


        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new employee with the given values
        long id = database.insert(EmployeeContract.EmployeeEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the employee content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return updateEmployee(uri, contentValues, selection, selectionArgs);
            case EMPLOYEE_ID:
                // For the employee_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = EmployeeContract.EmployeeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateEmployee(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update employees in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more employees).
     * Return the number of rows that were successfully updated.
     */
    private int updateEmployee(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // check that the name value is not null.
        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_FIRSTNAME)) {
            String firstname = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_FIRSTNAME);
            if (firstname == null) {
                throw new IllegalArgumentException("Employee requires a firstname");
            }
        }

        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_LASTNAME)) {
            String lastname = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_LASTNAME);
            if (lastname == null) {
                throw new IllegalArgumentException("Employee requires a lastname");
            }
        }

        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_TITLE)) {
            String title = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Employee requires a title");
            }
        }

        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_DEPARTMENT)) {
            String department = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_DEPARTMENT);
            if (department == null) {
                throw new IllegalArgumentException("Employee requires a department");
            }
        }

        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_CITY)) {
            String city = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_CITY);
            if (city == null) {
                throw new IllegalArgumentException("Employee requires a city");
            }
        }

        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_PHONE)) {
            String phone = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Employee requires a phone");
            }
        }

        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_EMAIL)) {
            String email = values.getAsString(EmployeeContract.EmployeeEntry.COLUMN_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("Employee requires a email");
            }
        }

        // If the {@link employeeEntry#COLUMN_EMPLOYEE_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(EmployeeContract.EmployeeEntry.COLUMN_EMPLOYEE_GENDER)) {
            Integer gender = values.getAsInteger(EmployeeContract.EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
            if (gender == null || !EmployeeContract.EmployeeEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("employee requires valid gender");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(EmployeeContract.EmployeeEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(EmployeeContract.EmployeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EMPLOYEE_ID:
                // Delete a single row given by the ID in the URI
                selection = EmployeeContract.EmployeeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(EmployeeContract.EmployeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return EmployeeContract.EmployeeEntry.CONTENT_LIST_TYPE;
            case EMPLOYEE_ID:
                return EmployeeContract.EmployeeEntry.CONTENT_ITEM_TYPE;
            case SEARCH_SUGGEST:
                return null;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
