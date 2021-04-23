package com.example.sipmobileapp.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentFullScreenImageDialogBinding;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class FullScreenImageDialogFragment extends DialogFragment {
    private FragmentFullScreenImageDialogBinding binding;
    private AttachmentViewModel viewModel;

    private Uri uri;

    private static final String ARGS_IMAGE = "image";
    private static final String ARGS_ATTACH_ID = "attachID";

    public static final String TAG = FullScreenImageDialogFragment.class.getSimpleName();

    public static FullScreenImageDialogFragment newInstance(Uri image, int attachID) {
        FullScreenImageDialogFragment fragment = new FullScreenImageDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_IMAGE, image);
        args.putInt(ARGS_ATTACH_ID, attachID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_full_screen_image_dialog,
                null,
                false);

        initViews();
        handleClicked();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();

        return dialog;
    }

    private void setupObserver() {
        viewModel.getDeleteAttachResultSingleLiveEvent().observe(this, new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                SuccessRemoveAttachDialogFragment fragment = SuccessRemoveAttachDialogFragment.newInstance(attachResult.getAttachs()[0].getAttachID());
                fragment.show(getParentFragmentManager(), SuccessRemoveAttachDialogFragment.TAG);
                dismiss();
            }
        });

        viewModel.getErrorDeleteAttachResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getNoConnectivitySingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeOutExceptionHappenSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTimeOutExceptionHappen) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("اتصال به اینترنت با خطا مواجه شد");
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getYesDelete().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDelete) {
                String centerName = SipMobileAppPreferences.getCenterName(getContext());
                String userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
                int attachID = getArguments().getInt(ARGS_ATTACH_ID);
                ServerData serverData = viewModel.getServerData(centerName);
                viewModel.getDeleteAttachService(serverData.getIPAddress() + ":" + serverData.getPort());
                viewModel.deleteAttach(userLoginKey, attachID);
            }
        });
    }

    private void initViews() {
        uri = getArguments().getParcelable(ARGS_IMAGE);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        binding.imgViewFullScreen.setImage(ImageSource.bitmap(bitmap));
    }

    private void handleClicked() {
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteQuestionDialogFragment fragment = DeleteQuestionDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), DeleteQuestionDialogFragment.TAG);
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(uri)
                        .setAllowFlipping(false)
                        .setCropMenuCropButtonTitle("بریدن")
                        .start((Activity) getContext());
            }
        });
    }
}