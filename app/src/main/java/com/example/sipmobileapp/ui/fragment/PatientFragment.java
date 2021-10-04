package com.example.sipmobileapp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.PatientAdapter;
import com.example.sipmobileapp.databinding.FragmentPatientBinding;
import com.example.sipmobileapp.model.PatientInfo;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.activity.PhotoGalleryContainerActivity;
import com.example.sipmobileapp.ui.activity.LoginContainerActivity;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.PatientViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.Arrays;
import java.util.List;

public class PatientFragment extends Fragment {
    private FragmentPatientBinding binding;
    private PatientViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;

    public static PatientFragment newInstance() {
        PatientFragment fragment = new PatientFragment();
        Bundle args = new Bundle();
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
                R.layout.fragment_patient,
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
        viewModel = new ViewModelProvider(this).get(PatientViewModel.class);
    }

    private void initVariables() {
        centerName = SipMobileAppPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
    }

    private void initViews() {
        binding.recyclerViewPatient.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPatient.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewPatient.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.imgButtonMore.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(getContext())
                        .addItem(new PowerMenuItem("خروج از حساب کاربری"))
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (position) {
                            case 0:
                                SipMobileAppPreferences.setUserLoginKey(getContext(), null);
                                Intent intent = LoginContainerActivity.newIntent(getContext());
                                startActivity(intent);
                                getActivity().finish();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
            }
        });

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                binding.txtNoPatient.setVisibility(View.GONE);
                viewModel.getSearchService(serverData.getIPAddress() + ":" + serverData.getPort());
                String path = "/api/v1/patients/search/";
                viewModel.fetchPatients(path, userLoginKey, binding.edTextSearch.getText().toString());
            }
        });

        binding.edTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.progressBarLoading.setVisibility(View.VISIBLE);
                    binding.txtNoPatient.setVisibility(View.GONE);
                    viewModel.getSearchService(serverData.getIPAddress() + ":" + serverData.getPort());
                    String path = "/api/v1/patients/search/";
                    viewModel.fetchPatients(path, userLoginKey, binding.edTextSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void setupObserver() {
        viewModel.getPatientsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PatientResult>() {
            @Override
            public void onChanged(PatientResult patientResult) {
                if (patientResult != null) {
                    if (patientResult.getErrorCode().equals("0")) {
                        if (patientResult.getPatients() == null || patientResult.getPatients().length == 0) {
                            binding.progressBarLoading.setVisibility(View.GONE);
                            binding.recyclerViewPatient.setVisibility(View.GONE);
                            binding.txtNoPatient.setVisibility(View.VISIBLE);
                        } else {
                            binding.progressBarLoading.setVisibility(View.GONE);
                            binding.txtNoPatient.setVisibility(View.GONE);
                            binding.recyclerViewPatient.setVisibility(View.VISIBLE);
                            setupAdapter(patientResult.getPatients());
                        }
                    } else {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        binding.recyclerViewPatient.setVisibility(View.GONE);
                        binding.txtNoPatient.setVisibility(View.VISIBLE);
                        handleError(patientResult.getError());
                    }
                }
            }
        });

        viewModel.getNavigateToGallery().observe(getViewLifecycleOwner(), new Observer<PatientInfo>() {
            @Override
            public void onChanged(PatientInfo patientInfo) {
                Intent intent = PhotoGalleryContainerActivity.start(getContext(), patientInfo.getSickID());
                startActivity(intent);
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.recyclerViewPatient.setVisibility(View.GONE);
                binding.txtNoPatient.setVisibility(View.VISIBLE);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.recyclerViewPatient.setVisibility(View.GONE);
                binding.txtNoPatient.setVisibility(View.VISIBLE);
                handleError(message);
            }
        });
    }

    private void setupAdapter(PatientInfo[] patients) {
        binding.recyclerViewPatient.setVisibility(View.VISIBLE);
        List<PatientInfo> patientInfoList = Arrays.asList(patients);
        PatientAdapter adapter = new PatientAdapter(getContext(), patientInfoList, viewModel);
        binding.recyclerViewPatient.setAdapter(adapter);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }
}