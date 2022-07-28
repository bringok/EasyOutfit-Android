package com.easyoutfit.easyoutfit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Diogo on 31/01/2018.
 */

@Dao
public interface ClothesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClothes(Clothes clothes);

    @Update
    void updateClothes(Clothes clothes);

    @Delete
    void deleteClothes(Clothes clothes);

    @Query("SELECT * FROM clothes")
    List<Clothes> getAll();

    @Query("SELECT * FROM clothes WHERE id = :id LIMIT 1")
    Clothes getClothesFromId(int id);

    @Query("SELECT * FROM clothes WHERE type IN (:types) AND " +
            "category IN (:categories) AND "+
            "((NOT wishlist) OR (wishlist = :includeWishlist)) AND "+
            "(:includeAllBrands OR brand IN (:brands)) AND "+
            "((:includeNoSeason AND noSeason) OR (:includeWinter AND seasonWinter) OR "+
            "(:includeSpring AND seasonSpring) OR (:includeFall AND seasonFall) OR "+
            "(:includeSummer AND seasonSummer)) AND " +
            "((price >= :priceMin AND price <= :priceMax) OR (:includeNoPrice AND price < 0)) AND " +
            "id NOT IN (:blackListIDS)")
    List<Clothes> filterClothes(int[] types, int[] categories, boolean includeWishlist, List<String> brands, boolean includeAllBrands, boolean includeWinter,
                                boolean includeSummer, boolean includeFall, boolean includeSpring, boolean includeNoSeason, double priceMin, double priceMax, boolean includeNoPrice, List<Integer> blackListIDS);



}
