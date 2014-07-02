package edu.rosehulman.sqlhighscores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScoreDataAdapter {
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

	private ContentValues getContentValuesFromScore(Score score) {
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
		ContentValues row = getContentValuesFromScore(score);
		return mDatabase.insert(TABLE_NAME, null, row);
	}

	public Cursor getScoresCursor() {
		String[] projection = new String[] { KEY_ID, KEY_NAME, KEY_SCORE };
		return mDatabase.query(TABLE_NAME, projection, null, null, null, null,
				KEY_SCORE + " DESC");
	}

	public Score getScore(long id) {
		String[] projection = new String[] { KEY_ID, KEY_NAME, KEY_SCORE };
		String selection = KEY_ID + " = " + id;
		boolean distinctRows = true;
		Cursor c = mDatabase.query(distinctRows, TABLE_NAME, projection,
				selection, null, null, null, null, null);
		if (c != null && c.moveToFirst()) {
			return getScoreFromCursor(c);
		}
		return null;
	}

	private Score getScoreFromCursor(Cursor c) {
		Score s = new Score();
		s.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
		s.setName(c.getString(c.getColumnIndexOrThrow(KEY_NAME)));
		s.setScore(c.getInt(c.getColumnIndexOrThrow(KEY_SCORE)));
		return s;
	}

	public void updateScore(Score score) {
		ContentValues row = getContentValuesFromScore(score);
		String selection = KEY_ID + " = " + score.getId();
		mDatabase.update(TABLE_NAME, row, selection, null);
	}

	public boolean removeScore(long id) {
		return mDatabase.delete(TABLE_NAME, KEY_ID + " = " + id, null) > 0;
	}

	public boolean removeScore(Score s) {
		return removeScore(s.getId());
	}

	public void logAll() {
		Cursor c = getScoresCursor();
		if (c != null && c.moveToFirst()) {
			Log.d(ScoresListActivity.SLS, "LOGGING TABLE");
			while (!c.isAfterLast()) {
				Score score = getScoreFromCursor(c);
				Log.d(ScoresListActivity.SLS, score.toString());
				c.moveToNext();
			}
		}
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
			Log.d(ScoresListActivity.SLS, "Updating from version " + oldVersion + " to "
					+ newVersion + ", which will destroy old table(s).");
			db.execSQL(DROP_STATEMENT);
			onCreate(db);
		}

	}

}
