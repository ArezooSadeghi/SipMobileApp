package com.example.sipmobileapp.ui.activity;

import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.ui.fragment.AttachmentFragment;

public class AttachmentContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return AttachmentFragment.newInstance();
    }
}