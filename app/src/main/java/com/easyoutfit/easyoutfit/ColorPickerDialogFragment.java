package com.easyoutfit.easyoutfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Created by Diogo on 24/01/2018.
 */

public class ColorPickerDialogFragment extends DialogFragment {

    ColorPickerDialogListener colorPickerDialogListener;
    ImageView imageView;
    ImageHandle imageHandle;
    View content_layout;
    Button colorPickedButton;
    Bitmap originalBitmap;
    Bitmap crosshairBitmap;

    int selectedColor;

    private static final String MCURRENTCACHEIMAGEPATH = "com.easyoutfit.easyoutfit.mCurrentCacheImagePathColorPicker";



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bitmap bitmap;
        selectedColor = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.color_chooser_dialog,null, false);

        content_layout = view.findViewById(R.id.contentLayoutColorPicker);
        imageHandle = new ImageHandle(getActivity(), content_layout);
        imageView = view.findViewById(R.id.imageViewColorPicker);
        colorPickedButton = view.findViewById(R.id.colorPickedButton);

        if(colorPickerDialogListener.getCurrentImageCachePath() == null)
        {
            if(savedInstanceState.getString(MCURRENTCACHEIMAGEPATH) == null)
                return builder.create();
            else
                bitmap = imageHandle.getBitmapFromFile(savedInstanceState.getString(MCURRENTCACHEIMAGEPATH));
        }
        else
        {
            bitmap = imageHandle.getBitmapFromFile(colorPickerDialogListener.getCurrentImageCachePath());
        }

        originalBitmap = imageHandle.getDisplayableBitmap(bitmap, 0.5f);
        imageView.setImageBitmap(originalBitmap);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        // you can use your textview.
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                int[] location = new int[2];
                imageView.getLocationOnScreen(location);

                if(event.getRawX() < location[0] || event.getRawY() < location[1] ||
                        event.getRawX() > location[0] + imageView.getWidth() || event.getRawY() > location[1] + imageView.getHeight())
                    return false;
                else{
                    int x = (int)((event.getRawX()-(float)location[0])*(float)originalBitmap.getWidth()/(float)imageView.getWidth());
                    int y = (int)((event.getRawY()-(float)location[1])*(float)originalBitmap.getHeight()/(float)imageView.getHeight());
                    selectedColor = originalBitmap.getPixel(x,y);
                    colorPickedButton.setBackgroundColor(selectedColor);
                    crosshairBitmap = originalBitmap.copy(ARGB_8888, true);

                    int i, j;
                    int ray = Math.min(originalBitmap.getWidth(), originalBitmap.getHeight())/20;
                    for(i = x-ray; i<=x+ray;i++)
                    {
                        if(i>=0 && i<crosshairBitmap.getWidth())
                        {

                            for(j = y-ray/10; j <=y+ray/10;j++)
                            {
                                if(j>=0 && j<crosshairBitmap.getHeight())
                                {
                                    crosshairBitmap.setPixel(i,j,0);
                                }


                            }

                        }

                    }

                    for(i = y-ray; i<=y+ray;i++)
                    {
                        if(i>=0 && i<crosshairBitmap.getHeight())
                        {
                            for(j = x-ray/10; j <=x+ray/10;j++)
                            {
                                if(j>=0 && j<crosshairBitmap.getWidth())
                                    crosshairBitmap.setPixel(j,i,0);
                            }

                        }

                    }

                    imageView.setImageBitmap(crosshairBitmap);

                }
                return true;
            }
        });


        builder.setView(view)
                .setTitle(R.string.pick_color)
                // Add action buttons
                .setPositiveButton(R.string.OK_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        colorPickerDialogListener.onColorPickerDialogPositiveClick(ColorPickerDialogFragment.this, selectedColor);
                    }
                })
                .setNegativeButton(R.string.cancel_message, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof ColorPickerDialogListener) {
            colorPickerDialogListener = (ColorPickerDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ColorPickerDialogListener");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        if(colorPickerDialogListener.getCurrentImageCachePath() != null)
            savedInstanceState.putString(MCURRENTCACHEIMAGEPATH, colorPickerDialogListener.getCurrentImageCachePath());
        super.onSaveInstanceState(savedInstanceState);
    }


    public interface ColorPickerDialogListener{

        String getCurrentImageCachePath();
        void onColorPickerDialogPositiveClick(DialogFragment dialog, int color);

    }

}
