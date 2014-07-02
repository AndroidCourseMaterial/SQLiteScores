package edu.rosehulman.sqlhighscores;

public class ScoreDataAdapter {
	// Becomes the filename of the database
	private static final String DATABASE_NAME = "scores.db";
	// Only one table in this database
	private static final String TABLE_NAME = "scores";
	// We increment this every time we change the database schema which will
	// kick off an automatic upgrade
	private static final int DATABASE_VERSION = 1;
	// TODO: Implement a SQLite database

}
