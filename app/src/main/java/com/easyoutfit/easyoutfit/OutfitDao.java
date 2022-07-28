package com.easyoutfit.easyoutfit;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Diogo on 08/02/2018.
 */

@Dao
public interface OutfitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOutfit(Outfit outfit);

    @Update
    void updateOutfit(Outfit outfit);

    @Delete
    void deleteOutfit(Outfit outfit);

    @Query("SELECT * FROM outfit WHERE id = :id LIMIT 1")
    Outfit getOutfitFromId(int id);

    @Query("SELECT * FROM outfit")
    List<Outfit> getAll();
}
