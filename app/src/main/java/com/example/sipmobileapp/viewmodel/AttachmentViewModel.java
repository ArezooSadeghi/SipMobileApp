package com.example.sipmobileapp.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.sipmobileapp.model.AttachParameter;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.repository.SipMobileAppRepository;

import java.util.Map;

public class AttachmentViewModel extends AndroidViewModel {
    private SipMobileAppRepository repository;

    private SingleLiveEvent<Boolean> requestPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> allowPermission = new SingleLiveEvent<>();
    private SingleLiveEvent<String> finishWriteToStorage = new SingleLiveEvent<>();
    private SingleLiveEvent<String> photoClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesDeleteClicked = new SingleLiveEvent<>();
    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent;

    private SingleLiveEvent<String> convertBitmapToStringBase64 = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent;
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent;

    private SingleLiveEvent<AttachResult> patientAttachmentsResultSingleLiveEvent;


    private SingleLiveEvent<AttachResult> attachInfoResultSingleLiveEvent;


    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent;


    private SingleLiveEvent<Boolean> showAttachAgainDialog = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noAttachAgain = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> yesAttachAgain = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> updateGallery = new SingleLiveEvent<>();

    private SingleLiveEvent<Map<Bitmap, String>> showFullScreenImage = new SingleLiveEvent<>();
    private SingleLiveEvent<Map<Uri, String>> testShowFullScreenImage = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> deleteImageFromGallery = new SingleLiveEvent<>();

    private SingleLiveEvent<Boolean> yesDelete = new SingleLiveEvent<>();

    public AttachmentViewModel(@NonNull Application application) {
        super(application);

        repository = SipMobileAppRepository.getInstance(getApplication());

        noConnectionExceptionHappenSingleLiveEvent = repository.getNoConnectionExceptionHappenSingleLiveEvent();
        timeoutExceptionHappenSingleLiveEvent = repository.getTimeoutExceptionHappenSingleLiveEvent();

        patientAttachmentsResultSingleLiveEvent = repository.getPatientAttachmentsResultSingleLiveEvent();

        attachInfoResultSingleLiveEvent = repository.getAttachInfoResultSingleLiveEvent();

        attachResultSingleLiveEvent = repository.getAttachResultSingleLiveEvent();

        deleteAttachResultSingleLiveEvent = repository.getDeleteAttachResultSingleLiveEvent();
    }

    public SingleLiveEvent<Boolean> getRequestPermission() {
        return requestPermission;
    }

    public SingleLiveEvent<Boolean> getAllowPermission() {
        return allowPermission;
    }

    public SingleLiveEvent<String> getConvertBitmapToStringBase64() {
        return convertBitmapToStringBase64;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getPatientAttachmentsResultSingleLiveEvent() {
        return patientAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachInfoResultSingleLiveEvent() {
        return attachInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getShowAttachAgainDialog() {
        return showAttachAgainDialog;
    }

    public SingleLiveEvent<Boolean> getNoAttachAgain() {
        return noAttachAgain;
    }

    public SingleLiveEvent<Boolean> getYesAttachAgain() {
        return yesAttachAgain;
    }

    public SingleLiveEvent<AttachResult> getRefresh() {
        return updateGallery;
    }

    public SingleLiveEvent<Map<Bitmap, String>> getShowFullScreenImage() {
        return showFullScreenImage;
    }

    public SingleLiveEvent<Map<Uri, String>> getTestShowFullScreenImage() {
        return testShowFullScreenImage;
    }

    public SingleLiveEvent<Integer> getDeleteImageFromGallery() {
        return deleteImageFromGallery;
    }

    public SingleLiveEvent<Boolean> getYesDelete() {
        return yesDelete;
    }

    public SingleLiveEvent<String> getFinishWriteToStorage() {
        return finishWriteToStorage;
    }

    public SingleLiveEvent<String> getPhotoClicked() {
        return photoClicked;
    }

    public SingleLiveEvent<Boolean> getYesDeleteClicked() {
        return yesDeleteClicked;
    }

    public ServerData getServerData(String centerName) {
        return repository.getServerData(centerName);
    }

    public void getServicePatientResult(String newBaseUrl) {
        repository.getServicePatientResult(newBaseUrl);
    }

    public void getServiceAttachResult(String newBaseUrl) {
        repository.getServiceAttachResult(newBaseUrl);
    }

    public void fetchPatientAttachments(String path, String userLoginKey, int sickID) {
        repository.fetchPatientAttachments(path, userLoginKey, sickID);
    }

    public void fetchAttachInfo(String path, String userLoginKey, int attachID) {
        repository.fetchAttachInfo(path, userLoginKey, attachID);
    }

    public void attach(String path, String userLoginKey, AttachParameter attachParameter) {
        repository.attach(path, userLoginKey, attachParameter);
    }

    public void deleteAttach(String path, String userLoginKey, int attachID) {
        repository.deleteAttach(path, userLoginKey, attachID);
    }
}
