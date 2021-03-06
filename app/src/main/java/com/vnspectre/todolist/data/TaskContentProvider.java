package com.vnspectre.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.vnspectre.todolist.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.vnspectre.todolist.data.TaskContract.TaskEntry.TABLE_NAME;

/**
 * Created by Spectre on 10/30/17.
 */

public class TaskContentProvider extends ContentProvider {

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);

        /*
+          All paths added to the UriMatcher have a corresponding int.
+          For each kind of uri you may want to access, add the corresponding match with addURI.
+          The two calls below add matches for the task directory and a single item by ID.
+         */
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_WITH_ID);

        return uriMatcher;
    }

    private TaskDbHelper mTaskDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get access to the task database (to read only for query).
        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        //identify the match for th tasks directory.
        int match = mUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {
            // Query for the tasks directory.
            case TASKS:
                retCursor = db.query(TABLE_NAME, projection, selection, selectionArgs,null, null, sortOrder);
                break;

            // Add a case to query for a single row of data by ID
            // Use selections and selectionArgs to filter for that ID
            case TASK_WITH_ID:
                // Get the id from the URI
                String id = uri.getPathSegments().get(1);

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                retCursor =  db.query(TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        // Get access to the task database (to write new data to).
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        // identify the match for the tasks directory
        int match = mUriMatcher.match(uri);
        Uri returnUri; // URI to be returned;

        switch (match) {
            case TASKS:
                // Inserting values to tasks table.
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            // Default case throws an UnsupportedOperationException.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return  returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item.
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        // Keep track of the number of deleted tasks
        int tasksDeleted;

        int match = mUriMatcher.match(uri);

        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path.
            case TASK_WITH_ID:
                // Get the task ID from the URI path.
                String id = uri.getPathSegments().get(1);

                // Use selections/selectionArgs to filter for this ID.
                tasksDeleted = db.delete(TABLE_NAME, "_ID = ?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
