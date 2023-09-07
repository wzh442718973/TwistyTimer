package com.aricneto.twistytimer.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.DialogPuzzleSpinnerBinding;
import com.aricneto.twistytimer.adapter.BottomSheetSpinnerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Implements a layout for easy creation of spinner-like bottom sheet dialogs (like the ones seen in
 * the puzzle selection screen).
 */

public class BottomSheetSpinnerDialog extends BottomSheetDialogFragment {

    AppCompatTextView titleTextView;
    ListView          listView;

    private Context mContext;

    private BottomSheetSpinnerAdapter mAdapter;
    private AdapterView.OnItemClickListener mClickListener;

    private String titleText;
    private int titleIcon;
    public static BottomSheetSpinnerDialog newInstance() {
        return new BottomSheetSpinnerDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // This makes the bottomSheet dialog start in the expanded state
        dialog.setOnShowListener(dia -> {
            BottomSheetDialog bottomDialog = (BottomSheetDialog) dia;
            FrameLayout bottomSheet =  bottomDialog .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogPuzzleSpinnerBinding binding = DialogPuzzleSpinnerBinding.inflate(inflater);

        mContext = getContext();
        titleTextView = binding.title;
        listView = binding.list;


        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mClickListener);

        titleTextView.setText(titleText);

        if (titleIcon != 0) {
            Drawable icon = VectorDrawableCompat.create(mContext.getResources(), titleIcon, null);
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }

        listView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            v.onTouchEvent(event);
            return true;
        });
    }

    public void setTitle(String title, @DrawableRes int iconRes) {
        titleText = title;
        titleIcon = iconRes;
    }

    public void setListAdapter(BottomSheetSpinnerAdapter adapter) {
        mAdapter = adapter;
    }

    public void setListClickListener(AdapterView.OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
