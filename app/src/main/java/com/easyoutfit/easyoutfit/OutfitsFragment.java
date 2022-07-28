package com.easyoutfit.easyoutfit;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import java.util.ArrayList;
import java.util.List;


public class OutfitsFragment extends Fragment {

    private final static String SELECTED_OUTFITS_IDS_LIST = "easyoutfit.easyoutfit.selectedOutfitsIdsList";
    private final static String SELECTMODE = "easyoutfit.easyoutfit.selectMode";

    private float x1,x2;
    static final int MIN_DISTANCE = 50;

    private final static int TRANSPARENT_YELLOW = 0x40FFD600;

    private final static double OUTFIT_INDIVIDUAL_LAYOUT_SCREEN_FACTOR = 0.48;

    LinearLayout linearLayout;

    private AppDatabase Database;

    ImageHandle imageHandle;
    FloatingActionButton addOutfitsButton;

    private OnFragmentInteractionListener mListener;

    private boolean selectMode;
    private MenuItem deleteMenu;
    private List<Outfit> outfitsSelected;
    private List<ImageView> selectedCheckmarks;
    private List<Integer> selectedOutfitsIdsList;


    public OutfitsFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener.setCurrentOutfitsFragment(this);
        setHasOptionsMenu(true);

        outfitsSelected = new ArrayList<>();
        selectedCheckmarks = new ArrayList<>();

        Database = AppDatabase.getAppDatabase(getActivity().getApplicationContext()); //create the database


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clothes, container, false);


        linearLayout = view.findViewById(R.id.linearLayoutClothesFragment);

        addOutfitsButton = view.findViewById(R.id.addClothesButton);

        addOutfitsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(mListener != null)
                    mListener.addOutfits();
            }
        });

        imageHandle = new ImageHandle(getActivity(), view);

        mListener.updateDatabase();

        mListener.updateDatabase();

        if(savedInstanceState != null)
        {
            selectedOutfitsIdsList = savedInstanceState.getIntegerArrayList(SELECTED_OUTFITS_IDS_LIST);

            selectMode = savedInstanceState.getBoolean(SELECTMODE, false);

        }

        createOutfitsGrid();

        return view;


    }

    private void createOutfitsGrid()
    {

        List<Outfit> listOfOutfits = Database.outfitDao().getAll();


        LinearLayout rowsLayout = null;

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);

        int outfitsPerRow;
        int spaceBetweenOutfitsPx;


        final int outfitIndividualLayoutSize = (int)(Math.min(size.x,size.y) * OUTFIT_INDIVIDUAL_LAYOUT_SCREEN_FACTOR);
        outfitsPerRow = size.x/outfitIndividualLayoutSize;
        spaceBetweenOutfitsPx = (size.x-outfitsPerRow * outfitIndividualLayoutSize)/(outfitsPerRow + 1);



        int i = 0;

        for(final Outfit outfit : listOfOutfits)
        {

            if(i%outfitsPerRow == 0 || i == 0)
            {
                rowsLayout = new LinearLayout(getActivity());
                rowsLayout.setOrientation(LinearLayout.HORIZONTAL);

                rowsLayout.setPadding(spaceBetweenOutfitsPx/2,0,spaceBetweenOutfitsPx/2,0);
            }



            final RelativeLayout individualLayout = new RelativeLayout(getActivity());
            individualLayout.setMinimumWidth(outfitIndividualLayoutSize);
            individualLayout.setMinimumHeight(outfitIndividualLayoutSize);


            final ImageView selectedSymbolImageView  = new ImageView(getActivity());
            drawOutfitInRelativeLayout(outfit, individualLayout, outfitIndividualLayoutSize, 0, selectedSymbolImageView);


            individualLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch(motionEvent.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            x1 = motionEvent.getX();
                            return false;
                        case MotionEvent.ACTION_UP:
                            x2 = motionEvent.getX();
                            float deltaX = x2 - x1;
                            if (deltaX > MIN_DISTANCE)
                            {
                                drawOutfitInRelativeLayout(outfit, individualLayout, outfitIndividualLayoutSize, -1, selectedSymbolImageView);
                                return true;
                            }
                            else if(deltaX < -MIN_DISTANCE)
                            {
                                drawOutfitInRelativeLayout(outfit, individualLayout, outfitIndividualLayoutSize, 1, selectedSymbolImageView);
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                    }
                    return false;

                }

            });
            individualLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(selectMode)
                        return false;
                    selectMode = true;
                    deleteMenu.setVisible(true);
                    deleteMenu.setEnabled(true);
                    addOutfitsButton.setVisibility(View.GONE);
                    outfitsSelected.add(outfit);
                    selectedSymbolImageView.setVisibility(View.VISIBLE);
                    selectedCheckmarks.add(selectedSymbolImageView);
                    return true;
                }

            });

            individualLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(selectMode)
                    {
                        if(outfitsSelected.contains(outfit))
                        {
                            outfitsSelected.remove(outfit);
                            selectedSymbolImageView.setVisibility(View.GONE);


                            selectedCheckmarks.remove(selectedSymbolImageView);
                            if(outfitsSelected.isEmpty())
                                cancelSelectMode();
                        }
                        else
                        {
                            outfitsSelected.add(outfit);
                            selectedSymbolImageView.setVisibility(View.VISIBLE);
                            selectedCheckmarks.add(selectedSymbolImageView);
                        }

                    }
                    else
                    {
                        mListener.dispatchEditOutfit(outfit);
                    }
                }
            });

            if(selectMode &&  selectedOutfitsIdsList != null && selectedOutfitsIdsList.contains(outfit.getId()))
            {

                addOutfitsButton.setVisibility(View.GONE);
                outfitsSelected.add(outfit);
                selectedCheckmarks.add(selectedSymbolImageView);
                selectedSymbolImageView.setVisibility(View.VISIBLE);
            }


            individualLayout.setPadding(spaceBetweenOutfitsPx/2,spaceBetweenOutfitsPx/2,spaceBetweenOutfitsPx/2,spaceBetweenOutfitsPx/2);
            rowsLayout.addView(individualLayout);

            if(i % outfitsPerRow == 0)
                linearLayout.addView(rowsLayout);

            i++;
        }

        Space space = new Space(getActivity());
        space.setMinimumHeight(outfitIndividualLayoutSize);
        linearLayout.addView(space);

    }

    private void drawOutfitInRelativeLayout(Outfit outfit, RelativeLayout individualLayout, int layoutSize, int shiftImages, ImageView selectedSymbolImageView) {

        individualLayout.removeAllViews();

        List<Clothes> clothesInOutfit = new ArrayList<>();
        ArrayList<Integer> clothesIDS = outfit.getClothesIDS();
        if(shiftImages == 1)
        {
            int first = clothesIDS.get(0);
            clothesIDS.remove((Object) first);
            clothesIDS.add(first);
            outfit.setClothesIDS(clothesIDS);

        }
        else if(shiftImages == -1)
        {
            int last = clothesIDS.get(clothesIDS.size()-1);
            clothesIDS.remove((Object) last);
            clothesIDS.add(0, last);
            outfit.setClothesIDS(clothesIDS);
        }
        for(int id:clothesIDS)
        {
            clothesInOutfit.add(Database.clothesDao().getClothesFromId(id));
        }
        if(clothesInOutfit.isEmpty() || clothesInOutfit.get(0) == null)
            return;

        if(shiftImages == 1)
        {
            Clothes first = clothesInOutfit.get(0);
            clothesInOutfit.remove(first);
            clothesInOutfit.add(first);
        }

        int sizeOfThumbnail = imageHandle.getBitmapFromFile(clothesInOutfit.get(0).getThumbnailPath()).getWidth();

        if(clothesInOutfit.size() == 1)
        {
            Clothes clothes = clothesInOutfit.get(0);
            ImageView iv;
            RelativeLayout.LayoutParams params;

            iv = new ImageView(getActivity());
            iv.setImageBitmap(imageHandle.getBitmapFromFile(clothes.getThumbnailPath()));
            if(clothes.isWishlist())
                iv.setColorFilter(TRANSPARENT_YELLOW);
            params = new RelativeLayout.LayoutParams(sizeOfThumbnail, sizeOfThumbnail);
            params.leftMargin = (layoutSize-sizeOfThumbnail)/2;
            params.topMargin = (layoutSize-sizeOfThumbnail)/2;
            params.rightMargin = (layoutSize-sizeOfThumbnail)/2;
            params.bottomMargin = (layoutSize-sizeOfThumbnail)/2;

            individualLayout.addView(iv, params);
        }
        else
        {
            int distanceBetweenThumbnails = (layoutSize - sizeOfThumbnail)/(clothesInOutfit.size() - 1);
            int i = 0;
            for(Clothes clothes:clothesInOutfit)
            {
                ImageView iv;
                RelativeLayout.LayoutParams params;

                iv = new ImageView(getActivity());
                iv.setImageBitmap(imageHandle.getBitmapFromFile(clothes.getThumbnailPath()));
                if(clothes.isWishlist())
                    iv.setColorFilter(TRANSPARENT_YELLOW);
                params = new RelativeLayout.LayoutParams(sizeOfThumbnail,sizeOfThumbnail);
                params.leftMargin = i*distanceBetweenThumbnails;
                params.topMargin = i*distanceBetweenThumbnails;

                individualLayout.addView(iv, params);
                i++;
            }
        }

        selectedSymbolImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_blue_24dp));
        if(outfitsSelected != null && outfitsSelected.contains(outfit))
            selectedSymbolImageView.setVisibility(View.VISIBLE);
        else
            selectedSymbolImageView.setVisibility(View.GONE);
        individualLayout.addView(selectedSymbolImageView);



    }


    public boolean isSelectMode() {
        return selectMode;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_outfits, menu);
        deleteMenu = menu.findItem(R.id.menu_outfits_fragment_delete);
        if(selectMode)
        {
            deleteMenu.setVisible(true);
            deleteMenu.setEnabled(true);
        }
        super.onCreateOptionsMenu(menu,inflater);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_outfits_fragment_delete:
                selectMode = false;
                mListener.deleteOutfits(outfitsSelected);
                mListener.updateDatabase();
                mListener.reloadFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if(selectMode)
        {

            ArrayList<Integer> ids = new ArrayList<>();
            for(Outfit outfit:outfitsSelected)
                ids.add(outfit.getId());
            outState.putIntegerArrayList(SELECTED_OUTFITS_IDS_LIST, ids);


        }
        outState.putBoolean(SELECTMODE, selectMode);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void cancelSelectMode() {
        selectMode = false;
        outfitsSelected.clear();
        deleteMenu.setEnabled(false);
        deleteMenu.setVisible(false);
        for(ImageView selected : selectedCheckmarks)
            selected.setVisibility(View.GONE);
        selectedCheckmarks.clear();
        addOutfitsButton.setVisibility(View.VISIBLE);

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void reloadFragment();
        void addOutfits();
        void updateDatabase();
        void deleteOutfits(List<Outfit> outfitsToDelete);
        void setCurrentOutfitsFragment(OutfitsFragment outfitsFragment);
        void dispatchEditOutfit(Outfit outfit);
    }


}
