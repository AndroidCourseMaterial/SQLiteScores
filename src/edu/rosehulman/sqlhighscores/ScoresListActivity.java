package edu.rosehulman.sqlhighscores;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Activity that displays a list view of Names and Scores Currently using no
 * data storage Need to add SQLite for data storage
 * 
 * @author Dave Fisher
 * 
 */
public class ScoresListActivity extends ListActivity {
	// A ListActivity is an Activity that supports a ListView. Its layout must
	// include a ListView (and optionally TextView for when the list is empty) with specific ids: see that file.

	/**
	 * TAG for debug log messages
	 */
	public static final String SLS = "SLS";

	/**
	 * Dialog ID for adding and editing scores (one dialog for both tasks)
	 */
	private static final int DIALOG_ID = 1;

	/**
	 * Constant to indicate that no row is selected for editing Used when adding
	 * a new score entry
	 */
	public static final long NO_ID_SELECTED = -1;

	/**
	 * Index of the score / row selected
	 */
	private long mSelectedId = NO_ID_SELECTED;

	/**
	 * Array holding the scores
	 */
	private ArrayList<Score> mScores = new ArrayList<Score>();

	/**
	 * Adapter to fill the List View with mScores data Note: Could use the
	 * ListActivity function getListAdapter, but that results in a lot of ugly
	 * casting
	 */
	private ArrayAdapter<Score> mScoreAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scores_list_activity);
		
		mScoreAdapter = new ArrayAdapter<Score>(this, android.R.layout.simple_list_item_1, mScores);

		// This is how a ListActivity sets the adapter, similar to how a ListView sets it.
		setListAdapter(mScoreAdapter); 
		
		registerForContextMenu(getListView());
		
	}

	/**
	 * ListActivity sets up the onItemClick listener for the list view
	 * automatically via this function
	 */
	@Override
	protected void onListItemClick(ListView listView, View selectedView,
			int position, long id) {
		super.onListItemClick(listView, selectedView, position, id);
		mSelectedId = id;
		showDialog(DIALOG_ID);
	}

	/**
	 * Standard menu. Only has one item CONSIDER: Could add an edit and/or
	 * remove option when an item is selected
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Standard listener for the option menu item selections
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			mSelectedId = NO_ID_SELECTED;
			showDialog(DIALOG_ID);
			return true;
		default:
			return false;
		}
	}

	/**
	 * Create a context menu for the list view Secretly surprised ListActivity
	 * doesn't provide a special magic feature here too. :)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflator = getMenuInflater();
		if (v == getListView()) {
			inflator.inflate(R.menu.scores_list_view_context_menu, menu);
		}
	}

	/**
	 * Standard listener for the context menu item selections
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_item_list_view_delete:
			removeScore(info.id);
			return true;
		case R.id.menu_item_list_view_edit:
			mSelectedId = info.id;
			showDialog(DIALOG_ID);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Called when the activity is removed from memory (placeholder for later)
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// ======================================================================
	// Data CRUD mechanisms (Create, read, update, and delete)
	// ======================================================================

	/**
	 * Create: Add a new score to the data storage mechanism
	 * 
	 * @param s
	 *            New score to add
	 */
	private void addScore(Score s) {
		mScores.add(s);
		Collections.sort(mScores);
		mScoreAdapter.notifyDataSetChanged();
	}

	/**
	 * Read: Get a score for the data storage mechanism
	 * 
	 * @param id
	 *            Index of the score in the data storage mechanism
	 */
	private Score getScore(long id) {
		return mScores.get((int) id);
	}

	/**
	 * Update: Edit a score in the data storage mechanism Uses the values in the
	 * pass Score to updates the score at the mSelectedId location
	 * 
	 * @param s
	 *            Container for the new values to use in the update
	 */
	private void editScore(Score s) {
		if (mSelectedId == NO_ID_SELECTED) {
			Log.e(SLS, "Attempt to update with no score selected.");
		}
		Score selectedScore = getScore(mSelectedId);
		selectedScore.setName(s.getName());
		selectedScore.setScore(s.getScore());
		Collections.sort(mScores);
		mScoreAdapter.notifyDataSetChanged();
	}

	/**
	 * Delete: Remove a score from the data storage mechanism
	 * 
	 * @param id
	 *            Index of the score in the data storage mechanism
	 */
	private void removeScore(long id) {
		mScores.remove((int) id);
		Collections.sort(mScores);
		mScoreAdapter.notifyDataSetChanged();
	}

	// ======================================================================
	// Dialog for adding and updating Scores
	// ======================================================================

	/**
	 * Create the dialog if it has never been launched Uses a custom dialog
	 * layout
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		final Dialog dialog = new Dialog(this);
		switch (id) {
		case DIALOG_ID:
			dialog.setContentView(R.layout.score_dialog);
			dialog.setTitle(R.string.add_score);
			final EditText nameText = (EditText) dialog
					.findViewById(R.id.name_entry);
			final EditText scoreText = (EditText) dialog
					.findViewById(R.id.score_entry);
			final Button confirmButton = (Button) dialog
					.findViewById(R.id.confirm_score_button);
			final Button cancelButton = (Button) dialog
					.findViewById(R.id.cancel_score_button);

			confirmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Score s = new Score(); // Create an object to hold the
											// values
					s.setName(nameText.getText().toString());
					try {
						s.setScore(Integer.parseInt(scoreText.getText()
								.toString()));
					} catch (NumberFormatException e) {
						s.setScore(0);
					}
					if (mSelectedId == NO_ID_SELECTED) {
						addScore(s);
					} else {
						editScore(s);
					}
					dialog.dismiss();
				}
			});

			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		default:
			break;
		}
		return dialog;
	}

	/**
	 * Update the dialog with appropriate text before presenting to the user
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DIALOG_ID:
			final EditText nameText = (EditText) dialog
					.findViewById(R.id.name_entry);
			final EditText scoreText = (EditText) dialog
					.findViewById(R.id.score_entry);
			final Button confirmButton = (Button) dialog
					.findViewById(R.id.confirm_score_button);
			if (mSelectedId == NO_ID_SELECTED) {
				dialog.setTitle(R.string.add_score);
				confirmButton.setText(R.string.add);
				nameText.setText("");
				scoreText.setText("");
			} else {
				dialog.setTitle(R.string.update_score);
				confirmButton.setText(R.string.update);
				Score selectedScore = getScore(mSelectedId);
				nameText.setText(selectedScore.getName());
				scoreText.setText("" + selectedScore.getScore());
			}
			break;
		}
	}
}