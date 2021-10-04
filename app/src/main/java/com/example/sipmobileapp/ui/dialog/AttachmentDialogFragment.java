package com.example.sipmobileapp.ui.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentAttachmentDialogBinding;
import com.example.sipmobileapp.model.AttachParameter;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AttachmentDialogFragment extends DialogFragment {
    private FragmentAttachmentDialogBinding binding;
    private AttachmentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private Uri photoUri;
    private File photoFile;
    private Bitmap bitmap;
    private Matrix matrix;
    private int numberOfRotate, sickID;

    private static final String ARGS_SICK_ID = "sickID";
    private static final String AUTHORITY = "com.example.sipmobileapp.fileProvider";

    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;

    public static final String TAG = AttachmentDialogFragment.class.getSimpleName();

    public static AttachmentDialogFragment newInstance(int sickID) {
        AttachmentDialogFragment fragment = new AttachmentDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_SICK_ID, sickID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_attachment_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PHOTO:
                    if (photoFile.length() != 0) {
                        try {
                            photoUri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);

                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                matrix.postRotate(90);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            }

                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    break;
                case REQUEST_CODE_PICK_PHOTO:
                    photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        centerName = SipMobileAppPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        sickID = getArguments().getInt(ARGS_SICK_ID);
        matrix = new Matrix();
    }

    private void setupObserver() {
        viewModel.getAllowPermission().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPermissionAllow) {
                openCamera();
            }
        });

        viewModel.getAttachResultSingleLiveEvent().observe(this, new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                binding.loadingLayout.setVisibility(View.GONE);
                binding.ivClose.setEnabled(true);
                binding.ivSend.setEnabled(true);
                binding.ivCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.ivRotate.setEnabled(true);
                binding.ivChooseFromGallery.setEnabled(true);

                if (attachResult != null) {
                    if (attachResult.getErrorCode().equals("0")) {
                        viewModel.getRefresh().setValue(attachResult);
                        SuccessAttachDialogFragment fragment = SuccessAttachDialogFragment.newInstance(getString(R.string.success_attach_message));
                        fragment.show(getParentFragmentManager(), SuccessAttachDialogFragment.TAG);
                    } else {
                        handleError(attachResult.getError());
                    }
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.loadingLayout.setVisibility(View.GONE);
                binding.ivClose.setEnabled(true);
                binding.ivSend.setEnabled(true);
                binding.ivCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.ivRotate.setEnabled(true);
                binding.ivChooseFromGallery.setEnabled(true);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.loadingLayout.setVisibility(View.GONE);
                binding.ivClose.setEnabled(true);
                binding.ivSend.setEnabled(true);
                binding.ivCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.ivRotate.setEnabled(true);
                binding.ivChooseFromGallery.setEnabled(true);
                handleError(message);
            }
        });

        viewModel.getShowAttachAgainDialog().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean showAttachAgainDialog) {
                AttachAgainDialogFragment fragment = AttachAgainDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), AttachAgainDialogFragment.TAG);
            }
        });

        viewModel.getNoAttachAgain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean noAttachAgain) {
                dismiss();
            }
        });

        viewModel.getYesAttachAgain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesAttachAgain) {
                photoUri = null;
                binding.ivPhoto.setImageResource(0);
                binding.edTextDescription.setText("");
            }
        });
    }

    private void handleEvents() {
        binding.ivChooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "انتخاب تصویر"), REQUEST_CODE_PICK_PHOTO);
            }
        });

        binding.ivRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoUri != null) {
                    switch (numberOfRotate) {
                        case 0:
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                            numberOfRotate++;
                            break;
                        case 1:
                            matrix.postRotate(180);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                            numberOfRotate++;
                            break;
                        case 2:
                            matrix.postRotate(270);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                            numberOfRotate = 0;
                            break;
                    }
                } else {
                    handleError("هنوز فایلی انتخاب نشده است");
                }
            }
        });

        binding.ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        binding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoUri != null) {
                    binding.loadingLayout.setVisibility(View.VISIBLE);
                    binding.ivSend.setEnabled(false);
                    binding.ivRotate.setEnabled(false);
                    binding.ivChooseFromGallery.setEnabled(false);
                    binding.ivCamera.setEnabled(false);

                    AttachParameter attachParameter = new AttachParameter();
                    String image = convertBitmapToBase64(bitmap);
                    attachParameter.setImage(image);
                    attachParameter.setSickID(sickID);
                    String description = binding.edTextDescription.getText().toString();
                    attachParameter.setDescription(description);
                    attachParameter.setAttachTypeID(1);
                    attachParameter.setImageTypeID(2);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            attach(attachParameter);
                        }
                    }).start();
                } else {
                    handleError("هنوز فایلی انتخاب نشده است");
                }
            }
        });

        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void attach(AttachParameter attachParameter) {
        viewModel.getServiceAttachResult(serverData.getIPAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attachs/Add/";
        viewModel.attach(path, userLoginKey, attachParameter);
    }

    private void requestCameraPermission() {
        viewModel.getRequestPermission().setValue(true);
    }

    private void openCamera() {
        Intent starterCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (starterCamera.resolveActivity(getActivity().getPackageManager()) != null) {
            File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String name = "img_" + new Date().getTime() + ".jpg";
            photoFile = new File(dir, name);
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(starterCamera, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                starterCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(starterCamera, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        System.gc();
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }
}