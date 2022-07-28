package com.easyoutfit.easyoutfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by Diogo on 24/01/2018.
 */

public class ClothesInputDialogFragment extends DialogFragment {

    public static final int GALLERY = 0;
    public static final int CAMERA = 1;

    ClothesInputDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] array;

        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) //no camera
        {
            array = new String[1];
            array[0] = getString(R.string.gallery);
        }
        else {

            array = new String[2];
            array[0] = getString(R.string.gallery);
            array[1] = getString(R.string.camera);

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_clothes_input_title)
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which == GALLERY) {
                            mListener.addClothesGallery();
                        }
                        else if(which == CAMERA)
                        {
                            mListener.addClothesCamera();
                        }

                    }
                });
        return builder.create();
    }



    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof ClothesInputDialogListener) {
            mListener = (ClothesInputDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ClothesInputDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

    }

    public interface ClothesInputDialogListener{
        void addClothesCamera();
        void addClothesGallery();

    }

}
