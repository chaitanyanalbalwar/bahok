package com.app.bahokrider.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.BuildConfig;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.app.bahokrider.activities.BankDetailsActivity;
import com.app.bahokrider.activities.NewRestaurantActivity;
import com.app.bahokrider.activities.ProfileActivity;
import com.app.bahokrider.R;
import com.app.bahokrider.activities.WebViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    LinearLayout llMyProfile;
    LinearLayout llNewRest;
    LinearLayout llMyBankDetails;

    LinearLayout llAboutUs;
    LinearLayout llPrivacyPolicy;
    LinearLayout llTermsConditions;
    LinearLayout llContactUs;
    LinearLayout llRateApp;
    LinearLayout llLogout;


    public MoreFragment() {
        // Required empty public constructor
    }

    public static MoreFragment newInstance(Bundle argument) {
        MoreFragment fragment = new MoreFragment();
        if (null != argument) {
            fragment.setArguments(argument);
        }
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initListener() {

        llMyProfile.setOnClickListener(this);
        llNewRest.setOnClickListener(this);
        llMyBankDetails.setOnClickListener(this);
        llAboutUs.setOnClickListener(this);
        llPrivacyPolicy.setOnClickListener(this);
        llTermsConditions.setOnClickListener(this);
        llContactUs.setOnClickListener(this);
        llRateApp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
    }

    private void initViews(View view) {

        llMyProfile = view.findViewById(R.id.llMyProfile);
        llNewRest = view.findViewById(R.id.llNewRest);
        llMyBankDetails = view.findViewById(R.id.llMyBankDetails);

        llAboutUs = view.findViewById(R.id.llAboutUs);
        llPrivacyPolicy = view.findViewById(R.id.llPrivacyPolicy);
        llTermsConditions = view.findViewById(R.id.llTermsConditions);
        llContactUs = view.findViewById(R.id.llContactUs);
        llRateApp = view.findViewById(R.id.llRateApp);
        llLogout = view.findViewById(R.id.llLogout);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llMyProfile:
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.llMyBankDetails:
                Intent intentBank = new Intent(getActivity(), BankDetailsActivity.class);
                startActivity(intentBank);
                break;

            case R.id.llNewRest:
                Intent intentRest = new Intent(getActivity(), NewRestaurantActivity.class);
                startActivity(intentRest);
                break;

            case R.id.llAboutUs:
                Intent intentAbout = new Intent(getActivity(), WebViewActivity.class);
                intentAbout.putExtra("type", "aboutus");
                intentAbout.putExtra("link", "https://foodbahokbd.com/pages/about");
                startActivity(intentAbout);
                break;

            case R.id.llContactUs:
                Intent intentCont = new Intent(getActivity(), WebViewActivity.class);
                intentCont.putExtra("type", "contact");
                intentCont.putExtra("link", "https://foodbahokbd.com/pages/contact");
                startActivity(intentCont);
                break;

            case R.id.llPrivacyPolicy:
                Intent intentPrivacy = new Intent(getActivity(), WebViewActivity.class);
                intentPrivacy.putExtra("type", "privacy");
                intentPrivacy.putExtra("link", "https://foodbahokbd.com/pages/privacy");
                startActivity(intentPrivacy);
                break;

            case R.id.llTermsConditions:
                Intent intentTerms = new Intent(getActivity(), WebViewActivity.class);
                intentTerms.putExtra("type", "terms");
                intentTerms.putExtra("link", "https://foodbahokbd.com/pages/terms_and_condition");
                startActivity(intentTerms);
                break;

            case R.id.llRateApp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                break;

            case R.id.llLogout:
                DialogFragment fragment = new LogoutDialogFragment();
                fragment.setCancelable(false);
                fragment.show(getChildFragmentManager(), "");
                break;

            default:
                break;

        }

    }
}
