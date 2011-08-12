package edu.rosehulman.sqlhighscores;


public class SQLiteScoreAdapter {

    private static final String TAG = "SQLiteScoreAdapter"; 	// Just the tag we use to log
    
    private static final String DATABASE_NAME = "scores.db"; 	// Becomes the filename of the database
    private static final String TABLE_NAME = "scores"; 			// Only one table in this database
    private static final int DATABASE_VERSION = 1; 				// We increment this every time we change the database schema
                                                   				// which will kick off an automatic upgrade
    // TODO: Implement a SQLite database
}
