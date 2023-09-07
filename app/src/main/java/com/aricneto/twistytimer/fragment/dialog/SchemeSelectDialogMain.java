package com.aricneto.twistytimer.fragment.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.DialogSchemeSelectMainBinding;
import com.aricneto.twistytimer.TwistyTimer;
import com.aricneto.twistytimer.activity.MainActivity;
import com.aricneto.twistytimer.spans.ChromaDialogFixed;
import com.aricneto.twistytimer.utils.ThemeUtils;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;


/**
 * Created by Ari on 09/02/2016.
 */
public class SchemeSelectDialogMain extends DialogFragment {

    private Context mContext;

    View top;
    View left;
    View front;
    View right;
    View back;
    View down;
    TextView reset;
    TextView done;

    public static SchemeSelectDialogMain newInstance() {
        return new SchemeSelectDialogMain();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TwistyTimer.getAppContext());
            final SharedPreferences.Editor editor = sp.edit();
            String currentHex = "FFFFFF";
            switch (view.getId()) {
                case R.id.top:
                    currentHex = sp.getString("cubeTop", "FFFFFF");
                    break;
                case R.id.left:
                    currentHex = sp.getString("cubeLeft", "FF8B24");
                    break;
                case R.id.front:
                    currentHex = sp.getString("cubeFront", "02D040");
                    break;
                case R.id.right:
                    currentHex = sp.getString("cubeRight", "EC0000");
                    break;
                case R.id.back:
                    currentHex = sp.getString("cubeBack", "304FFE");
                    break;
                case R.id.down:
                    currentHex = sp.getString("cubeDown", "FDD835");
                    break;
            }

            new ChromaDialogFixed.Builder()
                    .initialColor(Color.parseColor("#" + currentHex))
                    .colorMode(ColorMode.RGB)
                    .indicatorMode(IndicatorMode.HEX)
                    .onColorSelected(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(@ColorInt int color) {
                            String hexColor = Integer.toHexString(color).toUpperCase().substring(2);
                            switch (view.getId()) {
                                case R.id.top:
                                    setColor(top, Color.parseColor("#" + hexColor));
                                    editor.putString("cubeTop", hexColor);
                                    break;
                                case R.id.left:
                                    setColor(left, Color.parseColor("#" + hexColor));
                                    editor.putString("cubeLeft", hexColor);
                                    break;
                                case R.id.front:
                                    setColor(front, Color.parseColor("#" + hexColor));
                                    editor.putString("cubeFront", hexColor);
                                    break;
                                case R.id.right:
                                    setColor(right, Color.parseColor("#" + hexColor));
                                    editor.putString("cubeRight", hexColor);
                                    break;
                                case R.id.back:
                                    setColor(back, Color.parseColor("#" + hexColor));
                                    editor.putString("cubeBack", hexColor);
                                    break;
                                case R.id.down:
                                    setColor(down, Color.parseColor("#" + hexColor));
                                    editor.putString("cubeDown", hexColor);
                                    break;
                            }
                            editor.apply();
                        }
                    })
                    .create()
                    .show(getFragmentManager(), "ChromaDialog");

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DialogSchemeSelectMainBinding binding = DialogSchemeSelectMainBinding.inflate(inflater, container, false);
        top = binding.top;
        left = binding.left;
        front = binding.front;
        right = binding.right;
        back = binding.back;
        down = binding.down;
        reset = binding.reset;
        done = binding.done;


        mContext = getContext();

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TwistyTimer.getAppContext());

        setColor(top, Color.parseColor("#" + sp.getString("cubeTop", "FFFFFF")));
        setColor(left, Color.parseColor("#" + sp.getString("cubeLeft", "FF8B24")));
        setColor(front, Color.parseColor("#" + sp.getString("cubeFront", "02D040")));
        setColor(right, Color.parseColor("#" + sp.getString("cubeRight", "EC0000")));
        setColor(back, Color.parseColor("#" + sp.getString("cubeBack", "304FFE")));
        setColor(down, Color.parseColor("#" + sp.getString("cubeDown", "FDD835")));

        top.setOnClickListener(clickListener);
        left.setOnClickListener(clickListener);
        front.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
        back.setOnClickListener(clickListener);
        down.setOnClickListener(clickListener);

        reset.setOnClickListener(view -> ThemeUtils.roundAndShowDialog(mContext, new MaterialDialog.Builder(mContext)
                .content(R.string.reset_colorscheme)
                .positiveText(R.string.action_reset_colorscheme)
                .negativeText(R.string.action_cancel)
                .onPositive((dialog, which) -> {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("cubeTop", "FFFFFF");
                    editor.putString("cubeLeft", "EF6C00");
                    editor.putString("cubeFront", "02D040");
                    editor.putString("cubeRight", "EC0000");
                    editor.putString("cubeBack", "304FFE");
                    editor.putString("cubeDown", "FDD835");
                    editor.apply();
                    setColor(top, Color.parseColor("#FFFFFF"));
                    setColor(left, Color.parseColor("#EF6C00"));
                    setColor(front, Color.parseColor("#02D040"));
                    setColor(right, Color.parseColor("#EC0000"));
                    setColor(back, Color.parseColor("#304FFE"));
                    setColor(down, Color.parseColor("#FDD835"));
                })
                .build()));

        done.setOnClickListener(view -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onRecreateRequired();
            }
            dismiss();
        });

        return binding.getRoot();
    }

    private void setColor(View view, int color) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.square);
        Drawable wrap = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrap, color);
        DrawableCompat.setTintMode(wrap, PorterDuff.Mode.MULTIPLY);
        wrap = wrap.mutate();
        view.setBackground(wrap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
