package com.example.sipmobileapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipmobileapp.model.PatientInfo;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

public class PatientViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;

    private SingleLiveEvent<PatientResult> patientsResultSingleLiveEvent;

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<PatientInfo> navigateToGallery = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> navigateToAnswer = new SingleLiveEvent<>();

    public PatientViewModel(@NonNull Application application) {
        super(application);

        repository = SipMobileAppRepository.getInstance(getApplication());

        patientsResultSingleLiveEvent = repository.getPatientsResultSingleLiveEvent();

        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<PatientResult> getPatientsResultSingleLiveEvent() {
        return patientsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<PatientInfo> getNavigateToGallery() {
        return navigateToGallery;
    }

    public SingleLiveEvent<Integer> getNavigateToAnswer() {
        return navigateToAnswer;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSearchService(String newBaseUrl) {
        repository.getServicePatientResult(newBaseUrl);
    }

    public void fetchPatients(String path, String userLoginKey, String patientName) {
        repository.fetchPatients(path, userLoginKey, patientName);
    }
}
