package com.vnspectre.todolist.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Spectre on 10/30/17.
 */

public class TaskContract  {

    // The authority, which is how your code knows which Content Provider to access.
    public static final String AUTHORITY = "com.vnspectre.todolist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // This is the path for the "tasks" directory.
    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";

    }
}
