package com.aricneto.twistytimer.fragment.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.aricneto.twistify.BuildConfig;
import com.aricneto.twistify.R;
import com.aricneto.twistify.databinding.ContentDonateBinding;
import com.aricneto.twistytimer.activity.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DonateDialog extends DialogFragment {

    TextView tier5;
    TextView tier4;
    TextView tier3;
    TextView tier2;
    TextView tier1;

    private MainActivity activity;
    private BillingProcessor bp;

    public static DonateDialog newInstance() {
        return new DonateDialog();
    }

    View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.tier5:
                activity.purchase("donation_tier5");
                break;
            case R.id.tier4:
                activity.purchase("donation_tier4");
                break;
            case R.id.tier3:
                activity.purchase("donation_tier3");
                break;
            case R.id.tier2:
                activity.purchase("donation_tier2");
                break;
            case R.id.tier1:
                activity.purchase("donation_tier1");
                break;
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ContentDonateBinding binding = ContentDonateBinding.inflate(inflater, container, false);
        tier5 = binding.tier5;
        tier4 = binding.tier4;
        tier3 = binding.tier3;
        tier2 = binding.tier2;
        tier1 = binding.tier1;

        activity = ((MainActivity) getActivity());
        bp = activity.getBp();

        tier5.setOnClickListener(clickListener);
        tier4.setOnClickListener(clickListener);
        tier3.setOnClickListener(clickListener);
        tier2.setOnClickListener(clickListener);
        tier1.setOnClickListener(clickListener);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> donationTiers = new ArrayList<>(
                Arrays.asList("donation_tier1", "donation_tier2", "donation_tier3", "donation_tier4", "donation_tier5"));

        List<SkuDetails> tiers = new ArrayList();
        bp.getPurchaseListingDetailsAsync(donationTiers, new BillingProcessor.ISkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@Nullable List<SkuDetails> products) {
                Log.d("DonateDialog","onSkuDetailsResponse: ");
                tiers.addAll(products);
            }

            @Override
            public void onSkuDetailsError(String error) {

            }
        });

        // If size < 5, the app was not able to fetch all value info
        if (tiers != null && tiers.size() == 5) {
            tier5.setText(tiers.get(4).currency + " " + String.valueOf(tiers.get(4).priceValue));
            tier4.setText(tiers.get(3).currency + " " + String.valueOf(tiers.get(3).priceValue));
            tier3.setText(tiers.get(2).currency + " " + String.valueOf(tiers.get(2).priceValue));
            tier2.setText(tiers.get(1).currency + " " + String.valueOf(tiers.get(1).priceValue));
            tier1.setText(tiers.get(0).currency + " " + String.valueOf(tiers.get(0).priceValue));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
