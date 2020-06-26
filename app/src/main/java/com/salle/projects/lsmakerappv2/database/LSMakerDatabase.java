package com.salle.projects.lsmakerappv2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.salle.projects.lsmakerappv2.database.dao.BtDeviceDao;
import com.salle.projects.lsmakerappv2.model.BtDevice;

@Database(entities = {BtDevice.class}, version = 1, exportSchema = false)
public abstract class LSMakerDatabase extends RoomDatabase {

    private static LSMakerDatabase instance;
    private static Object mutex = new Object();

    public abstract BtDeviceDao deviceDao();

    public static LSMakerDatabase getInstance(Context context) {
        LSMakerDatabase result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    result = Room.databaseBuilder(context.getApplicationContext(),
                            LSMakerDatabase.class, "lsmaker_db")
                            .fallbackToDestructiveMigration()
                            .build();
                    instance = result;
                }
            }
        }
        return result;
    }
}
