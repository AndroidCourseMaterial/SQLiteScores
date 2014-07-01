package edu.rosehulman.sqlhighscores;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScoreDataAdapter {
	// Just the tag we use to log
	private static final String TAG = "SQLiteScoreAdapter";
	// Becomes the filename of the database
	private static final String DATABASE_NAME = "scores.db";
	// Only one table in this database
	private static final String TABLE_NAME = "scores";
	// We increment this every time we change the database schema which will
	// kick off an automatic upgrade
	private static final int DATABASE_VERSION = 1;

	// TODO: Implement a SQLite database
	private SQLiteOpenHelper mOpenHelper;
	private SQLiteDatabase mDatabase;
	// Android naming convention for IDs
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_SCORE = "score";

	private static String DROP_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
	private static String CREATE_STATEMENT;
	static {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABLE_NAME + " (");
		sb.append(KEY_ID + " integer primary key autoincrement, ");
		sb.append(KEY_NAME + " text, ");
		sb.append(KEY_SCORE + " integer");
		sb.append(")");
		CREATE_STATEMENT = sb.toString();
	}

	public ScoreDataAdapter(Context context) {
		// Create a SQLiteOpenHelper
		mOpenHelper = new ScoreDbHelper(context);
	}

	public void open() {
		// Open the database
		mDatabase = mOpenHelper.getWritableDatabase();
	}

	public void close() {
		// Close the database
		mDatabase.close();
	}

	private ContentValues getConventValuesFromScore(Score score) {
		ContentValues row = new ContentValues();
		row.put(KEY_NAME, score.getName());
		row.put(KEY_SCORE, score.getScore());
		return row;
	}

	/**
	 * Add score to the table. If is successful, return the new id for that
	 * Score, otherwise return -1.
	 * 
	 * @param score
	 * @return id of the inserted row or -1 if failed
	 */
	public long addScore(Score score) {
		ContentValues row = getConventValuesFromScore(score);
		return mDatabase.insert(TABLE_NAME, null, row);
	}

	private static class ScoreDbHelper extends SQLiteOpenHelper {

		public ScoreDbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_STATEMENT);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Updating from version " + oldVersion + " to "
					+ newVersion + ", which will destroy old table(s).");
			db.execSQL(DROP_STATEMENT);
			onCreate(db);
		}

	}

}
