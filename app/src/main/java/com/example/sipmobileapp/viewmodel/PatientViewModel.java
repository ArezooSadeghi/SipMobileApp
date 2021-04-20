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

    private SingleLiveEvent<PatientResult> searchSingleLiveEvent;
    private SingleLiveEvent<String> errorSearchSingleLiveEvent;

    private SingleLiveEvent<String> noConnectivitySingleLiveEvent;
    private SingleLiveEvent<Boolean> timeOutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<PatientInfo> navigateToGallery = new SingleLiveEvent<>();

    public PatientViewModel(@NonNull Application application) {
        super(application);

        repository = SipMobileAppRepository.getInstance(getApplication());

        searchSingleLiveEvent = repository.getSearchSingleLiveEvent();
        errorSearchSingleLiveEvent = repository.getErrorSearchSingleLiveEvent();

        noConnectivitySingleLiveEvent = repository.getNoConnectivitySingleLiveEvent();
        timeOutExceptionHappenSingleLiveEvent = repository.getTimeOutExceptionHappenSingleLiveEvent();
    }

    public SingleLiveEvent<PatientResult> getSearchSingleLiveEvent() {
        return searchSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorSearchSingleLiveEvent() {
        return errorSearchSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectivitySingleLiveEvent() {
        return noConnectivitySingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getTimeOutExceptionHappenSingleLiveEvent() {
        return timeOutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<PatientInfo> getNavigateToGallery() {
        return navigateToGallery;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getSearchService(String newBaseUrl) {
        repository.getSearchService(newBaseUrl);
    }

    public void search(String userLoginKey, String patientName) {
        repository.search(userLoginKey, patientName);
    }
}
