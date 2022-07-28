package com.easyoutfit.easyoutfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ClothesFragment extends Fragment {


    private final static String SELECTEDCLOTHESIDSLIST = "easyoutfit.easyoutfit.selectedClothesIdsList";
    private final static String SELECTMODE = "easyoutfit.easyoutfit.selectMode";
    private final static String GROUPINGMODE = "easyoutfit.easyoutfit.groupingMode";

    private final static int TRANSPARENT_BLACK = 0x80000000;
    private final static int TRANSPARENT = 0x00000000;
    private final static int LIGHT_GRAY = 0x20000000;

    private final static int TRANSPARENT_YELLOW = 0x40FFD600;

    LinearLayout linearLayout;

    private OnClothesSelectedListener mListener;

    private AppDatabase Database;


    FloatingActionButton addClothesButton;

    private boolean selectMode;
    private int groupingMode;
    private MenuItem deleteMenu;
    private MenuItem createOutfitButton;
    private List<Clothes> clothesSelected;
    private List<ImageView> selectedCheckmarks;
    private List<ImageView> selectedThumbnails;
    private List<Integer> selectedClothesIdsList;

    public static final int GROUPING_TYPE = 1;
    public static final int GROUPING_CATEGORY = 2;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener.setCurrentClothesFragment(this);
        setHasOptionsMenu(true);

        clothesSelected = new ArrayList<>();
        selectedCheckmarks = new ArrayList<>();
        selectedThumbnails = new ArrayList<>();

        Database = AppDatabase.getAppDatabase(getActivity().getApplicationContext()); //create the database


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clothes, container, false);


        linearLayout = view.findViewById(R.id.linearLayoutClothesFragment);

        addClothesButton = view.findViewById(R.id.addClothesButton);

        addClothesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(mListener != null)
                    mListener.addClothes();
            }
        });


        mListener.updateDatabase();

        if(savedInstanceState != null)
        {
            selectedClothesIdsList = savedInstanceState.getIntegerArrayList(SELECTEDCLOTHESIDSLIST);

            selectMode = savedInstanceState.getBoolean(SELECTMODE, false);

        }

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        groupingMode = sharedPref.getInt(GROUPINGMODE, GROUPING_TYPE);

        createThumbnailGridGrouping(view);


        return view;
    }




    private void createThumbnailGridGrouping(View view)
    {
        
        String[] divisions;
        if(groupingMode == GROUPING_TYPE)
            divisions = getResources().getStringArray(R.array.clothes_type_array_plural);
        else if(groupingMode == GROUPING_CATEGORY)
            divisions = getResources().getStringArray(R.array.clothes_category_array);
        else
            return;

        ImageHandle imageHandle = new ImageHandle(getActivity(), view);

        List<Clothes> listOfClothes = Database.clothesDao().getAll();


        LinearLayout rowsLayout = null;

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);

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

            final LinearLayout groupLayout = new LinearLayout(getActivity());
            groupLayout.setOrientation(LinearLayout.VERTICAL);
            groupLayout.setVisibility(View.VISIBLE);

            TextView tv = new TextView(getActivity());
            tv.setPadding(spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx);
            tv.setTextColor(Color.BLACK);
            tv.setText(group + " ");
            tv.setTextSize(16);

            final ImageView iv = new ImageView(getActivity());
            iv.setPadding(spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx);
            iv.setBackgroundResource(R.drawable.ic_down_arrow_black_12dp);

            LinearLayout groupHeader = new LinearLayout(getActivity());
            groupHeader.setVisibility(View.GONE);
            groupHeader.setPadding(spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx,spaceBetweenImagesPx);
            groupHeader.setOrientation(LinearLayout.HORIZONTAL);
            groupHeader.setVerticalGravity(Gravity.CENTER_VERTICAL);
            groupHeader.setBackgroundColor(LIGHT_GRAY);
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
                    rowsLayout = new LinearLayout(getActivity());
                    rowsLayout.setOrientation(LinearLayout.HORIZONTAL);

                    rowsLayout.setPadding(spaceBetweenImagesPx/2,0,spaceBetweenImagesPx/2,0);
                }


                groupHeader.setVisibility(View.VISIBLE);

                final ImageView thumbnailImageView  = new ImageView(getActivity());
                final ImageView selectedSymbolImageView  = new ImageView(getActivity());
                selectedSymbolImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_blue_24dp));
                selectedSymbolImageView.setVisibility(View.GONE);
                thumbnailImageView.setImageBitmap(imageHandle.getBitmapFromFile(clothes.getThumbnailPath()));
                if(clothes.isWishlist())
                    thumbnailImageView.setColorFilter(TRANSPARENT_YELLOW);
                thumbnailImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(selectMode)
                            return false;
                        selectMode = true;
                        deleteMenu.setVisible(true);
                        deleteMenu.setEnabled(true);
                        createOutfitButton.setVisible(true);
                        createOutfitButton.setEnabled(true);
                        addClothesButton.setVisibility(View.GONE);
                        clothesSelected.add(clothes);
                        selectedSymbolImageView.setVisibility(View.VISIBLE);
                        thumbnailImageView.setColorFilter(TRANSPARENT_BLACK);
                        selectedCheckmarks.add(selectedSymbolImageView);
                        selectedThumbnails.add(thumbnailImageView);
                        return true;
                    }

                });

                thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(selectMode)
                        {
                            if(clothesSelected.contains(clothes))
                            {

                                clothesSelected.remove(clothes);
                                selectedSymbolImageView.setVisibility(View.GONE);
                                thumbnailImageView.setColorFilter(TRANSPARENT);
                                if(clothes.isWishlist())
                                    thumbnailImageView.setColorFilter(TRANSPARENT_YELLOW);


                                selectedCheckmarks.remove(selectedSymbolImageView);
                                selectedThumbnails.remove(thumbnailImageView);
                                if(clothesSelected.isEmpty())
                                    cancelSelectMode();
                            }
                            else
                            {
                                clothesSelected.add(clothes);
                                selectedSymbolImageView.setVisibility(View.VISIBLE);
                                thumbnailImageView.setColorFilter(TRANSPARENT_BLACK);
                                selectedCheckmarks.add(selectedSymbolImageView);
                                selectedThumbnails.add(thumbnailImageView);
                            }

                        }
                        else
                        {
                            mListener.dispatchEditClothes(clothes);
                        }
                    }
                });

                if(selectMode &&  selectedClothesIdsList != null && selectedClothesIdsList.contains(clothes.getId()))
                {

                    addClothesButton.setVisibility(View.GONE);
                    clothesSelected.add(clothes);
                    selectedCheckmarks.add(selectedSymbolImageView);
                    selectedThumbnails.add(thumbnailImageView);
                    selectedSymbolImageView.setVisibility(View.VISIBLE);
                    thumbnailImageView.setColorFilter(TRANSPARENT_BLACK);
                }

                RelativeLayout individualLayout = new RelativeLayout(getActivity());
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
        Space space = new Space(getActivity());
        space.setMinimumHeight((imageHandle.getBitmapFromFile(listOfClothes.get(0).getThumbnailPath())).getWidth());
        linearLayout.addView(space);

    }

    public boolean isSelectMode() {
        return selectMode;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_clothes, menu);
        deleteMenu = menu.findItem(R.id.menu_clothes_fragment_delete);
        createOutfitButton = menu.findItem(R.id.menu_clothes_fragment_add_to_outfit);
        if(selectMode)
        {
            deleteMenu.setVisible(true);
            deleteMenu.setEnabled(true);
            createOutfitButton.setVisible(true);
            createOutfitButton.setEnabled(true);
        }
        super.onCreateOptionsMenu(menu,inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        switch (item.getItemId()) {
            case R.id.menu_clothes_fragment_delete:
                selectMode = false;
                mListener.deleteClothes(clothesSelected);
                mListener.updateDatabase();
                mListener.reloadFragment();
                return true;
            case R.id.menu_clothes_fragment_add_to_outfit:
                selectMode = false;
                ArrayList<Integer> idsList = new ArrayList<>();
                for( Clothes clothes:clothesSelected )
                {
                    idsList.add(clothes.getId());
                }
                mListener.dispatchEditOutfit(mListener.saveOutfit(idsList));
                return true;
            case R.id.menu_clothes_fragment_group_category:
                if(groupingMode == GROUPING_CATEGORY)
                    return true;
                groupingMode = GROUPING_CATEGORY;

                editor.putInt(GROUPINGMODE, groupingMode);
                editor.commit();

                mListener.reloadFragment();
                return true;

            case R.id.menu_clothes_fragment_group_type:
                if(groupingMode == GROUPING_TYPE)
                    return true;
                groupingMode = GROUPING_TYPE;

                editor.putInt(GROUPINGMODE, groupingMode);
                editor.commit();

                mListener.reloadFragment();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClothesSelectedListener) {
            mListener = (OnClothesSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if(selectMode)
        {

            ArrayList<Integer> ids = new ArrayList<>();
            for(Clothes clothes:clothesSelected)
                ids.add(clothes.getId());
            outState.putIntegerArrayList(SELECTEDCLOTHESIDSLIST, ids);


        }
        outState.putBoolean(SELECTMODE, selectMode);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }





    public void cancelSelectMode() {
        selectMode = false;
        deleteMenu.setEnabled(false);
        deleteMenu.setVisible(false);
        createOutfitButton.setEnabled(false);
        createOutfitButton.setVisible(false);
        for(ImageView selected : selectedCheckmarks)
            selected.setVisibility(View.GONE);
        selectedCheckmarks.clear();
        for(ImageView selected : selectedThumbnails)
            selected.setColorFilter(TRANSPARENT);
        int i = 0;
        for(Clothes clothes:clothesSelected)
        {
            if(clothes.isWishlist())
                selectedThumbnails.get(i).setColorFilter(TRANSPARENT_YELLOW);
            i++;
        }

        selectedThumbnails.clear();
        addClothesButton.setVisibility(View.VISIBLE);
        clothesSelected.clear();

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnClothesSelectedListener {

        void reloadFragment();
        void addClothes();
        void updateDatabase();
        void deleteClothes(List<Clothes> clothesToDelete);
        void dispatchEditClothes(Clothes clothes);
        void setCurrentClothesFragment(ClothesFragment clothesFragment);
        Outfit saveOutfit(ArrayList<Integer> clothesToAddIDS);
        void dispatchEditOutfit(Outfit outfit);
    }

}
