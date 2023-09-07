package com.aricneto.twistytimer.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.FragmentAlgListBinding;
import com.aricneto.twistytimer.activity.MainActivity;
import com.aricneto.twistytimer.adapter.AlgCursorAdapter;
import com.aricneto.twistytimer.database.AlgTaskLoader;
import com.aricneto.twistytimer.utils.TTIntent.TTFragmentBroadcastReceiver;
import com.aricneto.twistytimer.utils.ThemeUtils;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static com.aricneto.twistytimer.utils.TTIntent.ACTION_ALGS_MODIFIED;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_CHANGED_CATEGORY;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_CHANGED_THEME;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_SELECTION_MODE_OFF;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_SELECTION_MODE_ON;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_TIMER_STARTED;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_TIMER_STOPPED;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_TIME_SELECTED;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_TIME_UNSELECTED;
import static com.aricneto.twistytimer.utils.TTIntent.ACTION_TOOLBAR_RESTORED;
import static com.aricneto.twistytimer.utils.TTIntent.CATEGORY_ALG_DATA_CHANGES;
import static com.aricneto.twistytimer.utils.TTIntent.CATEGORY_UI_INTERACTIONS;
import static com.aricneto.twistytimer.utils.TTIntent.broadcast;
import static com.aricneto.twistytimer.utils.TTIntent.registerReceiver;
import static com.aricneto.twistytimer.utils.TTIntent.unregisterReceiver;

public class AlgListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String KEY_SUBSET = "subset";

    LinearLayout rootLayout;

    View spinnerIcon;

    TextView titleView;

    TextView subtitleView;

    View button1;

    View button2;

    View buttonSettings;

    RecyclerView recyclerView;

    private String currentSubset;
    private AlgCursorAdapter algCursorAdapter;
    // Receives broadcasts about changes to the algorithm data.
    private TTFragmentBroadcastReceiver mAlgDataChangedReceiver
            = new TTFragmentBroadcastReceiver(this, CATEGORY_ALG_DATA_CHANGES) {
        @Override
        public void onReceiveWhileAdded(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_ALGS_MODIFIED:
                    reloadList();
                    break;
            }
        }
    };

    // Receives broadcasts about changes to the time user interface.
    private TTFragmentBroadcastReceiver mUIInteractionReceiver
            = new TTFragmentBroadcastReceiver(this, CATEGORY_UI_INTERACTIONS) {
        @Override
        public void onReceiveWhileAdded(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_CHANGED_THEME:
                    try {
                        // If the theme has been changed, then the activity will need to be recreated. The
                        // theme can only be applied properly during the inflation of the layouts, so it has
                        // to go back to "Activity.updateLocale()" to do that.
                        ((MainActivity) getActivity()).onRecreateRequired();
                    } catch (Exception e) {}
                    break;
            }
        }
    };


    public AlgListFragment() {
        // Required empty public constructor
    }

    public static AlgListFragment newInstance(String subset) {
        AlgListFragment fragment = new AlgListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SUBSET, subset);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentSubset = getArguments().getString(KEY_SUBSET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAlgListBinding binding = FragmentAlgListBinding.inflate(inflater, container, false);

        rootLayout = binding.root;
        spinnerIcon = binding.mainActionbar.spinnerIcon;
        titleView = binding.mainActionbar.puzzleName;
        subtitleView = binding.mainActionbar.puzzleCategory;
        button1 = binding.mainActionbar.navButtonHistory;
        button2 = binding.mainActionbar.navButtonCategory;
        buttonSettings = binding.mainActionbar.navButtonSettings;
        recyclerView = binding.list;



        rootLayout.setBackground(ThemeUtils.fetchBackgroundGradient(getContext(), ThemeUtils.getPreferredTheme()));

        titleView.setText(R.string.title_algorithms);
        subtitleView.setText(currentSubset);

        spinnerIcon.setVisibility(View.GONE);
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().openDrawer();
            }
        });

        setupRecyclerView();

        getLoaderManager().initLoader(MainActivity.ALG_LIST_LOADER_ID, null, this);

        // Register a receiver to update if something has changed
        registerReceiver(mAlgDataChangedReceiver);
        registerReceiver(mUIInteractionReceiver);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // To fix memory leaks
        unregisterReceiver(mAlgDataChangedReceiver);
        unregisterReceiver(mUIInteractionReceiver);
        getLoaderManager().destroyLoader(MainActivity.ALG_LIST_LOADER_ID);
    }

    public void reloadList() {
        getLoaderManager().restartLoader(MainActivity.ALG_LIST_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new AlgTaskLoader(currentSubset);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        algCursorAdapter.swapCursor(cursor);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        algCursorAdapter.swapCursor(null);
    }

    private void setupRecyclerView() {
        Activity parentActivity = getActivity();

        algCursorAdapter = new AlgCursorAdapter(getActivity(), null, this);

        // Set different managers to support different orientations
        StaggeredGridLayoutManager gridLayoutManagerHorizontal =
                new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        StaggeredGridLayoutManager gridLayoutManagerVertical =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        // Adapt to orientation
        if (parentActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.setLayoutManager(gridLayoutManagerVertical);
        else
            recyclerView.setLayoutManager(gridLayoutManagerHorizontal);

        recyclerView.setAdapter(algCursorAdapter);
    }
}
