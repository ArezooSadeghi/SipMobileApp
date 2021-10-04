package com.example.sipmobileapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentSuccessDialogBinding;
import com.example.sipmobileapp.utils.Converter;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;


public class SuccessRemoveAttachDialogFragment extends DialogFragment {
    private FragmentSuccessDialogBinding binding;
    private AttachmentViewModel viewModel;

    private static final String ARGS_MESSAGE = "message";
    private static final String ARGS_ATTACH_ID = "attachID";
    public static final String TAG = SuccessRemoveAttachDialogFragment.class.getSimpleName();

    public static SuccessRemoveAttachDialogFragment newInstance(int attachID, String message) {
        SuccessRemoveAttachDialogFragment fragment = new SuccessRemoveAttachDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_ATTACH_ID, attachID);
        args.putString(ARGS_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_success_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        String message = Converter.letterConverter(getArguments().getString(ARGS_MESSAGE));
        binding.txtSuccessMessage.setText(message);
    }

    private void handleEvents() {
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int attachID = getArguments().getInt(ARGS_ATTACH_ID);
                viewModel.getDeleteImageFromGallery().setValue(attachID);
                dismiss();
            }
        });
    }
}