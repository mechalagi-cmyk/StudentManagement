package com.example.studentmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "StudentDB.db";
        private static final int DB_VERSION = 1;
        private static final String TABLE_NAME = "students";

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                    "student_id TEXT PRIMARY KEY, " +
                    "student_name TEXT NOT NULL, " +
                    "major TEXT NOT NULL)";

            db.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            // No database changes yet.
        }

        // CREATE: Add a new student
        public boolean addStudent(String id, String name, String major) {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("student_id", id);
            values.put("student_name", name);
            values.put("major", major);

            return db.insert(TABLE_NAME, null, values) != -1;
        }

        // UPDATE: Edit an existing student
        public boolean editStudent(String id, String name, String major) {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("student_name", name);
            values.put("major", major);

            return db.update(
                    TABLE_NAME,
                    values,
                    "student_id = ?",
                    new String[]{id}
            ) > 0;
        }

        // DELETE: Remove a student
        public boolean deleteStudent(String id) {
            SQLiteDatabase db = getWritableDatabase();

            return db.delete(
                    TABLE_NAME,
                    "student_id = ?",
                    new String[]{id}
            ) > 0;
        }

        // READ: Get all students
        public Cursor getAllStudents() {
            SQLiteDatabase db = getReadableDatabase();

            return db.rawQuery(
                    "SELECT student_id, student_name, major " +
                            "FROM " + TABLE_NAME +
                            " ORDER BY student_name ASC",
                    null
            );
        }
    }
