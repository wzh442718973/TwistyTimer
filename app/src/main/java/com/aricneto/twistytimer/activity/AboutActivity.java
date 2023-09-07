package com.aricneto.twistytimer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.ActivityAboutBinding;
import com.aricneto.twistytimer.utils.LocaleUtils;
import com.aricneto.twistytimer.utils.StoreUtils;
import com.aricneto.twistytimer.utils.ThemeUtils;

import de.psdev.licensesdialog.LicensesDialog;

public class AboutActivity extends AppCompatActivity {

    private final static String APP_TITLE = "Twisty Timer";
    private final static String APP_PNAME = "com.aricneto.twistytimer";

    AppCompatTextView rateButton;
    AppCompatTextView feedbackButton;
    AppCompatTextView licenseButton;
    AppCompatTextView testersButton;
    AppCompatTextView sourceButton;
    TextView appVersion;
    AppCompatTextView translateButton;
    AppCompatTextView translatorsButton;
    AppCompatTextView contributorsButton;
    View backButton;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.feedbackButton:
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "aricnetodev@gmail.com"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_title)));
                    break;
                case R.id.licenseButton:
                    new LicensesDialog.Builder(AboutActivity.this)
                            .setNotices(R.raw.notices_app)
                            .build()
                            .show();
                    break;
                case R.id.rateButton:
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.testersButton:
                    ThemeUtils.roundAndShowDialog(AboutActivity.this, new MaterialDialog.Builder(AboutActivity.this)
                            .title(R.string.testers)
                            .content(R.string.testers_content)
                            .positiveText(R.string.action_ok)
                            .build());
                    break;
                case R.id.translatorsButton:
                    ThemeUtils.roundAndShowDialog(AboutActivity.this, new MaterialDialog.Builder(AboutActivity.this)
                            .title(R.string.translators)
                            .content(getString(R.string.translators_content, StoreUtils.getStringFromRaw(getResources(), R.raw.translators)))
                            .positiveText(R.string.action_ok)
                            .build());
                    break;
                case R.id.contributorsButton:
                    ThemeUtils.roundAndShowDialog(AboutActivity.this, new MaterialDialog.Builder(AboutActivity.this)
                            .title(R.string.contributors)
                            .content(getString(R.string.contributors_content, getString(R.string.contributors_names)))
                            .positiveText(R.string.action_ok)
                            .build());
                    break;
                case R.id.sourceButton:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/aricneto/TwistyTimer"));
                    startActivity(browserIntent);
                    break;
                case R.id.translateButton:
                    Intent translateBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://crwd.in/twisty-timer"));
                    startActivity(translateBrowserIntent);
                    break;
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.updateLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeUtils.getPreferredTheme());

        super.onCreate(savedInstanceState);

        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        rateButton = binding.rateButton;
        feedbackButton = binding.feedbackButton;
        licenseButton = binding.licenseButton;
        testersButton = binding.testersButton;
        sourceButton = binding.sourceButton;
        appVersion = binding.appVersion;
        translateButton = binding.translateButton;
        translatorsButton = binding.translatorsButton;
        contributorsButton = binding.contributorsButton;
        backButton = binding.back;


        backButton.setOnClickListener(v -> onBackPressed());

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            appVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        feedbackButton.setOnClickListener(clickListener);
        licenseButton.setOnClickListener(clickListener);
        rateButton.setOnClickListener(clickListener);
        testersButton.setOnClickListener(clickListener);
        sourceButton.setOnClickListener(clickListener);
        translatorsButton.setOnClickListener(clickListener);
        contributorsButton.setOnClickListener(clickListener);
        translateButton.setOnClickListener(clickListener);
    }
}
