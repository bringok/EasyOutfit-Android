package com.easyoutfit.easyoutfit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditOutfitActivity extends AppCompatActivity {

    public static final String OUTFIT_BUNDLE = "com.easyoutfit.easyoutfit.outfitBundle";
    public static final String EDITED_OUTFIT_BUNDLE = "com.easyoutfit.easyoutfit.editedOutfitBundle";
    public static final String CLOTHES_ID_TO_REMOVE = "com.easyoutfit.easyoutfit.clothesIdToRemove";
    private static final int ADD_MULTIPLE_CLOTHES_INTENT_TAG = 1;
    private static final int ADD_SINGLE_CLOTHES_INTENT_TAG = 2;

    private Outfit outfitCurrent;

    private int clothesIdToRemove;

    private LinearLayout mainLayout;

    AppDatabase Database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_outfit);


        Database = AppDatabase.getAppDatabase(getApplicationContext());

        if(savedInstanceState != null)
            clothesIdToRemove = savedInstanceState.getInt(CLOTHES_ID_TO_REMOVE, -1);

        if((savedInstanceState == null || savedInstanceState.getBundle(OUTFIT_BUNDLE) == null) && getIntent() != null && getIntent().getBundleExtra(OUTFIT_BUNDLE) != null)
            outfitCurrent = Outfit.outfitFromBundle(getIntent().getBundleExtra(OUTFIT_BUNDLE));
        else if(savedInstanceState != null && savedInstanceState.getBundle(OUTFIT_BUNDLE) != null)
            outfitCurrent = Outfit.outfitFromBundle(savedInstanceState.getBundle(OUTFIT_BUNDLE));

        if(outfitCurrent == null)
            this.finish();

        mainLayout = findViewById(R.id.mainLinearLayoutEditClothes);

        createOutfitImagesList();

    }

    private void createOutfitImagesList() {

        ImageHandle imageHandle = new ImageHandle(this, mainLayout);

        ArrayList<Clothes> clothesInOutfit = new ArrayList<>();
        for(int id:outfitCurrent.getClothesIDS())
        {
            clothesInOutfit.add(Database.clothesDao().getClothesFromId(id));
        }

        Button addClothesButton = findViewById(R.id.addClothesButtonEditOutfit);

        final Activity activity = this;
        addClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SelectClothesActivity.class);
                Bundle b = new Bundle();
                b.putIntegerArrayList(FilterClothesActivity.BLACKLISTEDIDS, outfitCurrent.getClothesIDS());
                intent.putExtras(b);
                startActivityForResult(intent, ADD_MULTIPLE_CLOTHES_INTENT_TAG);
            }
        });

        double totalPrice = 0;
        boolean priceUncertain = false;

        for(final Clothes clothes:clothesInOutfit)
        {
            if(clothes.getPrice() > 0)
                totalPrice += clothes.getPrice();
            else
                priceUncertain = true;

            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
            rowLayout.setPadding(16,16,16,16);

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(imageHandle.getDisplayableBitmap(imageHandle.getBitmapFromFile(clothes.getImagePath()), 0.5));


            LinearLayout buttonsLayout = new LinearLayout(this);
            buttonsLayout.setOrientation(LinearLayout.VERTICAL);
            buttonsLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);

            Button replaceButton = new Button(this);
            replaceButton.setText(R.string.replace);
            Button deleteButton = new Button(this);
            deleteButton.setText(R.string.delete);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    outfitCurrent.getClothesIDS().remove((Object) clothes.getId());
                    mainLayout.removeAllViews();
                    createOutfitImagesList();
                }
            });

            replaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, SelectClothesActivity.class);
                    Bundle b = new Bundle();
                    b.putIntegerArrayList(FilterClothesActivity.BLACKLISTEDIDS, outfitCurrent.getClothesIDS());
                    boolean[] types = new boolean[getResources().getStringArray(R.array.clothes_type_array).length];
                    for(boolean type:types)
                        type = false;
                    types[clothes.getType()-1] = true;
                    b.putBooleanArray(FilterClothesActivity.TYPES, types);
                    b.putBoolean(SelectClothesActivity.SELECTSINGLECLOTHES, true);
                    intent.putExtras(b);
                    clothesIdToRemove = clothes.getId();
                    startActivityForResult(intent, ADD_SINGLE_CLOTHES_INTENT_TAG);

                }
            });

            TextView priceTV = findViewById(R.id.priceTextViewEditOutfit);
            if(priceUncertain)
                priceTV.setText(" > " + Double.toString(totalPrice) + getResources().getString(R.string.currency_symbol));
            else
                priceTV.setText(" " + Double.toString(totalPrice) + getResources().getString(R.string.currency_symbol));


            rowLayout.addView(imageView);
            buttonsLayout.addView(deleteButton);
            buttonsLayout.addView(replaceButton);
            rowLayout.addView(buttonsLayout);
            mainLayout.addView(rowLayout);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outstate)
    {
        outstate.putBundle(OUTFIT_BUNDLE, outfitCurrent.toBundle());
        outstate.putInt(CLOTHES_ID_TO_REMOVE, clothesIdToRemove);
        super.onSaveInstanceState(outstate);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_MULTIPLE_CLOTHES_INTENT_TAG:
                    List<Integer> newIds = data.getIntegerArrayListExtra(Outfit.OUTFIT_LIST_CLOTHES_IDS);
                    outfitCurrent.getClothesIDS().addAll(newIds);
                    outfitCurrent.sortClothesListByType(Database);
                    mainLayout.removeAllViews();
                    createOutfitImagesList();
                    break;
                case ADD_SINGLE_CLOTHES_INTENT_TAG:
                    int newId = data.getIntExtra(Outfit.OUTFIT_LIST_CLOTHES_IDS, -1);
                    if(newId != -1)
                    {
                        outfitCurrent.getClothesIDS().remove((Object) clothesIdToRemove);
                        outfitCurrent.getClothesIDS().add(newId);
                        outfitCurrent.sortClothesListByType(Database);
                        mainLayout.removeAllViews();
                        createOutfitImagesList();
                    }
                    break;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_outfit_activity, menu);

        super.onCreateOptionsMenu(menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.menu_edit_outfit_done:
                Intent intent = new Intent();
                intent.putExtra(EDITED_OUTFIT_BUNDLE, outfitCurrent.toBundle());
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return false;
    }
}
