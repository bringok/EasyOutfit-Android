package com.easyoutfit.easyoutfit;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class AddClothesActivity extends AppCompatActivity implements ClothesInputDialogFragment.ClothesInputDialogListener, ColorPickerDialogFragment.ColorPickerDialogListener {

    static final int INTENT_REQUEST_IMAGE_CAPTURE = 1;
    static final int INTENT_REQUEST_IMAGE_GALLERY = 2;
    static final int INTENT_REQUEST_EDIT_IMAGE = 3;
    private static final String DIALOG_CLOTHES_INPUT_FRAGMENT_TAG = "com.easyoutfit.easyoutfit.clothes_input";
    private static final String DIALOG_COLOR_PICKER_PRIMARY_FRAGMENT_TAG = "com.easyoutfit.easyoutfit.color_picker_primary";
    private static final String DIALOG_COLOR_PICKER_SECONDARY_2_FRAGMENT_TAG = "com.easyoutfit.easyoutfit.color_picker_secondary_2";
    private static final String DIALOG_COLOR_PICKER_SECONDARY_1_FRAGMENT_TAG = "com.easyoutfit.easyoutfit.color_picker_secondary_1";
    private static final String MCURRENTCACHEIMAGEPATH = "com.easyoutfit.easyoutfit.mCurrentCacheImagePath";
    private static final String MCURRENTPRIMARYCOLOR = "com.easyoutfit.easyoutfit.mCurrentPrimaryColor";
    private static final String MCURRENTSECONDARYCOLOR_1 = "com.easyoutfit.easyoutfit.mCurrentSecondaryColor_1";
    private static final String MCURRENTSECONDARYCOLOR_2 = "com.easyoutfit.easyoutfit.mCurrentSecondaryColor_2";
    private static final String EDIT_MODE = "com.easyoutfit.easyoutfit.editMode";
    private static final String CURRENT_CLOTHES_ID = "com.easyoutfit.easyoutfit.currentClothesId";


    private String mCurrentCacheImagePath;
    private String mOldCacheImagePath;
    private int mCurrentPrimaryColor, mCurrentSecondaryColor_1, mCurrentSecondaryColor_2;
    private boolean editMode;
    private int currentClothesId;

    private DialogFragment clothesInputDialogFragment;
    private DialogFragment colorPickerDialogFragment;

    ImageView imageView;
    Button addImageButton;
    Button replaceImageButton;
    Button editImageButton;
    Spinner typeSpinner;
    Button primaryColorButton;
    Button secondaryColor_1Button;
    Button secondaryColor_2Button;
    EditText brandEditText;
    EditText priceEditText;
    CheckBox summerCheckBox;
    CheckBox winterCheckBox;
    CheckBox fallCheckBox;
    CheckBox springCheckBox;
    Spinner categorySpinner;
    CheckBox wishlistCheckBox;
    Button addButton;
    View content_layout;


    ImageHandle imageHandle;

    private AppDatabase Database;


    public void onClickButton(View view)
    {
        switch (view.getId()){
            case R.id.addImageButton:
            case R.id.replaceImageButton:
                clothesInputDialogFragment.show(this.getSupportFragmentManager(), DIALOG_CLOTHES_INPUT_FRAGMENT_TAG);
                break;

            case R.id.editImageButton:
                dispatchEditPictureIntent();
                break;

            case R.id.buttonPrimaryColor:
                if(mCurrentCacheImagePath != null && new File(mCurrentCacheImagePath).exists())
                    colorPickerDialogFragment.show(this.getSupportFragmentManager(), DIALOG_COLOR_PICKER_PRIMARY_FRAGMENT_TAG);
                else{
                    Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.image_necessary_for_color, LENGTH_LONG);
                    quickMessage.show();
                }
                break;

            case R.id.buttonFirstSecondaryColors:
                if(mCurrentCacheImagePath != null && new File(mCurrentCacheImagePath).exists())
                    colorPickerDialogFragment.show(this.getSupportFragmentManager(), DIALOG_COLOR_PICKER_SECONDARY_1_FRAGMENT_TAG);
                else{
                    Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.image_necessary_for_color, LENGTH_LONG);
                    quickMessage.show();
                }
                break;

            case R.id.buttonSecondSecondaryColors:
                if(mCurrentCacheImagePath != null && new File(mCurrentCacheImagePath).exists())
                    colorPickerDialogFragment.show(this.getSupportFragmentManager(), DIALOG_COLOR_PICKER_SECONDARY_2_FRAGMENT_TAG);
                else{
                    Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.image_necessary_for_color, LENGTH_LONG);
                    quickMessage.show();
                }
                break;

            case R.id.buttonAdd:
                if(mCurrentCacheImagePath == null || !(new File(mCurrentCacheImagePath)).exists() || mCurrentPrimaryColor == 0)
                {
                    Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.image_and_primary_necessary, LENGTH_LONG);
                    quickMessage.show();
                }
                else
                    addToDatabase();
                break;



        }


    }

    private void addToDatabase()
    {
        //TODO
        int type = typeSpinner.getSelectedItemPosition() + 1;
        int category = categorySpinner.getSelectedItemPosition() + 1;
        float price;
        try {
            price = Float.parseFloat(priceEditText.getText().toString());
        }catch(NumberFormatException e){
            price = -1;
        }

        String permanentImagePath = imageHandle.createImageFile(ImageHandle.PERMANENT, "PERMANENT");

        if(permanentImagePath == null)
            return;

        String permanentThumbnailPath = imageHandle.createImageFile(ImageHandle.PERMANENT, "THUMBNAIL");
        if(permanentThumbnailPath == null)
            return;


        try{
            imageHandle.copy(new File(mCurrentCacheImagePath),new File(permanentImagePath));
            //(new File(mCurrentCacheImagePath)).delete();
            mCurrentCacheImagePath = null;
        }catch(IOException e){
            Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.error_saving_image, LENGTH_LONG);
            quickMessage.show();
            return;
        }
        //create thumbnail and save
        imageHandle.createAndSaveThumbnail(permanentThumbnailPath, permanentImagePath);


        Clothes newClothes = new Clothes(permanentImagePath, permanentThumbnailPath, type,mCurrentPrimaryColor, mCurrentSecondaryColor_1, mCurrentSecondaryColor_2,
                winterCheckBox.isChecked(), summerCheckBox.isChecked(), fallCheckBox.isChecked(), springCheckBox.isChecked(), category, wishlistCheckBox.isChecked(),
                brandEditText.getText().toString(), price);

        if(editMode)
        {
            newClothes.setId(currentClothesId);
            //TODO in main activity
            Database.clothesDao().updateClothes(newClothes);
        }
        else
            Database.clothesDao().insertClothes(newClothes);

        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothes);


        clothesInputDialogFragment = new ClothesInputDialogFragment();
        colorPickerDialogFragment = new ColorPickerDialogFragment();
        imageView = findViewById(R.id.addClothesImageView);
        addImageButton = findViewById(R.id.addImageButton);
        replaceImageButton = findViewById(R.id.replaceImageButton);
        editImageButton = findViewById(R.id.editImageButton);
        typeSpinner = findViewById(R.id.spinnerClothesType);
        primaryColorButton = findViewById(R.id.buttonPrimaryColor);
        secondaryColor_1Button = findViewById(R.id.buttonFirstSecondaryColors);
        secondaryColor_2Button = findViewById(R.id.buttonSecondSecondaryColors);
        brandEditText = findViewById(R.id.editTextBrand);
        priceEditText = findViewById(R.id.editTextPrice);
        summerCheckBox = findViewById(R.id.checkBoxSummer_filterClothes);
        winterCheckBox = findViewById(R.id.checkBoxWinter_filterClothes);
        fallCheckBox = findViewById(R.id.checkBoxFall_filterClothes);
        springCheckBox = findViewById(R.id.checkBoxSpring);
        categorySpinner = findViewById(R.id.spinnerClothesCategory);
        addButton = findViewById(R.id.buttonAdd);
        wishlistCheckBox = findViewById(R.id.checkBoxWishList_filterClothes);
        content_layout = findViewById(R.id.content_layout);



        replaceImageButton.setVisibility(View.GONE);
        editImageButton.setVisibility(View.GONE);

        imageHandle = new ImageHandle(this, content_layout);

        Database = AppDatabase.getAppDatabase(getApplicationContext());

        editMode = false;


        if(getIntent() != null)
        {
            int clothes_id = getIntent().getIntExtra(MainActivity.CLOTHES_ID, -1);
            if(clothes_id != -1)
            {
                editMode = true;
                currentClothesId = clothes_id;
                fillKnownFields(Database.clothesDao().getClothesFromId(clothes_id));
                addButton.setText(R.string.replace_final_button);
                setTitle("Edit Clothes");
            }
        }


    }

    private void fillKnownFields(Clothes clothes) {

        mCurrentCacheImagePath = clothes.getImagePath();

        Bitmap bitmap = imageHandle.getBitmapFromFile(mCurrentCacheImagePath);
        imageView.setImageBitmap(imageHandle.getDisplayableBitmap(bitmap, 0.5f));
        addImageButton.setVisibility(View.GONE);
        replaceImageButton.setVisibility(View.VISIBLE);
        editImageButton.setVisibility(View.VISIBLE);

        mCurrentPrimaryColor = clothes.getPrimary_color();
        if(mCurrentPrimaryColor != 0)
            primaryColorButton.setBackgroundColor(mCurrentPrimaryColor);

        mCurrentSecondaryColor_1 = clothes.getSecondary_color_1();
        if(mCurrentSecondaryColor_1 != 0)
            secondaryColor_1Button.setBackgroundColor(mCurrentSecondaryColor_1);

        mCurrentSecondaryColor_2 = clothes.getSecondary_color_2();
        if(mCurrentSecondaryColor_2 != 0)
            secondaryColor_2Button.setBackgroundColor(mCurrentSecondaryColor_2);

        typeSpinner.setSelection(clothes.getType()-1);

        if(clothes.getBrand() != null && !clothes.getBrand().matches(""))
            brandEditText.setText(clothes.getBrand());

        if(clothes.getPrice() >= 0)
            priceEditText.setText(Double.toString(clothes.getPrice()));

        if(clothes.isSeasonSummer())
            summerCheckBox.setChecked(true);

        if(clothes.isSeasonWinter())
            winterCheckBox.setChecked(true);

        if(clothes.isSeasonFall())
            fallCheckBox.setChecked(true);

        if(clothes.isSeasonSpring())
            springCheckBox.setChecked(true);

        categorySpinner.setSelection(clothes.getCategory()-1);

        if(clothes.isWishlist())
            wishlistCheckBox.setChecked(true);

    }

    @Override
    public void addClothesCamera() {

        mOldCacheImagePath = mCurrentCacheImagePath;
        mCurrentCacheImagePath = null;
        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            mCurrentCacheImagePath = imageHandle.createImageFile(ImageHandle.CACHE, "RAW_CAMERA");
            File photoFile = null;
            if (mCurrentCacheImagePath != null) {
                photoFile = new File(mCurrentCacheImagePath);
            }

            // Continue only if the File was successfully created
            if (photoFile != null && photoFile.exists()) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.easyoutfit.easyoutfit.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, INTENT_REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchEditPictureIntent(){

        mOldCacheImagePath = mCurrentCacheImagePath;
        mCurrentCacheImagePath = null;
        mCurrentCacheImagePath = imageHandle.createImageFile(ImageHandle.CACHE, "EDITED");
        try{
            imageHandle.copy(new File(mOldCacheImagePath), new File(mCurrentCacheImagePath));
        }catch(IOException e){
            Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.error_creating_file, LENGTH_LONG);
            quickMessage.show();
            return;
        }
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        if (editIntent.resolveActivity(this.getPackageManager()) != null) {

            File pictureFile = new File(mCurrentCacheImagePath);

            Uri uri = FileProvider.getUriForFile(this, "com.easyoutfit.easyoutfit.fileprovider", pictureFile);//Uri.parse(mCurrentPhotoPath);
            int flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION;

            editIntent.setDataAndType(uri, "image/*");
            editIntent.addFlags(flags);

            editIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(editIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                this.grantUriPermission(packageName, uri, flags);
            }

            startActivityForResult(Intent.createChooser(editIntent, null), INTENT_REQUEST_EDIT_IMAGE);
        }

    }

    @Override
    public void addClothesGallery() {

        mOldCacheImagePath = mCurrentCacheImagePath;
        mCurrentCacheImagePath = null;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        //intent.addCategory(CATEGORY_OPENABLE);
        startActivityForResult(intent, INTENT_REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {

            switch (requestCode){
                case INTENT_REQUEST_IMAGE_CAPTURE:
                    compressAndReplaceInCacheAndDisplay(mCurrentCacheImagePath);
                    addImageButton.setVisibility(View.GONE);
                    replaceImageButton.setVisibility(View.VISIBLE);
                    editImageButton.setVisibility(View.VISIBLE);
                    break;


                case INTENT_REQUEST_IMAGE_GALLERY:
                    String picturePath = imageHandle.getRealPathFromURI(data.getData());
                    try {
                        mCurrentCacheImagePath = imageHandle.createImageFile(ImageHandle.CACHE,"RAW_GALLERY");
                        imageHandle.copy(new File(picturePath), new File(mCurrentCacheImagePath));
                    }catch(IOException e){
                        Snackbar quickMessage = Snackbar.make(findViewById(R.id.content_layout), R.string.error_retrieving_from_gallery, LENGTH_LONG);
                        quickMessage.show();
                        mCurrentCacheImagePath = mOldCacheImagePath;
                        mOldCacheImagePath = null;
                        return;
                    }
                    compressAndReplaceInCacheAndDisplay(picturePath);
                    addImageButton.setVisibility(View.GONE);
                    replaceImageButton.setVisibility(View.VISIBLE);
                    editImageButton.setVisibility(View.VISIBLE);
                    break;

                case INTENT_REQUEST_EDIT_IMAGE:
                    compressAndReplaceInCacheAndDisplay(mCurrentCacheImagePath);
                    break;

            }
            if(mOldCacheImagePath != null){
                //(new File(mOldCacheImagePath)).delete();
                }
            mOldCacheImagePath = null;


        }
        else
        {
            try {
                //new File(mOldCacheImagePath).delete();
                mCurrentCacheImagePath = mOldCacheImagePath;
                mOldCacheImagePath = null;
            }catch(NullPointerException e){
                mCurrentCacheImagePath = mOldCacheImagePath;
                mOldCacheImagePath = null;
            }
        }




    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(MCURRENTCACHEIMAGEPATH, mCurrentCacheImagePath);
        savedInstanceState.putInt(MCURRENTPRIMARYCOLOR, mCurrentPrimaryColor);
        savedInstanceState.putInt(MCURRENTSECONDARYCOLOR_1, mCurrentSecondaryColor_1);
        savedInstanceState.putInt(MCURRENTSECONDARYCOLOR_2, mCurrentSecondaryColor_2);
        savedInstanceState.putBoolean(EDIT_MODE, editMode);
        if(editMode)
            savedInstanceState.putInt(CURRENT_CLOTHES_ID, currentClothesId);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getString(MCURRENTCACHEIMAGEPATH) != null)
        {
            mCurrentCacheImagePath = savedInstanceState.getString(MCURRENTCACHEIMAGEPATH);

            Bitmap bitmap = imageHandle.getBitmapFromFile(mCurrentCacheImagePath);
            imageView.setImageBitmap(imageHandle.getDisplayableBitmap(bitmap, 0.5f));
            addImageButton.setVisibility(View.GONE);
            replaceImageButton.setVisibility(View.VISIBLE);
            editImageButton.setVisibility(View.VISIBLE);
        }
        else{
            addImageButton.setVisibility(View.VISIBLE);
            replaceImageButton.setVisibility(View.GONE);
            editImageButton.setVisibility(View.GONE);

        }

        mCurrentPrimaryColor = savedInstanceState.getInt(MCURRENTPRIMARYCOLOR);
        if(mCurrentPrimaryColor != 0)
            primaryColorButton.setBackgroundColor(mCurrentPrimaryColor);

        mCurrentSecondaryColor_1 = savedInstanceState.getInt(MCURRENTSECONDARYCOLOR_1);
        if(mCurrentSecondaryColor_1 != 0)
            secondaryColor_1Button.setBackgroundColor(mCurrentSecondaryColor_1);

        mCurrentSecondaryColor_2 = savedInstanceState.getInt(MCURRENTSECONDARYCOLOR_2);
        if(mCurrentSecondaryColor_2 != 0)
            secondaryColor_2Button.setBackgroundColor(mCurrentSecondaryColor_2);


        editMode = savedInstanceState.getBoolean(EDIT_MODE, false);
        if(editMode)
        {
            addButton.setText(R.string.replace_final_button);
            savedInstanceState.getInt(CURRENT_CLOTHES_ID);
            setTitle("Edit Clothes");
        }

        else
            addImageButton.setText(R.string.add_final_button);

    }

    @Override
    public String getCurrentImageCachePath() {
        return mCurrentCacheImagePath;
    }


    @Override
    public void onColorPickerDialogPositiveClick(DialogFragment dialog, int color) {
        if(dialog.getTag().equals(DIALOG_COLOR_PICKER_PRIMARY_FRAGMENT_TAG) && color!=0)
        {
            mCurrentPrimaryColor = color;
            primaryColorButton.setBackgroundColor(color);
        }

        if(dialog.getTag().equals(DIALOG_COLOR_PICKER_SECONDARY_1_FRAGMENT_TAG) && color!=0)
        {
            mCurrentSecondaryColor_1 = color;
            secondaryColor_1Button.setBackgroundColor(color);
        }

        if(dialog.getTag().equals(DIALOG_COLOR_PICKER_SECONDARY_2_FRAGMENT_TAG) && color!=0)
        {
            mCurrentSecondaryColor_2 = color;
            secondaryColor_2Button.setBackgroundColor(color);
        }
    }

    private void compressAndReplaceInCacheAndDisplay(String rawImagePath){
        Bitmap bitmap = imageHandle.getCompressedBitmapFromFile(rawImagePath); //compressed bitmap
        //new File(mCurrentCacheImagePath).delete(); //delete raw image
        mCurrentCacheImagePath = imageHandle.createImageFile(ImageHandle.CACHE,"COMPRESSED");
        if (mCurrentCacheImagePath != null) {
            imageHandle.saveBitmap(new File(mCurrentCacheImagePath), bitmap); //save compressed bitmap
        }
        imageView.setImageBitmap(imageHandle.getDisplayableBitmap(bitmap, 0.5f)); //set imageView

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                if(mCurrentCacheImagePath != null)
                {
                    //new File(mCurrentCacheImagePath).delete();
                    mCurrentCacheImagePath = null;
                }
            }
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(mCurrentCacheImagePath != null)
        {
            //new File(mCurrentCacheImagePath).delete();
            mCurrentCacheImagePath = null;
        }


    }

}
