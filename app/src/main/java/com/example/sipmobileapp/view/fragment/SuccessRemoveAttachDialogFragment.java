package com.example.sipmobileapp.view.fragment;

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
import com.example.sipmobileapp.databinding.FragmentSuccessRemoveAttachDialogBinding;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;


public class SuccessRemoveAttachDialogFragment extends DialogFragment {
    private FragmentSuccessRemoveAttachDialogBinding binding;
    private AttachmentViewModel viewModel;

    private static final String ARGS_ATTACH_ID = "attachID";

    public static final String TAG = SuccessRemoveAttachDialogFragment.class.getSimpleName();

    public static SuccessRemoveAttachDialogFragment newInstance(int attachID) {
        SuccessRemoveAttachDialogFragment fragment = new SuccessRemoveAttachDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_ATTACH_ID, attachID);
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
                R.layout.fragment_success_remove_attach_dialog,
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
                int attachID = getArguments().getInt(ARGS_ATTACH_ID);
                viewModel.getDeleteImageFromGallery().setValue(attachID);
                dismiss();
            }
        });
    }
}