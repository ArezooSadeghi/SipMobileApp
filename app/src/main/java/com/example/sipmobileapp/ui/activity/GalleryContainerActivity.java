package com.example.sipmobileapp.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.ui.fragment.GalleryFragment;

public class GalleryContainerActivity extends SingleFragmentActivity {

    private static final String EXTRA_PATIENT_ID = "patientID";

    @Override
    public Fragment createFragment() {
        int patientID = getIntent().getIntExtra(EXTRA_PATIENT_ID, 0);
        return GalleryFragment.newInstance(patientID);
    }

    public static Intent newIntent(Context context, int patientID) {
        Intent intent = new Intent(context, GalleryContainerActivity.class);
        intent.putExtra(EXTRA_PATIENT_ID, patientID);
        return intent;
    }
}