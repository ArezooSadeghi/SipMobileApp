package com.example.sipmobileapp.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RequiredServerDataDialogFragment extends DialogFragment {

    public static final String TAG = RequiredServerDataDialogFragment.class.getSimpleName();

    public static RequiredServerDataDialogFragment newInstance() {
        RequiredServerDataDialogFragment fragment = new RequiredServerDataDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("لطفا آدرس ip خود را وارد کنید")
                .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddEditServerDataDialogFragment fragment = AddEditServerDataDialogFragment.newInstance("", "", "", true);
                        fragment.show(getParentFragmentManager(), AddEditServerDataDialogFragment.TAG);
                        dismiss();
                    }
                })
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}