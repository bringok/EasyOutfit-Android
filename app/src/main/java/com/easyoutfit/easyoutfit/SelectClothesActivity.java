package com.easyoutfit.easyoutfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectClothesActivity extends AppCompatActivity {

    private final static int TRANSPARENT_BLACK = 0x80000000;
    private final static int TRANSPARENT = 0x00000000;
    private final static int TRANSPARENT_YELLOW = 0x40FFD600;

    private final static String SELECTEDCLOTHESIDSLIST = "easyoutfit.easyoutfit.selectedClothesIdsList";
    private final static String GROUPINGMODE = "easyoutfit.easyoutfit.groupingMode";
    public final static String SELECTSINGLECLOTHES = "easyoutfit.easyoutfit.selectSingleClothes";
    public  final static String GENERATE_AUTO = "easyoutfit.easyoutfit.generateAuto";

    private int groupingMode;
    private boolean selectSingleClothes;

    LinearLayout linearLayout;

    private AppDatabase Database;

    private List<Clothes> listOfClothes;
    private List<Clothes> clothesSelected;
    private List<Integer> selectedClothesIdsList;


    public static final int GROUPING_TYPE = 1;
    public static final int GROUPING_CATEGORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        clothesSelected = new ArrayList<Clothes>();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        groupingMode = sharedPref.getInt(GROUPINGMODE, GROUPING_TYPE);

        Database = AppDatabase.getAppDatabase(getApplicationContext());

        selectSingleClothes = false;

        getFilteredClothesFromDatabase();

        if(getIntent().getExtras() != null) {
            selectSingleClothes = getIntent().getExtras().getBoolean(SELECTSINGLECLOTHES, false);
            if(getIntent().getExtras().getBoolean(GENERATE_AUTO, false))
            {
                generateAutomaticOutfit();
            }
        }



        setContentView(R.layout.activity_select_clothes);

        linearLayout = findViewById(R.id.linearLayoutSelectClothes);

        createThumbnailGridGrouping();


        
    }

    private void generateAutomaticOutfit() {

        if(listOfClothes.isEmpty())
            return;
        int maxClothesPerType = 0;
        List<Clothes> finalList = new ArrayList<>();
        int maxClothesType = listOfClothes.get(0).getType();
        Random rand = new Random();

        int[] numOfClothesPerType = new int[getResources().getStringArray(R.array.clothes_type_array).length];

        for(Clothes clothes:listOfClothes)
        {
            numOfClothesPerType[clothes.getType() - 1]++;
        }

        for(int i = 0; i < numOfClothesPerType.length; i++)
        {
            if(numOfClothesPerType[i] > maxClothesPerType)
            {
                maxClothesPerType = numOfClothesPerType[i];
                maxClothesType = i+1;
            }
        }

        List<Clothes> typeMaxClothesList = new ArrayList<>();
        for(Clothes clothes: listOfClothes)
        {
            if(clothes.getType() == maxClothesType)
                typeMaxClothesList.add(clothes);
        }


        int i = rand.nextInt(typeMaxClothesList.size());

        finalList.add(typeMaxClothesList.get(i));

        for(i = 0; i < numOfClothesPerType.length; i++)
        {
            int type = i+1;
            if(type == maxClothesType)
                continue;

            List<Clothes> clothesOfSameType = new ArrayList<>();
            for(Clothes clothes: listOfClothes)
            {
                if(clothes.getType() == type)
                    clothesOfSameType.add(clothes);
            }
            if(clothesOfSameType.isEmpty())
                continue;

            int score = 0;
            int winningScore = Integer.MAX_VALUE;
            int winningClothesPos = 0;
            int j = 0;
            for(Clothes clothesCompetitor:clothesOfSameType)
            {
                for(Clothes clothesFinal:finalList)
                {
                    score += distanceBetweenColors(clothesCompetitor, clothesFinal);
                }
                if(score < winningScore)
                {
                    winningScore = score;
                    winningClothesPos = j;
                }
                j++;

            }
            finalList.add(clothesOfSameType.get(winningClothesPos));


        }

        ArrayList<Integer> idsList = new ArrayList<>();
        for( Clothes clothes:finalList )
        {
            idsList.add(clothes.getId());
        }
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra(Outfit.OUTFIT_LIST_CLOTHES_IDS, idsList);
        setResult(RESULT_OK, intent);
        finish();

    }

    private int distanceBetweenColors(Clothes clothesCompetitor, Clothes clothesFinal) {


        int total = 0;
        int competitorPrimary = clothesCompetitor.getPrimary_color();
        int finalPrimary = clothesFinal.getPrimary_color();
        int distancePrimaries = (Color.red(competitorPrimary)-Color.red(finalPrimary))*(Color.red(competitorPrimary)-Color.red(finalPrimary))
                + (Color.green(competitorPrimary)-Color.green(finalPrimary))*(Color.green(competitorPrimary)-Color.green(finalPrimary)) +
                (Color.blue(competitorPrimary)-Color.blue(finalPrimary))*(Color.blue(competitorPrimary)-Color.blue(finalPrimary));

        int competitorSecondary_1 = clothesCompetitor.getSecondary_color_1();
        int finalSecondary_1 = clothesFinal.getSecondary_color_1();
        int competitorSecondary_2 = clothesCompetitor.getSecondary_color_2();
        int finalSecondary_2 = clothesFinal.getSecondary_color_2();
        int distanceSecondaries_1_1;
        int distanceSecondaries_1_2;
        int distanceSecondaries_2_1;
        int distanceSecondaries_2_2;
        if(competitorSecondary_1 == 0 || finalSecondary_1 == 0)
        {
            distanceSecondaries_1_1 = 0;
        }
        else
        {
            distanceSecondaries_1_1 = (Color.red(competitorSecondary_1)-Color.red(finalSecondary_1))*(Color.red(competitorSecondary_1)-Color.red(finalSecondary_1))
                    + (Color.green(competitorSecondary_1)-Color.green(finalSecondary_1))*(Color.green(competitorSecondary_1)-Color.green(finalSecondary_1)) +
                    (Color.blue(competitorSecondary_1)-Color.blue(finalSecondary_1))*(Color.blue(competitorSecondary_1)-Color.blue(finalSecondary_1));

        }

        if(competitorSecondary_1 == 0 || finalSecondary_2 == 0)
        {
            distanceSecondaries_1_2 = 0;
        }
        else
        {
            distanceSecondaries_1_2 = (Color.red(competitorSecondary_1)-Color.red(finalSecondary_2))*(Color.red(competitorSecondary_1)-Color.red(finalSecondary_2))
                    + (Color.green(competitorSecondary_1)-Color.green(finalSecondary_2))*(Color.green(competitorSecondary_1)-Color.green(finalSecondary_2)) +
                    (Color.blue(competitorSecondary_1)-Color.blue(finalSecondary_2))*(Color.blue(competitorSecondary_1)-Color.blue(finalSecondary_2));

        }

        if(competitorSecondary_2 == 0 || finalSecondary_1 == 0)
        {
            distanceSecondaries_2_1 = 0;
        }
        else
        {
            distanceSecondaries_2_1 = (Color.red(competitorSecondary_2)-Color.red(finalSecondary_1))*(Color.red(competitorSecondary_2)-Color.red(finalSecondary_1))
                    + (Color.green(competitorSecondary_2)-Color.green(finalSecondary_1))*(Color.green(competitorSecondary_2)-Color.green(finalSecondary_1)) +
                    (Color.blue(competitorSecondary_2)-Color.blue(finalSecondary_1))*(Color.blue(competitorSecondary_2)-Color.blue(finalSecondary_1));

        }

        if(competitorSecondary_2 == 0 || finalSecondary_2 == 0)
        {
            distanceSecondaries_2_2 = 0;
        }
        else
        {
            distanceSecondaries_2_2 = (Color.red(competitorSecondary_2)-Color.red(finalSecondary_2))*(Color.red(competitorSecondary_2)-Color.red(finalSecondary_2))
                    + (Color.green(competitorSecondary_2)-Color.green(finalSecondary_2))*(Color.green(competitorSecondary_2)-Color.green(finalSecondary_2)) +
                    (Color.blue(competitorSecondary_2)-Color.blue(finalSecondary_2))*(Color.blue(competitorSecondary_2)-Color.blue(finalSecondary_2));

        }

        int distanceSecondaries = distanceSecondaries_2_2 + distanceSecondaries_2_1 + distanceSecondaries_1_2 + distanceSecondaries_1_1;

        total = distancePrimaries*10 + distanceSecondaries;
        return total;


    }


    private void getFilteredClothesFromDatabase()
    {

        Bundle args = getIntent().getExtras();
        if(args == null)
            return;

        int[] types;
        int size = 0;

        int i, j;
        if(args.getBooleanArray(FilterClothesActivity.TYPES) != null)
        {
            for(Boolean type:args.getBooleanArray(FilterClothesActivity.TYPES))
            {
                if(type)
                    size++;
            }

            types = new int[size];
            i = 1;
            j=0;

            for(Boolean type:args.getBooleanArray(FilterClothesActivity.TYPES))
            {
                if(type)
                {
                    types[j] = i;
                    j++;
                }
                i++;
            }
        }
        else
        {
            types = new int[(getResources().getStringArray(R.array.clothes_type_array)).length];
            i = 0;
            for(int t:types)
            {
                types[i] = i;
                i++;
            }

        }


        int[] categories;
        size = 0;

        if(args.getBooleanArray(FilterClothesActivity.CATEGORIES) != null) {
            for (Boolean category : args.getBooleanArray(FilterClothesActivity.CATEGORIES)) {
                if (category)
                    size++;
            }
            categories = new int[size];
            i = 1;
            j = 0;
            for (Boolean category : args.getBooleanArray(FilterClothesActivity.CATEGORIES)) {
                if (category) {
                    categories[j] = i;
                    j++;
                }
                i++;
            }
        }
        else
        {
            categories = new int[(getResources().getStringArray(R.array.clothes_category_array)).length];
            i = 0;
            for(int t:categories)
            {
                categories[i] = i;
                i++;
            }

        }


        boolean includeWishlist = args.getBoolean(FilterClothesActivity.WISHLIST, true);
        boolean includeWinter = args.getBoolean(FilterClothesActivity.WINTER, true);
        boolean includeSummer = args.getBoolean(FilterClothesActivity.SUMMER, true);
        boolean includeFall = args.getBoolean(FilterClothesActivity.FALL, true);
        boolean includeSpring = args.getBoolean(FilterClothesActivity.SPRING, true);
        boolean includeNoSeason = args.getBoolean(FilterClothesActivity.SEASONLESS, true);
        boolean includeNoPrice = args.getBoolean(FilterClothesActivity.PRICELESS, true);

        double priceLow, priceHigh;
        if(args.getString(FilterClothesActivity.PRICELOW) == null || args.getString(FilterClothesActivity.PRICELOW).matches(""))
            priceLow = Integer.MIN_VALUE;
        else
            priceLow = Double.parseDouble(args.getString(FilterClothesActivity.PRICELOW));

        if(args.getString(FilterClothesActivity.PRICEHIGH) == null || args.getString(FilterClothesActivity.PRICEHIGH).matches(""))
            priceHigh = Integer.MAX_VALUE;
        else
            priceHigh = Double.parseDouble(args.getString(FilterClothesActivity.PRICEHIGH));

        ArrayList<String> brandsStringArray;
        boolean includeAllBrands =  false;
        if(args.getStringArrayList(FilterClothesActivity.BRANDS) != null && args.getStringArrayList(FilterClothesActivity.BRANDS).size() != 0)
        {
            brandsStringArray = args.getStringArrayList(FilterClothesActivity.BRANDS);
            if(args.getBoolean(FilterClothesActivity.BRANDLESS))
                brandsStringArray.add("");
        }
        else
        {
            brandsStringArray = new ArrayList<>();
            includeAllBrands = true;
        }

        ArrayList<Integer> blacklistedIDS;
        if(args.getIntegerArrayList(FilterClothesActivity.BLACKLISTEDIDS) == null)
            blacklistedIDS = new ArrayList<>();
        else
            blacklistedIDS = args.getIntegerArrayList(FilterClothesActivity.BLACKLISTEDIDS);


        listOfClothes = Database.clothesDao().filterClothes(types,categories,includeWishlist,brandsStringArray, includeAllBrands, includeWinter,
                includeSummer,includeFall,includeSpring,includeNoSeason,priceLow,priceHigh,includeNoPrice, blacklistedIDS);

    }

    private void createThumbnailGridGrouping()
    {

        String[] divisions;
        if(groupingMode == GROUPING_TYPE)
            divisions = getResources().getStringArray(R.array.clothes_type_array_plural);
        else if(groupingMode == GROUPING_CATEGORY)
            divisions = getResources().getStringArray(R.array.clothes_category_array);
        else
            return;

        ImageHandle imageHandle = new ImageHandle(this, findViewById(R.id.scrollViewSelectClothesActivity));


        LinearLayout rowsLayout = null;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        int imagesPerRow;
        int spaceBetweenImagesPx;

        if(!listOfClothes.iterator().hasNext() || listOfClothes.get(0).getThumbnailPath() == null || !new File(listOfClothes.get(0).getThumbnailPath()).exists())
            return;

        imagesPerRow = size.x/(imageHandle.getBitmapFromFile(listOfClothes.get(0).getThumbnailPath())).getWidth();
        spaceBetweenImagesPx = (size.x-imagesPerRow * (imageHandle.getBitmapFromFile(listOfClothes.get(0).getThumbnailPath())).getWidth())/(imagesPerRow + 1);



        int j = 1;
        for(String group : divisions)
        {
            int i = 0;

            final LinearLayout groupLayout = new LinearLayout(this);
            groupLayout.setOrientation(LinearLayout.VERTICAL);
            groupLayout.setVisibility(View.VISIBLE);

            TextView tv = new TextView(this);
            tv.setPadding(spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx);
            tv.setTextColor(Color.BLACK);
            tv.setText(group + " ");
            tv.setTextSize(16);

            final ImageView iv = new ImageView(this);
            iv.setPadding(spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx);
            iv.setBackgroundResource(R.drawable.ic_down_arrow_black_12dp);

            LinearLayout groupHeader = new LinearLayout(this);
            groupHeader.setVisibility(View.GONE);
            groupHeader.setPadding(spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx);
            groupHeader.setOrientation(LinearLayout.HORIZONTAL);
            groupHeader.setVerticalGravity(Gravity.CENTER_VERTICAL);
            groupHeader.setBackgroundColor(Color.LTGRAY);
            groupHeader.addView(tv);
            groupHeader.addView(iv);
            groupHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(groupLayout.getVisibility() == View.GONE)
                    {
                        groupLayout.setVisibility(View.VISIBLE);
                        iv.setBackgroundResource(R.drawable.ic_down_arrow_black_12dp);
                    }
                    else
                    {
                        groupLayout.setVisibility(View.GONE);
                        iv.setBackgroundResource(R.drawable.ic_right_arrow_back_12dp);
                    }

                }
            });

            linearLayout.addView(groupHeader);
            linearLayout.addView(groupLayout);

            for(final Clothes clothes : listOfClothes)
            {
                if(groupingMode == GROUPING_TYPE && clothes.getType() != j)
                    continue;

                if(groupingMode == GROUPING_CATEGORY && clothes.getCategory() != j)
                    continue;

                if(clothes.getThumbnailPath() == null || !(new File(clothes.getThumbnailPath())).exists())
                    continue;


                if(i%imagesPerRow == 0 || i == 0)
                {
                    rowsLayout = new LinearLayout(this);
                    rowsLayout.setOrientation(LinearLayout.HORIZONTAL);

                    rowsLayout.setPadding(spaceBetweenImagesPx/2,0,spaceBetweenImagesPx/2,0);
                }


                groupHeader.setVisibility(View.VISIBLE);

                final ImageView thumbnailImageView  = new ImageView(this);
                final ImageView selectedSymbolImageView  = new ImageView(this);
                selectedSymbolImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_blue_24dp));
                selectedSymbolImageView.setVisibility(View.GONE);
                thumbnailImageView.setImageBitmap(imageHandle.getBitmapFromFile(clothes.getThumbnailPath()));
                if(clothes.isWishlist())
                    thumbnailImageView.setColorFilter(TRANSPARENT_YELLOW);

                thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(selectSingleClothes)
                        {
                            Intent intent = new Intent();
                            intent.putExtra(Outfit.OUTFIT_LIST_CLOTHES_IDS, clothes.getId());
                            setResult(RESULT_OK, intent);
                            finish();
                            return;
                        }

                        if(clothesSelected.contains(clothes))
                        {


                            clothesSelected.remove(clothes);
                            selectedSymbolImageView.setVisibility(View.GONE);
                            thumbnailImageView.setColorFilter(TRANSPARENT);
                            if(clothes.isWishlist())
                                thumbnailImageView.setColorFilter(TRANSPARENT_YELLOW);



                        }
                        else
                        {

                            clothesSelected.add(clothes);
                            selectedSymbolImageView.setVisibility(View.VISIBLE);
                            thumbnailImageView.setColorFilter(TRANSPARENT_BLACK);
                        }


                    }
                });

                if(selectedClothesIdsList != null && selectedClothesIdsList.contains(clothes.getId()))
                {

                    clothesSelected.add(clothes);
                    selectedSymbolImageView.setVisibility(View.VISIBLE);
                    thumbnailImageView.setColorFilter(TRANSPARENT_BLACK);
                }

                RelativeLayout individualLayout = new RelativeLayout(this);
                individualLayout.addView(thumbnailImageView );
                individualLayout.addView(selectedSymbolImageView);
                individualLayout.setPadding(spaceBetweenImagesPx/2,spaceBetweenImagesPx/2,spaceBetweenImagesPx/2,spaceBetweenImagesPx/2);
                rowsLayout.addView(individualLayout);

                if(i % imagesPerRow == 0)
                    groupLayout.addView(rowsLayout);

                i++;
            }
            j++;
        }
        Space space = new Space(this);
        space.setMinimumHeight((imageHandle.getBitmapFromFile(listOfClothes.get(0).getThumbnailPath())).getWidth());
        linearLayout.addView(space);

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {

        ArrayList<Integer> ids = new ArrayList<>();
        for(Clothes clothes:clothesSelected)
            ids.add(clothes.getId());
        outState.putIntegerArrayList(SELECTEDCLOTHESIDSLIST, ids);
        outState.putBoolean(SELECTSINGLECLOTHES, selectSingleClothes);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        selectedClothesIdsList = savedInstanceState.getIntegerArrayList(SELECTEDCLOTHESIDSLIST);
        selectSingleClothes = savedInstanceState.getBoolean(SELECTSINGLECLOTHES);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_clothes, menu);

        MenuItem done = menu.findItem(R.id.menu_select_clothes_done);
        if(selectSingleClothes)
        {
            done.setEnabled(false);
            done.setVisible(false);
        }


        super.onCreateOptionsMenu(menu);
        return true;


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.menu_select_clothes_done:
                ArrayList<Integer> idsList = new ArrayList<>();
                for( Clothes clothes:clothesSelected )
                {
                    idsList.add(clothes.getId());
                }
                Intent intent = new Intent();
                intent.putIntegerArrayListExtra(Outfit.OUTFIT_LIST_CLOTHES_IDS, idsList);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.menu_select_clothes_group_category:
                if(groupingMode == GROUPING_CATEGORY)
                    return true;
                groupingMode = GROUPING_CATEGORY;

                editor.putInt(GROUPINGMODE, groupingMode);
                editor.commit();
                recreate();
                return true;
            case R.id.menu_select_clothes_group_type:
                if(groupingMode == GROUPING_TYPE)
                    return true;
                groupingMode = GROUPING_TYPE;

                editor.putInt(GROUPINGMODE, groupingMode);
                editor.commit();
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
