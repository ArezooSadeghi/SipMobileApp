package com.example.sipmobileapp.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentSuccessAttachDialogBinding;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

public class SuccessAttachDialogFragment extends DialogFragment {
    private FragmentSuccessAttachDialogBinding binding;
    private AttachmentViewModel viewModel;

    public static final String TAG = SuccessAttachDialogFragment.class.getSimpleName();

    public static SuccessAttachDialogFragment newInstance() {
        SuccessAttachDialogFragment fragment = new SuccessAttachDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_success_attach_dialog,
                null,
                false);

        handleClicked();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void handleClicked() {
        binding.imgCloseWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getShowAttachAgainDialog().setValue(true);
                dismiss();
            }
        });
    }
}