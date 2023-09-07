package com.aricneto.twistytimer.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.ItemAlgListBinding;
import com.aricneto.twistytimer.database.DatabaseHandler;
import com.aricneto.twistytimer.fragment.AlgListFragment;
import com.aricneto.twistytimer.fragment.dialog.AlgDialog;
import com.aricneto.twistytimer.layout.Cube;
import com.aricneto.twistytimer.listener.DialogListener;
import com.aricneto.twistytimer.utils.AlgUtils;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Ari on 05/06/2015.
 */

public class AlgCursorAdapter extends CursorRecyclerAdapter<RecyclerView.ViewHolder> implements DialogListener {
    private final Context mContext;  // Current context
    private final FragmentManager mFragmentManager;
    HashMap<Character, Integer> colorHash;

    // Locks opening new windows until the last one is dismissed
    private boolean isLocked;

    public AlgCursorAdapter(Context context, Cursor cursor, Fragment listFragment) {
        super(cursor);
        this.mContext = context;
        this.mFragmentManager = listFragment.getFragmentManager();
        colorHash = AlgUtils.getColorLetterHashMap();
    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        super.swapCursor(cursor);
        return cursor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        ItemAlgListBinding binding = ItemAlgListBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        viewHolder = new AlgHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolderCursor(final RecyclerView.ViewHolder viewHolder, final Cursor cursor) {
        AlgHolder holder = (AlgHolder) viewHolder;
        handleTime(holder, cursor);

    }

    @Override
    public void onUpdateDialog() {
        // Do nothing.
    }

    @Override
    public void onDismissDialog() {
        setIsLocked(false);
    }

    public void handleTime(final AlgHolder holder, final Cursor cursor) {
        final long mId = cursor.getLong(0); // id
        final String pName = cursor.getString(2);
        final String pSubset = cursor.getString(1);
        final String pState = AlgUtils.getCaseState(mContext, pSubset, pName);
        final int pProgress = cursor.getInt(5);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocked()) {
                    setIsLocked(true);
                    AlgDialog algDialog = AlgDialog.newInstance(mId);
                    algDialog.show(mFragmentManager, "alg_dialog");
                    algDialog.setDialogListener(AlgCursorAdapter.this);
                }

            }
        });

        holder.name.setText(pName);
        holder.progressBar.setProgress(pProgress);
        holder.cube.setCubeState(pState);

        // If the subset is PLL, it'll need to show the pll arrows.
        if (cursor.getString(1).equals("PLL")) {
            holder.pllArrows.setImageDrawable(AlgUtils.getPllArrow(mContext, pName));
            holder.pllArrows.setVisibility(View.VISIBLE);
        }

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    static class AlgHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView pllArrows;
        MaterialProgressBar progressBar;
        RelativeLayout root;
        CardView card;
        Cube cube;

        public AlgHolder(ItemAlgListBinding binding) {
            super(binding.getRoot());

            name = binding.name;
            pllArrows = binding.pllArrows;
            progressBar = binding.progressBar;
            root = binding.root;
            card = binding.card;
            cube = binding.cube;
        }
    }
}
