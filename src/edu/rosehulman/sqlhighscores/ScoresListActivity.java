package edu.rosehulman.sqlhighscores;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity that displays a list view of Names & Scores
 * 
 * @author fisherds
 *
 */
public class ScoresListActivity extends Activity {

	/**
	 * Dialog for adding and editing scores
	 */
	private static final int DIALOG_ID = 1;

	/**
	 * The list view that fills the entire screen 
	 */
	private ListView mScoresListView;
	
	public static final long NEW_ENTRY = -1;
	private long mEditingScoreId = NEW_ENTRY;

	private ArrayList<Score> mScores = new ArrayList<Score>();
	private ArrayAdapter<Score> mScoreAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scores_list_activity);

		mScoresListView = (ListView) findViewById(R.id.scores_list_view);
		registerForContextMenu(mScoresListView);

		mScoreAdapter = new ArrayAdapter<Score>(this, android.R.layout.simple_list_item_1, mScores);
		mScoresListView.setAdapter(mScoreAdapter);
		mScoresListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchEditDialog(id);
			}});
	}

	protected void launchEditDialog(long id) {
		mEditingScoreId = id;
		showDialog(DIALOG_ID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.menu_add) {
			mEditingScoreId = NEW_ENTRY;
			showDialog(DIALOG_ID);
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflator = getMenuInflater();
		if(v == mScoresListView) {
			inflator.inflate(R.menu.scores_list_view_context_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case R.id.menu_item_list_view_delete:
			removeScore(info.id);
			return true;
		case R.id.menu_item_list_view_edit:
			launchEditDialog(info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void addScore(Score s) {
		mScores.add(s);
		Collections.sort(mScores);
		mScoreAdapter.notifyDataSetChanged();
	}

	private void removeScore(long id) {
		mScores.remove((int)id);
		Collections.sort(mScores);
		mScoreAdapter.notifyDataSetChanged();
	}

	private void editScore(Score s) {
		mScores.get((int) mEditingScoreId).setName(s.getName());
		mScores.get((int) mEditingScoreId).setScore(s.getScore());
		Collections.sort(mScores);
		mScoreAdapter.notifyDataSetChanged();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		final Dialog dialog = new Dialog(this);
		switch (id) {
		case DIALOG_ID:
			dialog.setContentView(R.layout.add_dialog);
			dialog.setTitle(R.string.add_score);

			final EditText nameText = (EditText) dialog.findViewById(R.id.name_entry);
			final EditText scoreText = (EditText) dialog.findViewById(R.id.score_entry);
			final Button confirmButton = (Button) dialog.findViewById(R.id.confirm_score_button);
			final Button cancelButton = (Button) dialog.findViewById(R.id.cancel_score_button);

			confirmButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Score s = new Score();
					s.setName(nameText.getText().toString());
					try {
						s.setScore(Integer.parseInt(scoreText.getText().toString()));
					} catch (NumberFormatException e) {
						s.setScore(0);
					}
					if (mEditingScoreId == NEW_ENTRY) {
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

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DIALOG_ID:

			final EditText nameText = (EditText) dialog.findViewById(R.id.name_entry);
			final EditText scoreText = (EditText) dialog.findViewById(R.id.score_entry);
			final Button confirmButton = (Button) dialog.findViewById(R.id.confirm_score_button);
			
			if (mEditingScoreId == NEW_ENTRY) {
				confirmButton.setText(R.string.add);
				nameText.setText("");
				scoreText.setText("");
			} else {
				confirmButton.setText(R.string.update);
				nameText.setText(mScores.get((int) mEditingScoreId).getName());
				scoreText.setText("" + mScores.get((int) mEditingScoreId).getScore());
			}
			break;

		}
	}
}