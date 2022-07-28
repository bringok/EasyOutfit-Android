package com.easyoutfit.easyoutfit;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Diogo on 05/02/2018.
 */


@Entity
@TypeConverters({Outfit.Converters.class})
public class Outfit {

    public static final String OUTFIT_ID = "com.easyoutfit.easyoutfit.outfitID";
    public static final String OUTFIT_LIST_CLOTHES_IDS = "com.easyoutfit.easyoutfit.listClothesIDS";

    @PrimaryKey(autoGenerate = true)
    private int id;

    private ArrayList<Integer> clothesIDS;


    public Outfit(ArrayList<Integer> clothesIDS) {
        this.clothesIDS = clothesIDS;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getClothesIDS() {
        return clothesIDS;
    }

    public void setClothesIDS(ArrayList<Integer> clothesIDS) {
        this.clothesIDS = clothesIDS;
    }

    public static class Converters {
        @TypeConverter
        public ArrayList<Integer> fromString(String string) {
            Scanner scanner = new Scanner(string);
            ArrayList<Integer> list = new ArrayList<Integer>();
            while (scanner.hasNextInt()) {
                list.add(scanner.nextInt());
            }
            return list;
        }

        @TypeConverter
        public String arrayListIntegerToString(ArrayList<Integer> list) {
            StringBuilder strbul  = new StringBuilder();
            Iterator<Integer> iter = list.iterator();
            while(iter.hasNext())
            {
                strbul.append(iter.next());
                if(iter.hasNext()){
                    strbul.append(" ");
                }
            }
            return strbul.toString();
        }
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(OUTFIT_LIST_CLOTHES_IDS, clothesIDS);
        bundle.putInt(OUTFIT_ID, id);
        return  bundle;
    }

    public static Outfit outfitFromBundle(Bundle b)
    {
        Outfit outfit = new Outfit(b.getIntegerArrayList(OUTFIT_LIST_CLOTHES_IDS));
        outfit.setId(b.getInt(OUTFIT_ID));
        return outfit;
    }

    public void sortClothesListByType(AppDatabase Database)
    {
        List<Clothes> listOfClothes = new ArrayList<>();
        for(int id:this.getClothesIDS())
        {
            listOfClothes.add(Database.clothesDao().getClothesFromId(id));
        }

        Collections.sort(listOfClothes, new Comparator<Clothes>(){

            public int compare(Clothes obj1, Clothes obj2) {
                // ## Ascending order
                return obj1.getType() - obj2.getType(); // To compare integer values
            }
        });

        ArrayList<Integer> newIDSList = new ArrayList<>();
        for(Clothes clothes:listOfClothes)
        {
            newIDSList.add(clothes.getId());
        }
        this.setClothesIDS(newIDSList);
    }


}
