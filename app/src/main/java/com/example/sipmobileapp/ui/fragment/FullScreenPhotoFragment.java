package com.example.sipmobileapp.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentFullScreenPhotoBinding;
import com.example.sipmobileapp.event.DeleteEvent;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.ui.dialog.QuestionDialogFragment;
import com.example.sipmobileapp.ui.dialog.SuccessDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;

public class FullScreenPhotoFragment extends Fragment {
    private FragmentFullScreenPhotoBinding binding;
    private AttachmentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int attachID;

    private static final String ARGS_FILE_PATH = "filePath";
    private static final String ARGS_ATTACH_ID = "attachID";

    public static FullScreenPhotoFragment newInstance(String filePath, int attachID) {
        FullScreenPhotoFragment fragment = new FullScreenPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_FILE_PATH, filePath);
        args.putInt(ARGS_ATTACH_ID, attachID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_full_screen_photo,
                container,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        centerName = SipMobileAppPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        attachID = getArguments().getInt(ARGS_ATTACH_ID);
    }

    private void initViews() {
        String filePath = getArguments().getString(ARGS_FILE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null) {
            binding.imgViewFullScreen.setImage(ImageSource.bitmap(bitmap));
        }
    }

    private void handleEvents() {
        binding.imgViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance("آیا می خواهید فایل مربوطه را حذف نمایید؟");
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });
    }

    private void deleteAttach(int attachID) {
        viewModel.getServiceAttachResult(serverData.getIPAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attachs/Delete/";
        viewModel.deleteAttach(path, userLoginKey, attachID);
    }

    private void setupObserver() {
        viewModel.getDeleteAttachResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (attachResult != null) {
                    if (attachResult.getErrorCode().equals("0")) {
                        int attachID = attachResult.getAttachs()[0].getAttachID();
                        EventBus.getDefault().postSticky(new DeleteEvent(attachID));
                        showSuccessDialog("فایل با موفقیت حذف شد");
                    } else {
                        handleError(attachResult.getError());
                    }
                }
            }
        });

        viewModel.getYesDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDeleteClicked) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                deleteAttach(attachID);
            }
        });
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }
}