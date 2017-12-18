package com.codertal.moviehub.data.movies.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favMoviesDb.db";
    private static final int VERSION = 3;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "  + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID                + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_YEAR + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DESCR + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID    + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
