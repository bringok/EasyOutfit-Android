package com.easyoutfit.easyoutfit;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
import static android.view.View.GONE;

public class FilterClothesActivity extends AppCompatActivity {


    public static final String BRANDS = "com.easyoutfit.easyoutfit.brands";
    public static final String SUMMER = "com.easyoutfit.easyoutfit.summer";
    public static final String WINTER = "com.easyoutfit.easyoutfit.winter";
    public static final String TYPES = "com.easyoutfit.easyoutfit.types";
    public static final String SEASONLESS = "com.easyoutfit.easyoutfit.seasonless";
    public static final String SPRING = "com.easyoutfit.easyoutfit.spring";
    public static final String WISHLIST = "com.easyoutfit.easyoutfit.wishlist";
    public static final String PRICELOW = "com.easyoutfit.easyoutfit.priceLow";
    public static final String CATEGORIES = "com.easyoutfit.easyoutfit.categories";
    public static final String FALL = "com.easyoutfit.easyoutfit.fall";
    public static final String BRANDLESS = "com.easyoutfit.easyoutfit.brandless";
    public static final String PRICELESS = "com.easyoutfit.easyoutfit.priceless";
    public static final String PRICEHIGH = "com.easyoutfit.easyoutfit.priceHigh";
    public static final String BLACKLISTEDIDS = "com.easyoutfit.easyoutfit.blacklistedIDS";
    public static final int SELECTCLOTHESACTIVITYTAG = 1;


    Bundle savedState;

    CheckBox includeAll;
    LinearLayout typesLinearLayout;
    CheckBox[] types;
    LinearLayout brandsLinearLayout;
    List<EditText> brands;
    CheckBox includeBrandless;
    CheckBox includePriceless;
    EditText priceLow;
    EditText priceHigh;
    CheckBox seasonSummer;
    CheckBox seasonWinter;
    CheckBox seasonFall;
    CheckBox seasonSpring;
    CheckBox includeSeasonless;
    LinearLayout categoriesLinearLayout;
    CheckBox[] categories;
    CheckBox includeWishlist;
    Button generateOutfit;
    Button createOutfit;
    TextWatcher brandTextWatcher;
    TextWatcher priceTextWatcher;
    CompoundButton.OnCheckedChangeListener checkBoxesListener;

    boolean listenerBlocker;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_clothes);

        String[] typesStringArray = getResources().getStringArray(R.array.clothes_type_array_plural);
        String[] categoriesStringArray = getResources().getStringArray(R.array.clothes_category_array);

        listenerBlocker = false;

        brandTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(listenerBlocker)
                    return;
                updateBrandEditTexts();
                checkIfInIncludeAllState();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        checkBoxesListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(listenerBlocker)
                    return;
                checkIfInIncludeAllState();
            }
        };

        priceTextWatcher = new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(listenerBlocker)
                    return;
                updatePriceEditTexts();
                checkIfInIncludeAllState();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };


        includeAll = findViewById(R.id.includeAllCheckBox_filterClothes);

        includeAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(listenerBlocker)
                    return;
                if(b == true)
                {
                    savedState = new Bundle();
                    saveCurrentState(savedState);
                    includeAllState();
                }
                else
                {
                    if(savedState != null)
                        restorePreviousState(savedState);
                }
            }
        });

        typesLinearLayout = findViewById(R.id.clothesTypesLinearLayout_filterClothes);
        types = new CheckBox[typesStringArray.length];

        int i = 0;
        for(String type:typesStringArray)
        {
            types[i] = new CheckBox(this);
            types[i].setText(type);
            typesLinearLayout.addView(types[i]);
            types[i].setOnCheckedChangeListener(checkBoxesListener);
            i++;
        }


        brandsLinearLayout = findViewById(R.id.brandsLinearLayout_filterClothes);
        brands = new ArrayList<>();
        brands.add((EditText) findViewById(R.id.brandEditText_filterClothes));
        brands.get(0).addTextChangedListener(brandTextWatcher);

        includeBrandless = findViewById(R.id.includeBrandlessCheckBox_filterClothes);

        includePriceless = findViewById(R.id.includePricelessCheckBox_filterClothes);

        priceLow = findViewById(R.id.priceLowEditText_filterClothes);
        priceLow.addTextChangedListener(priceTextWatcher);
        priceHigh = findViewById(R.id.priceHighEditText_filterClothes);
        priceHigh.addTextChangedListener(priceTextWatcher);
        seasonSummer = findViewById(R.id.checkBoxSummer_filterClothes);
        seasonSummer.setOnCheckedChangeListener(checkBoxesListener);
        seasonWinter = findViewById(R.id.checkBoxWinter_filterClothes);
        seasonWinter.setOnCheckedChangeListener(checkBoxesListener);
        seasonFall = findViewById(R.id.checkBoxFall_filterClothes);
        seasonFall.setOnCheckedChangeListener(checkBoxesListener);
        seasonSpring = findViewById(R.id.checkBoxSpring_filterClothes);
        seasonSpring.setOnCheckedChangeListener(checkBoxesListener);
        includeSeasonless = findViewById(R.id.includeSeasonless_filterClothes);
        includeSeasonless.setOnCheckedChangeListener(checkBoxesListener);
        categoriesLinearLayout = findViewById(R.id.categoriesLinerLayout_filterClothes);
        categories = new CheckBox[categoriesStringArray.length];



        i = 0;
        for(String category:categoriesStringArray)
        {
            categories[i] = new CheckBox(this);
            categories[i].setText(category);
            categoriesLinearLayout.addView(categories[i]);
            categories[i].setOnCheckedChangeListener(checkBoxesListener);
            i++;
        }
        includeWishlist = findViewById(R.id.checkBoxWishList_filterClothes);
        includeWishlist.setOnCheckedChangeListener(checkBoxesListener);

        generateOutfit = findViewById(R.id.generateOutfitButton_filterClothes);
        generateOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchSelectClothesIntent(true);
            }
        });

        createOutfit = findViewById(R.id.buttonCreateOutfit_filterClothes);
        createOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchSelectClothesIntent(false);
            }
        });


    }

    private void dispatchSelectClothesIntent(boolean generateAuto)
    {

        Intent intent = new Intent(this, SelectClothesActivity.class);
        try{
            if((!priceLow.getText().toString().matches("") && Double.parseDouble(priceLow.getText().toString()) < 0)
                    || (!priceHigh.getText().toString().matches("") && Double.parseDouble(priceHigh.getText().toString()) < 0))
            {
                //TODO
                return;
            }
        }catch(NumberFormatException e){
            //TODO
            return;
        }
        savedState = new Bundle();
        saveCurrentState(savedState);
        savedState.putBoolean(SelectClothesActivity.GENERATE_AUTO, generateAuto);
        intent.putExtras(savedState);

        startActivityForResult(intent, SELECTCLOTHESACTIVITYTAG);

    }

    private void checkIfInIncludeAllState() {

        listenerBlocker = true;

        if(includeAll.isChecked())
        {
            for(CheckBox type:types)
            {
                if(!type.isChecked())
                {
                    includeAll.setChecked(false);
                    listenerBlocker = false;
                    return;
                }
            }
            for(CheckBox category:categories)
            {
                if(!category.isChecked())
                {
                    includeAll.setChecked(false);
                    listenerBlocker = false;
                    return;
                }
            }
            if(!seasonSummer.isChecked() || !seasonWinter.isChecked() || !seasonFall.isChecked() || !seasonSpring.isChecked()
                    || !includeSeasonless.isChecked() || !includeWishlist.isChecked())
            {
                includeAll.setChecked(false);
                listenerBlocker = false;
                return;
            }
            if(brands.size()>1)
            {
                includeAll.setChecked(false);
                listenerBlocker = false;
                return;
            }
            if(!priceLow.getText().toString().matches("") || !priceHigh.getText().toString().matches(""))
            {
                includeAll.setChecked(false);
                listenerBlocker = false;
                return;
            }
        }
        else
        {
            for(CheckBox type:types)
            {
                if(!type.isChecked())
                {
                    listenerBlocker = false;
                    return;
                }
            }
            for(CheckBox category:categories)
            {
                if(!category.isChecked())
                {
                    listenerBlocker = false;
                    return;
                }
            }
            if(!seasonSummer.isChecked() || !seasonWinter.isChecked() || !seasonFall.isChecked() || !seasonSpring.isChecked()
                    || !includeSeasonless.isChecked() || !includeWishlist.isChecked())
            {
                listenerBlocker = false;
                return;
            }
            if(brands.size()>1)
            {
                listenerBlocker = false;
                return;
            }
            if(!priceLow.getText().toString().matches("") || !priceHigh.getText().toString().matches(""))
            {
                listenerBlocker = false;
                return;
            }
            includeAll.setChecked(true);
        }

        listenerBlocker = false;


    }

    private void updatePriceEditTexts() {

        if(priceHigh.getText().toString().matches("") && priceLow.getText().toString().matches(""))
        {
            includePriceless.setVisibility(GONE);
        }
        else
        {
            includePriceless.setVisibility(View.VISIBLE);
        }
    }

    private void includeAllState() {

        listenerBlocker = true;

        for(CheckBox checkBox:types)
        {
            checkBox.setChecked(true);
        }
        for(CheckBox checkBox:categories)
        {
            checkBox.setChecked(true);
        }
        includeBrandless.setVisibility(GONE);
        includePriceless.setVisibility(GONE);

        brandsLinearLayout.removeAllViews();
        brands.clear();
        EditText emptyBrand = new EditText(this);
        emptyBrand.setInputType(TYPE_TEXT_FLAG_CAP_CHARACTERS);
        emptyBrand.addTextChangedListener(brandTextWatcher);
        emptyBrand.setTextSize(14);
        emptyBrand.setHint(R.string.brand);
        brands.add(emptyBrand);
        brandsLinearLayout.addView(emptyBrand);

        priceLow.setText(null);
        priceHigh.setText(null);

        seasonFall.setChecked(true);
        seasonSpring.setChecked(true);
        seasonSummer.setChecked(true);
        seasonWinter.setChecked(true);
        includeSeasonless.setChecked(true);

        includeWishlist.setChecked(true);

        listenerBlocker = false;
    }

    private void restorePreviousState(Bundle savedState) {

        listenerBlocker = true;

        boolean[] typesChecked = savedState.getBooleanArray(TYPES);
        int i = 0;
        for(CheckBox type:types)
        {
            type.setChecked(typesChecked[i]);
            i++;
        }

        boolean[] categoriesChecked = savedState.getBooleanArray(CATEGORIES);
        i = 0;
        for(CheckBox category:categories)
        {
            category.setChecked(categoriesChecked[i]);

            i++;
        }


        seasonSummer.setChecked(savedState.getBoolean(SUMMER));
        seasonWinter.setChecked(savedState.getBoolean(WINTER));
        seasonFall.setChecked(savedState.getBoolean(FALL));
        seasonSpring.setChecked(savedState.getBoolean(SPRING));
        includeSeasonless.setChecked(savedState.getBoolean(SEASONLESS));
        includeBrandless.setChecked(savedState.getBoolean(BRANDLESS));
        includeWishlist.setChecked(savedState.getBoolean(WISHLIST));
        includePriceless.setChecked(savedState.getBoolean(PRICELESS));

        priceLow.setText(savedState.getString(PRICELOW));
        priceHigh.setText(savedState.getString(PRICEHIGH));


        updatePriceEditTexts();


        ArrayList<String> brandsStringArray = savedState.getStringArrayList(BRANDS);
        brandsLinearLayout.removeAllViews();

        for(String brand:brandsStringArray)
        {
            EditText newBrandEditText = new EditText(this);
            newBrandEditText.setInputType(TYPE_TEXT_FLAG_CAP_CHARACTERS);
            newBrandEditText.setText(brand);
            newBrandEditText.setTextSize(14);
            newBrandEditText.setHint(R.string.brand);
            newBrandEditText.addTextChangedListener(brandTextWatcher);
            brandsLinearLayout.addView(newBrandEditText);
            brands.add(newBrandEditText);

        }
        EditText newBrandEditText = new EditText(this);
        newBrandEditText.setInputType(TYPE_TEXT_FLAG_CAP_CHARACTERS);
        newBrandEditText.setTextSize(14);
        newBrandEditText.setHint(R.string.brand);
        newBrandEditText.addTextChangedListener(brandTextWatcher);
        brandsLinearLayout.addView(newBrandEditText);
        brands.add(newBrandEditText);

        updateBrandEditTexts();

        checkIfInIncludeAllState();

        listenerBlocker = false;

    }

    private void saveCurrentState(Bundle savedState) {

        boolean[] typesChecked = new boolean[types.length];
        int i = 0;
        for(CheckBox type:types)
        {
            typesChecked[i] = type.isChecked();
            i++;
        }

        boolean[] categoriesChecked = new boolean[categories.length];
        i = 0;
        for(CheckBox category:categories)
        {
            categoriesChecked[i] = category.isChecked();

            i++;
        }


        savedState.putBooleanArray(TYPES, typesChecked);
        savedState.putBooleanArray(CATEGORIES, categoriesChecked);
        savedState.putBoolean(SUMMER, seasonSummer.isChecked());
        savedState.putBoolean(WINTER, seasonWinter.isChecked());
        savedState.putBoolean(FALL, seasonFall.isChecked());
        savedState.putBoolean(SPRING, seasonSpring.isChecked());
        savedState.putBoolean(SEASONLESS, includeSeasonless.isChecked());
        savedState.putBoolean(BRANDLESS, includeBrandless.isChecked());
        savedState.putBoolean(WISHLIST, includeWishlist.isChecked());
        savedState.putBoolean(PRICELESS, includePriceless.isChecked());

        savedState.putString(PRICELOW,priceLow.getText().toString());

        savedState.putString(PRICEHIGH,priceHigh.getText().toString());

        ArrayList<String> brandsStringArray = new ArrayList<>();
        for(EditText brand:brands)
        {
            if(!brand.getText().toString().matches(""))
                brandsStringArray.add(brand.getText().toString());
        }
        savedState.putStringArrayList(BRANDS,brandsStringArray);



    }

    private void updateBrandEditTexts()
    {
        Iterator<EditText> iterator = brands.iterator();
        while(iterator.hasNext())
        {
            EditText et = iterator.next();
            if(iterator.hasNext() && et.getText().toString().matches(""))
            {
                iterator.remove();
                brandsLinearLayout.removeView(et);
            }
            if(!iterator.hasNext() && !et.getText().toString().matches(""))
            {
                EditText newBrand = new EditText(this);
                newBrand.setInputType(TYPE_TEXT_FLAG_CAP_CHARACTERS);
                newBrand.setHint(R.string.brand);
                newBrand.setTextSize(14);
                newBrand.addTextChangedListener(brandTextWatcher);
                brands.add(newBrand);
                brandsLinearLayout.addView(newBrand);
            }


        }
        if(brands.size() == 1)
        {
            includeBrandless.setVisibility(GONE);
        }
        else
        {
            includeBrandless.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        saveCurrentState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        restorePreviousState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case SELECTCLOTHESACTIVITYTAG:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }

        }
    }


}
