package com.aricneto.twistytimer.fragment.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.DialogAlgDetailsBinding;
import com.aricneto.twistytimer.TwistyTimer;
import com.aricneto.twistytimer.database.DatabaseHandler;
import com.aricneto.twistytimer.items.Algorithm;
import com.aricneto.twistytimer.layout.Cube;
import com.aricneto.twistytimer.listener.DialogListener;
import com.aricneto.twistytimer.utils.AlgUtils;
import com.aricneto.twistytimer.utils.TTIntent;
import com.aricneto.twistytimer.utils.ThemeUtils;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Shows the algList dialog
 */
public class AlgDialog extends DialogFragment {

    private Context mContext;

    ImageView editButton;
    ImageView progressButton;
    MaterialProgressBar progressBar;
    TextView algText;
    TextView nameText;
    ImageView revertButton;
    ImageView pllArrows;
    Cube cube;

    private long mId;
    private Algorithm algorithm;
    private DialogListener dialogListener;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final DatabaseHandler dbHandler = TwistyTimer.getDBHandler();

            switch (view.getId()) {
                case R.id.editButton:
                    MaterialDialog dialog = ThemeUtils.roundDialog(mContext, new MaterialDialog.Builder(mContext)
                            .title(R.string.edit_algorithm)
                            .input("", algorithm.getAlgs(), (dialog1, input) -> {
                                algorithm.setAlgs(input.toString());
                                dbHandler.updateAlgorithmAlg(mId, input.toString());
                                algText.setText(input.toString());
                                updateList();
                            })
                            .inputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                            .positiveText(R.string.action_done)
                            .negativeText(R.string.action_cancel)
                            .build());
                    EditText editText = dialog.getInputEditText();
                    if (editText != null) {
                        editText.setSingleLine(false);
                        editText.setLines(5);
                        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    }
                    dialog.show();
                    break;

                case R.id.progressButton:
                    final AppCompatSeekBar seekBar = (AppCompatSeekBar) LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
                    seekBar.setProgress(algorithm.getProgress());
                    ThemeUtils.roundAndShowDialog(mContext, new MaterialDialog.Builder(mContext)
                            .title(R.string.dialog_set_progress)
                            .customView(seekBar, false)
                            .positiveText(R.string.action_update)
                            .negativeText(R.string.action_cancel)
                            .onPositive((dialog12, which) -> {
                                int seekProgress = seekBar.getProgress();
                                algorithm.setProgress(seekProgress);
                                dbHandler.updateAlgorithmProgress(mId, seekProgress);
                                progressBar.setProgress(seekProgress);
                                updateList();
                            })
                            .build());
                    break;

                case R.id.revertButton:
                    ThemeUtils.roundAndShowDialog(mContext, new MaterialDialog.Builder(mContext)
                            .title(R.string.dialog_revert_title_confirmation)
                            .content(R.string.dialog_revert_content_confirmation)
                            .positiveText(R.string.action_reset)
                            .negativeText(R.string.action_cancel)
                            .onPositive((dialog13, which) -> {
                                algorithm.setAlgs(AlgUtils.getDefaultAlgs(algorithm.getSubset(), algorithm.getName()));
                                dbHandler.updateAlgorithmAlg(mId, algorithm.getAlgs());
                                algText.setText(algorithm.getAlgs());
                            })
                            .build());
                    break;
            }
        }
    };

    public static AlgDialog newInstance(long id) {
        AlgDialog timeDialog = new AlgDialog();
        Bundle args = new Bundle();
        args.putLong("id", id);
        timeDialog.setArguments(args);
        return timeDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DialogAlgDetailsBinding binding = DialogAlgDetailsBinding.inflate(inflater);

        editButton = binding.editButton;
        progressButton = binding.progressButton;
        progressBar = binding.progressBar;
        algText = binding.algText;
        nameText = binding.nameText;
        revertButton = binding.revertButton;
        pllArrows = binding.pllArrows;
        cube = binding.cube;

        mContext = getContext();
        mId = getArguments().getLong("id");

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        final Algorithm matchedAlgorithm = TwistyTimer.getDBHandler().getAlgorithm(mId);

        if (matchedAlgorithm != null) {
            algorithm = matchedAlgorithm;
            algText.setText(algorithm.getAlgs());
            nameText.setText(algorithm.getName());

            cube.setCubeState(AlgUtils.getCaseState(getContext(), algorithm.getSubset(), algorithm.getName()));

            progressBar.setProgress(algorithm.getProgress());

            revertButton.setOnClickListener(clickListener);
            progressButton.setOnClickListener(clickListener);
            editButton.setOnClickListener(clickListener);

            // If the subset is PLL, it'll need to show the pll arrows.
            if (algorithm.getSubset().equals("PLL")) {
                pllArrows.setImageDrawable(AlgUtils.getPllArrow(getContext(), algorithm.getName()));
                pllArrows.setVisibility(View.VISIBLE);
            }

        }

        return binding.getRoot();
    }

    public void setDialogListener(DialogListener listener) {
        dialogListener = listener;
    }

    private void updateList() {
        TTIntent.broadcast(TTIntent.CATEGORY_ALG_DATA_CHANGES, TTIntent.ACTION_ALGS_MODIFIED);
        //dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dialogListener != null)
            dialogListener.onDismissDialog();
    }
}
