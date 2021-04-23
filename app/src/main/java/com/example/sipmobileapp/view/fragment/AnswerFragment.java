package com.example.sipmobileapp.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentAnswerBinding;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class AnswerFragment extends Fragment {
    private FragmentAnswerBinding binding;

    private int patientID;
    private String text;

    private static final String ARGS_PATIENT_ID = "patientID";

    public static final String TAG = AnswerFragment.class.getSimpleName();

    public static AnswerFragment newInstance(int patientID) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PATIENT_ID, patientID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        patientID = getArguments().getInt(ARGS_PATIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_answer,
                container,
                false);

        openConnection();
        initViews();
        handleClicked();

        return binding.getRoot();
    }

    private Socket _Socket;
    private DataOutputStream _dataOutputStream;

    private void openConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String IP = "192.168.1.105";
                int port = 4010;
                try {
                    _Socket = new Socket(IP, port);
                    String deviceName = Build.MODEL;
                    String appName = "سیپ موبایل اپ";
                    String version = "1.0";
                    String userName = SipMobileAppPreferences.getUsername(getContext());
                    String data = deviceName + ";" + appName + ";" + version + ";" + userName;
                    byte[] bData = data.getBytes("UTF-8");
                    byte[] bLenght = CreateIntArray(bData.length);

                    byte[] bPacket = new byte[12 + bData.length];
                    int tag = 103;
                    byte[] bTag = CreateIntArray(tag);

                    System.arraycopy(bTag, 0, bPacket, 4, 4);
                    System.arraycopy(bLenght, 0, bPacket, 8, 4);
                    System.arraycopy(bData, 0, bPacket, 12, bData.length);

                    _dataOutputStream = new DataOutputStream(_Socket.getOutputStream());
                    _dataOutputStream.write(bPacket);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    private byte[] CreateIntArray(int value) {
        byte[] bData = ByteBuffer.allocate(4).putInt(value).array();
        byte[] bData2 = new byte[4];
        bData2[0] = bData[3];
        bData2[1] = bData[2];
        bData2[2] = bData[1];
        bData2[3] = bData[0];
        return bData2;
    }

    private void initViews() {
        int patientID = getArguments().getInt(ARGS_PATIENT_ID);
        binding.txtPatientID.setText(String.valueOf(patientID));
    }

    private void handleClicked() {
        binding.txtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text = charSequence.toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = patientID + ";" + text ;
                            byte[] bData = data.getBytes("UTF-8");
                            byte[] bLenght = CreateIntArray(bData.length);

                            byte[] bPacket = new byte[12 + bData.length];
                            int tag = 2000001;
                            byte[] bTag = CreateIntArray(tag);

                            System.arraycopy(bTag, 0, bPacket, 4, 4);
                            System.arraycopy(bLenght, 0, bPacket, 8, 4);
                            System.arraycopy(bData, 0, bPacket, 12, bData.length);

                            _dataOutputStream.write(bPacket);
                            _dataOutputStream.flush();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}

