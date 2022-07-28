package com.easyoutfit.easyoutfit;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Diogo on 31/01/2018.
 */

@Database(entities = {Clothes.class,Outfit.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract ClothesDao clothesDao();
    public abstract OutfitDao outfitDao();

    static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "com.easyoutfit.easyoutfit.database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            //TODO
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    static void destroyInstance() {
        INSTANCE = null;
    }


}
