package com.vnspectre.todolist.data;

import android.provider.BaseColumns;

/**
 * Created by Spectre on 10/30/17.
 */

public class TaskContract  {

    public static final class TaskEntry implements BaseColumns {

        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority";

    }
}
