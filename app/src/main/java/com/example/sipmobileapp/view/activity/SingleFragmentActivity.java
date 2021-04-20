package com.example.sipmobileapp.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.ActivityFragmentContainerBinding;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    private ActivityFragmentContainerBinding mBinding;

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_fragment_container);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, createFragment())
                    .commit();
        }
    }
}
